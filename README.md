# peppol-uae

[![Maven Central](https://img.shields.io/maven-central/v/com.helger.peppol/peppol-vida-parent-pom)](https://img.shields.io/maven-central/v/com.helger.peppol/peppol-vida-parent-pom)
[![javadoc](https://javadoc.io/badge2/com.helger.peppol/peppol-vida-tdd/javadoc.svg)](https://javadoc.io/doc/com.helger.peppol/peppol-vida-tdd)

Special support for Peppol ViDA (pilot right now).

**This project does NOT (yet) contain an official data model of the European DRR as required by ViDA!**

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

Add the following to your `pom.xml` to use this artifact:, replacing `x.y.z` with the real version number.

```xml
<dependency>
  <groupId>com.helger.peppol</groupId>
  <artifactId>peppol-vida-tdd</artifactId>
  <version>x.y.z</version>
</dependency>
```

# Building

This project requires Apache Maven 3.x and Java 17 for building.
Simply run
```
mvn clean install
```
to build the solution.

# News and noteworthy

v0.9.0 - work in progress
* Initial version 

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
