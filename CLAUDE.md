# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Java library implementing the Peppol ViDA (VAT in the Digital Age) Tax Data Document (TDD) specification. Provides builders, marshallers, and validators for creating TDD XML documents for the ViDA pilot program.

Minimum Java version: **17**

## Build Commands

```bash
# Build everything (runs tests)
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run tests for a single module
mvn test -pl peppol-vida-tdd

# Run a single test class
mvn test -pl peppol-vida-tdd -Dtest=PeppolViDATDD090BuilderFuncTest

# Run a single test method
mvn test -pl peppol-vida-tdd -Dtest=PeppolViDATDD090BuilderFuncTest#testBasicMinimal
```

## Module Structure

Three Maven modules with strict build order (each depends on the previous):

1. **`peppol-vida-testfiles`** — Bundles test XML files as classpath resources. `PeppolViDATestFiles` provides programmatic access to valid UBL invoices, credit notes, and TDD documents.

2. **`peppol-vida-tdd-datatypes`** — JAXB-generated data model from `external/schemas/2026-02-08/ViDA-tdd-0.9.0.xsd`. Contains `CPeppolViDATDD` (schema constants/resources) and `PeppolViDATDD090Marshaller` (XML serialization).

3. **`peppol-vida-tdd`** — Main business logic. Key entry points:
   - `PeppolViDATDD090Builder` — builds TDD documents from scratch using fluent API
   - `PeppolViDATDDValidator` — Schematron validation using `external/schematron/2026-02-08/Peppol-ViDA-TDD-ph.sch`
   - `PeppolViDATDD090Marshaller` — read/write TDD XML

## Architecture

**Builder pattern** is used throughout with fluent APIs:
```java
new PeppolViDATDD090Builder()
    .documentTypeCode(EViDATDDDocumentTypeCode.SUBMIT)
    .documentScope(EViDATDDDocumentScope.DOMESTIC)
    .reporterRole(EViDATDDReporterRole.SENDER)
    .reportedTransaction(rt -> rt.customizationID(...).profileID(...))
    .build();
```

`build()` returns `null` (not an exception) if required fields are missing.

**UUID v5 generation:** Transaction and document UUIDs are derived from the Peppol ViDA namespace UUID defined in `CViDATDD`.

## Code Conventions

This codebase follows the [Helger framework](https://github.com/phax) conventions:

- **Annotations:** `@NonNull`/`@Nullable` (JSpecify), `@Immutable` for thread-safe classes
- **Preconditions:** `ValueEnforcer.notNull()` — not standard Java assertions
- **Validation:** `_isEveryRequiredFieldSet()` (private check), `isEveryRequiredFieldSet()` (public)
- **Enums** implement `IHasID<String>` for serialization: `EViDATDDDocumentTypeCode`, `EViDATDDDocumentScope`, `EViDATDDReporterRole`
- **Resources:** `ClassPathResource` for schema/schematron files, accessed via static `_getCL()` method
- **Logging:** `ConditionalLogger` for optional debug/error messages

## JAXB Code Generation

The classes in `peppol-vida-tdd-datatypes/src/main/java/.../jaxb/` are **auto-generated** from the XSD schema using the JAXB Maven plugin. Do not edit them manually — edit the XSD instead and regenerate with `mvn generate-sources`.

## Test Files

Test XML resources live in `peppol-vida-testfiles/src/main/resources/external/`:
- `invoice/good/` — Valid UBL 2.1 invoices
- `creditnote/good/` — Valid UBL 2.1 credit notes
- `tdd/0.9.0/good/` — Valid TDD v0.9.0 documents

`PeppolViDATestFiles` exposes these as `IReadableResource` collections.
