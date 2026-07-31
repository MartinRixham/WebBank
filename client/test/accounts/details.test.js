import { describe, it, expect } from "vitest";

import details from "../../src/accounts/details.js";

describe("personal details", () => {

    it("lists the details an account needs in the order of the design", () => {

        expect(details.map(detail => detail.label)).toEqual([
            "First Name",
            "Last Name",
            "Email Address",
            "Phone Number",
            "Date of Birth",
            "SSN (last 4)"
        ]);
    });

    it("shows the example of each detail that the design shows", () => {

        expect(details.map(detail => detail.placeholder)).toEqual([
            "Eleanor",
            "Whitmore",
            "e.whitmore@email.com",
            "+1 (555) 000-0000",
            "MM / DD / YYYY",
            "••••"
        ]);
    });
});
