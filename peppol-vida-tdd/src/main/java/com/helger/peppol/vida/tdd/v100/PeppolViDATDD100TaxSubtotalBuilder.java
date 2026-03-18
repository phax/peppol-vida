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

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.BigHelper;
import com.helger.base.numeric.mutable.MutableInt;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSubtotalType;

/**
 * Builder for Peppol ViDA pilot TDD 1.0.0 sub element called "TaxSubtotal".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD100TaxSubtotalBuilder implements IBuilder <TaxSubtotalType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD100TaxSubtotalBuilder.class);

  private final String m_sCurrencyCode;
  private BigDecimal m_aTaxableAmount;
  private BigDecimal m_aTaxAmount;
  private TaxCategoryType m_aTaxCategory;

  public PeppolViDATDD100TaxSubtotalBuilder (@NonNull final String sCurrencyCode)
  {
    m_sCurrencyCode = sCurrencyCode;
  }

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD100TaxSubtotalBuilder initFromUBL (@NonNull final TaxSubtotalType aObj)
  {
    ValueEnforcer.notNull (aObj, "TaxSubtotal");

    taxableAmount (aObj.getTaxableAmountValue ());
    taxAmount (aObj.getTaxAmountValue ());
    taxCategory (x -> x.initFromUBL (aObj.getTaxCategory ()));
    return this;
  }

  @Nullable
  public BigDecimal taxableAmount ()
  {
    return m_aTaxableAmount;
  }

  @NonNull
  public PeppolViDATDD100TaxSubtotalBuilder taxableAmount (@Nullable final BigDecimal a)
  {
    m_aTaxableAmount = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD100TaxSubtotalBuilder taxableAmount (final long n)
  {
    return taxableAmount (BigHelper.toBigDecimal (n));
  }

  @Nullable
  public BigDecimal taxAmount ()
  {
    return m_aTaxAmount;
  }

  @NonNull
  public PeppolViDATDD100TaxSubtotalBuilder taxAmount (@Nullable final BigDecimal a)
  {
    m_aTaxAmount = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD100TaxSubtotalBuilder taxAmount (final long n)
  {
    return taxAmount (BigHelper.toBigDecimal (n));
  }

  @Nullable
  public TaxCategoryType taxCategory ()
  {
    return m_aTaxCategory;
  }

  @NonNull
  public PeppolViDATDD100TaxSubtotalBuilder taxCategory (@Nullable final TaxCategoryType a)
  {
    m_aTaxCategory = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD100TaxSubtotalBuilder taxCategory (@Nullable final PeppolViDATDD100TaxCategoryBuilder a)
  {
    return taxCategory (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD100TaxSubtotalBuilder taxCategory (@NonNull final Consumer <? super PeppolViDATDD100TaxCategoryBuilder> aBuilderConsumer)
  {
    final PeppolViDATDD100TaxCategoryBuilder aBuilder = new PeppolViDATDD100TaxCategoryBuilder ();
    aBuilderConsumer.accept (aBuilder);
    return taxCategory (aBuilder);
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 1.0.0 TaxSubtotal builder: ";

    if (m_aTaxableAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxableAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxAmount is missing");
      aReportedDocsErrs.inc ();
    }
    if (m_aTaxCategory == null)
    {
      aCondLog.error (sErrorPrefix + "TaxCategory is missing");
      aReportedDocsErrs.inc ();
    }

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Nullable
  public TaxSubtotalType build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD TaxSubtotal cannot be build.");
      return null;
    }

    final TaxSubtotalType ret = new TaxSubtotalType ();
    ret.setTaxableAmount (m_aTaxableAmount).setCurrencyID (m_sCurrencyCode);
    ret.setTaxAmount (m_aTaxAmount).setCurrencyID (m_sCurrencyCode);
    ret.setTaxCategory (m_aTaxCategory);
    return ret;
  }
}
