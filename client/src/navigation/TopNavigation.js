import AccountSelector from "../accounts/AccountSelector.js";
import NavigationMenu from "./NavigationMenu.js";

import "./top-navigation.css";

export default class TopNavigation {

    menu = new NavigationMenu();

    accountSelector = new AccountSelector();

    async onBind(element) {

        const html = await import("./top-navigation.html?raw");

        element.innerHTML = html.default;
    }
}
