import { Text } from "@datumjs/datum";

export default class HelloWorld {

    #message = "Hello, World!";

    async onBind(element) {

        const html = await import("./hello-world.html?raw")

        element.innerHTML = html.default;
    }

    greeting = new Text(() => this.#message);
}
