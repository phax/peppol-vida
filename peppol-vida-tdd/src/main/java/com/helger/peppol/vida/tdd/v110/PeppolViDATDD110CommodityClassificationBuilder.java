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

import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.list.ErrorList;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ItemClassificationCodeType;

/**
 * Builder for Peppol ViDA pilot TDD 1.1.0 sub element called "CommodityClassification".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD110CommodityClassificationBuilder implements IBuilder <CommodityClassificationType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD110CommodityClassificationBuilder.class);

  private String m_sItemClassification;
  private String m_sItemClassificationListID;
  private String m_sItemClassificationListVersionID;

  public PeppolViDATDD110CommodityClassificationBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD110CommodityClassificationBuilder initFromUBL (@NonNull final CommodityClassificationType aObj)
  {
    ValueEnforcer.notNull (aObj, "Item");

    final ItemClassificationCodeType aICC = aObj.getItemClassificationCode ();
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
  public PeppolViDATDD110CommodityClassificationBuilder itemClassification (@Nullable final String s)
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
  public PeppolViDATDD110CommodityClassificationBuilder itemClassificationListID (@Nullable final String s)
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
  public PeppolViDATDD110CommodityClassificationBuilder itemClassificationListVersionID (@Nullable final String s)
  {
    m_sItemClassificationListVersionID = s;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (@NonNull final Consumer <String> aErrorHandler, @NonNull final MutableInt aErrorCount)
  {
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 1.1.0 CommodityClassification builder: ";

    if (StringHelper.isEmpty (m_sItemClassification))
    {
      aErrorHandler.accept (sErrorPrefix + "ItemClassification is missing");
      aErrorCount.inc ();
    }
    if (StringHelper.isEmpty (m_sItemClassificationListID))
    {
      aErrorHandler.accept (sErrorPrefix + "ItemClassificationListID is missing");
      aErrorCount.inc ();
    }
    // m_sItemClassificationListVersionID is optional

    return aErrorCount.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (sError -> aCondLog.error (sError), aReportedDocErrs);
  }

  public boolean isEveryRequiredFieldSet (@NonNull final ErrorList aErrorList)
  {
    ValueEnforcer.notNull (aErrorList, "ErrorList");
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (sError -> aErrorList.add (SingleError.builderError ().errorText (sError).build ()), aReportedDocErrs);
  }

  @Nullable
  public CommodityClassificationType build ()
  {
    if (!isEveryRequiredFieldSet (true))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD CommodityClassification cannot be build.");
      return null;
    }

    final CommodityClassificationType ret = new CommodityClassificationType ();
    {
      final ItemClassificationCodeType aIC = new ItemClassificationCodeType ();
      aIC.setValue (m_sItemClassification);
      aIC.setListID (m_sItemClassificationListID);
      aIC.setListVersionID (m_sItemClassificationListVersionID);
      ret.setItemClassificationCode (aIC);
    }
    return ret;
  }
}
