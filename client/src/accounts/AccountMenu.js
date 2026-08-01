import AccountMenuItem from "./AccountMenuItem.js";

import "./account-menu.css";

export default class AccountMenu {

    constructor(accounts, selection) {

        this.items =
            accounts.map(account => new AccountMenuItem(account, selection));
    }

    async onBind(element) {

        const html = await import("./account-menu.html?raw");

        element.innerHTML = html.default;
    }
}
