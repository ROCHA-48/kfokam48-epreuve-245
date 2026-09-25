import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// L'API Spring Boot tourne sur http://localhost:8080 (port du contrat).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
  },
});
