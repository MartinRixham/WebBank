import { describe, it, expect } from "vitest";

import sections from "../../src/navigation/sections.js";

describe("navigation sections", () => {

    it("lists the domain areas in the order of the design", () => {

        expect(sections.map(section => section.label)).toEqual([
            "Account Creation",
            "Account Summary",
            "Transactions",
            "Transfers & Payments",
            "Statements",
            "Audit"
        ]);
    });

    it("identifies each domain area by the id of its section", () => {

        expect(sections.map(section => section.id)).toEqual([
            "create",
            "summary",
            "transactions",
            "transfers",
            "statements",
            "audit"
        ]);
    });
});
