# Changelog

All notable changes to this project will be documented in this file.

The format is based on Keep a Changelog and this project follows Semantic Versioning.

## [3.0.0] - Unreleased

### Added
- Full Spring Boot 4 support.
- Automatic OpenAPI documentation based on conventions.
- Zero-annotation endpoint documentation.
- Automatic controller tag generation.
- Automatic endpoint summary generation.
- Automatic endpoint description generation.
- Automatic response schema inference.
- Smart HTTP status code inference.
- `@ApiDoc` annotation for documentation customization.
- Support for excluding generated HTTP status codes using `exclude`.
- Sample projects for Spring Boot 4 (Maven and Gradle).

### Changed
- Replaced multiple HTTP method annotations (`@ApiDocGet`, `@ApiDocPost`, `@ApiDocPut`, `@ApiDocPatch`, `@ApiDocDelete`) with a single optional `@ApiDoc` annotation.
- Documentation generation is now convention-based by default.
- Simplified library configuration and customization workflow.
- Improved OpenAPI generation performance.
- Updated README with compatibility matrix, Quick Start, and customization guide.

### Removed
- Requirement to annotate endpoints for automatic documentation generation.
