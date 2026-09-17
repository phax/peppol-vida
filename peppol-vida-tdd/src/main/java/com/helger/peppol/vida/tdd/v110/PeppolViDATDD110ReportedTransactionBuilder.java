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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.BigHelper;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.datetime.helper.PDTFactory;
import com.helger.datetime.xml.XMLOffsetDate;
import com.helger.datetime.xml.XMLOffsetTime;
import com.helger.peppol.vida.tdd.CViDATDD;
import com.helger.peppol.vida.tdd.codelist.EViDATDDReporterRole;
import com.helger.peppol.vida.tdd.codelist.EViDATDDTaxDataTypeCode;
import com.helger.peppol.vida.tdd.v2026_09_14.DocumentLineType;
import com.helger.peppol.vida.tdd.v2026_09_14.MonetaryTotalType;
import com.helger.peppol.vida.tdd.v2026_09_14.ReportedDocumentType;
import com.helger.peppol.vida.tdd.v2026_09_14.ReportedTransactionType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.BillingReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DeliveryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyLegalEntityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyTaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ChargeTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CustomizationIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DocumentCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.EndpointIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.LineExtensionAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PayableAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PayableRoundingAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PrepaidAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ProfileIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TaxCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TaxExclusiveAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TaxInclusiveAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.UUIDType;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

/**
 * Builder for Peppol ViDA pilot TDD 1.1.0 sub element called "ReportedTransaction".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD110ReportedTransactionBuilder implements IBuilder <ReportedTransactionType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD110ReportedTransactionBuilder.class);

  private final EViDATDDTaxDataTypeCode m_eDocumentTypeCode;
  private final EViDATDDReporterRole m_eReporterRole;
  // TDT-018
  private String m_sTransmissionUUID;
  private String m_sCustomizationID;
  private String m_sProfileID;
  private String m_sID;
  private LocalDate m_aIssueDate;
  private OffsetTime m_aIssueTime;
  private String m_sDocumentTypeCode;
  private String m_sNote;
  private String m_sDocumentCurrencyCode;
  private String m_sTaxCurrencyCode;
  private LocalDate m_aInvoicePeriodStart;
  private LocalDate m_aInvoicePeriodEnd;
  private String m_sInvoicePeriodDescriptionCode;
  private final ICommonsList <BillingReferenceType> m_aBillingReferences = new CommonsArrayList <> ();
  // BT-34
  private String m_sSellerEndpointIDSchemeID;
  private String m_sSellerEndpointID;
  // BT-29 - deprecated, neither part of the TDD nor an input of the TDT-017 calculation
  private String m_sSellerIDSchemeID;
  private String m_sSellerID;
  private String m_sSellerTaxID;
  private String m_sSellerCountryCode;
  private String m_sBuyerTaxID;
  private String m_sBuyerCountryCode;
  private String m_sBuyerName;
  private String m_sTaxRepresentativeID;
  private String m_sTaxRepresentativeCountryCode;
  private LocalDate m_aDeliveryDate;
  private final ICommonsList <PaymentMeansType> m_aPaymentMeans = new CommonsArrayList <> ();
  private final ICommonsList <AllowanceChargeType> m_aAllowanceCharges = new CommonsArrayList <> ();
  private TaxTotalType m_aTaxTotalDocumentCurrency;
  private TaxTotalType m_aTaxTotalTaxCurrency;

  private BigDecimal m_aLineExtensionAmount;
  private BigDecimal m_aTaxExclusiveTotalAmount;
  private BigDecimal m_aTaxInclusiveTotalAmount;
  private BigDecimal m_aAllowanceTotalAmount;
  private BigDecimal m_aChargeTotalAmount;
  private BigDecimal m_aPrepaidAmount;
  private BigDecimal m_aPayableRoundingAmount;
  private BigDecimal m_aPayableAmount;

  private final ICommonsList <DocumentLineType> m_aDocumentLines = new CommonsArrayList <> ();

  public PeppolViDATDD110ReportedTransactionBuilder (@NonNull final EViDATDDTaxDataTypeCode eDocumentTypeCode,
                                                    @NonNull final EViDATDDReporterRole eReporterRole)
  {
    ValueEnforcer.notNull (eDocumentTypeCode, "DocumentTypeCode");
    ValueEnforcer.notNull (eReporterRole, "ReporterRole");
    m_eDocumentTypeCode = eDocumentTypeCode;
    m_eReporterRole = eReporterRole;
  }

  /**
   * @return The Reporter role (TDT-012) this ReportedTransaction is built for. Never
   *         <code>null</code>. It determines whether the buy side VAT fields are mandatory.
   * @since 0.11.0
   */
  @NonNull
  public EViDATDDReporterRole reporterRole ()
  {
    return m_eReporterRole;
  }

  /**
   * @return The Invoice Transmission UUID (TDT-018). May be <code>null</code>.
   * @since 0.11.0
   */
  @Nullable
  public String transmissionUUID ()
  {
    return m_sTransmissionUUID;
  }

  /**
   * Set the Invoice Transmission UUID (TDT-018). This identifies one single exchange of the
   * reported Invoice or Credit Note across the network. It is not derived from the document
   * content, but taken from the transmission itself (e.g. the SBDH Instance Identifier), and must
   * therefore always be provided by the caller.
   *
   * @param s
   *        The Invoice Transmission UUID to use. May be <code>null</code>.
   * @return this for chaining
   * @since 0.11.0
   */
  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder transmissionUUID (@Nullable final String s)
  {
    m_sTransmissionUUID = s;
    return this;
  }

  /**
   * Set all fields from the provided UBL 2.1 Invoice
   *
   * @param aInv
   *        The Invoice to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder initFromInvoice (@NonNull final InvoiceType aInv)
  {
    ValueEnforcer.notNull (aInv, "Invoice");

    customizationID (aInv.getCustomizationIDValue ());
    profileID (aInv.getProfileIDValue ());
    id (aInv.getIDValue ());
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
        final EndpointIDType aEndpoint = aParty.getEndpointID ();
        if (aEndpoint != null)
        {
          sellerEndpointIDSchemeID (aEndpoint.getSchemeID ());
          sellerEndpointID (aEndpoint.getValue ());
        }

        // The Seller identifier (BT-29) is not part of the TDD and is no longer an input of the
        // Invoice UUID (TDT-017) calculation since TDD v1.1.0

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
        {
          // BT-55
          buyerCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
        }

        if (aParty.hasPartyLegalEntityEntries ())
        {
          final PartyLegalEntityType aPLE = aParty.getPartyLegalEntityAtIndex (0);
          // BT-44
          buyerName (aPLE.getRegistrationNameValue ());
        }
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

    for (final var aPM : aInv.getPaymentMeans ())
      addPaymentMeans (x -> x.initFromUBL (aPM));

    for (final var aAC : aInv.getAllowanceCharge ())
      addAllowanceCharge (x -> x.initFromUBL (aAC));

    if (m_sDocumentCurrencyCode != null)
    {
      final var aTT = aInv.getTaxTotal ()
                          .stream ()
                          .filter (x -> m_sDocumentCurrencyCode.equals (x.getTaxAmount ().getCurrencyID ()))
                          .findFirst ()
                          .orElse (null);
      if (aTT != null)
        taxTotalDocumentCurrency (x -> x.initFromUBL (aTT));
    }

    if (m_sTaxCurrencyCode != null)
    {
      final var aTT = aInv.getTaxTotal ()
                          .stream ()
                          .filter (x -> m_sTaxCurrencyCode.equals (x.getTaxAmount ().getCurrencyID ()))
                          .findFirst ()
                          .orElse (null);
      if (aTT != null)
        taxTotalTaxCurrency (x -> x.initFromUBL (aTT));
    }

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
  public PeppolViDATDD110ReportedTransactionBuilder initFromCreditNote (@NonNull final CreditNoteType aCN)
  {
    ValueEnforcer.notNull (aCN, "CreditNote");

    customizationID (aCN.getCustomizationIDValue ());
    profileID (aCN.getProfileIDValue ());
    id (aCN.getIDValue ());
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
        final EndpointIDType aEndpoint = aParty.getEndpointID ();
        if (aEndpoint != null)
        {
          sellerEndpointIDSchemeID (aEndpoint.getSchemeID ());
          sellerEndpointID (aEndpoint.getValue ());
        }

        // The Seller identifier (BT-29) is not part of the TDD and is no longer an input of the
        // Invoice UUID (TDT-017) calculation since TDD v1.1.0

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
        {
          // BT-55
          buyerCountryCode (aPA.getCountry ().getIdentificationCodeValue ());
        }

        if (aParty.hasPartyLegalEntityEntries ())
        {
          final PartyLegalEntityType aPLE = aParty.getPartyLegalEntityAtIndex (0);
          // BT-44
          buyerName (aPLE.getRegistrationNameValue ());
        }
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

    for (final var aPM : aCN.getPaymentMeans ())
      addPaymentMeans (x -> x.initFromUBL (aPM));

    for (final var aAC : aCN.getAllowanceCharge ())
      addAllowanceCharge (x -> x.initFromUBL (aAC));

    if (m_sDocumentCurrencyCode != null)
    {
      final var aTT = aCN.getTaxTotal ()
                         .stream ()
                         .filter (x -> m_sDocumentCurrencyCode.equals (x.getTaxAmount ().getCurrencyID ()))
                         .findFirst ()
                         .orElse (null);
      if (aTT != null)
        taxTotalDocumentCurrency (x -> x.initFromUBL (aTT));
    }

    if (m_sTaxCurrencyCode != null)
    {
      final var aTT = aCN.getTaxTotal ()
                         .stream ()
                         .filter (x -> m_sTaxCurrencyCode.equals (x.getTaxAmount ().getCurrencyID ()))
                         .findFirst ()
                         .orElse (null);
      if (aTT != null)
        taxTotalTaxCurrency (x -> x.initFromUBL (aTT));
    }

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
  public PeppolViDATDD110ReportedTransactionBuilder customizationID (@Nullable final String s)
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
  public PeppolViDATDD110ReportedTransactionBuilder profileID (@Nullable final String s)
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
  public PeppolViDATDD110ReportedTransactionBuilder id (@Nullable final String s)
  {
    m_sID = s;
    return this;
  }

  @Nullable
  public LocalDate issueDate ()
  {
    return m_aIssueDate;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder issueDate (@Nullable final LocalDate a)
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
  public PeppolViDATDD110ReportedTransactionBuilder issueTime (@Nullable final XMLOffsetTime a)
  {
    return issueTime (a == null ? null : a.toOffsetTime ());
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder issueTime (@Nullable final OffsetTime a)
  {
    // XSD can only handle milliseconds
    m_aIssueTime = PDTFactory.getWithMillisOnly (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder issueDateTime (@Nullable final OffsetDateTime a)
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
  public PeppolViDATDD110ReportedTransactionBuilder documentTypeCode (@Nullable final String s)
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
  public PeppolViDATDD110ReportedTransactionBuilder note (@Nullable final String s)
  {
    m_sNote = s;
    return this;
  }

  @Nullable
  public String documentCurrencyCode ()
  {
    return m_sDocumentCurrencyCode;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder documentCurrencyCode (@Nullable final String s)
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
  public PeppolViDATDD110ReportedTransactionBuilder taxCurrencyCode (@Nullable final String s)
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
  public PeppolViDATDD110ReportedTransactionBuilder invoicePeriodStart (@Nullable final LocalDate a)
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
  public PeppolViDATDD110ReportedTransactionBuilder invoicePeriodEnd (@Nullable final LocalDate a)
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
  public PeppolViDATDD110ReportedTransactionBuilder invoicePeriodDescriptionCode (@Nullable final String s)
  {
    m_sInvoicePeriodDescriptionCode = s;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <BillingReferenceType> billingReferences ()
  {
    return m_aBillingReferences;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder billingReferences (@Nullable final ICommonsList <BillingReferenceType> a)
  {
    m_aBillingReferences.setAll (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addBillingReference (@Nullable final BillingReferenceType a)
  {
    if (a != null)
      m_aBillingReferences.add (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addBillingReference (@Nullable final PeppolViDATDD110BillingReferenceBuilder a)
  {
    return addBillingReference (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addBillingReference (@NonNull final Consumer <PeppolViDATDD110BillingReferenceBuilder> a)
  {
    final PeppolViDATDD110BillingReferenceBuilder aBuilder = new PeppolViDATDD110BillingReferenceBuilder ();
    a.accept (aBuilder);
    return addBillingReference (aBuilder);
  }

  @Nullable
  public String sellerEndpointIDSchemeID ()
  {
    return m_sSellerEndpointIDSchemeID;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder sellerEndpointIDSchemeID (@Nullable final String s)
  {
    m_sSellerEndpointIDSchemeID = s;
    return this;
  }

  @Nullable
  public String sellerEndpointID ()
  {
    return m_sSellerEndpointID;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder sellerEndpointID (@Nullable final String s)
  {
    m_sSellerEndpointID = s;
    return this;
  }

  /**
   * @return The Seller identifier scheme (BT-29-1). May be <code>null</code>.
   * @deprecated Since 0.11.0 - the Seller identifier is not part of the TDD and, since TDD v1.1.0,
   *             no longer an input of the Invoice UUID (TDT-017) calculation. The value is ignored.
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  @Nullable
  public String sellerIDSchemeID ()
  {
    return m_sSellerIDSchemeID;
  }

  /**
   * @param s
   *        The Seller identifier scheme (BT-29-1) to use. May be <code>null</code>.
   * @return this for chaining
   * @deprecated Since 0.11.0 - the Seller identifier is not part of the TDD and, since TDD v1.1.0,
   *             no longer an input of the Invoice UUID (TDT-017) calculation. The value is ignored.
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder sellerIDSchemeID (@Nullable final String s)
  {
    m_sSellerIDSchemeID = s;
    return this;
  }

  /**
   * @return The Seller identifier (BT-29). May be <code>null</code>.
   * @deprecated Since 0.11.0 - the Seller identifier is not part of the TDD and, since TDD v1.1.0,
   *             no longer an input of the Invoice UUID (TDT-017) calculation. The value is ignored.
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  @Nullable
  public String sellerID ()
  {
    return m_sSellerID;
  }

  /**
   * @param s
   *        The Seller identifier (BT-29) to use. May be <code>null</code>.
   * @return this for chaining
   * @deprecated Since 0.11.0 - the Seller identifier is not part of the TDD and, since TDD v1.1.0,
   *             no longer an input of the Invoice UUID (TDT-017) calculation. The value is ignored.
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder sellerID (@Nullable final String s)
  {
    m_sSellerID = s;
    return this;
  }

  @Nullable
  public String sellerTaxID ()
  {
    return m_sSellerTaxID;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder sellerTaxID (@Nullable final String s)
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
  public PeppolViDATDD110ReportedTransactionBuilder sellerCountryCode (@Nullable final String s)
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
  public PeppolViDATDD110ReportedTransactionBuilder buyerTaxID (@Nullable final String s)
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
  public PeppolViDATDD110ReportedTransactionBuilder buyerCountryCode (@Nullable final String s)
  {
    m_sBuyerCountryCode = s;
    return this;
  }

  @Nullable
  public String buyerName ()
  {
    return m_sBuyerName;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder buyerName (@Nullable final String s)
  {
    m_sBuyerName = s;
    return this;
  }

  @Nullable
  public String taxRepresentativeID ()
  {
    return m_sTaxRepresentativeID;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxRepresentativeID (@Nullable final String s)
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
  public PeppolViDATDD110ReportedTransactionBuilder taxRepresentativeCountryCode (@Nullable final String s)
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
  public PeppolViDATDD110ReportedTransactionBuilder deliveryDate (@Nullable final LocalDate a)
  {
    m_aDeliveryDate = a;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <PaymentMeansType> paymentMeans ()
  {
    return m_aPaymentMeans;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder paymentMeans (@Nullable final ICommonsList <PaymentMeansType> a)
  {
    m_aPaymentMeans.setAll (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addPaymentMeans (@Nullable final PaymentMeansType a)
  {
    if (a != null)
      m_aPaymentMeans.add (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addPaymentMeans (@Nullable final PeppolViDATDD110PaymentMeansBuilder a)
  {
    return addPaymentMeans (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addPaymentMeans (@NonNull final Consumer <PeppolViDATDD110PaymentMeansBuilder> a)
  {
    final PeppolViDATDD110PaymentMeansBuilder aBuilder = new PeppolViDATDD110PaymentMeansBuilder ();
    a.accept (aBuilder);
    return addPaymentMeans (aBuilder);
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <AllowanceChargeType> allowanceCharges ()
  {
    return m_aAllowanceCharges;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder allowanceCharges (@Nullable final ICommonsList <AllowanceChargeType> a)
  {
    m_aAllowanceCharges.setAll (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addAllowanceCharge (@Nullable final AllowanceChargeType a)
  {
    if (a != null)
      m_aAllowanceCharges.add (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addAllowanceCharge (@Nullable final PeppolViDATDD110AllowanceChargeBuilder a)
  {
    return addAllowanceCharge (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addAllowanceCharge (@NonNull final Consumer <PeppolViDATDD110AllowanceChargeBuilder> a)
  {
    final PeppolViDATDD110AllowanceChargeBuilder aBuilder = new PeppolViDATDD110AllowanceChargeBuilder (m_sDocumentCurrencyCode);
    a.accept (aBuilder);
    return addAllowanceCharge (aBuilder);
  }

  @Nullable
  public TaxTotalType taxTotalDocumentCurrency ()
  {
    return m_aTaxTotalDocumentCurrency;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxTotalDocumentCurrency (@Nullable final TaxTotalType a)
  {
    m_aTaxTotalDocumentCurrency = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxTotalDocumentCurrency (@Nullable final PeppolViDATDD110TaxTotalBuilder a)
  {
    return taxTotalDocumentCurrency (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxTotalDocumentCurrency (@NonNull final Consumer <PeppolViDATDD110TaxTotalBuilder> a)
  {
    if (StringHelper.isEmpty (m_sDocumentCurrencyCode))
      throw new IllegalStateException ("The TaxTotal can only be built, after the DocumentCurrencyCode is set!");
    final PeppolViDATDD110TaxTotalBuilder aBuilder = new PeppolViDATDD110TaxTotalBuilder (m_sDocumentCurrencyCode);
    a.accept (aBuilder);
    return taxTotalDocumentCurrency (aBuilder);
  }

  @Nullable
  public TaxTotalType taxTotalTaxCurrency ()
  {
    return m_aTaxTotalTaxCurrency;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxTotalTaxCurrency (@Nullable final TaxTotalType a)
  {
    m_aTaxTotalTaxCurrency = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxTotalTaxCurrency (@Nullable final PeppolViDATDD110TaxTotalBuilder a)
  {
    return taxTotalTaxCurrency (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxTotalTaxCurrency (@Nullable final Consumer <PeppolViDATDD110TaxTotalBuilder> a)
  {
    if (StringHelper.isEmpty (m_sTaxCurrencyCode))
      throw new IllegalStateException ("The TaxTotal can only be built, after the TaxCurrencyCode is set!");
    final PeppolViDATDD110TaxTotalBuilder aBuilder = new PeppolViDATDD110TaxTotalBuilder (m_sTaxCurrencyCode);
    a.accept (aBuilder);
    return taxTotalTaxCurrency (aBuilder);
  }

  @Nullable
  public BigDecimal lineExtensionAmount ()
  {
    return m_aLineExtensionAmount;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder lineExtensionAmount (@Nullable final BigDecimal a)
  {
    m_aLineExtensionAmount = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder lineExtensionAmount (final long n)
  {
    return lineExtensionAmount (BigHelper.toBigDecimal (n));
  }

  @Nullable
  public BigDecimal taxExclusiveTotalAmount ()
  {
    return m_aTaxExclusiveTotalAmount;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxExclusiveTotalAmount (@Nullable final BigDecimal a)
  {
    m_aTaxExclusiveTotalAmount = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxExclusiveTotalAmount (final long n)
  {
    return taxExclusiveTotalAmount (BigHelper.toBigDecimal (n));
  }

  @Nullable
  public BigDecimal taxInclusiveTotalAmount ()
  {
    return m_aTaxInclusiveTotalAmount;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxInclusiveTotalAmount (@Nullable final BigDecimal a)
  {
    m_aTaxInclusiveTotalAmount = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder taxInclusiveTotalAmount (final long n)
  {
    return taxInclusiveTotalAmount (BigHelper.toBigDecimal (n));
  }

  @Nullable
  public BigDecimal allowanceTotalAmount ()
  {
    return m_aAllowanceTotalAmount;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder allowanceTotalAmount (@Nullable final BigDecimal a)
  {
    m_aAllowanceTotalAmount = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder allowanceTotalAmount (final long n)
  {
    return allowanceTotalAmount (BigHelper.toBigDecimal (n));
  }

  @Nullable
  public BigDecimal chargeTotalAmount ()
  {
    return m_aChargeTotalAmount;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder chargeTotalAmount (@Nullable final BigDecimal a)
  {
    m_aChargeTotalAmount = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder chargeTotalAmount (final long n)
  {
    return chargeTotalAmount (BigHelper.toBigDecimal (n));
  }

  @Nullable
  public BigDecimal prepaidAmount ()
  {
    return m_aPrepaidAmount;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder prepaidAmount (@Nullable final BigDecimal a)
  {
    m_aPrepaidAmount = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder prepaidAmount (final long n)
  {
    return prepaidAmount (BigHelper.toBigDecimal (n));
  }

  @Nullable
  public BigDecimal payableRoundingAmount ()
  {
    return m_aPayableRoundingAmount;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder payableRoundingAmount (@Nullable final BigDecimal a)
  {
    m_aPayableRoundingAmount = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder payableRoundingAmount (final long n)
  {
    return payableRoundingAmount (BigHelper.toBigDecimal (n));
  }

  @Nullable
  public BigDecimal payableAmount ()
  {
    return m_aPayableAmount;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder payableAmount (@Nullable final BigDecimal a)
  {
    m_aPayableAmount = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder payableAmount (final long n)
  {
    return payableAmount (BigHelper.toBigDecimal (n));
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <DocumentLineType> documentLines ()
  {
    return m_aDocumentLines;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder documentLines (@Nullable final ICommonsList <DocumentLineType> a)
  {
    m_aDocumentLines.setAll (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addDocumentLine (@Nullable final DocumentLineType a)
  {
    if (a != null)
      m_aDocumentLines.add (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addDocumentLine (@Nullable final PeppolViDATDD110DocumentLineBuilder a)
  {
    return addDocumentLine (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD110ReportedTransactionBuilder addDocumentLine (@NonNull final Consumer <PeppolViDATDD110DocumentLineBuilder> a)
  {
    if (StringHelper.isEmpty (m_sDocumentCurrencyCode))
      throw new IllegalStateException ("The DocumentLine can only be built, after the DocumentCurrencyCode is set!");
    final PeppolViDATDD110DocumentLineBuilder aBuilder = new PeppolViDATDD110DocumentLineBuilder (m_sDocumentCurrencyCode);
    a.accept (aBuilder);
    return addDocumentLine (aBuilder);
  }

  /**
   * @return <code>true</code> if the reported document uses the VAT category "Not subject to VAT"
   *         (BT-118 = "O"). In that case BR-O-02 and BR-O-03 explicitly forbid the Seller VAT
   *         identifier (BT-31), the Seller tax representative VAT identifier (BT-63) and the Buyer
   *         VAT identifier (BT-48).
   */
  private boolean _isNotSubjectToVAT ()
  {
    if (m_aTaxTotalDocumentCurrency != null)
      for (final TaxSubtotalType aTaxSubtotal : m_aTaxTotalDocumentCurrency.getTaxSubtotal ())
      {
        final TaxCategoryType aTaxCategory = aTaxSubtotal.getTaxCategory ();
        if (aTaxCategory != null && "O".equals (aTaxCategory.getIDValue ()))
          return true;
      }
    return false;
  }

  /**
   * Check the fields that are only mandatory if the Reporter role (TDT-012) is "C3", according to
   * the Schematron rules ibr-tdd-90, ibr-tdd-91 and ibr-tdd-92. For the Reporter role "C2" - the
   * Seller, who is not liable for the VAT of the reported transaction - all of them are optional.
   *
   * @param aCondLog
   *        The logger to use. May not be <code>null</code>.
   * @param sErrorPrefix
   *        The error message prefix to use. May not be <code>null</code>.
   * @param aErrorCount
   *        The ReportedDocument error counter to increment. May not be <code>null</code>.
   */
  private void _checkBuySideFields (@NonNull final ConditionalLogger aCondLog,
                                                @NonNull final String sErrorPrefix,
                                                @NonNull final MutableInt aErrorCount)
  {
    // BT-110 - ibr-tdd-90
    if (m_aTaxTotalDocumentCurrency == null || m_aTaxTotalDocumentCurrency.getTaxAmount () == null)
    {
      aCondLog.error (sErrorPrefix +
                      "Invoice total VAT amount (BT-110) is missing - it is mandatory for the Reporter role 'C3'");
      aErrorCount.inc ();
    }

    // BT-112 - ibr-tdd-91
    if (m_aTaxInclusiveTotalAmount == null)
    {
      aCondLog.error (sErrorPrefix +
                      "Invoice total amount with VAT (BT-112) is missing - it is mandatory for the Reporter role 'C3'");
      aErrorCount.inc ();
    }

    // BT-117, BT-118 and BT-119 per VAT breakdown - ibr-tdd-92
    if (m_aTaxTotalDocumentCurrency != null)
    {
      int nIndex = 0;
      for (final TaxSubtotalType aTaxSubtotal : m_aTaxTotalDocumentCurrency.getTaxSubtotal ())
      {
        final TaxCategoryType aTaxCategory = aTaxSubtotal.getTaxCategory ();
        if (aTaxSubtotal.getTaxAmount () == null ||
            aTaxCategory == null ||
            aTaxCategory.getID () == null ||
            aTaxCategory.getPercent () == null)
        {
          aCondLog.error (sErrorPrefix +
                          "The VAT breakdown (BG-23) at index " +
                          nIndex +
                          " must contain the VAT category tax amount (BT-117), the VAT category code (BT-118) and the VAT category rate (BT-119) for the Reporter role 'C3'");
          aErrorCount.inc ();
        }
        nIndex++;
      }
    }
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aErrorCount)
  {
    int nErrs = 0;
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 1.1.0 ReportedTransaction builder: ";

    // TDT-018 belongs to the ReportedTransaction and is therefore also needed for "failed" TDDs
    if (StringHelper.isEmpty (m_sTransmissionUUID))
    {
      aCondLog.error (sErrorPrefix + "Invoice Transmission UUID (TDT-018) is missing");
      nErrs++;
    }

    // Check all ReportedDocument fields
    if (StringHelper.isEmpty (m_sCustomizationID))
    {
      aCondLog.error (sErrorPrefix + "CustomizationID is missing");
      aErrorCount.inc ();
    }
    if (StringHelper.isEmpty (m_sProfileID))
    {
      aCondLog.error (sErrorPrefix + "ProfileID is missing");
      aErrorCount.inc ();
    }
    if (StringHelper.isEmpty (m_sID))
    {
      aCondLog.error (sErrorPrefix + "ID is missing");
      aErrorCount.inc ();
    }
    if (m_aIssueDate == null)
    {
      aCondLog.error (sErrorPrefix + "IssueDate is missing");
      aErrorCount.inc ();
    }
    // IssueTime is optional
    if (StringHelper.isEmpty (m_sDocumentTypeCode))
    {
      aCondLog.error (sErrorPrefix + "DocumentTypeCode is missing");
      aErrorCount.inc ();
    }
    // Note is optional
    // TaxPointDate is optional
    if (StringHelper.isEmpty (m_sDocumentCurrencyCode))
    {
      aCondLog.error (sErrorPrefix + "DocumentCurrencyCode is missing");
      aErrorCount.inc ();
    }
    // InvoicePeriod is optional

    // m_aBillingReferences may be empty

    // m_sSellerEndpointIDSchemeID and m_sSellerEndpointID (BT-34) are optional - they never end up
    // in the TDD (ibr-tdd-30)
    // m_sSellerIDSchemeID and m_sSellerID (BT-29) are deprecated and ignored
    if (StringHelper.isEmpty (m_sSellerTaxID) && !_isNotSubjectToVAT ())
    {
      // Without BT-31 no meaningful Invoice UUID (TDT-017) can be calculated, and without that no
      // TDD can be created at all. The only exception is the VAT category "Not subject to VAT",
      // for which BR-O-02 forbids BT-31.
      aCondLog.error (sErrorPrefix + "Seller VAT identifier (BT-31) is missing");
      aErrorCount.inc ();
    }
    // m_sSellerCountryCode is optional

    // m_sBuyerTaxID (BT-48) is optional (needed in all cases except if TaxCategory is "O")
    // m_sBuyerCountryCode (BT-55) is optional
    // m_sBuyerName (BT-44) is optional - it is not part of the TDD semantic model
    if (StringHelper.isEmpty (m_sBuyerTaxID) &&
        StringHelper.isEmpty (m_sBuyerCountryCode) &&
        StringHelper.isEmpty (m_sBuyerName))
    {
      // The BUYER (BG-07) is mandatory (ibr-tdd-36) and must not be empty (PEPPOL-EN16931-R008)
      aCondLog.error (sErrorPrefix +
                      "The BUYER (BG-07) needs at least one of BuyerTaxID (BT-48), BuyerCountryCode (BT-55) or BuyerName (BT-44)");
      aErrorCount.inc ();
    }

    // m_sTaxRepresentativeID is optional
    // m_sTaxRepresentativeCountryCode is optional

    // m_aDeliveryDate is optional

    // m_aAllowanceCharges may be empty

    // The VAT amount (BT-110) inside is only mandatory for the Reporter role "C3", but exactly one
    // cac:TaxTotal carrying at least one VAT breakdown (BG-23) is needed in any case
    // (PEPPOL-EN16931-R053)
    if (m_aTaxTotalDocumentCurrency == null || m_aTaxTotalDocumentCurrency.getTaxSubtotal ().isEmpty ())
    {
      aCondLog.error (sErrorPrefix +
                      "TaxTotalDocumentCurrency with at least one VAT breakdown (BG-23) is missing");
      aErrorCount.inc ();
    }
    if (m_aTaxTotalTaxCurrency != null)
    {
      if (StringHelper.isEmpty (m_sTaxCurrencyCode))
      {
        aCondLog.error (sErrorPrefix +
                        "If TaxTotalAmountTaxCurrency is provided, TaxCurrencyCode must also be provided");
        aErrorCount.inc ();
      }
    }
    else
    {
      if (StringHelper.isNotEmpty (m_sTaxCurrencyCode))
      {
        aCondLog.error (sErrorPrefix +
                        "If TaxCurrencyCode is provided, TaxTotalAmountTaxCurrency must also be provided");
        aErrorCount.inc ();
      }
    }
    if (m_aLineExtensionAmount == null)
    {
      aCondLog.error (sErrorPrefix + "LineExtensionAmount is missing");
      aErrorCount.inc ();
    }
    if (m_aTaxExclusiveTotalAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxExclusiveTotalAmount is missing");
      aErrorCount.inc ();
    }
    // m_aTaxInclusiveTotalAmount is optional - only the Reporter role "C3" needs the Invoice total
    // amount with VAT (BT-112); see _checkBuySideFields
    // m_aAllowanceTotalAmount is optional
    // m_aChargeTotalAmount is optional
    // m_aPrepaidAmount is optional
    // m_aPayableRoundingAmount is optional
    if (m_aPayableAmount == null)
    {
      aCondLog.error (sErrorPrefix + "PayableAmount is missing");
      aErrorCount.inc ();
    }
    if (m_aDocumentLines.isEmpty ())
    {
      aCondLog.error (sErrorPrefix + "At least one DocumentLine is needed");
      aErrorCount.inc ();
    }

    if (m_eReporterRole == EViDATDDReporterRole.RECEIVER)
      _checkBuySideFields (aCondLog, sErrorPrefix, aErrorCount);

    // Failed TDDs don't need this
    // TODO missing in 1.0.0
    // if (m_eDocumentTypeCode != EViDATDDDocumentTypeCode.DISREGARD)
    nErrs += aErrorCount.intValue ();

    return nErrs == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Nullable
  public ReportedTransactionType build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD ReportedTransaction cannot be build.");
      return null;
    }

    final ReportedTransactionType ret = new ReportedTransactionType ();

    // TDT-018 - the identifier of the transmission, not derived from the document content
    ret.setTransmissionUUID (m_sTransmissionUUID);

    // ReportedDocument - optional for FAILED state
    if (m_eDocumentTypeCode != EViDATDDTaxDataTypeCode.DISREGARD || aReportedDocErrs.is0 ())
    {
      // TDT-017 is calculated from BT-31, BT-03, BT-01 and BT-02
      final UUID aRepDocUUID = CViDATDD.createInvoiceUUID (m_sSellerTaxID,
                                                           m_sDocumentTypeCode,
                                                           m_sID,
                                                           m_aIssueDate);

      final ReportedDocumentType a = new ReportedDocumentType ();
      if (StringHelper.isNotEmpty (m_sCustomizationID))
        a.setCustomizationID (new CustomizationIDType (m_sCustomizationID));
      if (StringHelper.isNotEmpty (m_sProfileID))
        a.setProfileID (new ProfileIDType (m_sProfileID));
      if (StringHelper.isNotEmpty (m_sID))
        a.setID (new IDType (m_sID));
      a.setUUID (new UUIDType (aRepDocUUID.toString ()));
      if (m_aIssueDate != null)
        a.setIssueDate (new IssueDateType (XMLOffsetDate.of (m_aIssueDate)));
      if (m_aIssueTime != null)
        a.setIssueTime (new IssueTimeType (XMLOffsetTime.of (m_aIssueTime)));
      if (StringHelper.isNotEmpty (m_sDocumentTypeCode))
        a.setDocumentTypeCode (m_sDocumentTypeCode);
      if (StringHelper.isNotEmpty (m_sNote))
        a.setNote (new NoteType (m_sNote));
      if (StringHelper.isNotEmpty (m_sDocumentCurrencyCode))
        a.setDocumentCurrencyCode (new DocumentCurrencyCodeType (m_sDocumentCurrencyCode));
      if (StringHelper.isNotEmpty (m_sTaxCurrencyCode))
        a.setTaxCurrencyCode (new TaxCurrencyCodeType (m_sTaxCurrencyCode));

      if (m_aInvoicePeriodStart != null ||
          m_aInvoicePeriodEnd != null ||
          StringHelper.isNotEmpty (m_sInvoicePeriodDescriptionCode))
      {
        final PeriodType aIP = new PeriodType ();
        if (m_aInvoicePeriodStart != null)
          aIP.setStartDate (XMLOffsetDate.of (m_aInvoicePeriodStart));
        if (m_aInvoicePeriodEnd != null)
          aIP.setEndDate (XMLOffsetDate.of (m_aInvoicePeriodEnd));
        if (StringHelper.isNotEmpty (m_sInvoicePeriodDescriptionCode))
          aIP.addDescriptionCode (new DescriptionCodeType (m_sInvoicePeriodDescriptionCode));
        a.setInvoicePeriod (aIP);
      }

      a.setBillingReference (m_aBillingReferences);

      {
        final SupplierPartyType a2 = new SupplierPartyType ();
        {
          final PartyType aParty = new PartyType ();
          {
            // SellerEndpointID does not go into the resulting TDD
            if (StringHelper.isNotEmpty (m_sSellerTaxID))
            {
              final PartyTaxSchemeType aPTS = new PartyTaxSchemeType ();
              aPTS.setCompanyID (m_sSellerTaxID);
              final TaxSchemeType aTS = new TaxSchemeType ();
              aTS.setID ("VAT");
              aPTS.setTaxScheme (aTS);
              aParty.addPartyTaxScheme (aPTS);
            }

            if (StringHelper.isNotEmpty (m_sSellerCountryCode))
            {
              final AddressType aPA = new AddressType ();
              final CountryType aC = new CountryType ();
              aC.setIdentificationCode (m_sSellerCountryCode);
              aPA.setCountry (aC);
              aParty.setPostalAddress (aPA);
            }
          }
          // Party is required in AccountingSupplierParty
          a2.setParty (aParty);
        }
        a.setAccountingSupplierParty (a2);
      }

      {
        final CustomerPartyType aAccountingCustomer = new CustomerPartyType ();
        {
          final PartyType aParty = new PartyType ();
          if (StringHelper.isNotEmpty (m_sBuyerCountryCode))
          {
            final AddressType aPA = new AddressType ();
            final CountryType aC = new CountryType ();
            aC.setIdentificationCode (m_sBuyerCountryCode);
            aPA.setCountry (aC);
            aParty.setPostalAddress (aPA);
          }

          if (StringHelper.isNotEmpty (m_sBuyerTaxID))
          {
            final PartyTaxSchemeType aPTS = new PartyTaxSchemeType ();
            aPTS.setCompanyID (m_sBuyerTaxID);
            final TaxSchemeType aTS = new TaxSchemeType ();
            aTS.setID ("VAT");
            aPTS.setTaxScheme (aTS);
            aParty.addPartyTaxScheme (aPTS);
          }

          if (StringHelper.isNotEmpty (m_sBuyerName))
          {
            final PartyLegalEntityType aPLE = new PartyLegalEntityType ();
            aPLE.setRegistrationName (m_sBuyerName);
            aParty.addPartyLegalEntity (aPLE);
          }

          aAccountingCustomer.setParty (aParty);
        }
        a.setAccountingCustomerParty (aAccountingCustomer);
      }

      if (StringHelper.isNotEmpty (m_sTaxRepresentativeID) || StringHelper.isNotEmpty (m_sTaxRepresentativeCountryCode))
      {
        final PartyType aTaxRep = new PartyType ();
        if (StringHelper.isNotEmpty (m_sTaxRepresentativeCountryCode))
        {
          final AddressType aPA = new AddressType ();
          final CountryType aC = new CountryType ();
          aC.setIdentificationCode (m_sTaxRepresentativeCountryCode);
          aPA.setCountry (aC);
          aTaxRep.setPostalAddress (aPA);
        }

        if (StringHelper.isNotEmpty (m_sTaxRepresentativeID))
        {
          final PartyTaxSchemeType aPTS = new PartyTaxSchemeType ();
          aPTS.setCompanyID (m_sTaxRepresentativeID);
          final TaxSchemeType aTS = new TaxSchemeType ();
          aTS.setID ("VAT");
          aPTS.setTaxScheme (aTS);
          aTaxRep.addPartyTaxScheme (aPTS);
        }
        a.setTaxRepresentativeParty (aTaxRep);
      }

      if (m_aDeliveryDate != null)
      {
        final DeliveryType aDel = new DeliveryType ();
        aDel.setActualDeliveryDate (XMLOffsetDate.of (m_aDeliveryDate));
        a.setDelivery (aDel);
      }

      a.setAllowanceCharge (m_aAllowanceCharges);

      if (m_aTaxTotalDocumentCurrency != null)
        a.addTaxTotal (m_aTaxTotalDocumentCurrency);
      if (m_aTaxTotalTaxCurrency != null)
        a.addTaxTotal (m_aTaxTotalTaxCurrency);

      {
        final MonetaryTotalType aMonetaryTotal = new MonetaryTotalType ();
        {
          final LineExtensionAmountType aAmount = new LineExtensionAmountType (m_aLineExtensionAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setLineExtensionAmount (aAmount);
        }
        {
          final TaxExclusiveAmountType aAmount = new TaxExclusiveAmountType (m_aTaxExclusiveTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setTaxExclusiveAmount (aAmount);
        }
        if (m_aTaxInclusiveTotalAmount != null)
        {
          final TaxInclusiveAmountType aAmount = new TaxInclusiveAmountType (m_aTaxInclusiveTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setTaxInclusiveAmount (aAmount);
        }
        if (m_aAllowanceTotalAmount != null)
        {
          final AllowanceTotalAmountType aAmount = new AllowanceTotalAmountType (m_aAllowanceTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setAllowanceTotalAmount (aAmount);
        }
        if (m_aChargeTotalAmount != null)
        {
          final ChargeTotalAmountType aAmount = new ChargeTotalAmountType (m_aChargeTotalAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setChargeTotalAmount (aAmount);
        }
        if (m_aPrepaidAmount != null)
        {
          final PrepaidAmountType aAmount = new PrepaidAmountType (m_aPrepaidAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setPrepaidAmount (aAmount);
        }
        if (m_aPayableRoundingAmount != null)
        {
          final PayableRoundingAmountType aAmount = new PayableRoundingAmountType (m_aPayableRoundingAmount);
          aAmount.setCurrencyID (m_sDocumentCurrencyCode);
          aMonetaryTotal.setPayableRoundingAmount (aAmount);
        }
        {
          final PayableAmountType aAmount = new PayableAmountType (m_aPayableAmount);
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
