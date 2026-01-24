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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.annotation.Nonempty;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.datetime.helper.PDTFactory;
import com.helger.datetime.xml.XMLOffsetTime;
import com.helger.peppol.vida.tdd.codelist.EViDATDDDocumentTypeCode;
import com.helger.peppol.vida.tdd.v090.TaxDataType.ReportedTransaction;
import com.helger.xml.XMLHelper;
import com.helger.xsds.bdxr.smp2.bc.IDType;
import com.helger.xsds.bdxr.smp2.ec.ExtensionContentType;
import com.helger.xsds.bdxr.smp2.udt.ValueType;

/**
 * Builder for Peppol ViDA pilot TDD 0.9.0 sub element called "ReportedTransaction".
 *
 * @author Philip Helger
 */
public class PeppolUAETDD10ReportedTransactionBuilder implements IBuilder <ReportedTransaction>
{
  public static final class CustomContent
  {
    private final String m_sID;
    private final String m_sValue;

    public CustomContent (@NonNull @Nonempty final String sID, @NonNull @Nonempty final String sValue)
    {
      ValueEnforcer.notEmpty (sID, "ID");
      ValueEnforcer.isTrue ( () -> sID.equals (sID.toUpperCase (Locale.ROOT)),
                             () -> "ID '" + sID + "' must be all uppercase");
      ValueEnforcer.notEmpty (sValue, "Value");
      m_sID = sID;
      m_sValue = sValue;
    }

    @NonNull
    @Nonempty
    public String getID ()
    {
      return m_sID;
    }

    @NonNull
    @Nonempty
    public String getValue ()
    {
      return m_sValue;
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolUAETDD10ReportedTransactionBuilder.class);

  private final @NonNull EViDATDDDocumentTypeCode m_eDocumentTypeCode;
  private String m_sTransportHeaderID;
  private String m_sCustomizationID;
  private String m_sProfileID;
  private String m_sID;
  private String m_sUUID;
  private LocalDate m_aIssueDate;
  private OffsetTime m_aIssueTime;
  private String m_sDocumentTypeCode;
  private String m_sDocumentCurrencyCode;
  private String m_sTaxCurrencyCode;
  private String m_sSellerTaxID;
  private String m_sSellerTaxSchemeID;
  private String m_sBuyerID;
  private String m_sBuyerIDSchemeID;
  private String m_sBuyerTaxID;
  private BigDecimal m_aTaxTotalAmountDocumentCurrency;
  private BigDecimal m_aTaxTotalAmountTaxCurrency;
  private BigDecimal m_aTaxExclusiveTotalAmount;
  private final ICommonsList <CustomContent> m_aCustomContents = new CommonsArrayList <> ();
  private Element m_aSourceDocument;

  public PeppolUAETDD10ReportedTransactionBuilder (@NonNull final EViDATDDDocumentTypeCode eDocumentTypeCode)
  {
    ValueEnforcer.notNull (eDocumentTypeCode, "DocumentTypeCode");
    m_eDocumentTypeCode = eDocumentTypeCode;
  }

  @NonNull
  public static InvoiceType getWithoutEmbeddedDocumentBinaryObject (@NonNull final InvoiceType aInv)
  {
    final InvoiceType ret = aInv.clone ();
    for (final var aAddDocRef : ret.getAdditionalDocumentReference ())
      if (aAddDocRef.getAttachment () != null)
        aAddDocRef.getAttachment ().setEmbeddedDocumentBinaryObject ((EmbeddedDocumentBinaryObjectType) null);
    return ret;
  }

  @NonNull
  public static CreditNoteType getWithoutEmbeddedDocumentBinaryObject (@NonNull final CreditNoteType aCN)
  {
    final CreditNoteType ret = aCN.clone ();
    for (final var aAddDocRef : ret.getAdditionalDocumentReference ())
      if (aAddDocRef.getAttachment () != null)
        aAddDocRef.getAttachment ().setEmbeddedDocumentBinaryObject ((EmbeddedDocumentBinaryObjectType) null);
    return ret;
  }

  /**
   * Set all fields except the TransportHeaderID from the provided UBL 2.1 Invoice
   *
   * @param aInv
   *        The Invoice to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder initFromInvoice (@NonNull final InvoiceType aInv)
  {
    ValueEnforcer.notNull (aInv, "Invoice");

    customizationID (aInv.getCustomizationIDValue ());
    profileID (aInv.getProfileIDValue ());
    id (aInv.getIDValue ());
    uuid (aInv.getUUIDValue ());
    issueDate (aInv.getIssueDateValueLocal ());
    issueTime (aInv.getIssueTimeValue ());
    documentTypeCode (aInv.getInvoiceTypeCodeValue ());
    documentCurrencyCode (aInv.getDocumentCurrencyCodeValue ());
    taxCurrencyCode (aInv.getTaxCurrencyCodeValue ());

    final SupplierPartyType aSupplier = aInv.getAccountingSupplierParty ();
    if (aSupplier != null)
    {
      final PartyType aParty = aSupplier.getParty ();
      if (aParty != null && aParty.hasPartyTaxSchemeEntries ())
      {
        final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
        sellerTaxID (aPTS.getCompanyIDValue ());
        final TaxSchemeType aTS = aPTS.getTaxScheme ();
        if (aTS != null)
          sellerTaxSchemeID (aTS.getIDValue ());
      }
    }

    final CustomerPartyType aCustomer = aInv.getAccountingCustomerParty ();
    if (aCustomer != null)
    {
      final PartyType aParty = aCustomer.getParty ();
      if (aParty != null)
      {
        if (aParty.hasPartyIdentificationEntries ())
        {
          final PartyIdentificationType aPID = aParty.getPartyIdentificationAtIndex (0);
          final IDType aID = aPID.getID ();
          if (aID != null)
          {
            buyerID (aID.getValue ());
            buyerIDSchemeID (aID.getSchemeID ());
          }
        }

        if (aParty.hasPartyTaxSchemeEntries ())
        {
          final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
          buyerTaxID (aPTS.getCompanyIDValue ());
        }
      }
    }

    if (m_sDocumentCurrencyCode != null)
      taxTotalAmountDocumentCurrency (aInv.getTaxTotal ()
                                          .stream ()
                                          .filter (x -> m_sDocumentCurrencyCode.equals (x.getTaxAmount ()
                                                                                         .getCurrencyID ()))
                                          .map (TaxTotalType::getTaxAmountValue)
                                          .findFirst ()
                                          .orElse (null));
    if (m_sTaxCurrencyCode != null)
      taxTotalAmountTaxCurrency (aInv.getTaxTotal ()
                                     .stream ()
                                     .filter (x -> m_sTaxCurrencyCode.equals (x.getTaxAmount ().getCurrencyID ()))
                                     .map (TaxTotalType::getTaxAmountValue)
                                     .findFirst ()
                                     .orElse (null));

    final oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType aLegalMonetaryTotal = aInv.getLegalMonetaryTotal ();
    if (aLegalMonetaryTotal != null)
    {
      taxExclusiveTotalAmount (aLegalMonetaryTotal.getTaxExclusiveAmountValue ());
    }

    // Make sure to disable Schema validation, so that invalid documents can also be serialized
    // properly
    sourceDocument (UBL21Marshaller.invoice ()
                                   .setUseSchema (false)
                                   .getAsElement (getWithoutEmbeddedDocumentBinaryObject (aInv)));

    return this;
  }

  /**
   * Set all fields except the TransportHeaderID from the provided UBL 2.1 CreditNote
   *
   * @param aCN
   *        The CreditNote to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder initFromCreditNote (@NonNull final CreditNoteType aCN)
  {
    ValueEnforcer.notNull (aCN, "Invoice");

    customizationID (aCN.getCustomizationIDValue ());
    profileID (aCN.getProfileIDValue ());
    id (aCN.getIDValue ());
    uuid (aCN.getUUIDValue ());
    issueDate (aCN.getIssueDateValueLocal ());
    issueTime (aCN.getIssueTimeValue ());
    documentTypeCode (aCN.getCreditNoteTypeCodeValue ());
    documentCurrencyCode (aCN.getDocumentCurrencyCodeValue ());
    taxCurrencyCode (aCN.getTaxCurrencyCodeValue ());

    final SupplierPartyType aSupplier = aCN.getAccountingSupplierParty ();
    if (aSupplier != null)
    {
      final PartyType aParty = aSupplier.getParty ();
      if (aParty != null && aParty.hasPartyTaxSchemeEntries ())
      {
        final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
        sellerTaxID (aPTS.getCompanyIDValue ());
        final TaxSchemeType aTS = aPTS.getTaxScheme ();
        if (aTS != null)
          sellerTaxSchemeID (aTS.getIDValue ());
      }
    }

    final CustomerPartyType aCustomer = aCN.getAccountingCustomerParty ();
    if (aCustomer != null)
    {
      final PartyType aParty = aCustomer.getParty ();
      if (aParty != null)
      {
        if (aParty.hasPartyIdentificationEntries ())
        {
          final PartyIdentificationType aPID = aParty.getPartyIdentificationAtIndex (0);
          final IDType aID = aPID.getID ();
          if (aID != null)
          {
            buyerID (aID.getValue ());
            buyerIDSchemeID (aID.getSchemeID ());
          }
        }

        if (aParty.hasPartyTaxSchemeEntries ())
        {
          final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
          buyerTaxID (aPTS.getCompanyIDValue ());
        }
      }
    }

    if (m_sDocumentCurrencyCode != null)
      taxTotalAmountDocumentCurrency (aCN.getTaxTotal ()
                                         .stream ()
                                         .filter (x -> m_sDocumentCurrencyCode.equals (x.getTaxAmount ()
                                                                                        .getCurrencyID ()))
                                         .map (TaxTotalType::getTaxAmountValue)
                                         .findFirst ()
                                         .orElse (null));
    if (m_sTaxCurrencyCode != null)
      taxTotalAmountTaxCurrency (aCN.getTaxTotal ()
                                    .stream ()
                                    .filter (x -> m_sTaxCurrencyCode.equals (x.getTaxAmount ().getCurrencyID ()))
                                    .map (TaxTotalType::getTaxAmountValue)
                                    .findFirst ()
                                    .orElse (null));

    final oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType aLegalMonetaryTotal = aCN.getLegalMonetaryTotal ();
    if (aLegalMonetaryTotal != null)
    {
      taxExclusiveTotalAmount (aLegalMonetaryTotal.getTaxExclusiveAmountValue ());
    }

    // Make sure to disable Schema validation, so that invalid documents can also be serialized
    // properly
    sourceDocument (UBL21Marshaller.creditNote ()
                                   .setUseSchema (false)
                                   .getAsElement (getWithoutEmbeddedDocumentBinaryObject (aCN)));
    return this;
  }

  @Nullable
  public String transportHeaderID ()
  {
    return m_sTransportHeaderID;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder transportHeaderID (@Nullable final String s)
  {
    m_sTransportHeaderID = s;
    return this;
  }

  @Nullable
  public String customizationID ()
  {
    return m_sCustomizationID;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder customizationID (@Nullable final String s)
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
  public PeppolUAETDD10ReportedTransactionBuilder profileID (@Nullable final String s)
  {
    m_sProfileID = s;
    return this;
  }

  @Nullable
  public String id ()
  {
    return m_sID;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder id (@Nullable final String s)
  {
    m_sID = s;
    return this;
  }

  @Nullable
  public String uuid ()
  {
    return m_sUUID;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder uuid (@Nullable final String s)
  {
    m_sUUID = s;
    return this;
  }

  @Nullable
  public LocalDate issueDate ()
  {
    return m_aIssueDate;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder issueDate (@Nullable final LocalDate a)
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
  public PeppolUAETDD10ReportedTransactionBuilder issueTime (@Nullable final XMLOffsetTime a)
  {
    return issueTime (a == null ? null : a.toOffsetTime ());
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder issueTime (@Nullable final OffsetTime a)
  {
    // XSD can only handle milliseconds
    m_aIssueTime = PDTFactory.getWithMillisOnly (a);
    return this;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder issueDateTime (@Nullable final OffsetDateTime a)
  {
    if (a == null)
      return issueDate (null).issueTime ((OffsetTime) null);
    return issueDate (a.toLocalDate ()).issueTime (a.toOffsetTime ());
  }

  @Nullable
  public String documentTypeCode ()
  {
    return m_sDocumentTypeCode;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder documentTypeCode (@Nullable final String s)
  {
    m_sDocumentTypeCode = s;
    return this;
  }

  @Nullable
  public String documentCurrencyCode ()
  {
    return m_sDocumentCurrencyCode;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder documentCurrencyCode (@Nullable final String s)
  {
    m_sDocumentCurrencyCode = s;
    return this;
  }

  @Nullable
  public String taxCurrencyCode ()
  {
    return m_sTaxCurrencyCode;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder taxCurrencyCode (@Nullable final String s)
  {
    m_sTaxCurrencyCode = s;
    return this;
  }

  @Nullable
  public String sellerTaxID ()
  {
    return m_sSellerTaxID;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder sellerTaxID (@Nullable final String s)
  {
    m_sSellerTaxID = s;
    return this;
  }

  @Nullable
  public String sellerTaxSchemeID ()
  {
    return m_sSellerTaxSchemeID;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder sellerTaxSchemeID (@Nullable final String s)
  {
    m_sSellerTaxSchemeID = s;
    return this;
  }

  @Nullable
  public String buyerID ()
  {
    return m_sBuyerID;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder buyerID (@Nullable final String s)
  {
    m_sBuyerID = s;
    return this;
  }

  @Nullable
  public String buyerIDSchemeID ()
  {
    return m_sBuyerIDSchemeID;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder buyerIDSchemeID (@Nullable final String s)
  {
    m_sBuyerIDSchemeID = s;
    return this;
  }

  @Nullable
  public String buyerTaxID ()
  {
    return m_sBuyerTaxID;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder buyerTaxID (@Nullable final String s)
  {
    m_sBuyerTaxID = s;
    return this;
  }

  @Nullable
  public BigDecimal taxTotalAmountDocumentCurrency ()
  {
    return m_aTaxTotalAmountDocumentCurrency;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder taxTotalAmountDocumentCurrency (@Nullable final BigDecimal a)
  {
    m_aTaxTotalAmountDocumentCurrency = a;
    return this;
  }

  @Nullable
  public BigDecimal taxTotalAmountTaxCurrency ()
  {
    return m_aTaxTotalAmountTaxCurrency;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder taxTotalAmountTaxCurrency (@Nullable final BigDecimal a)
  {
    m_aTaxTotalAmountTaxCurrency = a;
    return this;
  }

  @Nullable
  public BigDecimal taxExclusiveTotalAmount ()
  {
    return m_aTaxExclusiveTotalAmount;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder taxExclusiveTotalAmount (@Nullable final BigDecimal a)
  {
    m_aTaxExclusiveTotalAmount = a;
    return this;
  }

  @Nullable
  public ICommonsList <CustomContent> customContents ()
  {
    return m_aCustomContents;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder addCustomContent (@Nullable final CustomContent a)
  {
    if (a != null)
      m_aCustomContents.add (a);
    return this;
  }

  @Nullable
  public Element sourceDocument ()
  {
    return m_aSourceDocument;
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder sourceDocument (@Nullable final Document a)
  {
    return sourceDocument (a == null ? null : a.getDocumentElement ());
  }

  @NonNull
  public PeppolUAETDD10ReportedTransactionBuilder sourceDocument (@Nullable final Element a)
  {
    m_aSourceDocument = a;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    int nErrs = 0;
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol UAE TDD 1.0 ReportedTransaction builder: ";
    final String sWarnPrefix = "Warning in Peppol UAE TDD 1.0 ReportedTransaction builder: ";

    // TransportHeaderID is optional

    // Check all ReportedDocument fields
    if (StringHelper.isEmpty (m_sCustomizationID))
    {
      aCondLog.error (sErrorPrefix + "CustomizationID is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sProfileID))
    {
      aCondLog.error (sErrorPrefix + "ProfileID is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sID))
    {
      aCondLog.error (sErrorPrefix + "ID is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sUUID))
    {
      aCondLog.error (sErrorPrefix + "UUID is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aIssueDate == null)
    {
      aCondLog.error (sErrorPrefix + "IssueDate is missing");
      aReportedDocsErrs.inc ();
    }
    // IssueTime is optional
    if (StringHelper.isEmpty (m_sDocumentTypeCode))
    {
      aCondLog.error (sErrorPrefix + "DocumentTypeCode is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sDocumentCurrencyCode))
    {
      aCondLog.error (sErrorPrefix + "DocumentCurrencyCode is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sSellerTaxID))
    {
      aCondLog.error (sErrorPrefix + "SellerTaxID is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sSellerTaxSchemeID))
    {
      aCondLog.error (sErrorPrefix + "SellerTaxSchemeID is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isNotEmpty (m_sBuyerIDSchemeID))
    {
      if (StringHelper.isEmpty (m_sBuyerID))
      {
        // Warning only
        aCondLog.warn (sWarnPrefix + "BuyerIDSchemeID can only be used if BuyerID is also present");
      }
    }
    if (m_aTaxTotalAmountDocumentCurrency == null)
    {
      aCondLog.error (sErrorPrefix + "TaxTotalAmountDocumentCurrency is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxTotalAmountTaxCurrency != null)
    {
      if (StringHelper.isEmpty (m_sTaxCurrencyCode))
      {
        aCondLog.error (sErrorPrefix +
                        "If TaxTotalAmountTaxCurrency is provided, TaxCurrencyCode must also be provided");
        aReportedDocsErrs.inc ();
      }
    }
    else
    {
      if (StringHelper.isNotEmpty (m_sTaxCurrencyCode))
      {
        aCondLog.error (sErrorPrefix +
                        "If TaxCurrencyCode is provided, TaxTotalAmountTaxCurrency must also be provided");
        aReportedDocsErrs.inc ();
      }
    }
    if (m_aTaxExclusiveTotalAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxExclusiveTotalAmount is missing");
      aReportedDocsErrs.inc ();
    }

    // Failed TDDs don't need this
    if (m_eDocumentTypeCode != EViDATDDDocumentTypeCode.FAILED)
      nErrs += aReportedDocsErrs.intValue ();

    if (m_aSourceDocument == null)
    {
      aCondLog.error (sErrorPrefix + "SourceDocument is missing");
      nErrs++;
    }
    else
    {
      final QName aQName = XMLHelper.getQName (m_aSourceDocument);
      if (!aQName.equals (UBL21Marshaller.invoice ().getRootElementQName ()) &&
          !aQName.equals (UBL21Marshaller.creditNote ().getRootElementQName ()))
      {
        aCondLog.error (sErrorPrefix + "SourceDocument must be a UBL 2.1 Invoice or CreditNote");
        nErrs++;
      }
    }

    return nErrs == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Nullable
  public ReportedTransaction build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD ReportedTransaction cannot be build.");
      return null;
    }

    final ReportedTransaction ret = new ReportedTransaction ();

    // TransportHeaderID
    if (StringHelper.isNotEmpty (m_sTransportHeaderID))
    {
      final TransportHeaderIDType a = new TransportHeaderIDType ();
      a.setValue (m_sTransportHeaderID);
      ret.setTransportHeaderID (a);
    }

    // ReportedDocument - optional for FAILED state
    if (m_eDocumentTypeCode != EViDATDDDocumentTypeCode.FAILED || aReportedDocErrs.is0 ())
    {
      final ReportedDocumentType a = new ReportedDocumentType ();
      if (StringHelper.isNotEmpty (m_sCustomizationID))
        a.setCustomizationID (new CustomizationIDType (m_sCustomizationID));
      if (StringHelper.isNotEmpty (m_sProfileID))
        a.setProfileID (new ProfileIDType (m_sProfileID));
      a.setID (new IDType (m_sID));
      if (StringHelper.isNotEmpty (m_sUUID))
        a.setUUID (new UUIDType (m_sUUID));
      if (m_aIssueDate != null)
        a.setIssueDate (new IssueDateType (m_aIssueDate));
      if (m_aIssueTime != null)
        a.setIssueTime (new IssueTimeType (XMLOffsetTime.of (m_aIssueTime)));
      if (StringHelper.isNotEmpty (m_sDocumentTypeCode))
      {
        final ReferencedDocumentTypeCodeType a2 = new ReferencedDocumentTypeCodeType ();
        a2.setValue (m_sDocumentTypeCode);
        a.setDocumentTypeCode (a2);
      }
      if (StringHelper.isNotEmpty (m_sDocumentCurrencyCode))
        a.setDocumentCurrencyCode (new DocumentCurrencyCodeType (m_sDocumentCurrencyCode));
      if (StringHelper.isNotEmpty (m_sTaxCurrencyCode))
        a.setTaxCurrencyCode (new TaxCurrencyCodeType (m_sTaxCurrencyCode));
      {
        final SupplierPartyType a2 = new SupplierPartyType ();
        {
          final PartyType aParty = new PartyType ();
          {
            final PartyTaxSchemeType aPTS = new PartyTaxSchemeType ();
            {
              aPTS.setCompanyID (m_sSellerTaxID);
              final TaxSchemeType aTS = new TaxSchemeType ();
              aTS.setID (m_sSellerTaxSchemeID);
              aPTS.setTaxScheme (aTS);
            }
            aParty.addPartyTaxScheme (aPTS);
          }
          a2.setParty (aParty);
        }
        a.setAccountingSupplierParty (a2);
      }
      {
        final CustomerPartyType aAccountingCustomer = new CustomerPartyType ();
        {
          final PartyType aParty = new PartyType ();
          if (StringHelper.isNotEmpty (m_sBuyerID))
          {
            final PartyIdentificationType aPI = new PartyIdentificationType ();
            final IDType aID = new IDType (m_sBuyerID);
            if (StringHelper.isNotEmpty (m_sBuyerIDSchemeID))
              aID.setSchemeID (m_sBuyerIDSchemeID);
            aPI.setID (aID);
            aParty.addPartyIdentification (aPI);
          }
          if (StringHelper.isNotEmpty (m_sBuyerTaxID))
          {
            final PartyTaxSchemeType aPTS = new PartyTaxSchemeType ();
            {
              aPTS.setCompanyID (m_sBuyerTaxID);
              // TaxScheme is mandatory
              aPTS.setTaxScheme (new TaxSchemeType ());
            }
            aParty.addPartyTaxScheme (aPTS);
          }
          aAccountingCustomer.setParty (aParty);
        }
        a.setAccountingCustomerParty (aAccountingCustomer);
      }
      {
        final TaxTotalType aTaxTotal = new TaxTotalType ();
        final TaxAmountType aTaxAmount = new TaxAmountType ();
        aTaxAmount.setValue (m_aTaxTotalAmountDocumentCurrency);
        aTaxAmount.setCurrencyID (m_sDocumentCurrencyCode);
        aTaxTotal.setTaxAmount (aTaxAmount);
        a.addTaxTotal (aTaxTotal);
      }
      if (m_aTaxTotalAmountTaxCurrency != null)
      {
        final TaxTotalType aTaxTotal = new TaxTotalType ();
        final TaxAmountType aTaxAmount = new TaxAmountType ();
        aTaxAmount.setValue (m_aTaxTotalAmountTaxCurrency);
        aTaxAmount.setCurrencyID (m_sTaxCurrencyCode);
        aTaxTotal.setTaxAmount (aTaxAmount);
        a.addTaxTotal (aTaxTotal);
      }
      {
        final MonetaryTotalType aMonetaryTotal = new MonetaryTotalType ();
        {
          final TaxExclusiveAmountType aTaxEx = new TaxExclusiveAmountType (m_aTaxExclusiveTotalAmount);
          aTaxEx.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setTaxExclusiveAmount (aTaxEx);
        }
        a.addMonetaryTotal (aMonetaryTotal);
      }
      ret.setReportedDocument (a);
    }

    for (final CustomContent aCC : m_aCustomContents)
    {
      final CustomContentType a = new CustomContentType ();
      a.setID (new IDType (aCC.m_sID));
      a.setValue (new ValueType (aCC.m_sValue));
      ret.addCustomContent (a);
    }

    {
      final UBLExtensionType aUBLExt = new UBLExtensionType ();
      final ExtensionContentType aExtContent = new ExtensionContentType ();
      aExtContent.setAny (m_aSourceDocument);
      aUBLExt.setExtensionContent (aExtContent);
      ret.setSourceDocument (aUBLExt);
    }

    return ret;
  }
}
