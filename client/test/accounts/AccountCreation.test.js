import { describe, it, expect } from "vitest";

import AccountCreation from "../../src/accounts/AccountCreation.js";

describe("account creation", () => {

    it("asks for the details the design lists, in its order", () => {

        const viewModel = new AccountCreation();

        expect(viewModel.details.map(detail => detail.label().text())).toEqual([
            "First Name",
            "Last Name",
            "Email Address",
            "Phone Number",
            "Date of Birth",
            "SSN (last 4)"
        ]);
    });

    it("asks for the details it is given", () => {

        const viewModel =
            new AccountCreation([{ label: "Audit", placeholder: "Shield" }]);

        expect(viewModel.details.map(detail => detail.label().text()))
            .toEqual(["Audit"]);
    });

    it("asks for nothing when there are no details", () => {

        expect(new AccountCreation([]).details).toEqual([]);
    });

    it("starts with every detail empty", () => {

        const viewModel = new AccountCreation();

        expect(viewModel.details.map(detail => detail.value))
            .toEqual(["", "", "", "", "", ""]);
    });

    it("collects each detail separately", () => {

        const viewModel = new AccountCreation();

        viewModel.details[0].input().value("Eleanor");

        expect(viewModel.details.map(detail => detail.value))
            .toEqual(["Eleanor", "", "", "", "", ""]);
    });
});
