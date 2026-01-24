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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.string.StringHelper;
import com.helger.datetime.helper.PDTFactory;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.datetime.xml.XMLOffsetTime;
import com.helger.peppol.vida.tdd.codelist.EViDATDDDocumentScope;
import com.helger.peppol.vida.tdd.codelist.EViDATDDDocumentTypeCode;
import com.helger.peppol.vida.tdd.codelist.EViDATDDReporterRole;
import com.helger.peppol.vida.tdd.v090.TaxDataType;
import com.helger.peppol.vida.tdd.v090.TaxDataType.ReceivingParty;
import com.helger.peppol.vida.tdd.v090.TaxDataType.ReportedTransaction;
import com.helger.peppol.vida.tdd.v090.TaxDataType.ReportersRepresentative;
import com.helger.peppol.vida.tdd.v090.TaxDataType.ReportingParty;
import com.helger.peppol.vida.tdd.v090.TaxDataType.TaxAuthority;
import com.helger.peppol.vida.tdd.v090.cac.PartyIdentification;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;

/**
 * Builder for Peppol ViDA pilot TDD 0.9.0 document.
 *
 * @author Philip Helger
 */
public class PeppolViDATDD090Builder implements IBuilder <TaxDataType>
{
  // TODO bug in the 0.9.0 specification
  public static final String DEFAULT_CUSTOMIZATION_ID = true ? "urn:peppol:schema:taxdata:1.0::TaxData##urn:peppol:taxdata:ViDA-1::1.0"
                                                             : "urn:peppol:taxdata:ViDA-1";
  public static final String DEFAULT_PROFILE_ID = "urn:peppol:taxreporting";

  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD090Builder.class);

  private String m_sCustomizationID;
  private String m_sProfileID;
  private String m_sUUID;
  private LocalDate m_aIssueDate;
  private OffsetTime m_aIssueTime;
  private EViDATDDDocumentTypeCode m_eDocumentTypeCode;
  private EViDATDDDocumentScope m_eDocumentScope;
  private EViDATDDReporterRole m_eReporterRole;
  private String m_sTaxAuthorityID;
  private String m_sTaxAuthorityName;
  private IParticipantIdentifier m_aReportingParty;
  private IParticipantIdentifier m_aReceivingParty;
  private IParticipantIdentifier m_aReportersRepresentative;
  private ReportedTransaction m_aReportedTransaction;

  public PeppolViDATDD090Builder ()
  {
    customizationID (DEFAULT_CUSTOMIZATION_ID);
    profileID (DEFAULT_PROFILE_ID);
    randomUUID ();
    issueDateTimeNow ();
  }

  @Nullable
  public String customizationID ()
  {
    return m_sCustomizationID;
  }

  @NonNull
  public PeppolViDATDD090Builder customizationID (@Nullable final String s)
  {
    m_sCustomizationID = s;
    return this;
  }

  @Nullable
  public String profileID ()
  {
    return m_sProfileID;
  }

  @NonNull
  public PeppolViDATDD090Builder profileID (@Nullable final String s)
  {
    m_sProfileID = s;
    return this;
  }

  @Nullable
  public String uuid ()
  {
    return m_sUUID;
  }

  @NonNull
  public PeppolViDATDD090Builder uuid (@Nullable final String s)
  {
    m_sUUID = s;
    return this;
  }

  @NonNull
  public PeppolViDATDD090Builder randomUUID ()
  {
    return uuid (UUID.randomUUID ().toString ());
  }

  @Nullable
  public LocalDate issueDate ()
  {
    return m_aIssueDate;
  }

  @NonNull
  public PeppolViDATDD090Builder issueDateNow ()
  {
    return issueDate (PDTFactory.getCurrentLocalDate ());
  }

  @NonNull
  public PeppolViDATDD090Builder issueDate (@Nullable final LocalDate a)
  {
    m_aIssueDate = a;
    return this;
  }

  @Nullable
  public OffsetTime issueTime ()
  {
    return m_aIssueTime;
  }

  @NonNull
  public PeppolViDATDD090Builder issueTimeNow ()
  {
    return issueTime (PDTFactory.getCurrentOffsetTime ());
  }

  @NonNull
  public PeppolViDATDD090Builder issueTime (@Nullable final OffsetTime a)
  {
    // XSD can only handle milliseconds
    m_aIssueTime = PDTFactory.getWithMillisOnly (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090Builder issueDateTime (@Nullable final OffsetDateTime a)
  {
    if (a == null)
      return issueDate (null).issueTime (null);
    return issueDate (a.toLocalDate ()).issueTime (a.toOffsetTime ());
  }

  @NonNull
  public PeppolViDATDD090Builder issueDateTimeNow ()
  {
    return issueDateTime (PDTFactory.getCurrentOffsetDateTime ());
  }

  @Nullable
  public EViDATDDDocumentTypeCode documentTypeCode ()
  {
    return m_eDocumentTypeCode;
  }

  @NonNull
  public PeppolViDATDD090Builder documentTypeCode (@Nullable final EViDATDDDocumentTypeCode e)
  {
    m_eDocumentTypeCode = e;
    return this;
  }

  @Nullable
  public EViDATDDDocumentScope documentScope ()
  {
    return m_eDocumentScope;
  }

  @NonNull
  public PeppolViDATDD090Builder documentScope (@Nullable final EViDATDDDocumentScope e)
  {
    m_eDocumentScope = e;
    return this;
  }

  @Nullable
  public EViDATDDReporterRole reporterRole ()
  {
    return m_eReporterRole;
  }

  @NonNull
  public PeppolViDATDD090Builder reporterRole (@Nullable final EViDATDDReporterRole e)
  {
    m_eReporterRole = e;
    return this;
  }

  @Nullable
  public String taxAuthorityID ()
  {
    return m_sTaxAuthorityID;
  }

  @NonNull
  public PeppolViDATDD090Builder taxAuthorityID (@Nullable final String s)
  {
    m_sTaxAuthorityID = s;
    return this;
  }

  @Nullable
  public String taxAuthorityName ()
  {
    return m_sTaxAuthorityName;
  }

  @NonNull
  public PeppolViDATDD090Builder taxAuthorityName (@Nullable final String s)
  {
    m_sTaxAuthorityName = s;
    return this;
  }

  @Nullable
  public IParticipantIdentifier reportingParty ()
  {
    return m_aReportingParty;
  }

  /**
   * @param a
   *        Peppol Participant ID of C1/C4 of the business document.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD090Builder reportingParty (@Nullable final IParticipantIdentifier a)
  {
    m_aReportingParty = a;
    return this;
  }

  @Nullable
  public IParticipantIdentifier receivingParty ()
  {
    return m_aReceivingParty;
  }

  /**
   * @param a
   *        Peppol Participant ID of C5 of the TDD.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD090Builder receivingParty (@Nullable final IParticipantIdentifier a)
  {
    m_aReceivingParty = a;
    return this;
  }

  @Nullable
  public IParticipantIdentifier reportersRepresentative ()
  {
    return m_aReportersRepresentative;
  }

  /**
   * @param a
   *        Peppol Participant ID of C2/C3 of the business document. Must use the SPIS scheme.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD090Builder reportersRepresentative (@Nullable final IParticipantIdentifier a)
  {
    m_aReportersRepresentative = a;
    return this;
  }

  @Nullable
  public ReportedTransaction reportedTransaction ()
  {
    return m_aReportedTransaction;
  }

  @NonNull
  public PeppolViDATDD090Builder reportedTransaction (@NonNull final Consumer <PeppolViDATDD090ReportedTransactionBuilder> aBuilderConsumer)
  {
    if (m_eDocumentTypeCode == null)
      throw new IllegalStateException ("The ReportedTransaction can only be built, after the DocumentTypeCode is set!");
    final PeppolViDATDD090ReportedTransactionBuilder aBuilder = new PeppolViDATDD090ReportedTransactionBuilder (m_eDocumentTypeCode);
    aBuilderConsumer.accept (aBuilder);
    return reportedTransaction (aBuilder.build ());
  }

  @NonNull
  public PeppolViDATDD090Builder reportedTransaction (@Nullable final ReportedTransaction a)
  {
    m_aReportedTransaction = a;
    return this;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    int nErrs = 0;
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 0.9.0 builder: ";

    if (StringHelper.isEmpty (m_sCustomizationID))
    {
      aCondLog.error (sErrorPrefix + "CustomizationID is missing");
      nErrs++;
    }
    if (StringHelper.isEmpty (m_sProfileID))
    {
      aCondLog.error (sErrorPrefix + "ProfileID is missing");
      nErrs++;
    }
    if (StringHelper.isEmpty (m_sUUID))
    {
      aCondLog.error (sErrorPrefix + "UUID is missing");
      nErrs++;
    }
    if (m_aIssueDate == null)
    {
      aCondLog.error (sErrorPrefix + "IssueDate is missing");
      nErrs++;
    }
    if (m_aIssueTime == null)
    {
      aCondLog.error (sErrorPrefix + "IssueTime is missing");
      nErrs++;
    }
    if (m_eDocumentTypeCode == null)
    {
      aCondLog.error (sErrorPrefix + "DocumentTypeCode is missing");
      nErrs++;
    }
    if (m_eDocumentScope == null)
    {
      aCondLog.error (sErrorPrefix + "DocumentScope is missing");
      nErrs++;
    }
    if (m_eReporterRole == null)
    {
      aCondLog.error (sErrorPrefix + "ReporterRole is missing");
      nErrs++;
    }

    if (StringHelper.isEmpty (m_sTaxAuthorityID))
    {
      aCondLog.error (sErrorPrefix + "TaxAuthority ID is missing");
      nErrs++;
    }
    // m_sTaxAuthorityName is optional

    if (m_aReportingParty == null)
    {
      aCondLog.error (sErrorPrefix + "ReportingParty is missing");
      nErrs++;
    }
    else
      if (!aIF.isParticipantIdentifierSchemeValid (m_aReportingParty.getScheme ()))
      {
        aCondLog.error (sErrorPrefix +
                        "ReportingParty identifier scheme '" +
                        m_aReportingParty.getScheme () +
                        "' is invalid");
        nErrs++;
      }
      else
        if (!aIF.isParticipantIdentifierValueValid (m_aReportingParty.getScheme (), m_aReportingParty.getValue ()))
        {
          aCondLog.error (sErrorPrefix +
                          "ReportingParty identifier value '" +
                          m_aReportingParty.getValue () +
                          "' is invalid for scheme '" +
                          m_aReportingParty.getScheme () +
                          "'");
          nErrs++;
        }

    if (m_aReceivingParty == null)
    {
      aCondLog.error (sErrorPrefix + "ReceivingParty is missing");
      nErrs++;
    }
    else
      if (!aIF.isParticipantIdentifierSchemeValid (m_aReceivingParty.getScheme ()))
      {
        aCondLog.error (sErrorPrefix +
                        "ReceivingParty identifier scheme '" +
                        m_aReceivingParty.getScheme () +
                        "' is invalid");
        nErrs++;
      }
      else
        if (!aIF.isParticipantIdentifierValueValid (m_aReceivingParty.getScheme (), m_aReceivingParty.getValue ()))
        {
          aCondLog.error (sErrorPrefix +
                          "ReceivingParty identifier value '" +
                          m_aReceivingParty.getValue () +
                          "' is invalid for scheme '" +
                          m_aReceivingParty.getScheme () +
                          "'");
          nErrs++;
        }
        else
        {
          final String [] aParts = StringHelper.getExplodedArray (':', m_aReceivingParty.getValue (), 2);
          if (!"0242".equals (aParts[0]))
          {
            aCondLog.error (sErrorPrefix +
                            "ReceivingParty identifier value '" +
                            m_aReceivingParty.getValue () +
                            "' must use the 0242 identifier scheme");
            nErrs++;
          }
        }

    if (m_aReportersRepresentative == null)
    {
      aCondLog.error (sErrorPrefix + "ReportersRepresentative is missing");
      nErrs++;
    }
    else
      if (!aIF.isParticipantIdentifierSchemeValid (m_aReportersRepresentative.getScheme ()))
      {
        aCondLog.error (sErrorPrefix +
                        "ReportersRepresentative identifier meta scheme '" +
                        m_aReportersRepresentative.getScheme () +
                        "' is invalid");
        nErrs++;
      }
      else
        if (!aIF.isParticipantIdentifierValueValid (m_aReportersRepresentative.getScheme (),
                                                    m_aReportersRepresentative.getValue ()))
        {
          aCondLog.error (sErrorPrefix +
                          "ReportersRepresentative identifier value '" +
                          m_aReportersRepresentative.getValue () +
                          "' is invalid for meta scheme '" +
                          m_aReportersRepresentative.getScheme () +
                          "'");
          nErrs++;
        }
        else
        {
          final String [] aParts = StringHelper.getExplodedArray (':', m_aReportersRepresentative.getValue (), 2);
          if (!"0242".equals (aParts[0]))
          {
            aCondLog.error (sErrorPrefix +
                            "ReportersRepresentative identifier value '" +
                            m_aReportersRepresentative.getValue () +
                            "' must use the 0242 identifier scheme");
            nErrs++;
          }
        }

    // ViDA must have exactly one reported transaction
    if (m_aReportedTransaction == null)
    {
      aCondLog.error (sErrorPrefix + "ReportedTransaction is missing");
      nErrs++;
    }

    return nErrs == 0;
  }

  @Nullable
  public TaxDataType build ()
  {
    if (!isEveryRequiredFieldSet (true))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD cannot be build.");
      return null;
    }

    final TaxDataType ret = new TaxDataType ();
    ret.setCustomizationID (m_sCustomizationID);
    ret.setProfileID (m_sProfileID);
    ret.setUUID (m_sUUID);
    ret.setIssueDate (XMLOffsetDate.of (m_aIssueDate));
    ret.setIssueTime (XMLOffsetTime.of (m_aIssueTime));
    ret.setDocumentTypeCode (m_eDocumentTypeCode.getID ());
    // Duplicate element
    ret.setDocumentCurrencyCode (m_aReportedTransaction.getReportedDocument ().getDocumentCurrencyCode ());
    ret.setDocumentScope (m_eDocumentScope.getID ());
    ret.setReporterRole (m_eReporterRole.getID ());
    {
      final TaxAuthority a = new TaxAuthority ();
      a.setID (m_sTaxAuthorityID);
      a.setName (m_sTaxAuthorityName);
      ret.setTaxAuthority (a);
    }
    {
      final String [] aParts = StringHelper.getExplodedArray (':', m_aReportingParty.getValue (), 2);
      final ReportingParty aParty = new ReportingParty ();
      aParty.setEndpointID (aParts[1]).setSchemeID (aParts[0]);
      ret.setReportingParty (aParty);
    }
    {
      final String [] aParts = StringHelper.getExplodedArray (':', m_aReceivingParty.getValue (), 2);
      final ReceivingParty aParty = new ReceivingParty ();
      aParty.setEndpointID (aParts[1]).setSchemeID (aParts[0]);
      ret.setReceivingParty (aParty);
    }
    {
      final String [] aParts = StringHelper.getExplodedArray (':', m_aReportersRepresentative.getValue (), 2);
      final ReportersRepresentative aParty = new ReportersRepresentative ();
      final PartyIdentification aPID = new PartyIdentification ();
      aPID.setID (aParts[1]).setSchemeID (aParts[0]);
      aParty.setPartyIdentification (aPID);
      ret.setReportersRepresentative (aParty);
    }
    ret.addReportedTransaction (m_aReportedTransaction);
    return ret;
  }
}
