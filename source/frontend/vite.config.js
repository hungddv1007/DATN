import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import { fileURLToPath } from 'node:url'

const sharedEnvDir = fileURLToPath(new URL('../../', import.meta.url))

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, sharedEnvDir, 'VITE_')

  const allowedHosts = (env.VITE_ALLOWED_HOSTS || '')
    .split(',')
    .map(host => host.trim())
    .filter(Boolean)

  return {
    plugins: [react()],

    test: {
      environment: 'jsdom',
      setupFiles: './src/test/setup.js',
      css: false,
      exclude: ['e2e/**', 'node_modules/**'],
    },

    envDir: sharedEnvDir,

    server: {
      port: 5173,
      strictPort: true,
      allowedHosts,

      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
      },
    },
  }
})