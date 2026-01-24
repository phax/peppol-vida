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
package com.helger.peppol.vida.tdd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.numeric.BigHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.datetime.helper.PDTFactory;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.inmemory.ReadableResourceString;
import com.helger.peppol.uae.tdd.testfiles.PeppolViDATestFiles;
import com.helger.peppol.vida.tdd.PeppolViDATDD090Builder;
import com.helger.peppol.vida.tdd.PeppolUAETDD10ReportedTransactionBuilder.CustomContent;
import com.helger.peppol.vida.tdd.codelist.EViDATDDDocumentScope;
import com.helger.peppol.vida.tdd.codelist.EViDATDDDocumentTypeCode;
import com.helger.peppol.vida.tdd.codelist.EViDATDDReporterRole;
import com.helger.peppol.vida.tdd.jaxb.PeppolViDATDD090Marshaller;
import com.helger.peppol.vida.tdd.v100.TaxDataType;
import com.helger.peppol.vida.tdd.validate.PeppolUAETDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.helger.ubl21.UBL21Marshaller;
import com.helger.xml.serialize.read.DOMReader;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CustomizationIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test class for class {@link PeppolViDATDD090Builder}.
 *
 * @author Philip Helger
 */
public final class PeppolUAETDD10BuilderTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolUAETDD10BuilderTest.class);

  @Test
  public void testBasicMinimal () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolUAETDDValidator.getSchematronUAE_TDD_10 ();

    final TaxDataType aTDD = new PeppolViDATDD090Builder ().documentTypeCode (EViDATDDDocumentTypeCode.SUBMIT)
                                                         .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                         .reporterRole (EViDATDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0235:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         // Provide all fields manually
                                                         .reportedTransaction (rt -> rt.transportHeaderID ("my-sbdh-uuid-12345678")
                                                                                       .customizationID ("urn:peppol:pint:billing-1@ae-1")
                                                                                       .profileID ("urn:peppol:bis:billing")
                                                                                       .id ("invoice-1")
                                                                                       .uuid ("19e2c9a3-b000-4fb0-9bd5-a9c4ebda2358")
                                                                                       .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                               Month.SEPTEMBER,
                                                                                                                               20))
                                                                                       .documentTypeCode ("380")
                                                                                       .documentCurrencyCode ("AED")
                                                                                       .sellerTaxID ("123456789")
                                                                                       .sellerTaxSchemeID ("VAT")
                                                                                       .buyerID ("11223344")
                                                                                       .buyerIDSchemeID ("AE:TIN")
                                                                                       .buyerTaxID ("987654321")
                                                                                       .taxTotalAmountDocumentCurrency (BigHelper.toBigDecimal ("123.45"))
                                                                                       .taxExclusiveTotalAmount (BigHelper.toBigDecimal ("1200"))
                                                                                       .sourceDocument (DOMReader.readXMLDOM ("<Invoice xmlns='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'>" +
                                                                                                                              "\n... omitted for brevity ...\n" +
                                                                                                                              "</Invoice>")))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolViDATDD090Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (new ReadableResourceString (sXML,
                                                                                                            StandardCharsets.UTF_8));
    assertNotNull (aSVRL);
    assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
  }

  @Test
  public void testBasicMaximal () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolUAETDDValidator.getSchematronUAE_TDD_10 ();

    final TaxDataType aTDD = new PeppolViDATDD090Builder ().documentTypeCode (EViDATDDDocumentTypeCode.SUBMIT)
                                                         .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                         .reporterRole (EViDATDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0235:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         // Provide all fields manually
                                                         .reportedTransaction (rt -> rt.transportHeaderID ("my-sbdh-uuid-12345678")
                                                                                       .customizationID ("urn:peppol:pint:billing-1@ae-1")
                                                                                       .profileID ("urn:peppol:bis:billing")
                                                                                       .id ("invoice-1")
                                                                                       .uuid ("19e2c9a3-b000-4fb0-9bd5-a9c4ebda2358")
                                                                                       .issueDate (PDTFactory.createLocalDate (2025,
                                                                                                                               Month.SEPTEMBER,
                                                                                                                               20))
                                                                                       .issueTime (PDTFactory.createOffsetTime (20,
                                                                                                                                8,
                                                                                                                                0,
                                                                                                                                ZoneOffset.UTC))
                                                                                       .documentTypeCode ("380")
                                                                                       .documentCurrencyCode ("AED")
                                                                                       .taxCurrencyCode ("EUR")
                                                                                       .sellerTaxID ("123456789")
                                                                                       .sellerTaxSchemeID ("VAT")
                                                                                       .buyerID ("11223344")
                                                                                       .buyerIDSchemeID ("AE:TIN")
                                                                                       .buyerTaxID ("987654321")
                                                                                       .taxTotalAmountDocumentCurrency (BigHelper.toBigDecimal ("123.45"))
                                                                                       .taxTotalAmountTaxCurrency (BigHelper.toBigDecimal ("500"))
                                                                                       .taxExclusiveTotalAmount (BigHelper.toBigDecimal ("1200"))
                                                                                       .addCustomContent (new CustomContent ("ID1",
                                                                                                                             "val1"))
                                                                                       .sourceDocument (DOMReader.readXMLDOM ("<Invoice xmlns='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'>" +
                                                                                                                              "\n... omitted for brevity ...\n" +
                                                                                                                              "</Invoice>")))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolViDATDD090Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (new ReadableResourceString (sXML,
                                                                                                            StandardCharsets.UTF_8));
    assertNotNull (aSVRL);
    assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
  }

  @Test
  public void testCreateFromAllInvoices () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolUAETDDValidator.getSchematronUAE_TDD_10 ();

    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllGoodBillingInvoiceFiles ())
    {
      LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

      final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
      assertNotNull (aInvoice);

      final TaxDataType aTDD = new PeppolViDATDD090Builder ().documentTypeCode (EViDATDDDocumentTypeCode.SUBMIT)
                                                           .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                           .reporterRole (EViDATDDReporterRole.SENDER)
                                                           .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0235:c1id"))
                                                           .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                           .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                           // Read from pre-parsed UBL Invoice
                                                           .reportedTransaction (rt -> rt.transportHeaderID ("my-sbdh-uuid-12345678")
                                                                                         .initFromInvoice (aInvoice))
                                                           .build ();
      assertNotNull (aTDD);

      // Serialize
      final String sXML = new PeppolViDATDD090Marshaller ().setFormattedOutput (true).getAsString (aTDD);
      assertNotNull (sXML);

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (aRes);
      assertNotNull (aSVRL);
      assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
    }
  }

  @Test
  public void testCreateFromAllCreditNotes () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolUAETDDValidator.getSchematronUAE_TDD_10 ();

    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllGoodBillingCreditNoteFiles ())
    {
      LOGGER.info ("Converting CreditNote '" + aRes.getPath () + "' to a TDD");

      final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ().read (aRes);
      assertNotNull (aCreditNote);

      final TaxDataType aTDD = new PeppolViDATDD090Builder ().documentTypeCode (EViDATDDDocumentTypeCode.SUBMIT)
                                                           .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                           .reporterRole (EViDATDDReporterRole.SENDER)
                                                           .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0235:c1id"))
                                                           .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                           .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                           // Read from pre-parsed UBL CreditNote
                                                           .reportedTransaction (rt -> rt.transportHeaderID ("my-sbdh-uuid-12345678")
                                                                                         .initFromCreditNote (aCreditNote))
                                                           .build ();
      assertNotNull (aTDD);

      // Serialize and XSD validate
      final String sXML = new PeppolViDATDD090Marshaller ().setFormattedOutput (true).getAsString (aTDD);
      assertNotNull (sXML);

      if (false)
        LOGGER.info (sXML);

      // Schematron validation
      final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (aRes);
      assertNotNull (aSVRL);
      assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
    }
  }

  @Test
  public void testCreateFailedInvoiceWithReportedDocument () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolUAETDDValidator.getSchematronUAE_TDD_10 ();

    final ClassPathResource aRes = PeppolViDATestFiles.getAllGoodBillingInvoiceFiles ().getFirstOrNull ();
    LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

    final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
    assertNotNull (aInvoice);

    final TaxDataType aTDD = new PeppolViDATDD090Builder ().documentTypeCode (EViDATDDDocumentTypeCode.FAILED)
                                                         .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                         .reporterRole (EViDATDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0235:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         // It's not really an invalid invoice
                                                         .reportedTransaction (rt -> rt.transportHeaderID ("my-sbdh-uuid-12345678")
                                                                                       .initFromInvoice (aInvoice))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolViDATDD090Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    assertTrue (sXML.contains ("<pxs:ReportedDocument>"));

    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (aRes);
    assertNotNull (aSVRL);
    assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
  }

  @Test
  public void testCreateFailedInvoiceWithoutReportedDocument () throws Exception
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final ISchematronResource aSCHRes = PeppolUAETDDValidator.getSchematronUAE_TDD_10 ();

    final ClassPathResource aRes = PeppolViDATestFiles.getAllGoodBillingInvoiceFiles ().getFirstOrNull ();
    LOGGER.info ("Converting Invoice '" + aRes.getPath () + "' to a TDD");

    final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aRes);
    assertNotNull (aInvoice);

    // Explicitly sets a "null" CustomizationID to indicate an invalid source message
    aInvoice.setCustomizationID ((CustomizationIDType) null);
    // This one is special, because it is an XSD mandatory fields
    aInvoice.setID ((IDType) null);

    final TaxDataType aTDD = new PeppolViDATDD090Builder ().documentTypeCode (EViDATDDDocumentTypeCode.FAILED)
                                                         .documentScope (EViDATDDDocumentScope.DOMESTIC)
                                                         .reporterRole (EViDATDDReporterRole.SENDER)
                                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0235:c1id"))
                                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:c5id"))
                                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:987654"))
                                                         // This Invoice is really broken
                                                         .reportedTransaction (rt -> rt.transportHeaderID ("my-sbdh-uuid-12345678")
                                                                                       .initFromInvoice (aInvoice))
                                                         .build ();
    assertNotNull (aTDD);

    // Serialize
    final String sXML = new PeppolViDATDD090Marshaller ().setFormattedOutput (true).getAsString (aTDD);
    assertNotNull (sXML);
    assertFalse (sXML.contains ("<pxs:ReportedDocument>"));

    if (false)
      LOGGER.info (sXML);

    // Schematron validation
    final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (aRes);
    assertNotNull (aSVRL);
    assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
  }

  @Test
  public void testReadBadPayloads () throws Exception
  {
    final ISchematronResource aSCHRes = PeppolUAETDDValidator.getSchematronUAE_TDD_10 ();

    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllPayloadBadTDD090Files ())
    {
      LOGGER.info ("Reading Bad Payload TDD '" + aRes.getPath () + "'");

      final TaxDataType aTDD = new PeppolViDATDD090Marshaller ().read (aRes);
      assertNotNull (aTDD);

      // Serialize
      final String sXML = new PeppolViDATDD090Marshaller ().setFormattedOutput (true).getAsString (aTDD);
      assertNotNull (sXML);
      assertFalse (sXML.contains ("<pxs:ReportedDocument>"));

      if (true)
        LOGGER.info (sXML);

      // Schematron validation
      final SchematronOutputType aSVRL = aSCHRes.applySchematronValidationToSVRL (aRes);
      assertNotNull (aSVRL);
      assertEquals (new CommonsArrayList <> (), SVRLHelper.getAllFailedAssertions (aSVRL));
    }
  }
}
