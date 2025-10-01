# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/org/azeckoski/reflectutils/` contains the core reflection utilities; subpackages such as `converters`, `transcoders`, `beanutils`, and `map` group related helpers—add new utilities within these logical boundaries.
- `src/test/java/...` mirrors the production tree with fixture beans under `classes` and annotation stubs under `annotations`; reuse these helpers instead of creating ad-hoc test data.
- `src/main/java/overview.html` and the package-level `package.html` files drive the published Javadoc overview—update them whenever you expose new public APIs.
- `target/` holds compiled artifacts and reports; run `mvn clean` before committing so the directory stays out of version control.

## Build, Test, and Development Commands
- `mvn clean install` compiles against Java 1.5 bytecode, executes unit tests, and produces the jar in `target/reflectutils-*.jar`.
- `mvn test` is the fastest feedback loop and respects the Surefire include pattern `**/*Test.java`.
- `mvn site` or `mvn cobertura:cobertura` regenerate quality reports (PMD, Cobertura, JDepend, JXR) into `target/site/`.
- `mvn -Prelease deploy` signs and stages artifacts to OSSRH; verify `settings.xml` credentials before invoking it.

## Coding Style & Naming Conventions
- Use 4-space indentation with braces on the same line as declarations, matching existing classes like `ReflectUtils`.
- Preserve Java 1.5 compatibility: avoid streams, lambdas, and newer collection APIs; prefer explicit generics and iterator loops.
- Public APIs live under `org.azeckoski.reflectutils`; name new subpackages with a single descriptive noun (e.g., `metrics`, `cache`).
- Source files are encoded in UTF-8; keep imports explicit and group static imports below regular imports.

## Testing Guidelines
- Tests extend JUnit 3 `TestCase`; name files `SomethingTest.java` so Maven Surefire picks them up.
- Place reusable fixtures under `src/test/java/org/azeckoski/reflectutils/classes` and shared annotations under `.../annotations`.
- Aim to cover new converter paths and transcoder branches; review `target/site/cobertura/index.html` after coverage runs.
- When reproducing bugs, add a new `*Test` that fails before your fix and passes afterward.

## Commit & Pull Request Guidelines
- Keep commit messages short and imperative, mirroring existing history (`Fixed release plugin values`); include tags like `[maven-release-plugin]` only when running automated tools.
- Reference issues in branch names or PR titles (`issue-2` style) and describe both the problem and solution in the PR body.
- Confirm `mvn clean install` passes locally, note any skipped tests, and avoid committing artifacts from `target/`.
- For functional changes, call out API or serialization impacts and attach logs or sample payloads when they help reviewers.
