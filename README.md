# 🍔 Fungry – Food Delivery Application (Zomato Clone)

Fungry is a backend-focused food delivery application inspired by platforms like Zomato.  
It is designed to simulate **real-world, high-scale order processing**, focusing on **performance, consistency, and scalability** using Spring Boot and modern backend technologies.

---

## 🚀 Project Overview

Fungry replicates a food ordering ecosystem with **users, restaurants, admins, orders, payments, and promotions**.  
The system is built with production-grade practices such as **caching, transactions, distributed locking, scheduling, and secure payment handling**.

This project demonstrates how large-scale food delivery platforms manage **high traffic, concurrent orders, and asynchronous workflows**.

---

## 🧠 What I Learned

- Efficient caching strategies to reduce database load
- Transaction management for critical business flows (orders & payments)
- Distributed locking to handle concurrency safely
- Payment gateway integration with webhook handling
- Logging, auditing, and scheduling background jobs
- Scalable search and rate limiting mechanisms

---

## 🧩 Core Features

### 👤 User / Restaurant / Admin APIs
- User registration and authentication
- Restaurant onboarding and menu management
- Admin controls for moderation and monitoring

### 🛒 Order Management
- Order placement with transactional safety
- Real-time order status updates
- Inventory consistency using Redis locks

### 💸 Payments
- Secure payment processing using Stripe
- Webhook handling for asynchronous callbacks
- Idempotency keys to prevent duplicate payments

### 🎟️ Promotions
- Promo code application
- Automated expiration and activation using scheduled jobs

---

## ⚙️ Tech Stack

| Component | Tool / Technology | Purpose |
|---------|------------------|---------|
| Backend | Spring Boot | Core application framework |
| Database | PostgreSQL | Persistent data storage |
| Caching | Redis, Spring Cache | Reduce DB hits, faster responses |
| Transactions | Spring Transaction Management | Ensure atomic operations |
| Logging | SLF4J + Logback | Debugging & auditing |
| Scheduling | Spring Scheduler | Promo expiry, reports |
| Payments | Stripe API | Secure online payments |
| Search | Elasticsearch | Fast restaurant & menu search |
| Rate Limiting | Bucket4j + Redis | Prevent abuse |
| Geolocation | Google Maps API | Location-based delivery logic |

---

## 🔐 Key Engineering Challenges Solved

### ⚡ High Concurrency Order Handling
- Used **Redis distributed locks** to prevent race conditions
- Ensured inventory consistency during simultaneous orders

### 💳 Payment Reliability
- Implemented **Stripe webhooks** for async payment confirmation
- Used **idempotency keys** to handle retries safely

### 📈 Performance Optimization
- Cached frequently accessed data like menus and restaurants
- Reduced response time significantly under load testing

---

## 🌟 Extra Mile Implementations

- 📍 **Geolocation support** using Google Maps API
- 🚦 **Rate limiting** to prevent API abuse
- 🔎 **Elasticsearch integration** for fast and scalable search
- 🕒 **Cron-based promotions** using Spring Scheduler

---

## 📊 Performance (Simulation)

- Processed **5,000+ orders/hour** in simulated load tests
- Reduced API response times by **~60% using Redis**
- Improved search performance by **~80% using Elasticsearch**
- Handled **1,000+ concurrent users** with rate limiting enabled

---

## 📝 Resume-Ready Description

> Built **Fungry**, a Zomato-inspired food delivery backend using Spring Boot; processed 5,000+ orders/hour in simulations with Redis caching and Stripe payments, reducing response times by 60%.  
> Implemented transactional order handling, distributed locks, scheduled promotions, geolocation via Google Maps, and Elasticsearch-powered search, demonstrating scalable e-commerce system design.

---

## 📌 Future Enhancements

- Real-time delivery partner tracking (WebSocket)
- Microservices migration
- Event-driven architecture with Kafka
- Advanced analytics dashboard

---

## 👨‍💻 Author

**Rounak Gope**  
Java Full Stack Developer | Spring Boot | Backend Engineering  
Linux • PostgreSQL • Redis • Elasticsearch

---

## ⭐ If you like this project
Give it a star ⭐ and feel free to explore or contribute!

