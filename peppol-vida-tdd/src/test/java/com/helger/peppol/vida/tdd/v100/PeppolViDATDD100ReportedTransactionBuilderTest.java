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
package com.helger.peppol.vida.tdd.v100;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.Month;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.Test;

import com.helger.datetime.helper.PDTFactory;
import com.helger.peppol.vida.tdd.codelist.EViDATDDTaxDataTypeCode;
import com.helger.peppol.vida.tdd.v2026_03_18.ReportedTransactionType;

/**
 * Test class for class {@link PeppolViDATDD100ReportedTransactionBuilder}.
 *
 * @author Philip Helger
 */
public final class PeppolViDATDD100ReportedTransactionBuilderTest
{
  /**
   * Create a ReportedTransaction that only differs in the fields that are relevant for the
   * ReportedDocument UUID calculation according to rule ID-BDID-01. All other fields are filled with
   * arbitrary but valid values.
   */
  @Nullable
  private static ReportedTransactionType _createForUUID (@Nullable final String sSellerIDSchemeID,
                                                         @NonNull final String sSellerID,
                                                         @NonNull final String sDocumentTypeCode,
                                                         @NonNull final String sID,
                                                         @NonNull final LocalDate aIssueDate)
  {
    return new PeppolViDATDD100ReportedTransactionBuilder (EViDATDDTaxDataTypeCode.SUBMIT).customizationID ("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")
                                                                                          .profileID ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")
                                                                                          .id (sID)
                                                                                          .issueDate (aIssueDate)
                                                                                          .documentTypeCode (sDocumentTypeCode)
                                                                                          .documentCurrencyCode ("EUR")
                                                                                          .sellerEndpointIDSchemeID ("9913")
                                                                                          .sellerEndpointID ("001109-vidapilot.XX")
                                                                                          .sellerIDSchemeID (sSellerIDSchemeID)
                                                                                          .sellerID (sSellerID)
                                                                                          .buyerName ("Buyer Corp")
                                                                                          .taxTotalDocumentCurrency (x -> x.taxAmount (0)
                                                                                                                           .addTaxSubtotal (y -> y.taxableAmount (100)
                                                                                                                                                  .taxAmount (0)
                                                                                                                                                  .taxCategory (z -> z.id ("K")
                                                                                                                                                                      .percentage (0)
                                                                                                                                                                      .taxSchemeID ("VAT"))))
                                                                                          .lineExtensionAmount (100)
                                                                                          .taxExclusiveTotalAmount (100)
                                                                                          .taxInclusiveTotalAmount (100)
                                                                                          .payableAmount (100)
                                                                                          .addDocumentLine (x -> x.id ("1")
                                                                                                                  .quantity (1)
                                                                                                                  .quantityUnit ("H87")
                                                                                                                  .lineExtensionAmount (100)
                                                                                                                  .item (y -> y.name ("Great Good")
                                                                                                                               .classifiedTaxCategory (z -> z.id ("K")
                                                                                                                                                             .percentage (0)
                                                                                                                                                             .taxSchemeID ("VAT")))
                                                                                                                  .priceAmount (100))
                                                                                          .build ();
  }

  /**
   * Verify the ReportedDocument UUID calculation (rule ID-BDID-01) against the official sample TDDs
   * of https://github.com/OpenPEPPOL/vida-pilot-testing/
   */
  @Test
  public void testReportedDocumentUUIDvsOfficialSamples ()
  {
    // NW-HP-001.AT-DK
    ReportedTransactionType aRT = _createForUUID (null,
                                                  "9876-2222222",
                                                  "380",
                                                  "NW-HP-001 - AT-DK",
                                                  PDTFactory.createLocalDate (2026, Month.FEBRUARY, 10));
    assertNotNull (aRT);
    assertEquals ("5f2e5341-94b2-508b-a666-764e592d699b", aRT.getReportedDocument ().getUUID ().getValue ());

    // NW-HP-002.SE-BE
    aRT = _createForUUID (null,
                          "9999-654321",
                          "380",
                          "NW-HP-002 - SE-BE",
                          PDTFactory.createLocalDate (2026, Month.FEBRUARY, 10));
    assertNotNull (aRT);
    assertEquals ("52c00e0f-7a77-51df-98d5-44acf9b21089", aRT.getReportedDocument ().getUUID ().getValue ());

    // NW-HP-008.NO-FI - a CreditNote
    aRT = _createForUUID (null,
                          "9876-444444",
                          "381",
                          "NW-HP-008 - NO-FI",
                          PDTFactory.createLocalDate (2026, Month.FEBRUARY, 10));
    assertNotNull (aRT);
    assertEquals ("f6d0e98b-7949-5eab-aaea-45bbc1df02c3", aRT.getReportedDocument ().getUUID ().getValue ());
  }
}
