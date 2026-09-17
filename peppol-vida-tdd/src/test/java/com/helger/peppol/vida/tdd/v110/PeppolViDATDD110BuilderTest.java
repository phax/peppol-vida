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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.time.ZoneOffset;

import org.jspecify.annotations.NonNull;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.datetime.helper.PDTFactory;
import com.helger.diagnostics.error.IError;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.inmemory.ReadableResourceString;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.peppol.vida.tdd.codelist.EViDATDDDocumentScope;
import com.helger.peppol.vida.tdd.codelist.EViDATDDReporterRole;
import com.helger.peppol.vida.tdd.codelist.EViDATDDTaxDataTypeCode;
import com.helger.peppol.vida.tdd.jaxb.PeppolViDATDD110Marshaller;
import com.helger.peppol.vida.tdd.testfiles.PeppolViDATestFiles;
import com.helger.peppol.vida.tdd.v2026_09_14.TaxDataType;
import com.helger.peppol.vida.tdd.validate.PeppolViDATDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.ubl21.UBL21Marshaller;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CustomizationIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test class for class {@link PeppolViDATDD110Builder}.
 *
 * @author Philip Helger
 */
public final class PeppolViDATDD110BuilderTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD110BuilderTest.class);

  // TDT-018 - in production this is the identifier of the actual transmission
  private static final String TRANSMISSION_UUID = "d8269130-a816-5ab4-b2b3-089456b179e2";

  /**
   * Validate the given TDD against the ViDA TDD v1.1.0 Schematrons.
   *
   * @param aTDD
   *        The TDD to validate. May not be <code>null</code>.
   */
  private static void _validate (@NonNull final TaxDataType aTDD)
  {
    final String sXML = new PeppolViDATDD110Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    if (false)
      LOGGER.info (sXML);

    final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_110 (new ReadableResourceString (sXML,
                                                                                                               StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  /**
   * Create the TDD envelope that both the C2 and the C3 minimum TDD test use.
   *
   * @param eReporterRole
   *        The Reporter role (TDT-012) to use. May not be <code>null</code>.
   * @return The builder, ready to receive a ReportedTransaction.
   */
  @NonNull
  private static PeppolViDATDD110Builder _createEnvelope (@NonNull final EViDATDDReporterRole eReporterRole)
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    return new PeppolViDATDD110Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.SUBMIT)
                                         .documentScope (EViDATDDDocumentScope.INTRA_COMMUNITY)
                                         .reporterRole (eReporterRole)
                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                         .taxAuthorityID ("XX");
  }

  /**
   * The minimum TDD of a sell side (C2) reporter. The Seller is not liable for the VAT of the
   * reported transaction, so no VAT amount at all needs to be reported.
   *
   * @throws Exception
   *         in case of error
   */
  @Test
  public void testMinimumTDDForC2 () throws Exception
  {
    final TaxDataType aTDD = _createEnvelope (EViDATDDReporterRole.SENDER).reportedTransaction (rt -> rt.transmissionUUID (TRANSMISSION_UUID)
                                                                                                       .customizationID ("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")
                                                                                                       .profileID ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")
                                                                                                       .id ("INV-2025-0046")
                                                                                                       .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                                               Month.FEBRUARY,
                                                                                                                                               10))
                                                                                                       .documentTypeCode ("380")
                                                                                                       .documentCurrencyCode ("EUR")
                                                                                                       // BT-31 and BT-48
                                                                                                       .sellerTaxID ("DE811569869")
                                                                                                       .buyerTaxID ("SE556677889901")
                                                                                                       // Exactly one VAT breakdown is needed
                                                                                                       // (PEPPOL-EN16931-R053). BT-110 and BT-117
                                                                                                       // are mandatory in UBL 2.1 and BT-119 is
                                                                                                       // required by BR-48, so only BT-118 may be
                                                                                                       // left out by the sell side
                                                                                                       .taxTotalDocumentCurrency (x -> x.taxAmount (0)
                                                                                                                                        .addTaxSubtotal (y -> y.taxableAmount (100)
                                                                                                                                                               .taxAmount (0)
                                                                                                                                                               .taxCategory (z -> z.percentage (0)
                                                                                                                                                                                   .taxSchemeID ("VAT"))))
                                                                                                       // BT-106, BT-109 and BT-115. BT-112 is
                                                                                                       // needed because BR-CO-15 always applies
                                                                                                       // once BT-110 is present
                                                                                                       .lineExtensionAmount (100)
                                                                                                       .taxExclusiveTotalAmount (100)
                                                                                                       .taxInclusiveTotalAmount (100)
                                                                                                       .payableAmount (100)
                                                                                                       .addDocumentLine (x -> x.id ("1")
                                                                                                                               .quantity (1)
                                                                                                                               .quantityUnit ("H87")
                                                                                                                               .lineExtensionAmount (100)
                                                                                                                               .item (y -> y.name ("Great Good"))
                                                                                                                               .priceAmount (100)))
                                                                         .build ();
    assertNotNull (aTDD);
    _validate (aTDD);
  }

  /**
   * The minimum TDD of a buy side (C3) reporter. Compared to C2 it additionally needs BT-110,
   * BT-112 and, per VAT breakdown, BT-117, BT-118 and BT-119 - even though all of them are "0"
   * because of the reverse charge.
   *
   * @throws Exception
   *         in case of error
   */
  @Test
  public void testMinimumTDDForC3 () throws Exception
  {
    final TaxDataType aTDD = _createEnvelope (EViDATDDReporterRole.RECEIVER).reportedTransaction (rt -> rt.transmissionUUID (TRANSMISSION_UUID)
                                                                                                         .customizationID ("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")
                                                                                                         .profileID ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")
                                                                                                         .id ("INV-2025-0046")
                                                                                                         .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                                                 Month.FEBRUARY,
                                                                                                                                                 10))
                                                                                                         .documentTypeCode ("380")
                                                                                                         .documentCurrencyCode ("EUR")
                                                                                                         // BT-31 and BT-48 are needed for the reverse charge
                                                                                                         .sellerTaxID ("DE811569869")
                                                                                                         .sellerCountryCode ("DE")
                                                                                                         .buyerTaxID ("SE556677889901")
                                                                                                         .buyerCountryCode ("SE")
                                                                                                         // BT-110, BT-117, BT-118 and BT-119
                                                                                                         .taxTotalDocumentCurrency (x -> x.taxAmount (0)
                                                                                                                                          .addTaxSubtotal (y -> y.taxableAmount (100)
                                                                                                                                                                 .taxAmount (0)
                                                                                                                                                                 .taxCategory (z -> z.id ("AE")
                                                                                                                                                                                     .percentage (0)
                                                                                                                                                                                     .taxSchemeID ("VAT")
                                                                                                                                                                                     .taxExemptionReason ("Reverse charge"))))
                                                                                                         // BT-106, BT-109, BT-112 and BT-115
                                                                                                         .lineExtensionAmount (100)
                                                                                                         .taxExclusiveTotalAmount (100)
                                                                                                         .taxInclusiveTotalAmount (100)
                                                                                                         .payableAmount (100)
                                                                                                         .addDocumentLine (x -> x.id ("1")
                                                                                                                                 .quantity (1)
                                                                                                                                 .quantityUnit ("H87")
                                                                                                                                 .lineExtensionAmount (100)
                                                                                                                                 .item (y -> y.name ("Great Good")
                                                                                                                                              .classifiedTaxCategory (z -> z.id ("AE")
                                                                                                                                                                            .percentage (0)
                                                                                                                                                                            .taxSchemeID ("VAT")))
                                                                                                                                 .priceAmount (100)))
                                                                           .build ();
    assertNotNull (aTDD);
    _validate (aTDD);
  }

  @Test
  public void testBasicMinimal () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final TaxDataType aTDD = new PeppolViDATDD110Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.SUBMIT)
                                                           .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                           .reporterRole (EViDATDDReporterRole.SENDER)
                                                           .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                           .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                           .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                           .taxAuthorityID ("XX")
                                                           // Provide all fields manually
                                                           .reportedTransaction (rt -> rt.transmissionUUID (TRANSMISSION_UUID)
                                                                                         .customizationID ("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")
                                                                                         .profileID ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")
                                                                                         .id ("invoice-1")
                                                                                         .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                                 Month.SEPTEMBER,
                                                                                                                                 20))
                                                                                         .documentTypeCode ("380")
                                                                                         .documentCurrencyCode ("AED")
                                                                                         .sellerEndpointIDSchemeID ("0088")
                                                                                         .sellerEndpointID ("1234567890123")
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

    // Serialize
    final String sXML = new PeppolViDATDD110Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_110 (new ReadableResourceString (sXML,
                                                                                                               StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  @Test
  public void testBasicMaximal () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final TaxDataType aTDD = new PeppolViDATDD110Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.SUBMIT)
                                                           .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                           .reporterRole (EViDATDDReporterRole.SENDER)
                                                           .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                           .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                           .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                           .taxAuthorityID ("XX")
                                                           // Provide all fields manually
                                                           .reportedTransaction (rt -> rt.transmissionUUID (TRANSMISSION_UUID)
                                                                                         .customizationID ("urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0")
                                                                                         .profileID ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0")
                                                                                         .id ("invoice-1")
                                                                                         .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                                 Month.SEPTEMBER,
                                                                                                                                 20))
                                                                                         .issueTime (PDTFactory.createOffsetTime (20,
                                                                                                                                  8,
                                                                                                                                  0,
                                                                                                                                  ZoneOffset.UTC))
                                                                                         .documentTypeCode ("380")
                                                                                         .documentCurrencyCode ("EUR")
                                                                                         .taxCurrencyCode ("AED")
                                                                                         .sellerEndpointIDSchemeID ("0088")
                                                                                         .sellerEndpointID ("1234567890123")
                                                                                         .sellerTaxID ("DE11223344")
                                                                                         .sellerCountryCode ("DE")
                                                                                         .buyerTaxID ("ATU87654321")
                                                                                         .buyerCountryCode ("AT")
                                                                                         .buyerName ("Buyer Corp")
                                                                                         .taxRepresentativeID ("CH000111222")
                                                                                         .taxRepresentativeCountryCode ("CH")
                                                                                         .taxTotalDocumentCurrency (x -> x.taxAmount (120)
                                                                                                                          .addTaxSubtotal (y -> y.taxableAmount (1200)
                                                                                                                                                 .taxAmount (120)
                                                                                                                                                 .taxCategory (z -> z.id ("S")
                                                                                                                                                                     .percentage (10)
                                                                                                                                                                     .taxSchemeID ("VAT"))))
                                                                                         .taxTotalTaxCurrency (x -> x.taxAmount (500))
                                                                                         .lineExtensionAmount (1200)
                                                                                         .taxExclusiveTotalAmount (1200)
                                                                                         .taxInclusiveTotalAmount (1320)
                                                                                         .allowanceTotalAmount (0)
                                                                                         .chargeTotalAmount (0)
                                                                                         .payableRoundingAmount (0)
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

    // Serialize
    final String sXML = new PeppolViDATDD110Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_110 (new ReadableResourceString (sXML,
                                                                                                               StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  @Test
  public void testCreateFromAllInvoices () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllGoodBillingInvoiceFiles ())
    {
      LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

      final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
      assertNotNull (aInvoice);

      final TaxDataType aTDD = new PeppolViDATDD110Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.SUBMIT)
                                                             .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                             .reporterRole (EViDATDDReporterRole.SENDER)
                                                             .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                             .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                             .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                             .taxAuthorityID ("XX")
                                                             // Read from pre-parsed UBL Invoice
                                                             .reportedTransaction (rt -> rt.initFromInvoice (aInvoice).transmissionUUID (TRANSMISSION_UUID))
                                                             .build ();
      assertNotNull (aTDD);

      // Serialize
      final GenericJAXBMarshaller <TaxDataType> m = new PeppolViDATDD110Marshaller ().setFormattedOutput (true);
      final String sXML = m.getAsString (aTDD);
      assertNotNull (sXML);

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_110 (new ReadableResourceString (sXML,
                                                                                                                 StandardCharsets.UTF_8));
      assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                  aVRL.getOverallValidity ().isValid ());
    }
  }

  @Test
  public void testCreateFromAllCreditNotes () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllGoodBillingCreditNoteFiles ())
    {
      LOGGER.info ("Converting CreditNote '" + aRes.getPath () + "' to a TDD");

      final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ().read (aRes);
      assertNotNull (aCreditNote);

      final TaxDataType aTDD = new PeppolViDATDD110Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.SUBMIT)
                                                             .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                             .reporterRole (EViDATDDReporterRole.SENDER)
                                                             .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                             .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                             .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                             .taxAuthorityID ("XX")
                                                             // Read from pre-parsed UBL CreditNote
                                                             .reportedTransaction (rt -> rt.initFromCreditNote (aCreditNote).transmissionUUID (TRANSMISSION_UUID))
                                                             .build ();
      assertNotNull (aTDD);

      // Serialize and XSD validate
      final GenericJAXBMarshaller <TaxDataType> m = new PeppolViDATDD110Marshaller ().setFormattedOutput (true);
      final String sXML = m.getAsString (aTDD);
      assertNotNull (sXML);

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_110 (new ReadableResourceString (sXML,
                                                                                                                 StandardCharsets.UTF_8));
      assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                  aVRL.getOverallValidity ().isValid ());
    }
  }

  @Test
  public void testCreateFailedInvoiceWithReportedDocument () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final ClassPathResource aRes = PeppolViDATestFiles.getAllGoodBillingInvoiceFiles ().getFirstOrNull ();
    LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

    final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
    assertNotNull (aInvoice);

    final TaxDataType aTDD = new PeppolViDATDD110Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.DISREGARD)
                                                           .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                           .reporterRole (EViDATDDReporterRole.SENDER)
                                                           .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                           .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                           .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                           .taxAuthorityID ("XX")
                                                           // It's not really an invalid invoice
                                                           .reportedTransaction (rt -> rt.initFromInvoice (aInvoice).transmissionUUID (TRANSMISSION_UUID))
                                                           .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolViDATDD110Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    assertTrue (sXML.contains ("<pxs:ReportedDocument>"));

    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_110 (new ReadableResourceString (sXML,
                                                                                                               StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  @Test
  @Ignore ("Not supported by v1.1.0 - pxs:ReportedDocument is mandatory inside pxs:ReportedTransaction")
  public void testCreateFailedInvoiceWithoutReportedDocument () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    final ClassPathResource aRes = PeppolViDATestFiles.getAllGoodBillingInvoiceFiles ().getFirstOrNull ();
    LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

    final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
    assertNotNull (aInvoice);

    // Explicitly sets a "null" CustomizationID to indicate an invalid source message
    aInvoice.setCustomizationID ((CustomizationIDType) null);
    // This one is special, because it is an XSD mandatory fields
    aInvoice.setID ((IDType) null);

    final TaxDataType aTDD = new PeppolViDATDD110Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.DISREGARD)
                                                           .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                           .reporterRole (EViDATDDReporterRole.SENDER)
                                                           .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9915:c1id"))
                                                           .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                           .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                           .taxAuthorityID ("XX")
                                                           // This Invoice is really broken
                                                           .reportedTransaction (rt -> rt.initFromInvoice (aInvoice).transmissionUUID (TRANSMISSION_UUID))
                                                           .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolViDATDD110Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    assertFalse (sXML.contains ("<pxs:ReportedDocument>"));

    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_110 (new ReadableResourceString (sXML,
                                                                                                               StandardCharsets.UTF_8));
    assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                aVRL.getOverallValidity ().isValid ());
  }

  @Test
  public void testReadBadPayloads () throws Exception
  {
    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllPayloadBadTDD110Files ())
    {
      LOGGER.info ("Reading Bad Payload TDD '" + aRes.getPath () + "'");

      final TaxDataType aTDD = new PeppolViDATDD110Marshaller ().read (aRes);
      assertNotNull (aTDD);

      // Serialize
      final String sXML = new PeppolViDATDD110Marshaller ().setFormattedOutput (true).getAsString (aTDD);
      assertNotNull (sXML);
      assertFalse (sXML.contains ("<pxs:ReportedDocument>"));

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_110 (new ReadableResourceString (sXML,
                                                                                                                 StandardCharsets.UTF_8));
      assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                  aVRL.getOverallValidity ().isValid ());
    }
  }
}
