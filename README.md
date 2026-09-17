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
* ViDA pilot TDD v1.1.0: https://test-docs.peppol.eu/vida/2026-v1.1.0/Vida-tdd/

# Related resources

* OpenPeppol ViDA Pilot Testing repository: https://github.com/OpenPEPPOL/vida-pilot-testing/
    * Contains the official test data packages for the Peppol ViDA Pilot testing phase
    * Per test scenario (`NW-HP-001`, `NW-HP-002`, `NW-HP-002-RC`, `NW-HP-006`, `NW-HP-008`) it provides source Peppol BIS Billing 3.0 invoices and credit notes for many combinations of tax jurisdictions, plus non-normative sample TDDs, validation reports and PDF/HTML visualizations
    * The file `MASTERDATA.md` contains the fictitious seller and buyer master data used in all test files
    * All official sample TDDs of that repository are successfully validated by `PeppolViDATDDValidator` of this project (as of 2026-09-15, TDD v1.1.0)

# Submodules

This project consists of the following submodules (in alphabetic order)

* `peppol-vida-tdd` - contains the main logic to create Peppol ViDA pilot TDD documents based on the Peppol ViDA pilot documents as well as documentation
    * Main class to build a complete TDD from scratch is `PeppolViDATDD110Builder`
    * To run the Schematron validation, use class `PeppolViDATDDValidator`
* `peppol-vida-tdd-datatypes` - contains the JAXB generated Peppol ViDA pilot TDD data model
    * Main class to read and write TDD XML is `PeppolViDATDD110Marshaller`
* `peppol-vida-testfiles` - contains Peppol ViDA pilot specific test files as a reusable component
    * Main class is `PeppolViDATestFiles`
    * Also contains a subset of the official test data packages of the OpenPeppol ViDA Pilot Testing repository

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
TaxDataType tdd = new PeppolViDATDD110Builder ()
    .taxDataTypeCode (EViDATDDTaxDataTypeCode.SUBMIT)
    .documentScope (EViDATDDDocumentScope.DOMESTIC)
    .reporterRole (EViDATDDReporterRole.SENDER)
    .reportingParty (participantID)
    .receivingParty (receiverID)
    .reportersRepresentative (representativeID)
    .taxAuthorityID ("XX")
    // The Invoice Transmission UUID (TDT-018) is not part of the invoice - it identifies
    // the transmission itself and must be provided by the caller
    .reportedTransaction (rt -> rt.initFromInvoice (invoice).transmissionUUID (transmissionUUID))
    .build ();

// Serialize to XML
String xml = new PeppolViDATDD110Marshaller ().setFormattedOutput (true).getAsString (tdd);

// Validate with XSD and Schematron
ValidationResultList vrl = PeppolViDATDDValidator.validateViDA_TDD_110 (new ReadableResourceString (xml, StandardCharsets.UTF_8));
```

# Minimum TDD

How few invoice fields are enough to create a valid *and* fiscally sufficient TDD depends on the
Reporter role (TDT-012): the Buyer (C3) is liable for the VAT of the reported transaction, the
Seller (C2) is not. TDD v1.1.0 introduces that distinction via the rules `ibr-tdd-90` to
`ibr-tdd-93`.

In practice the gap is smaller than the semantic model suggests, because several terms that are
`0..1` there are enforced from elsewhere:

| Term | C2 | C3 | Enforced by |
|---|---|---|---|
| BT-110 Invoice total VAT amount | mandatory | mandatory | `cbc:TaxAmount` is mandatory inside `cac:TaxTotal` in UBL 2.1 |
| BT-112 Invoice total amount with VAT | mandatory | mandatory | `BR-CO-15`, which always applies once BT-110 is present |
| BT-116 VAT category taxable amount | mandatory | mandatory | semantic model |
| BT-117 VAT category tax amount | mandatory | mandatory | `cbc:TaxAmount` is mandatory inside `cac:TaxSubtotal` in UBL 2.1 |
| BT-119 VAT category rate | mandatory | mandatory | `BR-48`, except for the VAT category "O" |
| **BT-118 VAT category code** | **optional** | **mandatory** | `ibr-tdd-92` |
| BT-111 VAT total in accounting currency | optional | mandatory if BT-006 is present | `ibr-tdd-93` |

So the sell side may report "0" amounts without saying which VAT category they belong to, while the
buy side has to name the category - which is exactly the case for the reverse charge, where every
VAT amount is "0" anyway.

Besides that, the smallest TDD needs the TDD envelope (TDT-001 to TDT-015), the Invoice Transmission
UUID (TDT-018) and, inside the ReportedDocument: BT-024, BT-023, TDT-017, BT-001, BT-002, BT-003,
BT-005, the Seller VAT identifier (BT-31), a non-empty Buyer (BG-07), exactly one VAT breakdown
(BG-23, `PEPPOL-EN16931-R053`), BT-106, BT-109, BT-115 and at least one document line with BT-126,
BT-129, BT-130, BT-131, BT-153 and BT-146. The Seller electronic address (BT-34), the Buyer name
(BT-44), the postal addresses and the line VAT information (BG-30) are not needed.

Both minimums are built and Schematron validated in `PeppolViDATDD110BuilderTest.testMinimumTDDForC2`
and `testMinimumTDDForC3`.

# Building

This project requires Apache Maven 3.x and Java 17 for building.
Simply run
```
mvn clean install
```
to build the solution.

# News and noteworthy

v0.11.0 - 2026-09-17
* Updated to the Peppol ViDA TDD **v1.1.0** specification - XSD and Schematron from 2026-09-14. See https://test-docs.peppol.eu/vida/2026-v1.1.0/Vida-tdd/
* **Incompatible change**: the Invoice UUID (TDT-017) is now calculated from the Seller VAT identifier (BT-31), the invoice type code (BT-03), the invoice number (BT-01) and the invoice issue date (BT-02), each trimmed and joined with a single space character. Previously the Seller identifier (BT-29) and its scheme (BT-29-1) were used instead of BT-31, so Invoice UUIDs created with earlier versions differ
* **Incompatible change**: the Invoice Transmission UUID (TDT-018) is new and mandatory. It identifies one single exchange of the reported document, is not derived from the document content and must therefore always be provided by the caller
* Further changes of the TDD v1.1.0 rules:
    * New rules `ibr-tdd-87` (TDT-017 must be a version 5 UUID), `ibr-tdd-88` and `ibr-tdd-89` (TDT-018 present and a valid UUID)
    * New rules `ibr-tdd-90` to `ibr-tdd-93`: when the Reporter role (TDT-012) is "C3", the Invoice total VAT amount (BT-110), the Invoice total amount with VAT (BT-112), per VAT breakdown BT-117, BT-118 and BT-119 and - if a VAT accounting currency code (BT-006) is present - BT-111 are mandatory. For the Reporter role "C2" they stay optional
    * `ibr-tdd-05` now really enforces the time zone on the TDD issue time (TDT-005); the previous regular expression made the offset optional
    * Several inherited CEN EN 16931 and Peppol rules were repaired, because they could not match a TDD:
        * The VAT breakdown rules `BR-{AE,E,G,K,Z,S,O,AF,AG}-08/09/10` and `PEPPOL-EN16931-R051` were anchored at the document root (`/*/cac:TaxTotal/...`), which is `pxs:TaxData` in a TDD. They now use the `pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument` path respectively relative paths
        * The German rules `DE-R-001`, `DE-R-016`, `DE-R-017`, `DE-R-026` and `DE-R-031` had a duplicated path segment (`.../pxs:ReportedDocument/pxs:ReportedDocument`) and therefore never matched
        * `UBL-CR-674` tested for the non-existing element `cbc:PrimaryAccountNumber` instead of `cbc:PrimaryAccountNumberID`
        * `NL-R-007` accepted a `cac:PaymentMeans` anywhere in the document (`//cac:PaymentMeans`); it is now limited to the reported document (`../cac:PaymentMeans`)
* The version number in a class or package name now always matches the TDD version it handles. **All TDD v1.0.0 classes and resources are kept as deprecated**, so that legacy documents can still be created, read, written and validated:
    * The builders in `com.helger.peppol.vida.tdd.v110` (`PeppolViDATDD110Builder` and friends) create TDD v1.1.0 documents - they are the renamed former `…v100.PeppolViDATDD100*` classes
    * The builders in `com.helger.peppol.vida.tdd.v100` are deprecated and functionally unchanged - they still create TDD v1.0.0 documents
    * `PeppolViDATDD110Marshaller` binds the TDD v1.1.0 XSD, the deprecated `PeppolViDATDD100Marshaller` binds the TDD v1.0.0 XSD. Both JAXB models are generated: `…tdd.v2026_09_14` and `…tdd.v2026_03_18`. Neither marshaller can read the other version's documents
    * `CPeppolViDATDD` got `TDD_XSD_1_1_0`, `TDD_XSD_1_1_0_PATH` and `TDD_XSD_1_1_0_NS`; the `TDD_XSD_1_0_0*` counterparts are deprecated
* Changes in `PeppolViDATDD110ReportedTransactionBuilder` compared to the v1.0.0 builder:
    * Added `transmissionUUID` for TDT-018. It has no default - `build ()` returns `null` if it is not set
    * The constructor now takes the Reporter role (TDT-012) as a second argument, because the buy side (C3) must report VAT fields that the sell side (C2) may omit
    * The Seller VAT identifier (BT-31) is now required, except for the VAT category "Not subject to VAT", for which `BR-O-02` forbids it
    * Deprecated `sellerID` and `sellerIDSchemeID` (BT-29/BT-29-1) - they are neither part of the TDD nor an input of the UUID calculation any more, and are no longer read by `initFromInvoice`/`initFromCreditNote`
    * The Seller electronic address (BT-34), the Buyer name (BT-44), the VAT category code (BT-118) and the line VAT information (BG-30) are no longer required, so that a minimum TDD can be created. The Invoice total amount with VAT (BT-112) is only required for the Reporter role "C3"
    * New checks instead: the BUYER (BG-07) must carry at least one of BT-48, BT-55 or BT-44, because it must not be empty (`PEPPOL-EN16931-R008`), and exactly one `cac:TaxTotal` with at least one VAT breakdown (BG-23) is required (`PEPPOL-EN16931-R053`)
* Added `CViDATDD.createInvoiceUUID` to calculate the Invoice UUID (TDT-017) standalone. It is verified against the four test vectors of the specification
* `PeppolViDATDDValidator` got the VES ID `org.peppol.taxdata:vida:1.1.0` (`VID_TDD_VIDA_110`), the method `validateViDA_TDD_110` and the resources `XSLT_*_TDD_110`. The `…_100` counterparts are deprecated and the TDD v1.0.0 VES is registered as deprecated. Added the constants `GROUP_ID` and `ARTIFACT_ID`
* `peppol-vida-testfiles` now contains the TDD v1.1.0 examples in `tdd/1.1.0/good/`, available via `PeppolViDATestFiles.getAllGoodTDD110Files ()`. The v1.0.0 examples are kept as deprecated via `getAllGoodTDD100Files ()`, and `getAllSchematronBadTDD100Files`/`getAllPayloadBadTDD100Files` were renamed to `…TDD110Files`
    * The three "WithoutTaxes" examples of the specification are not listed, because they are not XSD valid: they contain a `cac:TaxTotal` without `cbc:TaxAmount` and a `cac:TaxSubtotal` without `cbc:TaxAmount`, but both are mandatory in UBL 2.1
* Updated the bundled OpenPeppol ViDA Pilot Testing sample TDDs to TDD v1.1.0

v0.10.2 - 2026-09-08
* Added a reference to the OpenPeppol ViDA Pilot Testing repository https://github.com/OpenPEPPOL/vida-pilot-testing/
* Added a subset of the official test data packages of that repository to `peppol-vida-testfiles`, available via `PeppolViDATestFiles.getAllPilotTestData ()`
* Added class `PeppolViDAPilotTestData` that combines a source document with the matching official sample TDD
* Added a test that verifies the `ReportedDocument` UUID calculation (rule ID-BDID-01) against the official sample TDDs
* Added a test that compares the created `ReportedDocument` with the official sample TDDs
* Fixed outdated class names in the `README.md` usage example

v0.10.1 - 2026-08-24
* Included TaxExemptionReason and TaxExemptionReasonCode. See [#2](https://github.com/phax/peppol-vida/pull/2) - thx @vrbyjimmy

v0.10.0 - 2026-07-19
* Updated to ph-schematron v10.x

v0.9.0 - 2026-06-06
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
