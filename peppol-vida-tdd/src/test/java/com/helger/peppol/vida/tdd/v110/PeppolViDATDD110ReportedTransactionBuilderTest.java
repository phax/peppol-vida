/*
 * Copyright (C) 2026 Philip Helger
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.vida.tdd.v110;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.Month;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.datetime.helper.PDTFactory;
import com.helger.peppol.vida.tdd.codelist.EViDATDDReporterRole;
import com.helger.peppol.vida.tdd.codelist.EViDATDDTaxDataTypeCode;
import com.helger.peppol.vida.tdd.v2026_09_14.ReportedTransactionType;

/**
 * Test class for class {@link PeppolViDATDD110ReportedTransactionBuilder}.
 *
 * @author Philip Helger
 */
public final class PeppolViDATDD110ReportedTransactionBuilderTest
{
  private static final String TRANSMISSION_UUID = "d8269130-a816-5ab4-b2b3-089456b179e2";

  /**
   * Create the smallest ReportedTransaction for the provided Reporter role. For the sell side (C2)
   * the VAT category code (BT-118) may be left out, because the Seller is not liable for the VAT of
   * the reported transaction.
   *
   * @param eReporterRole
   *        The Reporter role (TDT-012) to use. May not be <code>null</code>.
   * @return The builder, ready to be modified and built.
   */
  @NonNull
  private static PeppolViDATDD110ReportedTransactionBuilder _createMinimal (@NonNull final EViDATDDReporterRole eReporterRole)
  {
    return new PeppolViDATDD110ReportedTransactionBuilder (EViDATDDTaxDataTypeCode.SUBMIT, eReporterRole)
                                                                                                         .transmissionUUID (TRANSMISSION_UUID)
                                                                                                         .customizationID ("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")
                                                                                                         .profileID ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")
                                                                                                         .id ("INV-2025-0046")
                                                                                                         .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                                                 Month.FEBRUARY,
                                                                                                                                                 10))
                                                                                                         .documentTypeCode ("380")
                                                                                                         .documentCurrencyCode ("EUR")
                                                                                                         .sellerTaxID ("DE811569869")
                                                                                                         .buyerTaxID ("SE556677889901")
                                                                                                         .taxTotalDocumentCurrency (x -> x.taxAmount (0)
                                                                                                                                          .addTaxSubtotal (y -> y.taxableAmount (100)
                                                                                                                                                                 .taxAmount (0)
                                                                                                                                                                 .taxCategory (z -> z.percentage (0)
                                                                                                                                                                                     .taxSchemeID ("VAT"))))
                                                                                                         .lineExtensionAmount (100)
                                                                                                         .taxExclusiveTotalAmount (100)
                                                                                                         .taxInclusiveTotalAmount (100)
                                                                                                         .payableAmount (100)
                                                                                                         .addDocumentLine (x -> x.id ("1")
                                                                                                                                 .quantity (1)
                                                                                                                                 .quantityUnit ("H87")
                                                                                                                                 .lineExtensionAmount (100)
                                                                                                                                 .item (y -> y.name ("Great Good"))
                                                                                                                                 .priceAmount (100));
  }

  /**
   * Create a ReportedTransaction that only differs in the fields that are relevant for the Invoice
   * UUID (TDT-017) calculation. All other fields are filled with arbitrary but valid values.
   *
   * @return The built ReportedTransaction or <code>null</code> if a mandatory field is missing.
   */
  @NonNull
  private static ReportedTransactionType _createForUUID (@NonNull final String sSellerTaxID,
                                                         @NonNull final String sDocumentTypeCode,
                                                         @NonNull final String sID,
                                                         @NonNull final LocalDate aIssueDate)
  {
    final ReportedTransactionType ret = _createMinimal (EViDATDDReporterRole.SENDER).sellerTaxID (sSellerTaxID)
                                                                                    .documentTypeCode (sDocumentTypeCode)
                                                                                    .id (sID)
                                                                                    .issueDate (aIssueDate)
                                                                                    .build ();
    assertNotNull (ret);
    return ret;
  }

  /**
   * Verify the Invoice UUID (TDT-017) calculation against the test vectors of the Peppol ViDA TDD
   * v1.1.0 specification, chapter "Invoice UUID calculation".
   */
  @Test
  public void testInvoiceUUIDSpecTestVectors ()
  {
    ReportedTransactionType aRT = _createForUUID ("DE811569869",
                                                  "380",
                                                  "INV-2025-0046",
                                                  PDTFactory.createLocalDate (2025, Month.FEBRUARY, 10));
    assertEquals ("62fd54e5-8689-57a6-95a7-600c86734ea0", aRT.getReportedDocument ().getUUID ().getValue ());

    aRT = _createForUUID ("BE0477472701", "380", "INV-2025-0001", PDTFactory.createLocalDate (2025, Month.JANUARY, 10));
    assertEquals ("81270c84-bfb1-5a12-8314-8887ee7d3e27", aRT.getReportedDocument ().getUUID ().getValue ());

    // Same Seller, number and date as the first one - only the type code differs
    aRT = _createForUUID ("DE811569869", "389", "INV-2025-0046", PDTFactory.createLocalDate (2025, Month.FEBRUARY, 10));
    assertEquals ("936d1327-0863-5632-ac82-30001c004e01", aRT.getReportedDocument ().getUUID ().getValue ());

    aRT = _createForUUID ("FR40303265045", "261", "CN-2025-0003", PDTFactory.createLocalDate (2025, Month.FEBRUARY, 5));
    assertEquals ("9a787cd0-ef99-5bd1-ad5d-daf020699cf2", aRT.getReportedDocument ().getUUID ().getValue ());
  }

  /**
   * The Invoice Transmission UUID (TDT-018) is mandatory since TDD v1.1.0 and cannot be derived
   * from the document content.
   */
  @Test
  public void testTransmissionUUIDIsMandatory ()
  {
    assertNotNull (_createMinimal (EViDATDDReporterRole.SENDER).build ());
    assertNull (_createMinimal (EViDATDDReporterRole.SENDER).transmissionUUID (null).build ());
    assertEquals (TRANSMISSION_UUID, _createMinimal (EViDATDDReporterRole.SENDER).build ().getTransmissionUUID ());
  }

  /**
   * Without the Seller VAT identifier (BT-31) no Invoice UUID (TDT-017) can be calculated, so no
   * TDD can be created.
   */
  @Test
  public void testSellerVATIdentifierIsMandatory ()
  {
    assertNull (_createMinimal (EViDATDDReporterRole.SENDER).sellerTaxID (null).build ());
  }

  /**
   * The sell side (C2) may omit all VAT amounts, whereas the buy side (C3) must provide them -
   * Schematron rules ibr-tdd-90, ibr-tdd-91 and ibr-tdd-92.
   */
  @Test
  public void testBuySideVATFieldsAreOnlyMandatoryForC3 ()
  {
    // C2 without any VAT amount is fine
    assertNotNull (_createMinimal (EViDATDDReporterRole.SENDER).build ());

    // The very same content is not enough for C3, because the VAT category code (BT-118) is missing
    final PeppolViDATDD110ReportedTransactionBuilder aC3 = _createMinimal (EViDATDDReporterRole.RECEIVER);
    assertNull (aC3.build ());

    // BT-118 - "AE" and all amounts "0" because of the reverse charge, but they must be present
    aC3.taxTotalDocumentCurrency (x -> x.taxAmount (0)
                                        .addTaxSubtotal (y -> y.taxableAmount (100)
                                                               .taxAmount (0)
                                                               .taxCategory (z -> z.id ("AE")
                                                                                   .percentage (0)
                                                                                   .taxSchemeID ("VAT"))));
    assertNotNull (aC3.build ());
  }
}
