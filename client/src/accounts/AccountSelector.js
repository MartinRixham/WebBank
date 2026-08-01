import { Binding, Classes, Text } from "@datumjs/datum";

import AccountMenu from "./AccountMenu.js";
import allAccounts from "./accounts.js";
import Selection from "../navigation/Selection.js";

import "./account-selector.css";

export default class AccountSelector {

    // A public data property rather than a private field so that Datum tracks it
    // and repaints the menu and the chevron as the menu opens and closes.
    open = false;

    constructor(accounts = allAccounts) {

        this.selection =
            new Selection(accounts.length ? accounts[0] : null);

        this.menu = new AccountMenu(accounts, this.selection);

        this.label = new Text(() => this.selected ? this.selected.label : "");

        this.chevron = new Classes({

            "account-selector__chevron--open": () => this.open
        });

        this.button = new Binding({

            click: () => this.toggle()
        });

        this.dropdown = new Binding({

            visible: () => this.open,

            // A click on an account bubbles up to the dropdown, so switching
            // account closes the menu without the items knowing about it.
            click: () => this.close()
        });
    }

    async onBind(element) {

        const html = await import("./account-selector.html?raw");

        element.innerHTML = html.default;
    }

    get selected() {

        return this.selection.selected;
    }

    toggle() {

        this.open = !this.open;
    }

    close() {

        this.open = false;
    }
}
