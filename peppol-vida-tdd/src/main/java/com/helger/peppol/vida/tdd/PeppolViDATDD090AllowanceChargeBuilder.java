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
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.peppol.vida.tdd.v090.cac.AllowanceCharge;
import com.helger.peppol.vida.tdd.v090.cac.TaxCategory;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AllowanceChargeType;

/**
 * Builder for Peppol ViDA pilot TDD 0.9.0 sub element called "AllowanceCharge".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD090AllowanceChargeBuilder implements IBuilder <AllowanceCharge>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD090AllowanceChargeBuilder.class);

  private final String m_sDocumentCurrencyCode;
  private boolean m_bCharge;
  private String m_sReasonCode;
  private String m_sReason;
  private BigDecimal m_aMultFactor;
  private BigDecimal m_aAmount;
  private BigDecimal m_aBaseAmount;
  private TaxCategory m_aTaxCategory;

  public PeppolViDATDD090AllowanceChargeBuilder (@Nullable final String sDocumentCurrencyCode)
  {
    m_sDocumentCurrencyCode = sDocumentCurrencyCode;
  }

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aAC
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD090AllowanceChargeBuilder initFromUBL (@NonNull final AllowanceChargeType aAC)
  {
    ValueEnforcer.notNull (aAC, "AllowanceCharge");

    if (aAC.getChargeIndicator () != null)
      charge (aAC.getChargeIndicator ().isValue ());
    reasonCode (aAC.getAllowanceChargeReasonCodeValue ());
    if (aAC.hasAllowanceChargeReasonEntries ())
      reason (aAC.getAllowanceChargeReasonAtIndex (0).getValue ());
    multiplicationFactor (aAC.getMultiplierFactorNumericValue ());
    amount (aAC.getAmountValue ());
    baseAmount (aAC.getBaseAmountValue ());
    if (aAC.hasTaxCategoryEntries ())
      taxCategory (x -> x.initFromUBL (aAC.getTaxCategoryAtIndex (0)));

    return this;
  }

  public boolean charge ()
  {
    return m_bCharge;
  }

  @NonNull
  public PeppolViDATDD090AllowanceChargeBuilder charge (final boolean b)
  {
    m_bCharge = b;
    return this;
  }

  @Nullable
  public String reasonCode ()
  {
    return m_sReasonCode;
  }

  @NonNull
  public PeppolViDATDD090AllowanceChargeBuilder reasonCode (@Nullable final String s)
  {
    m_sReasonCode = s;
    return this;
  }

  @Nullable
  public String reason ()
  {
    return m_sReason;
  }

  @NonNull
  public PeppolViDATDD090AllowanceChargeBuilder reason (@Nullable final String s)
  {
    m_sReason = s;
    return this;
  }

  @Nullable
  public BigDecimal multiplicationFactor ()
  {
    return m_aMultFactor;
  }

  @NonNull
  public PeppolViDATDD090AllowanceChargeBuilder multiplicationFactor (@Nullable final BigDecimal a)
  {
    m_aMultFactor = a;
    return this;
  }

  @Nullable
  public BigDecimal amount ()
  {
    return m_aAmount;
  }

  @NonNull
  public PeppolViDATDD090AllowanceChargeBuilder amount (@Nullable final BigDecimal a)
  {
    m_aAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal baseAmount ()
  {
    return m_aBaseAmount;
  }

  @NonNull
  public PeppolViDATDD090AllowanceChargeBuilder baseAmount (@Nullable final BigDecimal a)
  {
    m_aBaseAmount = a;
    return this;
  }

  @Nullable
  public TaxCategory taxCategory ()
  {
    return m_aTaxCategory;
  }

  @NonNull
  public PeppolViDATDD090AllowanceChargeBuilder taxCategory (@Nullable final TaxCategory a)
  {
    m_aTaxCategory = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD090AllowanceChargeBuilder taxCategory (@Nullable final PeppolViDATDD090TaxCategoryBuilder a)
  {
    return taxCategory (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD090AllowanceChargeBuilder taxCategory (@NonNull final Consumer <PeppolViDATDD090TaxCategoryBuilder> aBuilderConsumer)
  {
    final PeppolViDATDD090TaxCategoryBuilder aBuilder = new PeppolViDATDD090TaxCategoryBuilder ();
    aBuilderConsumer.accept (aBuilder);
    return taxCategory (aBuilder);
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 0.9.0 AllowanceCharge builder: ";

    // m_sReasonCode is optional
    // m_sReason is optional
    // m_aMultFactor is optional
    if (m_aAmount == null)
    {
      aCondLog.error (sErrorPrefix + "Amount is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aBaseAmount is optional
    // m_aTaxCategory is optional

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Nullable
  public AllowanceCharge build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD AllowanceCharge cannot be build.");
      return null;
    }

    final AllowanceCharge ret = new AllowanceCharge ();
    ret.setChargeIndicator (m_bCharge);
    ret.setAllowanceChargeReasonCode (m_sReasonCode);
    ret.setAllowanceChargeReason (m_sReason);
    ret.setMultiplierFactorNumeric (m_aMultFactor);
    ret.setAmount (m_aAmount).setCurrencyID (m_sDocumentCurrencyCode);
    if (m_aBaseAmount != null)
      ret.setBaseAmount (m_aBaseAmount).setCurrencyID (m_sDocumentCurrencyCode);
    ret.setTaxCategory (m_aTaxCategory);

    return ret;
  }
}
