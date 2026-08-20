import { defineConfig } from "vitepress";

// The wiki is documentation only: it is not part of the Maven reactor and
// nothing in client, server or database depends on it.
export default defineConfig({

    title: "WebBank",
    description: "Documentation for the WebBank online banking demonstration.",
    lang: "en-GB",
    cleanUrls: true,

    themeConfig: {

        nav: [
            { text: "Home", link: "/" },
            { text: "Database API", link: "/database/" }
        ],

        sidebar: [
            {
                text: "The application",
                items: [
                    { text: "Overview", link: "/" }
                ]
            },
            {
                text: "Database API",
                items: [
                    { text: "Overview", link: "/database/" },
                    { text: "Tables", link: "/database/tables" },
                    { text: "Records", link: "/database/records" },
                    { text: "Scans", link: "/database/scans" },
                    { text: "Batches", link: "/database/batches" },
                    { text: "Reference", link: "/database/reference" }
                ]
            }
        ],

        outline: [2, 3],

        search: {
            provider: "local"
        }
    }
});
