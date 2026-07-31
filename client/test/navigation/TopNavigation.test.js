import { describe, it, expect } from "vitest";

import AccountSelector from "../../src/accounts/AccountSelector.js";
import NavigationMenu from "../../src/navigation/NavigationMenu.js";
import TopNavigation from "../../src/navigation/TopNavigation.js";

describe("top navigation", () => {

    it("shows the menu of domain areas", () => {

        expect(new TopNavigation().menu).toBeInstanceOf(NavigationMenu);
    });

    it("shows the account selector", () => {

        expect(new TopNavigation().accountSelector).toBeInstanceOf(AccountSelector);
    });
});
