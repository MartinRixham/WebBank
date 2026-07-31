export default class Selection {

    // A public data property rather than a private field so that Datum tracks
    // it and repaints every navigation item when the selection moves.
    selected;

    constructor(selected = null) {

        this.selected = selected;
    }

    isSelected(id) {

        return this.selected === id;
    }

    select(id) {

        this.selected = id;
    }
}
