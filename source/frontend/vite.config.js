import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    setupFiles: './src/test/setup.js',
    css: false,
    exclude: ['e2e/**', 'node_modules/**'],
  },
  // Dùng chung file D:/DATN/.env với backend.
  // Vite chỉ đưa các biến VITE_* vào bundle chạy trên trình duyệt.
  envDir: '../../',
  server: {
    port: 5173,
    strictPort: true,
    // Cho phép đúng Dev Tunnel dùng trong buổi demo, tránh Vite từ chối Host header.
    allowedHosts: ['qvsmt6pq-5173.asse.devtunnels.ms'],
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
