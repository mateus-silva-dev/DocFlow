# Changelog

All notable changes to this project will be documented in this file.

The format is based on Keep a Changelog and this project follows Semantic Versioning.

## [3.0.0] - 2026-07-23

### Added
- Full Spring Boot 4 support.
- Convention-based OpenAPI documentation.
- Zero-annotation endpoint documentation.
- Automatic controller tag generation.
- Automatic endpoint summary generation.
- Automatic endpoint description generation.
- Automatic response schema inference.
- Smart HTTP status code inference.
- `@ApiDoc` annotation for documentation customization.
- Support for excluding automatically generated HTTP status codes using `exclude`.
- Sample projects for Spring Boot 4 (Maven and Gradle).

### Changed
- Replaced multiple HTTP method annotations (`@ApiDocGet`, `@ApiDocPost`, `@ApiDocPut`, `@ApiDocPatch`, `@ApiDocDelete`) with a single optional `@ApiDoc` annotation.
- Documentation generation is now convention-based by default.
- Improved OpenAPI generation performance.
- Updated README with a compatibility matrix, Quick Start guide, and customization examples.

### Removed
- Requirement to annotate endpoints for automatic documentation generation.
