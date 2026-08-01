import { describe, it, expect } from "vitest";

import Account from "../../src/accounts/Account.js";
import AccountMenuItem from "../../src/accounts/AccountMenuItem.js";
import Selection from "../../src/navigation/Selection.js";

const ACCOUNT = new Account("Savings", "7203", 24810);

const OTHER = new Account("Investment", "9901", 138200);

const SELECTED = "account-menu-item--selected";

describe("account menu item", () => {

    it("labels the button with the account", () => {

        const viewModel = new AccountMenuItem(ACCOUNT, new Selection());

        expect(viewModel.label().text()).toBe("Savings ••7203");
    });

    it("names the type of the account", () => {

        const viewModel = new AccountMenuItem(ACCOUNT, new Selection());

        expect(viewModel.type().text()).toBe("Savings");
    });

    it("shows the balance of the account", () => {

        const viewModel = new AccountMenuItem(ACCOUNT, new Selection());

        expect(viewModel.balance().text()).toBe("$24,810.00");
    });

    it("keeps the account it was given", () => {

        const viewModel = new AccountMenuItem(ACCOUNT, new Selection());

        expect(viewModel.account).toBe(ACCOUNT);
    });

    it("is highlighted when its account is selected", () => {

        const viewModel = new AccountMenuItem(ACCOUNT, new Selection(ACCOUNT));

        expect(viewModel.button().classes[SELECTED]()).toBe(true);
    });

    it("is not highlighted when another account is selected", () => {

        const viewModel = new AccountMenuItem(ACCOUNT, new Selection(OTHER));

        expect(viewModel.button().classes[SELECTED]()).toBe(false);
    });

    it("checks the account that is selected", () => {

        const viewModel = new AccountMenuItem(ACCOUNT, new Selection(ACCOUNT));

        expect(viewModel.check().visible()).toBe(true);
    });

    it("leaves the account that is not selected unchecked", () => {

        const viewModel = new AccountMenuItem(ACCOUNT, new Selection(OTHER));

        expect(viewModel.check().visible()).toBe(false);
    });

    it("selects its account when clicked", () => {

        const selection = new Selection(OTHER);

        const viewModel = new AccountMenuItem(ACCOUNT, selection);

        viewModel.button().click();

        expect(selection.isSelected(ACCOUNT)).toBe(true);
    });

    it("is highlighted once it has been clicked", () => {

        const viewModel = new AccountMenuItem(ACCOUNT, new Selection(OTHER));

        viewModel.button().click();

        expect(viewModel.button().classes[SELECTED]()).toBe(true);
    });

    it("stays selected when clicked again", () => {

        const viewModel = new AccountMenuItem(ACCOUNT, new Selection(ACCOUNT));

        viewModel.button().click();

        expect(viewModel.button().classes[SELECTED]()).toBe(true);
    });
});
