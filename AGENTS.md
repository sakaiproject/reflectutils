# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/org/azeckoski/reflectutils/` hosts the runtime code; key subpackages include `converters`, `transcoders` (Jackson-backed JSON/XML support), `annotations`, and `interfaces`.
- Caching and core helpers live alongside the public APIs; caches prefer `WeakHashMap`/`ConcurrentHashMap` implementations—new utilities should follow this pattern.
- Tests under `src/test/java` mirror the production layout; reuse fixtures in `classes` rather than inventing bespoke beans.
- Build outputs land in `target/`; always run `mvn clean` before committing to avoid stray artifacts.

## Build, Test, and Development Commands
- `mvn clean verify` compiles against JDK 21, runs the JUnit 5 suite, and produces coverage via JaCoCo.
- `mvn -DskipTests package` is handy for packaging when tests are covered elsewhere.
- `mvn -P release deploy` signs and stages artifacts to OSSRH; ensure credentials exist in `~/.m2/settings.xml`.

## Coding Style & Naming Conventions
- Use 4-space indentation with braces on the same line; prefer explicit types over `var` unless readability improves.
- Embrace modern Java 21 features (records, pattern matching) when they simplify code, but keep APIs source-compatibility friendly.
- New subpackages should have concise, descriptive names (e.g., `cache`, `transcoders.jackson`).
- Source files are UTF-8; group imports by domain and keep static imports separate.

## Testing Guidelines
- Tests are JUnit 5 (`@Test`), using `org.junit.jupiter.api.Assertions` statics.
- For JSON/XML scenarios, assert using decoded `Map<String,Object>` representations rather than string equality.
- Favor focused unit tests over giant integration fixtures; extend or reuse helpers in `classes` and `annotations`.
- Run `mvn clean verify` before opening a PR; capture any CI-specific flakes in the PR description.

## Commit & Pull Request Guidelines
- Write imperative, scoped commit messages (e.g., `Replace beanutils cache with WeakHashMap`).
- Reference GitHub issues in branch names or PR titles (e.g., `modernize/jdk21-cache`).
- Document behavior or API changes in the PR body; include sample payloads when serializer output changes.
- Keep `target/` and other build outputs out of commits; prefer smaller PRs that are easy to review.
