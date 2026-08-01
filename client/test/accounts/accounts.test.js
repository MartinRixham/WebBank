import { describe, it, expect } from "vitest";

import accounts from "../../src/accounts/accounts.js";

describe("accounts", () => {

    it("has the accounts from the design", () => {

        expect(accounts.map(account => account.label))
            .toEqual(["Checking ••4821", "Savings ••7203", "Investment ••9901"]);
    });

    it("has the balances from the design", () => {

        expect(accounts.map(account => account.formattedBalance))
            .toEqual(["$51,340.28", "$24,810.00", "$138,200.00"]);
    });
});
