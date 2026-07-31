import { describe, it, expect } from "vitest";

import PersonalDetail from "../../src/accounts/PersonalDetail.js";

const DETAIL = { label: "First Name", placeholder: "Eleanor" };

describe("personal detail", () => {

    it("labels the input with the name of the detail", () => {

        const viewModel = new PersonalDetail(DETAIL);

        expect(viewModel.label().text()).toBe("First Name");
    });

    it("shows the example from the design in the empty input", () => {

        const viewModel = new PersonalDetail(DETAIL);

        const element = {};

        viewModel.input().init(element);

        expect(element.placeholder).toBe("Eleanor");
    });

    it("is empty to begin with", () => {

        const viewModel = new PersonalDetail(DETAIL);

        expect(viewModel.input().value()).toBe("");
    });

    it("keeps what the customer types", () => {

        const viewModel = new PersonalDetail(DETAIL);

        viewModel.input().value("Eleanor");

        expect(viewModel.value).toBe("Eleanor");
    });

    it("shows what the customer types", () => {

        const viewModel = new PersonalDetail(DETAIL);

        viewModel.input().value("Eleanor");

        expect(viewModel.input().value()).toBe("Eleanor");
    });

    it("empties itself when the customer clears the input", () => {

        const viewModel = new PersonalDetail(DETAIL);

        viewModel.input().value("Eleanor");
        viewModel.input().value("");

        expect(viewModel.value).toBe("");
    });

    it("shows the detail it is given rather than what it last showed", () => {

        const viewModel = new PersonalDetail(DETAIL);

        viewModel.value = "Whitmore";

        expect(viewModel.input().value()).toBe("Whitmore");
    });
});
