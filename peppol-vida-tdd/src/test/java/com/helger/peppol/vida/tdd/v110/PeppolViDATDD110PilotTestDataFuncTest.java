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
import com.helger.peppol.vida.tdd.jaxb.PeppolViDATDD110Marshaller;
import com.helger.peppol.vida.tdd.testfiles.PeppolViDAPilotTestData;
import com.helger.peppol.vida.tdd.testfiles.PeppolViDATestFiles;
import com.helger.peppol.vida.tdd.v2026_09_14.ReportedTransactionType;
import com.helger.peppol.vida.tdd.v2026_09_14.TaxDataType;
import com.helger.peppol.vida.tdd.validate.PeppolViDATDDValidator;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.ubl21.UBL21Marshaller;

import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Test class that creates TDDs from the source documents of the OpenPeppol ViDA Pilot Testing
 * repository (https://github.com/OpenPEPPOL/vida-pilot-testing/) and compares the created
 * ReportedDocument with the official non-normative sample TDD.
 *
 * @author Philip Helger
 */
public final class PeppolViDATDD110PilotTestDataFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD110PilotTestDataFuncTest.class);

  @NonNull
  private static PeppolViDATDD110Builder _createBuilder ()
  {
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    // The TDD envelope data is not derived from the source document, so it is irrelevant for the
    // comparison of the ReportedDocument
    return new PeppolViDATDD110Builder ().taxDataTypeCode (EViDATDDTaxDataTypeCode.SUBMIT)
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

      // The official sample TDD is read first, because the Invoice Transmission UUID (TDT-018) is
      // not derived from the source document and must therefore be taken over to be able to
      // compare the results
      final TaxDataType aSampleTDD = new PeppolViDATDD110Marshaller ().read (aTestData.getSampleTDDRes ());
      assertNotNull (aSampleTDD);
      final ReportedTransactionType aSampleRT = aSampleTDD.getReportedTransactionAtIndex (0);
      final String sTransmissionUUID = aSampleRT.getTransmissionUUID ();

      final PeppolViDATDD110Builder aBuilder = _createBuilder ();
      if (aTestData.isCreditNote ())
      {
        final CreditNoteType aCreditNote = UBL21Marshaller.creditNote ().read (aTestData.getSourceRes ());
        assertNotNull (aCreditNote);
        aBuilder.reportedTransaction (rt -> rt.initFromCreditNote (aCreditNote).transmissionUUID (sTransmissionUUID));
      }
      else
      {
        final InvoiceType aInvoice = UBL21Marshaller.invoice ().read (aTestData.getSourceRes ());
        assertNotNull (aInvoice);
        aBuilder.reportedTransaction (rt -> rt.initFromInvoice (aInvoice).transmissionUUID (sTransmissionUUID));
      }

      final TaxDataType aTDD = aBuilder.build ();
      assertNotNull (aTDD);

      // Schematron validation
      final String sXML = new PeppolViDATDD110Marshaller ().setFormattedOutput (true).getAsString (aTDD);
      assertNotNull (sXML);
      final ValidationResultList aVRL = PeppolViDATDDValidator.validateViDA_TDD_110 (new ReadableResourceString (sXML,
                                                                                                                 StandardCharsets.UTF_8));
      assertTrue (aVRL.getAllErrors ().getAllMapped (IError::getAsStringLocaleIndepdent).toString (),
                  aVRL.getOverallValidity ().isValid ());

      // Read back the created TDD, so that both objects use the same representation of empty
      // element lists
      final TaxDataType aOurTDD = new PeppolViDATDD110Marshaller ().read (new ReadableResourceString (sXML,
                                                                                                      StandardCharsets.UTF_8));
      assertNotNull (aOurTDD);

      // Compare with the official sample TDD
      final ReportedTransactionType aOurRT = aOurTDD.getReportedTransactionAtIndex (0);
      assertEquals (aTestData.getScenarioID () + " " + aTestData.getJurisdictions (), aSampleRT, aOurRT);
    }
  }
}
