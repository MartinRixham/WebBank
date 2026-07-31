import { describe, it, expect } from "vitest";

import NavigationMenu from "../../src/navigation/NavigationMenu.js";
import sections from "../../src/navigation/sections.js";

const SELECTED = "navigation-item--selected";

function isHighlighted(item) {

    return item.button().classes[SELECTED]();
}

describe("navigation menu", () => {

    it("has an item for every domain area", () => {

        const viewModel = new NavigationMenu();

        expect(viewModel.items.map(item => item.id))
            .toEqual(sections.map(section => section.id));
    });

    it("selects the first domain area to begin with", () => {

        const viewModel = new NavigationMenu();

        expect(viewModel.items.map(isHighlighted))
            .toEqual([true, false, false, false, false, false]);
    });

    it("highlights only the item that was clicked", () => {

        const viewModel = new NavigationMenu();

        viewModel.items[3].button().click();

        expect(viewModel.items.map(isHighlighted))
            .toEqual([false, false, false, true, false, false]);
    });

    it("moves the highlight on to the next item that is clicked", () => {

        const viewModel = new NavigationMenu();

        viewModel.items[3].button().click();
        viewModel.items[5].button().click();

        expect(viewModel.items.map(isHighlighted))
            .toEqual([false, false, false, false, false, true]);
    });

    it("takes the domain areas it is given", () => {

        const viewModel =
            new NavigationMenu([{ id: "audit", label: "Audit", icon: "shield" }]);

        expect(viewModel.items.map(item => item.id)).toEqual(["audit"]);
    });

    it("selects nothing when there are no domain areas", () => {

        const viewModel = new NavigationMenu([]);

        expect(viewModel.items).toEqual([]);
        expect(viewModel.selection.selected).toBe(null);
    });
});
