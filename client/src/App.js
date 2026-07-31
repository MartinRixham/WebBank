import AccountCreation from "./accounts/AccountCreation.js";
import TopNavigation from "./navigation/TopNavigation.js";

import "./app.css";

export default class App {

    topNavigation = new TopNavigation();

    accountCreation = new AccountCreation();

    async onBind(element) {

        const html = await import("./app.html?raw");

        element.innerHTML = html.default;
    }
}
