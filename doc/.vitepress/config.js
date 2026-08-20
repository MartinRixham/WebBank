import { defineConfig } from "vitepress";

// The wiki is documentation only: it is not part of the Maven reactor and
// nothing in client, server or database depends on it.
export default defineConfig({

    title: "WebBank",
    description: "The business requirements of the WebBank online banking demonstration.",
    lang: "en-GB",
    cleanUrls: true,

    themeConfig: {

        nav: [
            { text: "Home", link: "/" }
        ],

        sidebar: [
            {
                text: "The application",
                items: [
                    { text: "Overview", link: "/" }
                ]
            }
        ],

        outline: [2, 3],

        search: {
            provider: "local"
        }
    }
});
