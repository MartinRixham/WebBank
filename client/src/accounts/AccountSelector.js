import { Text } from "@datumjs/datum";

import { selectedAccount } from "./accounts.js";

import "./account-selector.css";

export default class AccountSelector {

    constructor(account = selectedAccount) {

        this.account = account;

        this.label = new Text(() => this.account.label);
    }

    async onBind(element) {

        const html = await import("./account-selector.html?raw");

        element.innerHTML = html.default;
    }
}
