import { describe, it, expect } from "vitest";

import NavigationItem from "../../src/navigation/NavigationItem.js";
import Selection from "../../src/navigation/Selection.js";

const SECTION = { id: "audit", label: "Audit", icon: "shield" };

const SELECTED = "navigation-item--selected";

describe("navigation item", () => {

    it("labels the button with the name of the domain area", () => {

        const viewModel = new NavigationItem(SECTION, new Selection());

        expect(viewModel.label().text()).toBe("Audit");
    });

    it("takes its id from its domain area", () => {

        const viewModel = new NavigationItem(SECTION, new Selection());

        expect(viewModel.id).toBe("audit");
    });

    it("draws the icon of its domain area", () => {

        const viewModel = new NavigationItem(SECTION, new Selection());

        expect(viewModel.icon().classes["navigation-item__icon--shield"]())
            .toBe(true);
    });

    it("draws no icon other than the one of its domain area", () => {

        const viewModel = new NavigationItem(SECTION, new Selection());

        expect(Object.keys(viewModel.icon().classes))
            .toEqual(["navigation-item__icon--shield"]);
    });

    it("is highlighted when its domain area is selected", () => {

        const viewModel = new NavigationItem(SECTION, new Selection("audit"));

        expect(viewModel.button().classes[SELECTED]()).toBe(true);
    });

    it("is not highlighted when another domain area is selected", () => {

        const viewModel = new NavigationItem(SECTION, new Selection("create"));

        expect(viewModel.button().classes[SELECTED]()).toBe(false);
    });

    it("selects its domain area when clicked", () => {

        const selection = new Selection("create");

        const viewModel = new NavigationItem(SECTION, selection);

        viewModel.button().click();

        expect(selection.isSelected("audit")).toBe(true);
    });

    it("is highlighted once it has been clicked", () => {

        const viewModel = new NavigationItem(SECTION, new Selection("create"));

        viewModel.button().click();

        expect(viewModel.button().classes[SELECTED]()).toBe(true);
    });

    it("stays selected when clicked again", () => {

        const viewModel = new NavigationItem(SECTION, new Selection("audit"));

        viewModel.button().click();

        expect(viewModel.button().classes[SELECTED]()).toBe(true);
    });
});
