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
import com.helger.peppol.vida.tdd.v090.cac.TaxCategory;
import com.helger.peppol.vida.tdd.v090.cac.TaxScheme;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSchemeType;

/**
 * Builder for Peppol ViDA pilot TDD 0.9.0 sub element called "TaxCategory".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD090TaxCategoryBuilder implements IBuilder <TaxCategory>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD090TaxCategoryBuilder.class);

  private String m_sID;
  private BigDecimal m_aPerc;
  private String m_sTaxSchemeID;

  public PeppolViDATDD090TaxCategoryBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD090TaxCategoryBuilder initFromUBL (@NonNull final TaxCategoryType aObj)
  {
    ValueEnforcer.notNull (aObj, "TaxCategory");

    id (aObj.getIDValue ());
    percentage (aObj.getPercentValue ());
    final TaxSchemeType aTS = aObj.getTaxScheme ();
    if (aTS != null)
      taxSchemeID (aTS.getIDValue ());

    return this;
  }

  @Nullable
  public String id ()
  {
    return m_sID;
  }

  @NonNull
  public PeppolViDATDD090TaxCategoryBuilder id (@Nullable final String s)
  {
    m_sID = s;
    return this;
  }

  @Nullable
  public BigDecimal percentage ()
  {
    return m_aPerc;
  }

  @NonNull
  public PeppolViDATDD090TaxCategoryBuilder percentage (@Nullable final BigDecimal a)
  {
    m_aPerc = a;
    return this;
  }

  @Nullable
  public String taxSchemeID ()
  {
    return m_sTaxSchemeID;
  }

  @NonNull
  public PeppolViDATDD090TaxCategoryBuilder taxSchemeID (@Nullable final String s)
  {
    m_sTaxSchemeID = s;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 0.9.0 TaxCategory builder: ";

    if (StringHelper.isEmpty (m_sID))
    {
      aCondLog.error (sErrorPrefix + "ID is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aPercentage is optional
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
  public TaxCategory build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD TaxCategory cannot be build.");
      return null;
    }

    final TaxCategory ret = new TaxCategory ();
    ret.setID (m_sID);
    ret.setPercent (m_aPerc);
    {
      final TaxScheme aTS = new TaxScheme ();
      aTS.setID (m_sTaxSchemeID);
      ret.setTaxScheme (aTS);
    }
    return ret;
  }
}
