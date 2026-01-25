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

import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.peppol.vida.tdd.v090.cac.TaxTotal;
import com.helger.peppol.vida.tdd.v090.cac.TaxTotal.TaxSubtotal;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxTotalType;

/**
 * Builder for Peppol ViDA pilot TDD 0.9.0 sub element called "TaxTotal".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD090TaxTotalBuilder implements IBuilder <TaxTotal>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD090TaxTotalBuilder.class);

  private final String m_sCurrencyCode;
  private BigDecimal m_aTaxAmount;
  private final ICommonsList <TaxSubtotal> m_aSubtotals = new CommonsArrayList <> ();

  public PeppolViDATDD090TaxTotalBuilder (@NonNull final String sCurrencyCode)
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
  public PeppolViDATDD090TaxTotalBuilder initFromUBL (@NonNull final TaxTotalType aObj)
  {
    ValueEnforcer.notNull (aObj, "TaxTotal");

    taxAmount (aObj.getTaxAmountValue ());
    for (final var aTS : aObj.getTaxSubtotal ())
      addTaxSubtotal (x -> x.initFromUBL (aTS));

    return this;
  }

  @Nullable
  public BigDecimal taxAmount ()
  {
    return m_aTaxAmount;
  }

  @NonNull
  public PeppolViDATDD090TaxTotalBuilder taxAmount (@Nullable final BigDecimal a)
  {
    m_aTaxAmount = a;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <TaxSubtotal> subtotals ()
  {
    return m_aSubtotals;
  }

  @NonNull
  public PeppolViDATDD090TaxTotalBuilder subtotals (@Nullable final ICommonsList <TaxSubtotal> a)
  {
    m_aSubtotals.setAll (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090TaxTotalBuilder addTaxSubtotal (@Nullable final TaxSubtotal a)
  {
    if (a != null)
      m_aSubtotals.add (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090TaxTotalBuilder addTaxSubtotal (@Nullable final PeppolViDATDD090TaxSubtotalBuilder a)
  {
    return addTaxSubtotal (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD090TaxTotalBuilder addTaxSubtotal (@NonNull final Consumer <PeppolViDATDD090TaxSubtotalBuilder> aBuilderConsumer)
  {
    final PeppolViDATDD090TaxSubtotalBuilder aBuilder = new PeppolViDATDD090TaxSubtotalBuilder (m_sCurrencyCode);
    aBuilderConsumer.accept (aBuilder);
    return addTaxSubtotal (aBuilder);
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 0.9.0 TaxTotal builder: ";

    if (m_aTaxAmount == null)
    {
      aCondLog.error (sErrorPrefix + "TaxAmount is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aSubtotals may be empty

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Nullable
  public TaxTotal build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD TaxTotal cannot be build.");
      return null;
    }

    final TaxTotal ret = new TaxTotal ();
    ret.setTaxAmount (m_aTaxAmount).setCurrencyID (m_sCurrencyCode);
    ret.setTaxSubtotal (m_aSubtotals);
    return ret;
  }
}
