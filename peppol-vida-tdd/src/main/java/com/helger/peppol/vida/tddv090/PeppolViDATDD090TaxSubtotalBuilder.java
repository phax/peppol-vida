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
package com.helger.peppol.vida.tddv090;

import java.math.BigDecimal;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.peppol.vida.tdd.v090.cac.TaxScheme;
import com.helger.peppol.vida.tdd.v090.cac.TaxTotal.TaxSubtotal;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;

/**
 * Builder for Peppol ViDA pilot TDD 0.9.0 sub element called "TaxSubtotal".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD090TaxSubtotalBuilder implements IBuilder <TaxSubtotal>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD090TaxSubtotalBuilder.class);

  private final String m_sCurrencyCode;
  private BigDecimal m_aTaxableAmount;
  private BigDecimal m_aTaxAmount;
  private String m_sTaxCategoryID;
  private String m_sTaxCategoryIDScheme;
  private BigDecimal m_aPercentage;
  private String m_sTaxExemptionReasonCode;
  private String m_sTaxExemptionReason;
  private String m_sTaxSchemeID;

  public PeppolViDATDD090TaxSubtotalBuilder (@NonNull final String sCurrencyCode)
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
  public PeppolViDATDD090TaxSubtotalBuilder initFromUBL (@NonNull final TaxSubtotalType aObj)
  {
    ValueEnforcer.notNull (aObj, "TaxSubtotal");

    taxableAmount (aObj.getTaxableAmountValue ());
    taxAmount (aObj.getTaxAmountValue ());

    final TaxCategoryType aTaxCategory = aObj.getTaxCategory ();
    if (aTaxCategory != null)
    {
      final IDType aID = aTaxCategory.getID ();
      if (aID != null)
      {
        taxCategoryID (aID.getValue ());
        taxCategoryIDScheme (aID.getSchemeID ());
      }
      percentage (aTaxCategory.getPercentValue ());
      taxExemptionReasonCode (aTaxCategory.getTaxExemptionReasonCodeValue ());
      if (aTaxCategory.hasTaxExemptionReasonEntries ())
        taxExemptionReason (aTaxCategory.getTaxExemptionReasonAtIndex (0).getValue ());

      final TaxSchemeType aTS = aTaxCategory.getTaxScheme ();
      if (aTS != null)
        taxSchemeID (aTS.getIDValue ());
    }

    return this;
  }

  @Nullable
  public BigDecimal taxableAmount ()
  {
    return m_aTaxableAmount;
  }

  @NonNull
  public PeppolViDATDD090TaxSubtotalBuilder taxableAmount (@Nullable final BigDecimal a)
  {
    m_aTaxableAmount = a;
    return this;
  }

  @Nullable
  public BigDecimal taxAmount ()
  {
    return m_aTaxAmount;
  }

  @NonNull
  public PeppolViDATDD090TaxSubtotalBuilder taxAmount (@Nullable final BigDecimal a)
  {
    m_aTaxAmount = a;
    return this;
  }

  @Nullable
  public String taxCategoryID ()
  {
    return m_sTaxCategoryID;
  }

  @NonNull
  public PeppolViDATDD090TaxSubtotalBuilder taxCategoryID (@Nullable final String s)
  {
    m_sTaxCategoryID = s;
    return this;
  }

  @Nullable
  public String taxCategoryIDScheme ()
  {
    return m_sTaxCategoryIDScheme;
  }

  @NonNull
  public PeppolViDATDD090TaxSubtotalBuilder taxCategoryIDScheme (@Nullable final String s)
  {
    m_sTaxCategoryIDScheme = s;
    return this;
  }

  @Nullable
  public BigDecimal percentage ()
  {
    return m_aPercentage;
  }

  @NonNull
  public PeppolViDATDD090TaxSubtotalBuilder percentage (@Nullable final BigDecimal a)
  {
    m_aPercentage = a;
    return this;
  }

  @Nullable
  public String taxExemptionReasonCode ()
  {
    return m_sTaxExemptionReasonCode;
  }

  @NonNull
  public PeppolViDATDD090TaxSubtotalBuilder taxExemptionReasonCode (@Nullable final String s)
  {
    m_sTaxExemptionReasonCode = s;
    return this;
  }

  @Nullable
  public String taxExemptionReason ()
  {
    return m_sTaxExemptionReason;
  }

  @NonNull
  public PeppolViDATDD090TaxSubtotalBuilder taxExemptionReason (@Nullable final String s)
  {
    m_sTaxExemptionReason = s;
    return this;
  }

  @Nullable
  public String taxSchemeID ()
  {
    return m_sTaxSchemeID;
  }

  @NonNull
  public PeppolViDATDD090TaxSubtotalBuilder taxSchemeID (@Nullable final String s)
  {
    m_sTaxSchemeID = s;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 0.9.0 TaxSubtotal builder: ";

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
    if (StringHelper.isEmpty (m_sTaxCategoryID))
    {
      aCondLog.error (sErrorPrefix + "TaxCategoryID is missing");
      aReportedDocsErrs.inc ();
    }
    // m_sTaxCategoryIDScheme is optional
    // m_aPercentage is optional
    // m_sTaxExemptionReasonCode is optional
    // m_sTaxExemptionReason is optional
    if (StringHelper.isEmpty (m_sTaxSchemeID))
    {
      aCondLog.error (sErrorPrefix + "TaxSchemeID is missing");
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
  public TaxSubtotal build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD TaxSubtotal cannot be build.");
      return null;
    }

    final TaxSubtotal ret = new TaxSubtotal ();
    ret.setTaxableAmount (m_aTaxableAmount).setCurrencyID (m_sCurrencyCode);
    ret.setTaxAmount (m_aTaxAmount).setCurrencyID (m_sCurrencyCode);
    {
      final var aTC = new TaxSubtotal.TaxCategory ();
      aTC.setID (m_sTaxCategoryID).setSchemeID (m_sTaxCategoryIDScheme);
      aTC.setPercent (m_aPercentage);
      aTC.setTaxExemptionReasonCode (m_sTaxExemptionReasonCode);
      aTC.setTaxExemptionReason (m_sTaxExemptionReason);
      {
        final var aTS = new TaxScheme ();
        aTS.setID (m_sTaxSchemeID);
        aTC.setTaxScheme (aTS);
      }
      ret.setTaxCategory (aTC);
    }
    return ret;
  }
}
