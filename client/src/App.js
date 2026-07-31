import TopNavigation from "./navigation/TopNavigation.js";

import "./app.css";

export default class App {

    topNavigation = new TopNavigation();

    async onBind(element) {

        const html = await import("./app.html?raw");

        element.innerHTML = html.default;
    }
}
