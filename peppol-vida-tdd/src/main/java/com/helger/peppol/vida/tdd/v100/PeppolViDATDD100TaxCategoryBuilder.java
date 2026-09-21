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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.BigHelper;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TaxExemptionReasonType;

/**
 * Builder for Peppol ViDA pilot TDD 1.0.0 sub element called "TaxCategory".
 *
 * @author Philip Helger
 * @deprecated Since 0.11.0 - use
 *             {@link com.helger.peppol.vida.tdd.v110.PeppolViDATDD110TaxCategoryBuilder} instead.
 *             This class is only kept around to create legacy TDD v1.0.0 documents. It still uses
 *             the Seller identifier (BT-29) for the UUID calculation and does not know the Invoice
 *             Transmission UUID (TDT-018).
 */
@Deprecated (since = "0.11.0", forRemoval = true)
public class PeppolViDATDD100TaxCategoryBuilder implements IBuilder <TaxCategoryType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD100TaxCategoryBuilder.class);

  private String m_sID;
  private BigDecimal m_aPerc;
  private String m_sTaxSchemeID;
  private String m_sTaxExemptionReason;
  private String m_sTaxExemptionReasonCode;

  @Deprecated (since = "0.11.0", forRemoval = true)
  public PeppolViDATDD100TaxCategoryBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public PeppolViDATDD100TaxCategoryBuilder initFromUBL (@NonNull final TaxCategoryType aObj)
  {
    ValueEnforcer.notNull (aObj, "TaxCategory");

    id (aObj.getIDValue ());
    percentage (aObj.getPercentValue ());
    final TaxSchemeType aTaxScheme = aObj.getTaxScheme ();
    if (aTaxScheme != null)
      taxSchemeID (aTaxScheme.getIDValue ());

    if (!aObj.getTaxExemptionReason ().isEmpty ())
    {
      for (final var taxExemptionReason : aObj.getTaxExemptionReason ())
      {
        if (!StringHelper.isEmpty (taxExemptionReason.getValue ()))
        {
          taxExemptionReason (taxExemptionReason.getValue ());
        }
      }
    }

    if (aObj.getTaxExemptionReasonCode () != null)
    {
      taxExemptionReasonCode (aObj.getTaxExemptionReasonCode ().getValue ());
    }

    return this;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @Nullable
  public String id ()
  {
    return m_sID;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public PeppolViDATDD100TaxCategoryBuilder id (@Nullable final String s)
  {
    m_sID = s;
    return this;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @Nullable
  public BigDecimal percentage ()
  {
    return m_aPerc;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public PeppolViDATDD100TaxCategoryBuilder percentage (@Nullable final BigDecimal a)
  {
    m_aPerc = a;
    return this;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public PeppolViDATDD100TaxCategoryBuilder percentage (final long n)
  {
    return percentage (BigHelper.toBigDecimal (n));
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @Nullable
  public String taxSchemeID ()
  {
    return m_sTaxSchemeID;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public PeppolViDATDD100TaxCategoryBuilder taxSchemeID (@Nullable final String s)
  {
    m_sTaxSchemeID = s;
    return this;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public PeppolViDATDD100TaxCategoryBuilder taxSchemeID_VAT ()
  {
    return taxSchemeID ("VAT");
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @Nullable
  public String taxExemptionReason ()
  {
    return m_sTaxExemptionReason;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public PeppolViDATDD100TaxCategoryBuilder taxExemptionReason (@Nullable final String s)
  {
    m_sTaxExemptionReason = s;
    return this;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @Nullable
  public String taxExemptionReasonCode ()
  {
    return m_sTaxExemptionReasonCode;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public PeppolViDATDD100TaxCategoryBuilder taxExemptionReasonCode (@Nullable final String s)
  {
    m_sTaxExemptionReasonCode = s;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aErrorCount)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 1.0.0 TaxCategory builder: ";

    if (StringHelper.isEmpty (m_sID))
    {
      aCondLog.error (sErrorPrefix + "ID is missing");
      aErrorCount.inc ();
    }
    // m_aPercentage is optional
    if (StringHelper.isEmpty (m_sTaxSchemeID))
    {
      aCondLog.error (sErrorPrefix + "TaxSchemeID is missing");
      aErrorCount.inc ();
    }

    return aErrorCount.intValue () == 0;
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Deprecated (since = "0.11.0", forRemoval = true)
  @Nullable
  public TaxCategoryType build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD TaxCategory cannot be build.");
      return null;
    }

    final TaxCategoryType ret = new TaxCategoryType ();
    ret.setID (m_sID);
    if (m_aPerc != null)
      ret.setPercent (m_aPerc);
    {
      final TaxSchemeType aTS = new TaxSchemeType ();
      aTS.setID (m_sTaxSchemeID);
      ret.setTaxScheme (aTS);
    }

    if (!StringHelper.isEmpty (m_sTaxExemptionReason))
    {
      final TaxExemptionReasonType aET = new TaxExemptionReasonType ();
      aET.setValue (m_sTaxExemptionReason);
      ret.getTaxExemptionReason ().add (aET);
    }

    if (!StringHelper.isEmpty (m_sTaxExemptionReasonCode))
    {
      ret.setTaxExemptionReasonCode (m_sTaxExemptionReasonCode);
    }

    return ret;
  }
}
