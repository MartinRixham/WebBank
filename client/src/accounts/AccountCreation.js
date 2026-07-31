import PersonalDetail from "./PersonalDetail.js";
import personalDetails from "./details.js";

import "./account-creation.css";

export default class AccountCreation {

    constructor(details = personalDetails) {

        this.details = details.map(detail => new PersonalDetail(detail));
    }

    async onBind(element) {

        const html = await import("./account-creation.html?raw");

        element.innerHTML = html.default;
    }
}
