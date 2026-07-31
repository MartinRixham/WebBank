import NavigationItem from "./NavigationItem.js";
import Selection from "./Selection.js";
import navigationSections from "./sections.js";

import "./navigation-menu.css";

export default class NavigationMenu {

    constructor(sections = navigationSections) {

        this.selection = new Selection(sections.length ? sections[0].id : null);

        this.items =
            sections.map(section => new NavigationItem(section, this.selection));
    }

    async onBind(element) {

        const html = await import("./navigation-menu.html?raw");

        element.innerHTML = html.default;
    }
}
