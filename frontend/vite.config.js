import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 3000,
    proxy: {
      '/api-v1.0': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      },
    },
  },
})
