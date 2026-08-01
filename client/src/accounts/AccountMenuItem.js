import { Binding, Text } from "@datumjs/datum";

import "./account-menu-item.css";

export default class AccountMenuItem {

    #account;

    // Held privately so that the selection shared with the sibling items is
    // owned by, and tracked through, the menu alone.
    #selection;

    constructor(account, selection) {

        this.#account = account;
        this.#selection = selection;

        this.label = new Text(() => account.label);

        this.type = new Text(() => account.name);

        this.balance = new Text(() => account.formattedBalance);

        this.check = new Binding({

            visible: () => this.isSelected()
        });

        this.button = new Binding({

            classes: {

                "account-menu-item--selected": () => this.isSelected()
            },
            click: () => this.select()
        });
    }

    async onBind(element) {

        const html = await import("./account-menu-item.html?raw");

        element.innerHTML = html.default;
    }

    get account() {

        return this.#account;
    }

    isSelected() {

        return this.#selection.isSelected(this.#account);
    }

    select() {

        this.#selection.select(this.#account);
    }
}
