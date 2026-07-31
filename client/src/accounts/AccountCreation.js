import { Click } from "@datumjs/datum";

import PersonalDetail from "./PersonalDetail.js";
import personalDetails from "./details.js";
import createAccount from "./createAccount.js";

import "./account-creation.css";

export default class AccountCreation {

    #createAccount;

    constructor(details = personalDetails, create = createAccount) {

        this.details = details.map(detail => new PersonalDetail(detail));
        this.#createAccount = create;
    }

    async onBind(element) {

        const html = await import("./account-creation.html?raw");

        element.innerHTML = html.default;
    }

    account() {

        return Object.fromEntries(
            this.details.map(detail => [detail.name, detail.value]));
    }

    create = new Click(() => this.#createAccount(this.account()));
}
