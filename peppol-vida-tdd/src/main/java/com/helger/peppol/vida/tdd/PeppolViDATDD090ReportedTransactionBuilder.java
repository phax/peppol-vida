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
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.datetime.helper.PDTFactory;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.datetime.xml.XMLOffsetTime;
import com.helger.peppol.vida.tdd.codelist.EViDATDDDocumentTypeCode;
import com.helger.peppol.vida.tdd.v090.TaxDataType.ReportedTransaction;
import com.helger.peppol.vida.tdd.v090.TaxDataType.ReportedTransaction.ReportedDocument;
import com.helger.peppol.vida.tdd.v090.TaxDataType.ReportedTransaction.ReportedDocument.DocumentLine;
import com.helger.peppol.vida.tdd.v090.TaxDataType.ReportedTransaction.ReportedDocument.MonetaryTotal;
import com.helger.peppol.vida.tdd.v090.cac.AccountingCustomerParty;
import com.helger.peppol.vida.tdd.v090.cac.AccountingSupplierParty;
import com.helger.peppol.vida.tdd.v090.cac.AllowanceCharge;
import com.helger.peppol.vida.tdd.v090.cac.BillingReference;
import com.helger.peppol.vida.tdd.v090.cac.Country;
import com.helger.peppol.vida.tdd.v090.cac.Delivery;
import com.helger.peppol.vida.tdd.v090.cac.InvoicePeriod;
import com.helger.peppol.vida.tdd.v090.cac.Party;
import com.helger.peppol.vida.tdd.v090.cac.PartyTaxScheme;
import com.helger.peppol.vida.tdd.v090.cac.PostalAddress;
import com.helger.peppol.vida.tdd.v090.cac.TaxRepresentativeParty;
import com.helger.peppol.vida.tdd.v090.cac.TaxScheme;
import com.helger.peppol.vida.tdd.v090.cac.TaxTotal;
import com.helger.peppol.vida.tdd.v090.cbc.AllowanceTotalAmount;
import com.helger.peppol.vida.tdd.v090.cbc.ChargeTotalAmount;
import com.helger.peppol.vida.tdd.v090.cbc.LineExtensionAmount;
import com.helger.peppol.vida.tdd.v090.cbc.PayableAmount;
import com.helger.peppol.vida.tdd.v090.cbc.PayableRoundingAmount;
import com.helger.peppol.vida.tdd.v090.cbc.PrepaidAmount;
import com.helger.peppol.vida.tdd.v090.cbc.TaxAmount;
import com.helger.peppol.vida.tdd.v090.cbc.TaxExclusiveAmount;
import com.helger.peppol.vida.tdd.v090.cbc.TaxInclusiveAmount;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DeliveryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyTaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Builder for Peppol ViDA pilot TDD 0.9.0 sub element called "ReportedTransaction".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD090ReportedTransactionBuilder implements IBuilder <ReportedTransaction>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD090ReportedTransactionBuilder.class);

  // TODO await clarifications
  public static final class ViDADocumentReference
  {
    private String m_sIDScheme;
    private String m_sID;
    private LocalDate m_aIssueDate;
  }

  private final EViDATDDDocumentTypeCode m_eDocumentTypeCode;
  private String m_sCustomizationID;
  private String m_sProfileID;
  private String m_sID;
  private String m_sUUID;
  private LocalDate m_aIssueDate;
  private OffsetTime m_aIssueTime;
  private String m_sDocumentTypeCode;
  private String m_sNote;
  private LocalDate m_aTaxPointDate;
  private String m_sDocumentCurrencyCode;
  private String m_sTaxCurrencyCode;
  private LocalDate m_aInvoicePeriodStart;
  private LocalDate m_aInvoicePeriodEnd;
  private String m_sInvoicePeriodDescriptionCode;
  private final ICommonsList <BillingReference> m_aBillingReferences = new CommonsArrayList <> ();
  private String m_sSellerTaxID;
  private String m_sSellerCountryCode;
  private String m_sBuyerTaxID;
  private String m_sBuyerCountryCode;
  private String m_sTaxRepresentativeID;
  private String m_sTaxRepresentativeCountryCode;
  private LocalDate m_aDeliveryDate;
  // TODO PaymentMeans
  private final ICommonsList <AllowanceCharge> m_aAllowanceCharges = new CommonsArrayList <> ();
  // TODO TaxTotal complex
  private BigDecimal m_aTaxTotalAmountDocumentCurrency;
  private BigDecimal m_aTaxTotalAmountTaxCurrency;

  private BigDecimal m_aLineExtensionAmount;
  private BigDecimal m_aTaxExclusiveTotalAmount;
  private BigDecimal m_aTaxInclusiveTotalAmount;
  private BigDecimal m_aAllowanceTotalAmount;
  private BigDecimal m_aChargeTotalAmount;
  private BigDecimal m_aPrepaidAmount;
  private BigDecimal m_aPayableRoundingAmount;
  private BigDecimal m_aPayableAmount;

  private final ICommonsList <DocumentLine> m_aDocumentLines = new CommonsArrayList <> ();

  public PeppolViDATDD090ReportedTransactionBuilder (@NonNull final EViDATDDDocumentTypeCode eDocumentTypeCode)
  {
    ValueEnforcer.notNull (eDocumentTypeCode, "DocumentTypeCode");
    m_eDocumentTypeCode = eDocumentTypeCode;
  }

  /**
   * Set all fields from the provided UBL 2.1 Invoice
   *
   * @param aInv
   *        The Invoice to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder initFromInvoice (@NonNull final InvoiceType aInv)
  {
    ValueEnforcer.notNull (aInv, "Invoice");

    customizationID (aInv.getCustomizationIDValue ());
    profileID (aInv.getProfileIDValue ());
    id (aInv.getIDValue ());
    uuid (aInv.getUUIDValue ());
    issueDate (aInv.getIssueDateValueLocal ());
    issueTime (aInv.getIssueTimeValue ());
    documentTypeCode (aInv.getInvoiceTypeCodeValue ());
    if (aInv.hasNoteEntries ())
      note (aInv.getNoteAtIndex (0).getValue ());
    documentCurrencyCode (aInv.getDocumentCurrencyCodeValue ());
    taxCurrencyCode (aInv.getTaxCurrencyCodeValue ());

    if (aInv.hasInvoicePeriodEntries ())
    {
      final PeriodType aIP = aInv.getInvoicePeriodAtIndex (0);
      invoicePeriodStart (aIP.getStartDateValueLocal ());
      invoicePeriodEnd (aIP.getEndDateValueLocal ());
      if (aIP.hasDescriptionCodeEntries ())
        invoicePeriodDescriptionCode (aIP.getDescriptionAtIndex (0).getValue ());
    }

    for (final var aBR : aInv.getBillingReference ())
    {
      final DocumentReferenceType aDocRef = aBR.getInvoiceDocumentReference ();
      final IDType aID = aDocRef.getID ();
      addBillingReference (x -> x.id (aID == null ? null : aID.getValue ())
                                 .idScheme (aID == null ? null : aID.getSchemeID ())
                                 .issueDate (aDocRef.getIssueDateValueLocal ()));
    }

    final SupplierPartyType aSupplier = aInv.getAccountingSupplierParty ();
    if (aSupplier != null)
    {
      final PartyType aParty = aSupplier.getParty ();
      if (aParty != null)
      {
        if (aParty.hasPartyTaxSchemeEntries ())
        {
          final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
          sellerTaxID (aPTS.getCompanyIDValue ());
        }

        final AddressType aPA = aParty.getPostalAddress ();
        if (aPA != null && aPA.getCountry () != null)
          sellerCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
      }
    }

    final CustomerPartyType aCustomer = aInv.getAccountingCustomerParty ();
    if (aCustomer != null)
    {
      final PartyType aParty = aCustomer.getParty ();
      if (aParty != null)
      {
        if (aParty.hasPartyTaxSchemeEntries ())
        {
          final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
          buyerTaxID (aPTS.getCompanyIDValue ());
        }

        final AddressType aPA = aParty.getPostalAddress ();
        if (aPA != null && aPA.getCountry () != null)
          buyerCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
      }
    }

    final PartyType aTaxRep = aInv.getTaxRepresentativeParty ();
    if (aTaxRep != null)
    {
      if (aTaxRep.hasPartyTaxSchemeEntries ())
      {
        final PartyTaxSchemeType aPTS = aTaxRep.getPartyTaxSchemeAtIndex (0);
        taxRepresentativeID (aPTS.getCompanyIDValue ());
      }

      final AddressType aPA = aTaxRep.getPostalAddress ();
      if (aPA != null && aPA.getCountry () != null)
        taxRepresentativeCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
    }

    if (aInv.hasDeliveryEntries ())
    {
      final DeliveryType aDelivery = aInv.getDeliveryAtIndex (0);
      deliveryDate (aDelivery.getActualDeliveryDateValueLocal ());
    }

    for (final var aAC : aInv.getAllowanceCharge ())
      addAllowanceCharge (x -> x.initFromUBL (aAC));

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
      lineExtensionAmount (aLegalMonetaryTotal.getLineExtensionAmountValue ());
      taxExclusiveTotalAmount (aLegalMonetaryTotal.getTaxExclusiveAmountValue ());
      taxInclusiveTotalAmount (aLegalMonetaryTotal.getTaxInclusiveAmountValue ());
      allowanceTotalAmount (aLegalMonetaryTotal.getAllowanceTotalAmountValue ());
      chargeTotalAmount (aLegalMonetaryTotal.getChargeTotalAmountValue ());
      prepaidAmount (aLegalMonetaryTotal.getPrepaidAmountValue ());
      payableRoundingAmount (aLegalMonetaryTotal.getPayableRoundingAmountValue ());
      payableAmount (aLegalMonetaryTotal.getPayableAmountValue ());
    }

    for (final var aLine : aInv.getInvoiceLine ())
      addDocumentLine (x -> x.initFromInvoice (aLine));

    return this;
  }

  /**
   * Set all fields from the provided UBL 2.1 CreditNote
   *
   * @param aCN
   *        The CreditNote to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder initFromCreditNote (@NonNull final CreditNoteType aCN)
  {
    ValueEnforcer.notNull (aCN, "Invoice");

    customizationID (aCN.getCustomizationIDValue ());
    profileID (aCN.getProfileIDValue ());
    id (aCN.getIDValue ());
    uuid (aCN.getUUIDValue ());
    issueDate (aCN.getIssueDateValueLocal ());
    issueTime (aCN.getIssueTimeValue ());
    documentTypeCode (aCN.getCreditNoteTypeCodeValue ());
    if (aCN.hasNoteEntries ())
      note (aCN.getNoteAtIndex (0).getValue ());
    documentCurrencyCode (aCN.getDocumentCurrencyCodeValue ());
    taxCurrencyCode (aCN.getTaxCurrencyCodeValue ());

    if (aCN.hasInvoicePeriodEntries ())
    {
      final PeriodType aIP = aCN.getInvoicePeriodAtIndex (0);
      invoicePeriodStart (aIP.getStartDateValueLocal ());
      invoicePeriodEnd (aIP.getEndDateValueLocal ());
      if (aIP.hasDescriptionCodeEntries ())
        invoicePeriodDescriptionCode (aIP.getDescriptionAtIndex (0).getValue ());
    }

    for (final var aBR : aCN.getBillingReference ())
    {
      final DocumentReferenceType aDocRef = aBR.getInvoiceDocumentReference ();
      final IDType aID = aDocRef.getID ();
      addBillingReference (x -> x.id (aID == null ? null : aID.getValue ())
                                 .idScheme (aID == null ? null : aID.getSchemeID ())
                                 .issueDate (aDocRef.getIssueDateValueLocal ()));
    }

    final SupplierPartyType aSupplier = aCN.getAccountingSupplierParty ();
    if (aSupplier != null)
    {
      final PartyType aParty = aSupplier.getParty ();
      if (aParty != null)
      {
        if (aParty.hasPartyTaxSchemeEntries ())
        {
          final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
          sellerTaxID (aPTS.getCompanyIDValue ());
        }

        final AddressType aPA = aParty.getPostalAddress ();
        if (aPA != null && aPA.getCountry () != null)
          sellerCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
      }
    }

    final CustomerPartyType aCustomer = aCN.getAccountingCustomerParty ();
    if (aCustomer != null)
    {
      final PartyType aParty = aCustomer.getParty ();
      if (aParty != null)
      {
        if (aParty.hasPartyTaxSchemeEntries ())
        {
          final PartyTaxSchemeType aPTS = aParty.getPartyTaxSchemeAtIndex (0);
          buyerTaxID (aPTS.getCompanyIDValue ());
        }

        final AddressType aPA = aParty.getPostalAddress ();
        if (aPA != null && aPA.getCountry () != null)
          buyerCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
      }
    }

    final PartyType aTaxRep = aCN.getTaxRepresentativeParty ();
    if (aTaxRep != null)
    {
      if (aTaxRep.hasPartyTaxSchemeEntries ())
      {
        final PartyTaxSchemeType aPTS = aTaxRep.getPartyTaxSchemeAtIndex (0);
        taxRepresentativeID (aPTS.getCompanyIDValue ());
      }

      final AddressType aPA = aTaxRep.getPostalAddress ();
      if (aPA != null && aPA.getCountry () != null)
        taxRepresentativeCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
    }

    if (aCN.hasDeliveryEntries ())
    {
      final DeliveryType aDelivery = aCN.getDeliveryAtIndex (0);
      deliveryDate (aDelivery.getActualDeliveryDateValueLocal ());
    }

    for (final var aAC : aCN.getAllowanceCharge ())
      addAllowanceCharge (x -> x.initFromUBL (aAC));

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
      lineExtensionAmount (aLegalMonetaryTotal.getLineExtensionAmountValue ());
      taxExclusiveTotalAmount (aLegalMonetaryTotal.getTaxExclusiveAmountValue ());
      taxInclusiveTotalAmount (aLegalMonetaryTotal.getTaxInclusiveAmountValue ());
      allowanceTotalAmount (aLegalMonetaryTotal.getAllowanceTotalAmountValue ());
      chargeTotalAmount (aLegalMonetaryTotal.getChargeTotalAmountValue ());
      prepaidAmount (aLegalMonetaryTotal.getPrepaidAmountValue ());
      payableRoundingAmount (aLegalMonetaryTotal.getPayableRoundingAmountValue ());
      payableAmount (aLegalMonetaryTotal.getPayableAmountValue ());
    }

    for (final var aLine : aCN.getCreditNoteLine ())
      addDocumentLine (x -> x.initFromCreditNote (aLine));

    return this;
  }

  @Nullable
  public String customizationID ()
  {
    return m_sCustomizationID;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder customizationID (@Nullable final String s)
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
  public PeppolViDATDD090ReportedTransactionBuilder profileID (@Nullable final String s)
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
  public PeppolViDATDD090ReportedTransactionBuilder id (@Nullable final String s)
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
  public PeppolViDATDD090ReportedTransactionBuilder uuid (@Nullable final String s)
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
  public PeppolViDATDD090ReportedTransactionBuilder issueDate (@Nullable final LocalDate a)
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
  public PeppolViDATDD090ReportedTransactionBuilder issueTime (@Nullable final XMLOffsetTime a)
  {
    return issueTime (a == null ? null : a.toOffsetTime ());
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder issueTime (@Nullable final OffsetTime a)
  {
    // XSD can only handle milliseconds
    m_aIssueTime = PDTFactory.getWithMillisOnly (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder issueDateTime (@Nullable final OffsetDateTime a)
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
  public PeppolViDATDD090ReportedTransactionBuilder documentTypeCode (@Nullable final String s)
  {
    m_sDocumentTypeCode = s;
    return this;
  }

  @Nullable
  public String note ()
  {
    return m_sNote;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder note (@Nullable final String s)
  {
    m_sNote = s;
    return this;
  }

  @Nullable
  public LocalDate taxPointDate ()
  {
    return m_aTaxPointDate;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder taxPointDate (@Nullable final LocalDate a)
  {
    m_aTaxPointDate = a;
    return this;
  }

  @Nullable
  public String documentCurrencyCode ()
  {
    return m_sDocumentCurrencyCode;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder documentCurrencyCode (@Nullable final String s)
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
  public PeppolViDATDD090ReportedTransactionBuilder taxCurrencyCode (@Nullable final String s)
  {
    m_sTaxCurrencyCode = s;
    return this;
  }

  @Nullable
  public LocalDate invoicePeriodStart ()
  {
    return m_aInvoicePeriodStart;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder invoicePeriodStart (@Nullable final LocalDate a)
  {
    m_aInvoicePeriodStart = a;
    return this;
  }

  @Nullable
  public LocalDate invoicePeriodEnd ()
  {
    return m_aInvoicePeriodEnd;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder invoicePeriodEnd (@Nullable final LocalDate a)
  {
    m_aInvoicePeriodEnd = a;
    return this;
  }

  @Nullable
  public String invoicePeriodDescriptionCode ()
  {
    return m_sInvoicePeriodDescriptionCode;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder invoicePeriodDescriptionCode (@Nullable final String s)
  {
    m_sInvoicePeriodDescriptionCode = s;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <BillingReference> billingReferences ()
  {
    return m_aBillingReferences;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder billingReferences (@Nullable final ICommonsList <BillingReference> a)
  {
    m_aBillingReferences.setAll (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder addBillingReference (@Nullable final BillingReference a)
  {
    if (a != null)
      m_aBillingReferences.add (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder addBillingReference (@Nullable final PeppolViDATDD090BillingReferenceBuilder a)
  {
    return addBillingReference (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder addBillingReference (@NonNull final Consumer <PeppolViDATDD090BillingReferenceBuilder> aBuilderConsumer)
  {
    final PeppolViDATDD090BillingReferenceBuilder aBuilder = new PeppolViDATDD090BillingReferenceBuilder ();
    aBuilderConsumer.accept (aBuilder);
    return addBillingReference (aBuilder);
  }

  @Nullable
  public String sellerTaxID ()
  {
    return m_sSellerTaxID;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder sellerTaxID (@Nullable final String s)
  {
    m_sSellerTaxID = s;
    return this;
  }

  @Nullable
  public String sellerCountryCode ()
  {
    return m_sSellerCountryCode;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder sellerCountryCode (@Nullable final String s)
  {
    m_sSellerCountryCode = s;
    return this;
  }

  @Nullable
  public String buyerTaxID ()
  {
    return m_sBuyerTaxID;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder buyerTaxID (@Nullable final String s)
  {
    m_sBuyerTaxID = s;
    return this;
  }

  @Nullable
  public String buyerCountryCode ()
  {
    return m_sBuyerCountryCode;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder buyerCountryCode (@Nullable final String s)
  {
    m_sBuyerCountryCode = s;
    return this;
  }

  @Nullable
  public String taxRepresentativeID ()
  {
    return m_sTaxRepresentativeID;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder taxRepresentativeID (@Nullable final String s)
  {
    m_sTaxRepresentativeID = s;
    return this;
  }

  @Nullable
  public String taxRepresentativeCountryCode ()
  {
    return m_sTaxRepresentativeCountryCode;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder taxRepresentativeCountryCode (@Nullable final String s)
  {
    m_sTaxRepresentativeCountryCode = s;
    return this;
  }

  @Nullable
  public LocalDate deliveryDate ()
  {
    return m_aDeliveryDate;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder deliveryDate (@Nullable final LocalDate a)
  {
    m_aDeliveryDate = a;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <AllowanceCharge> allowanceCharges ()
  {
    return m_aAllowanceCharges;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder allowanceCharges (@Nullable final ICommonsList <AllowanceCharge> a)
  {
    m_aAllowanceCharges.setAll (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder addAllowanceCharge (@Nullable final AllowanceCharge a)
  {
    if (a != null)
      m_aAllowanceCharges.add (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder addAllowanceCharge (@Nullable final PeppolViDATDD090AllowanceChargeBuilder a)
  {
    return addAllowanceCharge (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder addAllowanceCharge (@NonNull final Consumer <PeppolViDATDD090AllowanceChargeBuilder> aBuilderConsumer)
  {
    final PeppolViDATDD090AllowanceChargeBuilder aBuilder = new PeppolViDATDD090AllowanceChargeBuilder (m_sDocumentCurrencyCode);
    aBuilderConsumer.accept (aBuilder);
    return addAllowanceCharge (aBuilder);
  }

  @Nullable
  public BigDecimal taxTotalAmountDocumentCurrency ()
  {
    return m_aTaxTotalAmountDocumentCurrency;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder taxTotalAmountDocumentCurrency (@Nullable final BigDecimal a)
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
  public PeppolViDATDD090ReportedTransactionBuilder taxTotalAmountTaxCurrency (@Nullable final BigDecimal a)
  {
    m_aTaxTotalAmountTaxCurrency = a;
    return this;
  }

  @Nullable
  public BigDecimal lineExtensionAmount ()
  {
    return m_aLineExtensionAmount;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder lineExtensionAmount (@Nullable final BigDecimal a)
  {
    m_aLineExtensionAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal taxExclusiveTotalAmount ()
  {
    return m_aTaxExclusiveTotalAmount;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder taxExclusiveTotalAmount (@Nullable final BigDecimal a)
  {
    m_aTaxExclusiveTotalAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal taxInclusiveTotalAmount ()
  {
    return m_aTaxInclusiveTotalAmount;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder taxInclusiveTotalAmount (@Nullable final BigDecimal a)
  {
    m_aTaxInclusiveTotalAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal allowanceTotalAmount ()
  {
    return m_aAllowanceTotalAmount;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder allowanceTotalAmount (@Nullable final BigDecimal a)
  {
    m_aAllowanceTotalAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal chargeTotalAmount ()
  {
    return m_aChargeTotalAmount;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder chargeTotalAmount (@Nullable final BigDecimal a)
  {
    m_aChargeTotalAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal prepaidAmount ()
  {
    return m_aPrepaidAmount;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder prepaidAmount (@Nullable final BigDecimal a)
  {
    m_aPrepaidAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal payableRoundingAmount ()
  {
    return m_aPayableRoundingAmount;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder payableRoundingAmount (@Nullable final BigDecimal a)
  {
    m_aPayableRoundingAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal payableAmount ()
  {
    return m_aPayableAmount;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder payableAmount (@Nullable final BigDecimal a)
  {
    m_aPayableAmount = a;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <DocumentLine> documentLines ()
  {
    return m_aDocumentLines;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder documentLines (@Nullable final ICommonsList <DocumentLine> a)
  {
    m_aDocumentLines.setAll (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder addDocumentLine (@Nullable final DocumentLine a)
  {
    if (a != null)
      m_aDocumentLines.add (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder addDocumentLine (@Nullable final PeppolViDATDD090DocumentLineBuilder a)
  {
    return addDocumentLine (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD090ReportedTransactionBuilder addDocumentLine (@NonNull final Consumer <PeppolViDATDD090DocumentLineBuilder> aBuilderConsumer)
  {
    if (StringHelper.isEmpty (m_sDocumentCurrencyCode))
      throw new IllegalStateException ("The DocumentLine can only be built, after the DocumentCurrencyCode is set!");
    final PeppolViDATDD090DocumentLineBuilder aBuilder = new PeppolViDATDD090DocumentLineBuilder (m_sDocumentCurrencyCode);
    aBuilderConsumer.accept (aBuilder);
    return addDocumentLine (aBuilder);
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    int nErrs = 0;
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 0.9.0 ReportedTransaction builder: ";

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
    // Note is optional
    // TaxPointDate is optional
    if (StringHelper.isEmpty (m_sDocumentCurrencyCode))
    {
      aCondLog.error (sErrorPrefix + "DocumentCurrencyCode is missing");
      aReportedDocsErrs.inc ();
    }
    // InvoicePeriod is optional

    // m_aBillingReferences may be empty

    // m_sSellerTaxID is optional
    // m_sSellerCountryCode is optional

    // m_sBuyerTaxID is optional
    // m_sBuyerCountryCode is optional

    // m_sTaxRepresentativeID is optional
    // m_sTaxRepresentativeCountryCode is optional

    // m_aDeliveryDate is optional

    // m_aAllowanceCharges may be empty

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
    if (m_aLineExtensionAmount == null)
    {
      aCondLog.error (sErrorPrefix + "LineExtensionAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxExclusiveTotalAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxExclusiveTotalAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxInclusiveTotalAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxInclusiveTotalAmount is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aAllowanceTotalAmount is optional
    // m_aChargeTotalAmount is optional
    // m_aPrepaidAmount is optional
    // m_aPayableRoundingAmount is optional
    if (m_aPayableAmount == null)
    {
      aCondLog.error (sErrorPrefix + "PayableAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aDocumentLines.isEmpty ())
    {
      aCondLog.error (sErrorPrefix + "At least one DocumentLine is needed");
      aReportedDocsErrs.inc ();
    }

    // Failed TDDs don't need this
    // TODO missing in 0.9.0
    // if (m_eDocumentTypeCode != EViDATDDDocumentTypeCode.DISREGARD)
    nErrs += aReportedDocsErrs.intValue ();

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

    // ReportedDocument - optional for FAILED state
    if (m_eDocumentTypeCode != EViDATDDDocumentTypeCode.DISREGARD || aReportedDocErrs.is0 ())
    {
      final ReportedDocument a = new ReportedDocument ();
      if (StringHelper.isNotEmpty (m_sCustomizationID))
        a.setCustomizationID (m_sCustomizationID);
      if (StringHelper.isNotEmpty (m_sProfileID))
        a.setProfileID (m_sProfileID);
      a.setID (m_sID);
      if (StringHelper.isNotEmpty (m_sUUID))
        a.setUUID (m_sUUID);
      if (m_aIssueDate != null)
        a.setIssueDate (XMLOffsetDate.of (m_aIssueDate));
      if (m_aIssueTime != null)
        a.setIssueTime (XMLOffsetTime.of (m_aIssueTime));
      if (StringHelper.isNotEmpty (m_sDocumentTypeCode))
        a.setDocumentTypeCode (m_sDocumentTypeCode);
      if (StringHelper.isNotEmpty (m_sNote))
        a.setNote (m_sNote);
      if (m_aTaxPointDate != null)
        a.setTaxPointDate (XMLOffsetDate.of (m_aTaxPointDate));
      if (StringHelper.isNotEmpty (m_sDocumentCurrencyCode))
        a.setDocumentCurrencyCode (m_sDocumentCurrencyCode);
      if (StringHelper.isNotEmpty (m_sTaxCurrencyCode))
        a.setTaxCurrencyCode (m_sTaxCurrencyCode);

      if (m_aInvoicePeriodStart != null ||
          m_aInvoicePeriodEnd != null ||
          StringHelper.isNotEmpty (m_sInvoicePeriodDescriptionCode))
      {
        final InvoicePeriod aIP = new InvoicePeriod ();
        if (m_aInvoicePeriodStart != null)
          aIP.setStartDate (XMLOffsetDate.of (m_aInvoicePeriodStart));
        if (m_aInvoicePeriodEnd != null)
          aIP.setEndDate (XMLOffsetDate.of (m_aInvoicePeriodEnd));
        if (StringHelper.isNotEmpty (m_sInvoicePeriodDescriptionCode))
          aIP.setDescriptionCode (m_sInvoicePeriodDescriptionCode);
        a.setInvoicePeriod (aIP);
      }

      a.setBillingReference (m_aBillingReferences);

      {
        final AccountingSupplierParty a2 = new AccountingSupplierParty ();
        {
          final Party aParty = new Party ();
          {
            if (StringHelper.isNotEmpty (m_sSellerCountryCode))
            {
              final PostalAddress aPA = new PostalAddress ();
              final Country aC = new Country ();
              aC.setIdentificationCode (m_sSellerCountryCode);
              aPA.setCountry (aC);
              aParty.addPostalAddress (aPA);
            }

            if (StringHelper.isNotEmpty (m_sSellerTaxID))
            {
              final PartyTaxScheme aPTS = new PartyTaxScheme ();
              aPTS.setCompanyID (m_sSellerTaxID);
              final TaxScheme aTS = new TaxScheme ();
              aTS.setID ("VAT");
              aPTS.setTaxScheme (aTS);
              aParty.setPartyTaxScheme (aPTS);
            }
          }
          a2.setParty (aParty);
        }
        a.setAccountingSupplierParty (a2);
      }

      {
        final AccountingCustomerParty aAccountingCustomer = new AccountingCustomerParty ();
        {
          final Party aParty = new Party ();
          if (StringHelper.isNotEmpty (m_sBuyerCountryCode))
          {
            final PostalAddress aPA = new PostalAddress ();
            final Country aC = new Country ();
            aC.setIdentificationCode (m_sBuyerCountryCode);
            aPA.setCountry (aC);
            aParty.addPostalAddress (aPA);
          }

          if (StringHelper.isNotEmpty (m_sBuyerTaxID))
          {
            final PartyTaxScheme aPTS = new PartyTaxScheme ();
            aPTS.setCompanyID (m_sBuyerTaxID);
            final TaxScheme aTS = new TaxScheme ();
            aTS.setID ("VAT");
            aPTS.setTaxScheme (aTS);
            aParty.setPartyTaxScheme (aPTS);
          }
          aAccountingCustomer.setParty (aParty);
        }
        a.setAccountingCustomerParty (aAccountingCustomer);
      }

      if (StringHelper.isNotEmpty (m_sTaxRepresentativeID) || StringHelper.isNotEmpty (m_sTaxRepresentativeCountryCode))
      {
        final TaxRepresentativeParty aTaxRep = new TaxRepresentativeParty ();
        if (StringHelper.isNotEmpty (m_sTaxRepresentativeCountryCode))
        {
          final PostalAddress aPA = new PostalAddress ();
          final Country aC = new Country ();
          aC.setIdentificationCode (m_sTaxRepresentativeCountryCode);
          aPA.setCountry (aC);
          aTaxRep.addPostalAddress (aPA);
        }

        if (StringHelper.isNotEmpty (m_sTaxRepresentativeID))
        {
          final PartyTaxScheme aPTS = new PartyTaxScheme ();
          aPTS.setCompanyID (m_sTaxRepresentativeID);
          final TaxScheme aTS = new TaxScheme ();
          aTS.setID ("VAT");
          aPTS.setTaxScheme (aTS);
          aTaxRep.setPartyTaxScheme (aPTS);
        }
        a.setTaxRepresentativeParty (aTaxRep);
      }

      if (m_aDeliveryDate != null)
      {
        final Delivery aDel = new Delivery ();
        aDel.setActualDeliveryDate (XMLOffsetDate.of (m_aDeliveryDate));
        a.setDelivery (aDel);
      }

      a.setAllowanceCharge (m_aAllowanceCharges);

      {
        final TaxTotal aTaxTotal = new TaxTotal ();
        final TaxAmount aTaxAmount = new TaxAmount ();
        aTaxAmount.setValue (m_aTaxTotalAmountDocumentCurrency);
        aTaxAmount.setCurrencyID (m_sDocumentCurrencyCode);
        aTaxTotal.setTaxAmount (aTaxAmount);
        a.addTaxTotal (aTaxTotal);
      }

      if (m_aTaxTotalAmountTaxCurrency != null)
      {
        final TaxTotal aTaxTotal = new TaxTotal ();
        final TaxAmount aTaxAmount = new TaxAmount ();
        aTaxAmount.setValue (m_aTaxTotalAmountTaxCurrency);
        aTaxAmount.setCurrencyID (m_sTaxCurrencyCode);
        aTaxTotal.setTaxAmount (aTaxAmount);
        a.addTaxTotal (aTaxTotal);
      }

      {
        final MonetaryTotal aMonetaryTotal = new MonetaryTotal ();
        {
          final LineExtensionAmount aAmount = new LineExtensionAmount (m_aLineExtensionAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setLineExtensionAmount (aAmount);
        }
        {
          final TaxExclusiveAmount aAmount = new TaxExclusiveAmount (m_aTaxExclusiveTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setTaxExclusiveAmount (aAmount);
        }
        {
          final TaxInclusiveAmount aAmount = new TaxInclusiveAmount (m_aTaxInclusiveTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setTaxInclusiveAmount (aAmount);
        }
        if (m_aAllowanceTotalAmount != null)
        {
          final AllowanceTotalAmount aAmount = new AllowanceTotalAmount (m_aAllowanceTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setAllowanceTotalAmount (aAmount);
        }
        if (m_aChargeTotalAmount != null)
        {
          final ChargeTotalAmount aAmount = new ChargeTotalAmount (m_aChargeTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setChargeTotalAmount (aAmount);
        }
        if (m_aPrepaidAmount != null)
        {
          final PrepaidAmount aAmount = new PrepaidAmount (m_aPrepaidAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setPrepaidAmount (aAmount);
        }
        if (m_aPayableRoundingAmount != null)
        {
          final PayableRoundingAmount aAmount = new PayableRoundingAmount (m_aPayableRoundingAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setPayableRoundingAmount (aAmount);
        }
        {
          final PayableAmount aAmount = new PayableAmount (m_aPayableAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setPayableAmount (aAmount);
        }
        a.setMonetaryTotal (aMonetaryTotal);
      }
      // Set all lines
      a.setDocumentLine (m_aDocumentLines);
      ret.setReportedDocument (a);
    }

    return ret;
  }
}
