import { describe, it, expect } from "vitest";

import App from "../src/App.js";
import TopNavigation from "../src/navigation/TopNavigation.js";

describe("app", () => {

    it("shows the top navigation", () => {

        expect(new App().topNavigation).toBeInstanceOf(TopNavigation);
    });
});
