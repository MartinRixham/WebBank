const MASK = "••";

const VISIBLE_DIGITS = 4;

const CURRENCY = new Intl.NumberFormat("en-US", {

    style: "currency",
    currency: "USD"
});

export default class Account {

    #name;

    #number;

    #balance;

    constructor(name, number, balance = 0) {

        if (!name) {

            throw new Error("An account must have a name.");
        }

        if (!number) {

            throw new Error("An account must have a number.");
        }

        this.#name = name;
        this.#number = number;
        this.#balance = balance;
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

    get balance() {

        return this.#balance;
    }

    get formattedBalance() {

        return CURRENCY.format(this.#balance);
    }
}
