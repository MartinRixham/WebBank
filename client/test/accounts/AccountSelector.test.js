import { describe, it, expect } from "vitest";

import Account from "../../src/accounts/Account.js";
import AccountSelector from "../../src/accounts/AccountSelector.js";

const ACCOUNTS = [

    new Account("Savings", "9911", 24810),
    new Account("Investment", "9901", 138200)
];

const OPEN = "account-selector__chevron--open";

describe("account selector", () => {

    it("labels the button with the selected account", () => {

        const viewModel = new AccountSelector(ACCOUNTS);

        expect(viewModel.label().text()).toBe("Savings ••9911");
    });

    it("shows the account from the design by default", () => {

        expect(new AccountSelector().label().text()).toBe("Checking ••4821");
    });

    it("offers every account in the design by default", () => {

        expect(new AccountSelector().menu.items.map(item => item.label().text()))
            .toEqual(["Checking ••4821", "Savings ••7203", "Investment ••9901"]);
    });

    it("offers the accounts it is given", () => {

        const viewModel = new AccountSelector(ACCOUNTS);

        expect(viewModel.menu.items.map(item => item.account)).toEqual(ACCOUNTS);
    });

    it("relabels the button when another account is switched to", () => {

        const viewModel = new AccountSelector(ACCOUNTS);

        viewModel.menu.items[1].button().click();

        expect(viewModel.label().text()).toBe("Investment ••9901");
    });

    it("labels nothing when there are no accounts", () => {

        expect(new AccountSelector([]).label().text()).toBe("");
    });

    it("keeps the menu shut to begin with", () => {

        expect(new AccountSelector(ACCOUNTS).dropdown().visible()).toBe(false);
    });

    it("opens the menu when the button is clicked", () => {

        const viewModel = new AccountSelector(ACCOUNTS);

        viewModel.button().click();

        expect(viewModel.dropdown().visible()).toBe(true);
    });

    it("shuts the menu when the button is clicked again", () => {

        const viewModel = new AccountSelector(ACCOUNTS);

        viewModel.button().click();
        viewModel.button().click();

        expect(viewModel.dropdown().visible()).toBe(false);
    });

    it("shuts the menu when an account is switched to", () => {

        const viewModel = new AccountSelector(ACCOUNTS);

        viewModel.button().click();
        viewModel.dropdown().click();

        expect(viewModel.dropdown().visible()).toBe(false);
    });

    it("turns the chevron over while the menu is open", () => {

        const viewModel = new AccountSelector(ACCOUNTS);

        viewModel.button().click();

        expect(viewModel.chevron().classes[OPEN]()).toBe(true);
    });

    it("leaves the chevron alone while the menu is shut", () => {

        const viewModel = new AccountSelector(ACCOUNTS);

        expect(viewModel.chevron().classes[OPEN]()).toBe(false);
    });
});
