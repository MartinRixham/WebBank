import { Binding, Text } from "@datumjs/datum";

import "./personal-detail.css";

export default class PersonalDetail {

    #detail

    // A public data property rather than a private field so that Datum tracks
    // what the customer types and keeps the input in step with it.
    value = "";

    constructor(detail) {

        this.#detail = detail;
    }

    async onBind(element) {

        const html = await import("./personal-detail.html?raw");

        element.innerHTML = html.default;
    }

    get name() {

        return this.#detail.name;
    }

    label = new Text(() => this.#detail.label);

    input = new Binding({

        init: element => {

            element.placeholder = this.#detail.placeholder;
        },
        value: value => {

            if (value !== undefined) {

                this.value = value;
            }

            return this.value;
        }
    });
}
