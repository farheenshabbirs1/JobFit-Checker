import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev server proxies /api straight to api-service, so the frontend never needs to know the
// backend's host/port itself -- matches how it's fronted in docker-compose / behind an
// ingress in the AKS deployment too.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: process.env.VITE_API_PROXY_TARGET || 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
});
