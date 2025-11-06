import react from "@vitejs/plugin-react"
import { defineConfig } from "vite"
import tsconfigPaths from "vite-tsconfig-paths"

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), tsconfigPaths()],
  server: {
    port: 5173,
    strictPort: true, // fail if 5173 is busy to keep OAuth origin stable
  },
  preview: {
    port: 5173,
    strictPort: true,
  },
})