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

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionType;

/**
 * Builder for Peppol ViDA pilot TDD 1.0.0 sub element called "Item".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD100ItemBuilder implements IBuilder <ItemType>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD100ItemBuilder.class);

  private String m_sDescription;
  private String m_sName;
  private final ICommonsList <CommodityClassificationType> m_aCommodityClassifications = new CommonsArrayList <> ();
  private TaxCategoryType m_aClassifiedTaxCategory;

  public PeppolViDATDD100ItemBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD100ItemBuilder initFromUBL (@NonNull final ItemType aObj)
  {
    ValueEnforcer.notNull (aObj, "Item");

    if (aObj.hasDescriptionEntries ())
      description (aObj.getDescriptionAtIndex (0).getValue ());
    name (aObj.getNameValue ());

    for (final var aCC : aObj.getCommodityClassification ())
      addCommodityClassification (x -> x.initFromUBL (aCC));

    if (aObj.hasClassifiedTaxCategoryEntries ())
      classifiedTaxCategory (x -> x.initFromUBL (aObj.getClassifiedTaxCategoryAtIndex (0)));

    return this;
  }

  @Nullable
  public String description ()
  {
    return m_sDescription;
  }

  @NonNull
  public PeppolViDATDD100ItemBuilder description (@Nullable final String s)
  {
    m_sDescription = s;
    return this;
  }

  @Nullable
  public String name ()
  {
    return m_sName;
  }

  @NonNull
  public PeppolViDATDD100ItemBuilder name (@Nullable final String s)
  {
    m_sName = s;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <CommodityClassificationType> commodityClassifications ()
  {
    return m_aCommodityClassifications;
  }

  @NonNull
  public PeppolViDATDD100ItemBuilder commodityClassifications (@Nullable final ICommonsList <CommodityClassificationType> a)
  {
    m_aCommodityClassifications.setAll (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD100ItemBuilder addCommodityClassification (@Nullable final CommodityClassificationType a)
  {
    if (a != null)
      m_aCommodityClassifications.add (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD100ItemBuilder addCommodityClassification (@Nullable final PeppolViDATDD100CommodityClassificationBuilder a)
  {
    return addCommodityClassification (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD100ItemBuilder addCommodityClassification (@NonNull final Consumer <PeppolViDATDD100CommodityClassificationBuilder> aBuilderConsumer)
  {
    final PeppolViDATDD100CommodityClassificationBuilder aBuilder = new PeppolViDATDD100CommodityClassificationBuilder ();
    aBuilderConsumer.accept (aBuilder);
    return addCommodityClassification (aBuilder);
  }

  @Nullable
  public TaxCategoryType classifiedTaxCategory ()
  {
    return m_aClassifiedTaxCategory;
  }

  @NonNull
  public PeppolViDATDD100ItemBuilder classifiedTaxCategory (@Nullable final TaxCategoryType a)
  {
    m_aClassifiedTaxCategory = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD100ItemBuilder classifiedTaxCategory (@Nullable final PeppolViDATDD100TaxCategoryBuilder a)
  {
    return classifiedTaxCategory (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD100ItemBuilder classifiedTaxCategory (@NonNull final Consumer <PeppolViDATDD100TaxCategoryBuilder> aBuilderConsumer)
  {
    final PeppolViDATDD100TaxCategoryBuilder aBuilder = new PeppolViDATDD100TaxCategoryBuilder ();
    aBuilderConsumer.accept (aBuilder);
    return classifiedTaxCategory (aBuilder);
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aErrorCount)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 1.0.0 Item builder: ";

    // m_sDescription is optional
    if (StringHelper.isEmpty (m_sName))
    {
      aCondLog.error (sErrorPrefix + "Name is missing");
      aErrorCount.inc ();
    }
    // m_aCommodityClassifications may be empty
    if (m_aClassifiedTaxCategory == null)
    {
      aCondLog.error (sErrorPrefix + "ClassifiedTaxCategory is missing");
      aErrorCount.inc ();
    }

    return aErrorCount.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Nullable
  public ItemType build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD Item cannot be build.");
      return null;
    }

    final ItemType ret = new ItemType ();
    if (StringHelper.isNotEmpty (m_sDescription))
      ret.addDescription (new DescriptionType (m_sDescription));
    ret.setName (m_sName);
    ret.setCommodityClassification (m_aCommodityClassifications);
    ret.addClassifiedTaxCategory (m_aClassifiedTaxCategory);
    return ret;
  }
}
