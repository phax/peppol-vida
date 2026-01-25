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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.peppol.vida.tdd.v090.cac.CommodityClassification;
import com.helger.peppol.vida.tdd.v090.cbc.ItemClassificationCode;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ItemClassificationCodeType;

/**
 * Builder for Peppol ViDA pilot TDD 0.9.0 sub element called "CommodityClassification".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD090CommodityClassificationBuilder implements IBuilder <CommodityClassification>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD090CommodityClassificationBuilder.class);

  private String m_sItemClassification;
  private String m_sItemClassificationListID;
  private String m_sItemClassificationListVersionID;

  public PeppolViDATDD090CommodityClassificationBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aItem
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD090CommodityClassificationBuilder initFromUBL (@NonNull final CommodityClassificationType aItem)
  {
    ValueEnforcer.notNull (aItem, "Item");

    final ItemClassificationCodeType aICC = aItem.getItemClassificationCode ();
    if (aICC != null)
    {
      itemClassification (aICC.getValue ());
      itemClassificationListID (aICC.getListID ());
      itemClassificationListVersionID (aICC.getListVersionID ());
    }
    return this;
  }

  @Nullable
  public String itemClassification ()
  {
    return m_sItemClassification;
  }

  @NonNull
  public PeppolViDATDD090CommodityClassificationBuilder itemClassification (@Nullable final String s)
  {
    m_sItemClassification = s;
    return this;
  }

  @Nullable
  public String itemClassificationListID ()
  {
    return m_sItemClassificationListID;
  }

  @NonNull
  public PeppolViDATDD090CommodityClassificationBuilder itemClassificationListID (@Nullable final String s)
  {
    m_sItemClassificationListID = s;
    return this;
  }

  @Nullable
  public String itemClassificationListVersionID ()
  {
    return m_sItemClassificationListVersionID;
  }

  @NonNull
  public PeppolViDATDD090CommodityClassificationBuilder itemClassificationListVersionID (@Nullable final String s)
  {
    m_sItemClassificationListVersionID = s;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 0.9.0 CommodityClassification builder: ";

    if (StringHelper.isEmpty (m_sItemClassification))
    {
      aCondLog.error (sErrorPrefix + "ItemClassification is missing");
      aReportedDocsErrs.inc ();
    }
    if (StringHelper.isEmpty (m_sItemClassificationListID))
    {
      aCondLog.error (sErrorPrefix + "ItemClassificationListID is missing");
      aReportedDocsErrs.inc ();
    }
    // m_sItemClassificationListVersionID is optional

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Nullable
  public CommodityClassification build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD CommodityClassification cannot be build.");
      return null;
    }

    final CommodityClassification ret = new CommodityClassification ();
    {
      final ItemClassificationCode aIC = new ItemClassificationCode ();
      aIC.setValue (m_sItemClassification);
      aIC.setListID (m_sItemClassificationListID);
      aIC.setListVersionID (m_sItemClassificationListVersionID);
      ret.setItemClassificationCode (aIC);
    }
    return ret;
  }
}
