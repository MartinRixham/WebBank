import { describe, it, expect } from "vitest";

import Account from "../../src/accounts/Account.js";
import AccountMenu from "../../src/accounts/AccountMenu.js";
import Selection from "../../src/navigation/Selection.js";

const ACCOUNTS = [

    new Account("Checking", "4821", 51340.28),
    new Account("Savings", "7203", 24810)
];

const SELECTED = "account-menu-item--selected";

function isHighlighted(item) {

    return item.button().classes[SELECTED]();
}

describe("account menu", () => {

    it("has an item for every account", () => {

        const viewModel = new AccountMenu(ACCOUNTS, new Selection());

        expect(viewModel.items.map(item => item.account)).toEqual(ACCOUNTS);
    });

    it("highlights the account that is selected", () => {

        const viewModel = new AccountMenu(ACCOUNTS, new Selection(ACCOUNTS[1]));

        expect(viewModel.items.map(isHighlighted)).toEqual([false, true]);
    });

    it("moves the highlight on to the account that is clicked", () => {

        const viewModel = new AccountMenu(ACCOUNTS, new Selection(ACCOUNTS[0]));

        viewModel.items[1].button().click();

        expect(viewModel.items.map(isHighlighted)).toEqual([false, true]);
    });

    it("shares its selection with the items", () => {

        const selection = new Selection(ACCOUNTS[0]);

        const viewModel = new AccountMenu(ACCOUNTS, selection);

        viewModel.items[1].button().click();

        expect(selection.selected).toBe(ACCOUNTS[1]);
    });

    it("has no items when there are no accounts", () => {

        expect(new AccountMenu([], new Selection()).items).toEqual([]);
    });
});
