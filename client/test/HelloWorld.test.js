import { describe, it, expect } from "vitest";

import HelloWorld from "../src/hello/HelloWorld.js";

describe("hello world view model", () => {

    it("greets the world", () => {

        const viewModel = new HelloWorld();

        const greeting = viewModel.greeting();

        expect(greeting.text()).toBe("Hello, World!");
    });
});
