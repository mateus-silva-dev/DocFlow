# Changelog

All notable changes to this project will be documented in this file.

## - 2026-06-01

### Added
- ApiDoc annotations for clean controller architecture.
- Automatic summary and description generation.
- Complete executable Maven sample project using Spring Boot 3.4.0+.
- Complete executable Gradle sample project using Spring Boot 3.4.0+ and Java 21 Toolchain.
- Native i18n support (English, Portuguese, Italian, French, and Simplified Chinese).
- Automatic HTTP 200/201/204 status detection.
- Smart Security inference with Spring Security (`@PreAuthorize`, `Secured`, `RolesAllowed`).
- Zero-Config JWT Authentication contract.

### Changed
- Refactored library build configuration to use `maven.compiler.release` target 17 for broader compatibility while building with JDK 21.
