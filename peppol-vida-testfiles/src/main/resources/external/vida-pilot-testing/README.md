# OpenPeppol ViDA Pilot Testing files

The files in this directory are unmodified copies taken from the official OpenPeppol
ViDA Pilot Testing repository at https://github.com/OpenPEPPOL/vida-pilot-testing/
(commit `80f388e`, retrieved 2026-09-08).

Only a representative subset of the test data packages is bundled here - one selection
of tax jurisdiction combinations per test scenario:

* `NW-HP-001` - Invoice for an Intra-Community Supply (ICS) of goods; VAT category `K` (`G` if Norway is involved)
* `NW-HP-002` - Invoice for services under reverse charge; VAT category `K`
* `NW-HP-002-RC` - Invoice for services under reverse charge; VAT category `AE`
* `NW-HP-006` - Invoice for an ICS of goods with PDF attachments; VAT category `K` (`G` if Norway is involved)
* `NW-HP-008` - Credit note (document type code `381`) for a prior ICS of goods; VAT category `K` (`G` if Norway is involved)

For each combination two files are present:

* `<scenario>/<scenario>.<seller>-<buyer>.PeppolBIS.xml` - the source Peppol BIS Billing 3.0 Invoice or CreditNote
* `<scenario>/sample-results/<scenario>.<seller>-<buyer>.PeppolBIS.TDD-C3.xml` - the non-normative buy-side (C3) sample TDD

Refer to the upstream repository for the scenario descriptions, the master data used,
the remaining tax jurisdiction combinations and the other supporting files.
