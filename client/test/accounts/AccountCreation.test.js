import { describe, it, expect } from "vitest";

import AccountCreation from "../../src/accounts/AccountCreation.js";

const DETAILS = [

    { name: "firstName", label: "First Name", placeholder: "Eleanor" },
    { name: "lastName", label: "Last Name", placeholder: "Whitmore" }
];

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

    it("makes an account of the details the customer has given", () => {

        const viewModel = new AccountCreation(DETAILS);

        viewModel.details[0].input().value("Eleanor");
        viewModel.details[1].input().value("Whitmore");

        expect(viewModel.account())
            .toEqual({ firstName: "Eleanor", lastName: "Whitmore" });
    });

    it("makes an account with the details left empty", () => {

        const viewModel = new AccountCreation(DETAILS);

        expect(viewModel.account()).toEqual({ firstName: "", lastName: "" });
    });

    it("makes an empty account when there are no details", () => {

        expect(new AccountCreation([]).account()).toEqual({});
    });

    it("creates the account when the customer clicks create", () => {

        const created = [];

        const viewModel =
            new AccountCreation(DETAILS, account => created.push(account));

        viewModel.details[0].input().value("Eleanor");
        viewModel.create().click();

        expect(created).toEqual([{ firstName: "Eleanor", lastName: "" }]);
    });

    it("creates no account until the customer clicks create", () => {

        const created = [];

        const viewModel =
            new AccountCreation(DETAILS, account => created.push(account));

        viewModel.details[0].input().value("Eleanor");

        expect(created).toEqual([]);
    });
});
