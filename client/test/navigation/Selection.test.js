import { describe, it, expect } from "vitest";

import Selection from "../../src/navigation/Selection.js";

describe("selection", () => {

    it("starts on the id it was given", () => {

        expect(new Selection("summary").isSelected("summary")).toBe(true);
    });

    it("does not select any other id", () => {

        expect(new Selection("summary").isSelected("audit")).toBe(false);
    });

    it("selects nothing by default", () => {

        expect(new Selection().isSelected("summary")).toBe(false);
    });

    it("moves to a newly selected id", () => {

        const selection = new Selection("summary");

        selection.select("audit");

        expect(selection.isSelected("audit")).toBe(true);
    });

    it("leaves the previously selected id", () => {

        const selection = new Selection("summary");

        selection.select("audit");

        expect(selection.isSelected("summary")).toBe(false);
    });
});
