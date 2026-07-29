import { defineConfig } from "vite";

export default defineConfig({

    build: {

        // The site is built into the maven target directory from where it is
        // packaged onto the classpath under web/.
        outDir: "target/dist",
        emptyOutDir: true
    }
});
