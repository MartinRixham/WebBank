import { Binding, Classes, Text } from "@datumjs/datum";

import "./navigation-item.css";

export default class NavigationItem {

    #section;

    // Held privately so that the selection shared with the sibling items is
    // owned by, and tracked through, the menu alone.
    #selection;

    constructor(section, selection) {

        this.#section = section;
        this.#selection = selection;

        // Naming the icon rather than drawing it leaves the svg file itself
        // referenced from the stylesheet.
        this.icon = new Classes({

            ["navigation-item__icon--" + section.icon]: () => true
        });

        this.label = new Text(() => section.label);

        this.button = new Binding({

            classes: {

                "navigation-item--selected": () => this.isSelected()
            },
            click: () => this.select()
        });
    }

    async onBind(element) {

        const html = await import("./navigation-item.html?raw");

        element.innerHTML = html.default;
    }

    get id() {

        return this.#section.id;
    }

    isSelected() {

        return this.#selection.isSelected(this.#section.id);
    }

    select() {

        this.#selection.select(this.#section.id);
    }
}
