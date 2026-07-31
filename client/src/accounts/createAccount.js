// The server serves the client itself, so the endpoint is reached with a
// relative path rather than an origin the client would have to be told.
export default async function createAccount(account) {

    const response = await fetch("/account", {

        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(account)
    });

    return response.ok;
}
