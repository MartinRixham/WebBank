# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

WebBank is an online banking demonstration: a Vite-built vanilla JS front end (Datum.js data binding) packaged into a Ktor/Netty Kotlin server as a single executable jar.

## Build and run

Everything is driven from the Maven reactor at the repo root (`webbank` → modules `client`, `server`). The `client` module owns the npm build via `frontend-maven-plugin`, which downloads its own Node into `client/node/` — a system Node install is not required for the Maven build.

```bash
mvn package                     # full build: npm install, vite build, vitest, kotlin compile, shaded jar
mvn package -DskipTests         # skips the vitest execution in the client module
java -jar target/webbank-1.0.0.jar   # serves the app on http://localhost:8080
```

Note that the shade plugin writes the executable jar to the **top-level** `target/`, not `server/target/`.

### Front-end iteration

From `client/`:

```bash
npm run dev                                    # vite dev server, no Java involved
npx vitest run                                 # all tests
npx vitest run test/navigation/Selection.test.js   # a single test file
npx vitest run -t "labels the button"          # a single test by name
```

`mvn -pl client test` runs the same vitest suite through Maven.

## Architecture

**Client → classpath → server.** `client/vite.config.js` builds into `client/target/dist`, and `client/pom.xml` declares that directory as a Maven resource packaged under `web/` in `client-1.0.0.jar`. The server depends on that jar and serves it with `staticResources("/", "web")` in `server/src/main/kotlin/com/webbank/Application.kt`. There is no proxy or CORS setup — client and server are the same origin in production. The app is a single page: `client/index.html` loads `src/main.js`, which does `new BindingRoot(new App())` once; navigation swaps content within that page rather than loading new documents.

### Datum.js view models

The client uses [Datum.js](https://datumjs.com) (`@datumjs/datum`, authored by this repo's owner), an opinionated data-binding library.

- A view model class holds data fields and *binding* properties (`Text`, `Value`, `Click`, `Events`, `Visible`, `Classes`, `Init`, `Update`, `Destroy`, plus the generic `Binding`).
- Each binding property name corresponds to a `data-bind="propertyName"` attribute in the template HTML.
- Bindings are constructed with callbacks (`new Text(() => section.label)`) and are re-evaluated when the data they read changes — dependency tracking is automatic, so read state through the callback rather than pushing updates.
- **Only public properties are tracked.** State that must trigger a repaint has to live in a public field, which is why `Selection.selected` is public while the view models that read it hold the `Selection` itself in a `#private` field. A `#private` field mutated after binding will not repaint.
- **Composition is by property.** A parent view model holds a child instance as a property (`menu = new NavigationMenu()`) and the parent's template binds it (`<nav data-bind="menu">`). `App` → `TopNavigation` → `NavigationMenu`/`AccountSelector` → `NavigationItem` is the whole tree today.
- **Arrays repeat their template's child.** `data-bind="items"` on a container whose single child element is the per-item template; each `NavigationItem` binds into a copy of that child (`client/src/navigation/navigation-menu.html`).
- Templates live next to their view model as `.html` files and are pulled in inside `onBind(element)` via `await import("./x.html?raw")`, assigned to `element.innerHTML`.

### Client layout and styling

Source is grouped by feature under `client/src/` (`navigation/`, `accounts/`, `theme/`, `icons/`), with the view model, its `.html` template and its `.css` co-located and the CSS imported from the view model module. Class names are BEM (`navigation-item__icon--shield`).

- `client/src/theme/theme.css` holds the design tokens and is imported once from `main.js`. It carries only the subset of tokens in use; copy further ones across from `design/homepage.html` as they are needed.
- Icons are SVG files in `client/src/icons/`, drawn by masking an element (`.icon` + a component-specific `mask-image`) so they take the surrounding colour. A view model *names* an icon by toggling a modifier class (`new Classes({ ["navigation-item__icon--" + section.icon]: () => true })`); the `url(...)` reference stays in the stylesheet.
- `design/` holds saved HTML mock-ups (Figma exports) used as visual reference; it is not part of the build.

**Versions** are centralised in the root `pom.xml` properties block (Kotlin, Ktor, Logback, Node); modules declare dependencies without versions via `dependencyManagement`.

## Testing

Testing is not optional here: **every change ships with tests**, and the suite must be green before a change is considered done.

- **Write tests for all new code.** A new view model, binding, route, or helper gets its own test file in the same change. Do not defer tests to "later" or leave a module untested because it looks trivial.
- **Change behaviour, change the tests.** When modifying existing code, update or add tests so the new behaviour is pinned down. A bug fix starts with a test that fails for the old code.
- **Cover behaviour, not just the happy path.** For each unit, test the expected result, the boundaries (empty, zero, missing, maximum), and the error/invalid cases. One assertion per behaviour, with a test name that states the behaviour (`it("rejects a transfer larger than the balance")`).
- **Client tests are vitest, DOM-free.** Test files mirror the source path under `client/test/` and are named `<Class>.test.js`. Construct the view model and invoke the binding as a function: `viewModel.label().text()`, `viewModel.button().classes["navigation-item--selected"]()`, `viewModel.button().click()`. See `client/test/navigation/NavigationItem.test.js`. If a view model is hard to test this way, the DOM concern belongs in the template or `onBind`, not in the view model.
- **Keep collaborators injectable.** View models take their data as a constructor parameter defaulting to the real thing (`new NavigationMenu(sections = navigationSections)`, `new AccountSelector(account = selectedAccount)`), so tests can pass a fixture or an empty list.
- **Server tests** go under `server/src/test/kotlin` (Surefire picks them up). None exist yet, so the first one also adds a test dependency to `server/pom.xml`. Use Ktor's `testApplication` for route-level tests rather than starting a real Netty server.
- **Run the tests before reporting completion**: `npx vitest run` from `client/` while iterating, and `mvn package` at the root for the full reactor. Report failures with their output rather than working around them.
- **Never weaken a test to make it pass** — no deleting assertions, loosening expectations, or skipping/commenting out tests to get a green run. If a test is genuinely wrong, fix it deliberately and say so.

## Conventions

- JavaScript: 4-space indent, double quotes, semicolons, and a blank line after every opening brace of a class body, method, function or `describe`/`it` callback. Match the surrounding files exactly — this style is applied consistently across `src/` and `test/`.
- Kotlin sources live under `server/src/main/kotlin` (configured as `sourceDirectory`), targeting JVM 21, and follow ordinary Kotlin style rather than the JavaScript spacing above.
- Comments are sparse and explain *why* a non-obvious arrangement exists (the `web/` classpath packaging, the top-level jar output, why `Selection.selected` is public). Match that density.
