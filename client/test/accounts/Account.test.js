import { describe, it, expect } from "vitest";

import Account from "../../src/accounts/Account.js";

describe("account", () => {

    it("keeps its name", () => {

        expect(new Account("Checking", "4821").name).toBe("Checking");
    });

    it("masks all but the last four digits of its number", () => {

        expect(new Account("Checking", "10098374821").maskedNumber).toBe("••4821");
    });

    it("masks a number that is exactly four digits long", () => {

        expect(new Account("Checking", "4821").maskedNumber).toBe("••4821");
    });

    it("shows a number shorter than four digits in full", () => {

        expect(new Account("Checking", "21").maskedNumber).toBe("••21");
    });

    it("labels itself with its name and its masked number", () => {

        expect(new Account("Checking", "10098374821").label)
            .toBe("Checking ••4821");
    });

    it("keeps its balance", () => {

        expect(new Account("Checking", "4821", 51340.28).balance).toBe(51340.28);
    });

    it("formats its balance as an amount of money", () => {

        expect(new Account("Checking", "4821", 51340.28).formattedBalance)
            .toBe("$51,340.28");
    });

    it("formats a whole balance with its cents", () => {

        expect(new Account("Savings", "7203", 24810).formattedBalance)
            .toBe("$24,810.00");
    });

    it("formats an overdrawn balance", () => {

        expect(new Account("Checking", "4821", -120.5).formattedBalance)
            .toBe("-$120.50");
    });

    it("starts with nothing in it when given no balance", () => {

        expect(new Account("Checking", "4821").formattedBalance).toBe("$0.00");
    });

    it("rejects an account without a name", () => {

        expect(() => new Account("", "4821"))
            .toThrow("An account must have a name.");
    });

    it("rejects an account without a number", () => {

        expect(() => new Account("Checking", ""))
            .toThrow("An account must have a number.");
    });
});
