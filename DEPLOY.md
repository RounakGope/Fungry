# Deploying Fungry v2

Production setup:

| Part | Where | Notes |
|---|---|---|
| Frontend (React + Vite) | Vercel, project `fungry-official` | `vercel.json` proxies `/api-v2.0/*` to the backend, so the browser only talks to one origin and the session cookie is first-party |
| Backend (Spring Boot, Docker) | Render web service `Fungry` | Free instance, sleeps after 15 min idle |
| Cache | Render Key Value (Redis-compatible) | Free instance, same region as the backend, internal URL |
| Database | PostgreSQL | Whatever `SPRING_DATASOURCE_URL` points to |
| Payments | Stripe (test mode) | PaymentIntents + Payment Element; the webhook confirms orders |

## Backend environment variables (Render → Fungry → Environment)

| Variable | Value |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host>:5432/<db>` (keep the current value) |
| `SPRING_DATASOURCE_USERNAME` | keep the current value |
| `SPRING_DATASOURCE_PASSWORD` | keep the current value |
| `CORS_ALLOWED_ORIGINS` | `https://fungry-official.vercel.app` |
| `REDIS_URL` | Internal URL of the Key Value instance, e.g. `redis://red-xxxxxxxx:6379` |
| `STRIPE_SECRET_KEY` | `sk_test_...` from Stripe → Developers → API keys |
| `STRIPE_WEBHOOK_SECRET` | `whsec_...` signing secret of the **dashboard** webhook endpoint (step 3), not the one `stripe listen` prints |
| `ORDER_EXPIRY_MINUTES` | optional, defaults to `10` |
| `COOKIE_SAME_SITE` | optional, defaults to `lax`; use `none` only if the frontend calls the API cross-origin |
| `SHOW_SQL` | optional, `true` to log SQL |

Every variable has a local default in `application.properties`, so the app still runs on a laptop with no env vars set. Locally, Stripe keys go in `backend/src/main/resources/application-secrets.properties`, which is gitignored.

## Frontend environment variables (Vercel → fungry-official → Settings → Environment Variables)

| Variable | Value |
|---|---|
| `VITE_STRIPE_PUBLISHABLE_KEY` | `pk_test_...` from Stripe → Developers → API keys |
| `VITE_API_BASE_URL` | **delete it**. The client defaults to `/api-v2.0`, which `vercel.json` proxies to Render |

Vite bakes these in at build time, so they only take effect on the next deploy.

## Steps

1. **Create the cache.** Render → New → Key Value. Use the **same region** as the `Fungry` web service, the Free plan, and maxmemory policy `allkeys-lru`. Copy the **Internal Key Value URL**.
2. **Add the backend variables.** On the `Fungry` web service, add `REDIS_URL`, `STRIPE_SECRET_KEY` and `STRIPE_WEBHOOK_SECRET`. Choose "Save only" so the old code doesn't redeploy yet.
3. **Create the Stripe webhook.** Stripe (test mode) → Developers → Webhooks → Add endpoint.
   - URL: `https://fungry-xt5f.onrender.com/api-v2.0/payment/webhook`
   - Events: `payment_intent.succeeded`, `payment_intent.payment_failed`
   - Copy its signing secret into `STRIPE_WEBHOOK_SECRET` on Render.
4. **Frontend variables.** On Vercel, add `VITE_STRIPE_PUBLISHABLE_KEY` and delete `VITE_API_BASE_URL`. The live site keeps working until the next build.
5. **Ship it.** Merge `Phase-2-Local` into `main`. Vercel builds `main` automatically. Render redeploys too if auto-deploy is on; otherwise click Manual Deploy → Deploy latest commit.
6. **Check.**
   - `https://fungry-xt5f.onrender.com/actuator/health` should return `{"status":"UP"}`.
   - On `https://fungry-official.vercel.app`, sign up, log in, open a restaurant, and reload: you should stay logged in.
   - Place an order and pay with test card `4242 4242 4242 4242`, any future expiry and any CVC. The page should move to the order within a few seconds, and Stripe → Webhooks should show a `200` delivery.
   - Render logs should show `Started FungryApplication`, `Order N confirmed via Stripe webhook`, and no `Cache GET failed` warnings.
7. **Rollback if needed.** Use Render → Deploys → Rollback on the previous deploy, and Vercel → Instant Rollback.

## Things to know

- **Schema changes:** `ddl-auto=update` adds new tables and columns on startup. If the first v2 boot fails with a schema error against the old v1 data, point `SPRING_DATASOURCE_*` at a fresh database.
- **Render free Postgres** databases expire 30 days after creation. Use a free Neon or Supabase database for anything that must stay up.
- **Cold starts:** after 15 idle minutes the backend sleeps and the next request takes about 50 s. Vercel waits up to 120 s for the proxy, so the page still loads, just slowly. To avoid it, ping `https://fungry-xt5f.onrender.com/actuator/health` every 10 minutes from a free uptime monitor (cron-job.org, UptimeRobot). One always-on service fits in Render's 750 free hours a month.
- **Cache outages:** if Redis is unreachable, requests fall through to the database and a warning is logged, instead of failing (`RedisConfig.errorHandler()`). The free Key Value instance keeps data in memory only, which is fine for a cache.
- **Sessions:** sessions live in the backend's memory, so a restart or sleep logs users out. Moving them to Redis with Spring Session is a natural next step.

## Running locally

```bash
cp .env.example .env        # fill in a DB password
docker compose up --build   # Postgres, Redis, backend :9080, frontend :8081
```

For payments locally, put `VITE_STRIPE_PUBLISHABLE_KEY` in `frontend/.env` and forward webhooks with the Stripe CLI:

```bash
stripe listen --forward-to localhost:9080/api-v2.0/payment/webhook
```

Put the `whsec_...` it prints into `application-secrets.properties`.

Or run Postgres and Redis yourself, start the backend from the IDE (defaults: `localhost:5432/fungry`, `redis://localhost:6379`), and run `npm run dev` in `frontend/` (Vite proxies `/api-v2.0` to `:9080`).
