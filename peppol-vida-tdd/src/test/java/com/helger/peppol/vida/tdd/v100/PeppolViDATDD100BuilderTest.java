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
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Month;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.datetime.helper.PDTFactory;
import com.helger.diagnostics.error.IError;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.inmemory.ReadableResourceString;
import com.helger.peppol.vida.tdd.codelist.EViDATDDDocumentScope;
import com.helger.peppol.vida.tdd.codelist.EViDATDDReporterRole;
import com.helger.peppol.vida.tdd.codelist.EViDATDDTaxDataTypeCode;
import com.helger.peppol.vida.tdd.jaxb.PeppolViDATDD100Marshaller;
import com.helger.peppol.vida.tdd.testfiles.PeppolViDATestFiles;
import com.helger.peppol.vida.tdd.v2026_03_18.TaxDataType;
import com.helger.peppol.vida.tdd.validate.PeppolViDATDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.ubl21.UBL21Marshaller;

import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test class for the deprecated class {@link PeppolViDATDD100Builder}. It only verifies that the
 * legacy TDD v1.0.0 path still works end to end - build, serialize and validate against the
 * deprecated TDD v1.0.0 rules.
 *
 * @author Philip Helger
 */
@SuppressWarnings ("removal")
public final class PeppolViDATDD100BuilderTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD100BuilderTest.class);

  private static void _validate (final TaxDataType aTDD)
  {
    final String sXML = new PeppolViDATDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);

    final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_100 (new ReadableResourceString (sXML,
                                                                                                               StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  @Test
  public void testBasic ()
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final TaxDataType aTDD = new PeppolViDATDD100Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.SUBMIT)
                                                           .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                           .reporterRole (EViDATDDReporterRole.SENDER)
                                                           .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                           .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                           .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                           .taxAuthorityID ("XX")
                                                           .reportedTransaction (rt -> rt.customizationID ("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")
                                                                                         .profileID ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")
                                                                                         .id ("invoice-1")
                                                                                         .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                                 Month.SEPTEMBER,
                                                                                                                                 20))
                                                                                         .documentTypeCode ("380")
                                                                                         .documentCurrencyCode ("AED")
                                                                                         .sellerEndpointIDSchemeID ("0088")
                                                                                         .sellerEndpointID ("1234567890123")
                                                                                         .sellerID ("id99887766")
                                                                                         .sellerTaxID ("DE11223344")
                                                                                         .buyerTaxID ("SK987654321")
                                                                                         .buyerName ("Buyer Corp")
                                                                                         .taxTotalDocumentCurrency (x -> x.taxAmount (120)
                                                                                                                          .addTaxSubtotal (y -> y.taxableAmount (1200)
                                                                                                                                                 .taxAmount (120)
                                                                                                                                                 .taxCategory (z -> z.id ("S")
                                                                                                                                                                     .percentage (10)
                                                                                                                                                                     .taxSchemeID ("VAT"))))
                                                                                         .lineExtensionAmount (1200)
                                                                                         .taxExclusiveTotalAmount (1200)
                                                                                         .taxInclusiveTotalAmount (1320)
                                                                                         .payableAmount (1320)
                                                                                         .addDocumentLine (x -> x.id ("1")
                                                                                                                 .quantity (10)
                                                                                                                 .quantityUnit ("STK")
                                                                                                                 .lineExtensionAmount (1200)
                                                                                                                 .item (y -> y.name ("What")
                                                                                                                              .classifiedTaxCategory (z -> z.id ("S")
                                                                                                                                                            .percentage (10)
                                                                                                                                                            .taxSchemeID ("VAT")))
                                                                                                                 .priceAmount (120)))
                                                           .build ();
    assertNotNull (aTDD);

    // No Invoice Transmission UUID (TDT-018) in TDD v1.0.0
    assertEquals (1, aTDD.getReportedTransactionCount ());
    _validate (aTDD);
  }

  /**
   * The legacy builder still derives the Invoice UUID from the Seller identifier (BT-29) instead of
   * the Seller VAT identifier (BT-31).
   */
  @Test
  public void testCreateFromAllInvoices ()
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllGoodBillingInvoiceFiles ())
    {
      LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD v1.0.0");

      final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
      assertNotNull (aInvoice);

      final TaxDataType aTDD = new PeppolViDATDD100Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.SUBMIT)
                                                             .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                             .reporterRole (EViDATDDReporterRole.SENDER)
                                                             .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                             .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                             .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                             .taxAuthorityID ("XX")
                                                             .reportedTransaction (rt -> rt.initFromInvoice (aInvoice))
                                                             .build ();
      assertNotNull (aTDD);
      _validate (aTDD);
    }
  }
}
