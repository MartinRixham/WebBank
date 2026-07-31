import { describe, it, expect } from "vitest";

import Account from "../../src/accounts/Account.js";
import AccountSelector from "../../src/accounts/AccountSelector.js";

describe("account selector", () => {

    it("labels the button with the selected account", () => {

        const viewModel = new AccountSelector(new Account("Savings", "9911"));

        expect(viewModel.label().text()).toBe("Savings ••9911");
    });

    it("shows the account from the design by default", () => {

        expect(new AccountSelector().label().text()).toBe("Checking ••4821");
    });

    it("relabels the button when the account changes", () => {

        const viewModel = new AccountSelector(new Account("Savings", "9911"));

        viewModel.account = new Account("Checking", "4821");

        expect(viewModel.label().text()).toBe("Checking ••4821");
    });
});
