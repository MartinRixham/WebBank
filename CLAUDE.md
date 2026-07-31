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
npm run dev                          # vite dev server, no Java involved
npx vitest run                       # all tests
npx vitest run test/HelloWorld.test.js   # a single test file
npx vitest run -t "greets the world"     # a single test by name
```

`mvn -pl client test` runs the same vitest suite through Maven.

## Architecture

**Client → classpath → server.** `client/vite.config.js` builds into `client/target/dist`, and `client/pom.xml` declares that directory as a Maven resource packaged under `web/` in `client-1.0.0.jar`. The server depends on that jar and serves it with `staticResources("/", "web")` in `server/src/main/kotlin/com/webbank/Application.kt`. There is no proxy or CORS setup — client and server are the same origin in production. Adding a page means adding it to the Vite build; it lands on the classpath automatically.

**Datum.js view models.** The client uses [Datum.js](https://datumjs.com) (`@datumjs/datum`, authored by this repo's owner), an opinionated data-binding library. The pattern, seen in `client/src/hello/`:

- A view model class holds private data fields and *binding* properties (`Text`, `Value`, `Click`, `Events`, `Visible`, `Classes`, `Init`, `Update`, `Destroy`, plus the generic `Binding`).
- Each binding property name corresponds to a `data-bind="propertyName"` attribute in the template HTML.
- Bindings are constructed with callbacks (`new Text(() => this.#message)`) and are re-evaluated when the data they read changes — dependency tracking is automatic, so read state through the callback rather than pushing updates.
- Templates live next to their view model as `.html` files and are pulled in inside `onBind(element)` via `await import("./x.html?raw")`, assigned to `element.innerHTML`.
- `new BindingRoot(viewModel)` in `client/src/main.js` binds the single root view model; it is declared once.

Tests exercise view models directly with no DOM — construct the class and invoke the binding as a function (`viewModel.greeting().text()`). Keep DOM concerns in the template and `onBind` so view models stay testable this way.

**Versions** are centralised in the root `pom.xml` properties block (Kotlin, Ktor, Logback, Node); modules declare dependencies without versions via `dependencyManagement`.

## Testing

Testing is not optional here: **every change ships with tests**, and the suite must be green before a change is considered done.

- **Write tests for all new code.** A new view model, binding, route, or helper gets its own test file in the same change. Do not defer tests to "later" or leave a module untested because it looks trivial.
- **Change behaviour, change the tests.** When modifying existing code, update or add tests so the new behaviour is pinned down. A bug fix starts with a test that fails for the old code.
- **Cover behaviour, not just the happy path.** For each unit, test the expected result, the boundaries (empty, zero, missing, maximum), and the error/invalid cases. One assertion per behaviour, with a test name that states the behaviour (`it("rejects a transfer larger than the balance")`).
- **Client tests are vitest, DOM-free.** Follow `client/test/HelloWorld.test.js`: construct the view model, invoke the binding as a function, assert on its result (`viewModel.greeting().text()`). Test files mirror the source path under `client/test/` and are named `<Class>.test.js`. If a view model is hard to test this way, the DOM concern belongs in the template or `onBind`, not in the view model.
- **Server tests are Kotlin under `server/src/test/kotlin`**, run by Maven Surefire. Use Ktor's `testApplication` for route-level tests rather than starting a real Netty server.
- **Run the tests before reporting completion**: `npx vitest run` from `client/` while iterating, and `mvn package` at the root for the full reactor. Report failures with their output rather than working around them.
- **Never weaken a test to make it pass** — no deleting assertions, loosening expectations, or skipping/commenting out tests to get a green run. If a test is genuinely wrong, fix it deliberately and say so.

## Conventions

- Kotlin sources live under `server/src/main/kotlin` (configured as `sourceDirectory`), targeting JVM 21.
- Comments in this codebase are sparse and explain *why* a non-obvious arrangement exists (e.g. the `web/` classpath packaging, the top-level jar output). Match that density.
- `design/` holds saved HTML mock-ups (Figma exports) used as visual reference; it is not part of the build.
