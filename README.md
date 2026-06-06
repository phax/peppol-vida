# peppol-vida

<!-- ph-badge-start -->
[![Sonatype Central](https://maven-badges.sml.io/sonatype-central/com.helger.peppol/peppol-vida-parent-pom/badge.svg)](https://maven-badges.sml.io/sonatype-central/com.helger.peppol/peppol-vida-parent-pom/)
[![javadoc](https://javadoc.io/badge2/com.helger.peppol/peppol-vida-testfiles/javadoc.svg)](https://javadoc.io/doc/com.helger.peppol/peppol-vida-testfiles)

> If this project saved you some time or made your day a little easier, a star would mean a lot — it helps others find it too.
<!-- ph-badge-end -->

Special support for Peppol ViDA (pilot right now).

**This project does NOT (yet) contain an official data model of the European DRR as required by ViDA!**

peppol-vida is part of my Peppol solution stack. See https://github.com/phax/peppol for other components and libraries in that area.

This contains a set of Java libraries.
They are licensed under the Apache 2.0 license.
The minimum requirement is Java 17.

The backing specifications are:
* ViDA pilot: https://test-docs.peppol.eu/vida/Vida-tdd/

# Submodules

This project consists of the following submodules (in alphabetic order)

* `peppol-vida-tdd` - contains the main logic to create Peppol ViDA pilot TDD documents based on the Peppol ViDA pilot documents as well as documentation
    * Main class to build a complete TDD from scratch is `PeppolViDATDD10Builder`
    * To run the Schematron validation, use class `PeppolViDATDDValidator`
* `peppol-vida-tdd-datatypes` - contains the JAXB generated Peppol ViDA pilot TDD data model
    * Main class to read and write TDD XML is `PeppolViDATDD10Marshaller`
* `peppol-vida-testfiles` - contains Peppol ViDA pilot specific test files as a reusable component
    * Main class is `PeppolViDATestFiles`

# Maven usage

Add the following to your `pom.xml` to use this artifact, replacing `x.y.z` with the real version number.

```xml
<dependency>
  <groupId>com.helger.peppol</groupId>
  <artifactId>peppol-vida-tdd</artifactId>
  <version>x.y.z</version>
</dependency>
```

# Usage example

```java
// Build a TDD document from scratch
TaxDataType tdd = new PeppolViDATDD090Builder ()
    .documentTypeCode (EViDATDDDocumentTypeCode.SUBMIT)
    .documentScope (EViDATDDDocumentScope.DOMESTIC)
    .reporterRole (EViDATDDReporterRole.SENDER)
    .reportingParty (participantID)
    .receivingParty (receiverID)
    .taxAuthorityID ("XX")
    .reportedTransaction (rt -> rt.initFromInvoice (invoice))
    .build ();

// Serialize to XML
String xml = new PeppolViDATDD090Marshaller ().setFormattedOutput (true).getAsString (tdd);

// Validate with Schematron
ISchematronResource schematron = PeppolViDATDDValidator.getSchematronViDA_TDD_090 ();
```

# Building

This project requires Apache Maven 3.x and Java 17 for building.
Simply run
```
mvn clean install
```
to build the solution.

# News and noteworthy

v0.2.2 - work in progress
* Requires at least phive 12.0.2
* Updated to TDD v1.0.0 specification (XSD and Schematron from 2026-06-02)

v0.2.1 - 2026-03-31
* Fixed `ReportedDocumentID` UUID5 generation to use Seller ID (BT-29) instead of Seller Endpoint ID (BT-34) as input ([#1](https://github.com/phax/peppol-vida/issues/1))
* Added `sellerID` and `sellerIDSchemeID` to `PeppolViDATDD100ReportedTransactionBuilder`
* Seller ID (BT-29) is now a required field in `PeppolViDATDD100ReportedTransactionBuilder`

v0.2.0 - 2026-03-18
* Requires at least ph-commons 12.1.5
* Requires at least peppol-commons 12.3.12
* Updated to TDD v1.0.0 specification (XSD and Schematron from 2026-03-18)
* Renamed all v090 classes to v100 (e.g. `PeppolViDATDD090Builder` -> `PeppolViDATDD100Builder`)
* Renamed `EViDATDDDocumentTypeCode` to `EViDATDDTaxDataTypeCode`
* Added `long` overloads for all `BigDecimal` setter methods in v100 builders
* Added `PeppolViDATDD100TaxSubtotalBuilder` with `TaxCategoryType`-based API (replacing individual field setters)
* Added `priceBaseQuantity` to `PeppolViDATDD100DocumentLineBuilder`
* Added TDD v1.0.0 test files
* Added pre-compiled XSLT Schematrons for CEN-EN16931-UBL, PEPPOL-EN16931-UBL, and Peppol-ViDA-TDD
* Removed `UUID5Helper` class (moved to ph-commons `UUID5Helper`)
* Fixed `peppol-vida-tdd` package name from `com.helger.peppol.vida.tddv090` to `com.helger.peppol.vida.tdd.v100`

v0.1.3 - 2026-02-10
* Updated to new Schematron from OpenPeppol

v0.1.2 - 2026-02-09
* Updated to new XSD from OpenPeppol

v0.1.1 - 2026-01-30
* Fixed comparison errors in `PeppolViDATDD090TaxSubtotalBuilder._isEveryRequiredFieldSet`
* Removed `PeppolViDATDD090ReportedTransactionBuilder.uuid` and building a UUID v5 manually instead

v0.1.0 - 2026-01-25
* Initial version targeting Peppol ViDA pilot TDD specs v1.0.0

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
