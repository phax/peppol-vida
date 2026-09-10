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

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.diagnostics.error.IError;
import com.helger.io.resource.inmemory.ReadableResourceString;
import com.helger.peppol.vida.tdd.codelist.EViDATDDDocumentScope;
import com.helger.peppol.vida.tdd.codelist.EViDATDDReporterRole;
import com.helger.peppol.vida.tdd.codelist.EViDATDDTaxDataTypeCode;
import com.helger.peppol.vida.tdd.jaxb.PeppolViDATDD100Marshaller;
import com.helger.peppol.vida.tdd.testfiles.PeppolViDAPilotTestData;
import com.helger.peppol.vida.tdd.testfiles.PeppolViDATestFiles;
import com.helger.peppol.vida.tdd.v2026_03_18.ReportedDocumentType;
import com.helger.peppol.vida.tdd.v2026_03_18.TaxDataType;
import com.helger.peppol.vida.tdd.validate.PeppolViDATDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.ubl21.UBL21Marshaller;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TaxExemptionReasonCodeType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test class that creates TDDs from the source documents of the OpenPeppol ViDA Pilot Testing
 * repository (https://github.com/OpenPEPPOL/vida-pilot-testing/) and compares the created
 * ReportedDocument with the official non-normative sample TDD.
 *
 * @author Philip Helger
 */
public final class PeppolViDATDD100PilotTestDataFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD100PilotTestDataFuncTest.class);

  /**
   * The official sample TDDs do not contain the optional Tax Exemption Reason (BT-120) and Tax
   * Exemption Reason Code (BT-121) of the source document, whereas this library copies them. Remove
   * them, so that the remaining content can be compared 1:1.
   *
   * @param aRepDoc
   *        The ReportedDocument to modify. May not be <code>null</code>.
   */
  private static void _removeTaxExemptionReason (@NonNull final ReportedDocumentType aRepDoc)
  {
    for (final TaxTotalType aTT : aRepDoc.getTaxTotal ())
      for (final TaxSubtotalType aTS : aTT.getTaxSubtotal ())
      {
        final TaxCategoryType aTC = aTS.getTaxCategory ();
        if (aTC != null)
        {
          aTC.setTaxExemptionReason (null);
          aTC.setTaxExemptionReasonCode ((TaxExemptionReasonCodeType) null);
        }
      }
  }

  @NonNull
  private static PeppolViDATDD100Builder _createBuilder ()
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    // The TDD envelope data is not derived from the source document, so it is irrelevant for the
    // comparison of the ReportedDocument
    return new PeppolViDATDD100Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.SUBMIT)
                                         .documentScope (EViDATDDDocumentScope.INTRA_COMMUNITY)
                                         .reporterRole (EViDATDDReporterRole.RECEIVER)
                                         .reportingParty (aIF.createParticipantIdentifierWithDefaultScheme ("9913:001110-vidapilot.test"))
                                         .receivingParty (aIF.createParticipantIdentifierWithDefaultScheme ("0242:001111-vidapilot.test"))
                                         .reportersRepresentative (aIF.createParticipantIdentifierWithDefaultScheme ("0242:001110-vidapilot"))
                                         .taxAuthorityID ("XX");
  }

  @Test
  public void testCreateFromAllPilotTestData ()
  {
    for (final PeppolViDAPilotTestData aTestData : PeppolViDATestFiles.getAllPilotTestData ())
    {
      LOGGER.info ("Converting '" + aTestData.getSourceRes ().getPath () + "' to a TDD");

      final PeppolViDATDD100Builder aBuilder = _createBuilder ();
      if (aTestData.isCreditNote ())
      {
        final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ().read (aTestData.getSourceRes ());
        assertNotNull (aCreditNote);
        aBuilder.reportedTransaction (rt -> rt.initFromCreditNote (aCreditNote));
      }
      else
      {
        final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aTestData.getSourceRes ());
        assertNotNull (aInvoice);
        aBuilder.reportedTransaction (rt -> rt.initFromInvoice (aInvoice));
      }

      final TaxDataType aTDD = aBuilder.build ();
      assertNotNull (aTDD);

      // Schematron validation
      final String sXML = new PeppolViDATDD100Marshaller ().setFormattedOutput (true).getAsString (aTDD);
      assertNotNull (sXML);
      final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_100 (new ReadableResourceString (sXML,
                                                                                                                 StandardCharsets.UTF_8));
      assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                  aVRL.getOverallValidity ().isValid ());

      // Compare with the official sample TDD
      final TaxDataType aSampleTDD = new PeppolViDATDD100Marshaller ().read (aTestData.getSampleTDDRes ());
      assertNotNull (aSampleTDD);

      // Read back the created TDD, so that both objects use the same representation of empty
      // element lists
      final TaxDataType aOurTDD = new PeppolViDATDD100Marshaller ().read (new ReadableResourceString (sXML,
                                                                                                      StandardCharsets.UTF_8));
      assertNotNull (aOurTDD);

      final ReportedDocumentType aOurRepDoc = aOurTDD.getReportedTransactionAtIndex (0).getReportedDocument ();
      _removeTaxExemptionReason (aOurRepDoc);
      assertEquals (aTestData.getScenarioID () + " " + aTestData.getJurisdictions (),
                    aSampleTDD.getReportedTransactionAtIndex (0).getReportedDocument (),
                    aOurRepDoc);
    }
  }
}
