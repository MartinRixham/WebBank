import { describe, it, expect, afterEach } from "vitest";

import createAccount from "../../src/accounts/createAccount.js";

const ACCOUNT = { firstName: "Eleanor", lastName: "Whitmore" };

const originalFetch = globalThis.fetch;

function respondWith(response) {

    const requests = [];

    globalThis.fetch = (url, options) => {

        requests.push({ url, options });

        return Promise.resolve(response);
    };

    return requests;
}

describe("create account", () => {

    afterEach(() => {

        globalThis.fetch = originalFetch;
    });

    it("posts the account to the account endpoint", async () => {

        const requests = respondWith({ ok: true });

        await createAccount(ACCOUNT);

        expect(requests.length).toBe(1);
        expect(requests[0].url).toBe("/account");
        expect(requests[0].options.method).toBe("POST");
    });

    it("posts the account as json", async () => {

        const requests = respondWith({ ok: true });

        await createAccount(ACCOUNT);

        expect(requests[0].options.headers)
            .toEqual({ "Content-Type": "application/json" });

        expect(JSON.parse(requests[0].options.body)).toEqual(ACCOUNT);
    });

    it("reports that the account was created", async () => {

        respondWith({ ok: true });

        expect(await createAccount(ACCOUNT)).toBe(true);
    });

    it("reports that the account was rejected", async () => {

        respondWith({ ok: false });

        expect(await createAccount(ACCOUNT)).toBe(false);
    });
});
