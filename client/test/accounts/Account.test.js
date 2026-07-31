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

    it("rejects an account without a name", () => {

        expect(() => new Account("", "4821"))
            .toThrow("An account must have a name.");
    });

    it("rejects an account without a number", () => {

        expect(() => new Account("Checking", ""))
            .toThrow("An account must have a number.");
    });
});
