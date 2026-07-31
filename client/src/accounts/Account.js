const MASK = "••";

const VISIBLE_DIGITS = 4;

export default class Account {

    #name;

    #number;

    constructor(name, number) {

        if (!name) {

            throw new Error("An account must have a name.");
        }

        if (!number) {

            throw new Error("An account must have a number.");
        }

        this.#name = name;
        this.#number = number;
    }

    get name() {

        return this.#name;
    }

    // Account numbers are never shown in full outside the account itself.
    get maskedNumber() {

        return MASK + this.#number.slice(-VISIBLE_DIGITS);
    }

    get label() {

        return this.#name + " " + this.maskedNumber;
    }
}
