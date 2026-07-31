import { describe, it, expect } from "vitest";

import AccountCreation from "../src/accounts/AccountCreation.js";
import App from "../src/App.js";
import TopNavigation from "../src/navigation/TopNavigation.js";

describe("app", () => {

    it("shows the top navigation", () => {

        expect(new App().topNavigation).toBeInstanceOf(TopNavigation);
    });

    it("shows the account creation form", () => {

        expect(new App().accountCreation).toBeInstanceOf(AccountCreation);
    });
});
