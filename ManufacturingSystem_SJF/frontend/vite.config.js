import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,
    // The backend only allows requests from http://localhost:5173 (CORS).
    // If this port is busy, stop with an error instead of silently using another one.
    strictPort: true,
  },
});
