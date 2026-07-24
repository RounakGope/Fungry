import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,
    proxy: {
      '/api-v1.0': {
        target: 'https://fungry-xt5f.onrender.com',
        changeOrigin: true,
      },
    },
  },
})
