# reflectutils

Modern reflection utilities for Java 21+, providing convenient field/property access, deep cloning, conversion helpers, and lightweight JSON/XML transcoders powered by Jackson. The library is dependency-light, thread-safe, and designed to integrate with contemporary JVM applications.

## Highlights
- Unified field access: read/write simple, nested, indexed, or mapped properties using `FieldUtils` and `ReflectUtils`.
- Pluggable caching: metadata caches default to `WeakHashMap` to avoid classloader leaks while keeping lookups fast.
- Type conversion: `ConversionUtils` covers primitives, collections, maps, enums, arrays, and common I/O types.
- Jackson-based transcoders: encode/decode arbitrary objects to JSON or XML via Jackson `ObjectMapper`/`XmlMapper`.
- Extensible annotations: helpers detect custom annotations (e.g., `@ReflectTransient`) when building metadata.

## Requirements
- JDK 21 or newer
- Maven 3.9+

## Build & Test
```bash
mvn clean verify
```
This runs the JUnit 5 suite and generates a JaCoCo coverage report in `target/site/jacoco`.

For a quicker iteration cycle:
```bash
mvn -DskipTests package
```

## Quick Start
```java
ReflectUtils reflect = ReflectUtils.getInstance();
TestBean bean = new TestBean();
reflect.setFieldValue(bean, "address.city", "Lisbon");
String city = (String) reflect.getFieldValue(bean, "address.city");

JSONTranscoder transcoder = new JSONTranscoder(true, true, true);
Map<String, Object> meta = Map.of("source", "demo");
String json = transcoder.encode(bean, "bean", meta);
Map<String, Object> decoded = transcoder.decode(json);
```

## Contributing
- Follow the guidelines in `AGENTS.md` for style, testing, and PR expectations.
- Submit issues or PRs via GitHub; keep changes focused and well-tested (`mvn clean verify`).

## License
Apache License 2.0. See `LICENSE` for details.
