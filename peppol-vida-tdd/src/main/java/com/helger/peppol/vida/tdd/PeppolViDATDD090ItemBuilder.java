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
import com.helger.peppol.vida.tdd.v090.cac.ClassifiedTaxCategory;
import com.helger.peppol.vida.tdd.v090.cac.CommodityClassification;
import com.helger.peppol.vida.tdd.v090.cac.Item;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;

/**
 * Builder for Peppol ViDA pilot TDD 0.9.0 sub element called "Item".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD090ItemBuilder implements IBuilder <Item>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD090ItemBuilder.class);

  private String m_sDescription;
  private String m_sName;
  private final ICommonsList <CommodityClassification> m_aCommodityClassifications = new CommonsArrayList <> ();
  private ClassifiedTaxCategory m_aClassifiedTaxCategory;

  public PeppolViDATDD090ItemBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aItem
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD090ItemBuilder initFromUBL (@NonNull final ItemType aItem)
  {
    ValueEnforcer.notNull (aItem, "Item");

    if (aItem.hasDescriptionEntries ())
      description (aItem.getDescriptionAtIndex (0).getValue ());
    name (aItem.getNameValue ());

    for (final var aCC : aItem.getCommodityClassification ())
      addCommodityClassification (x -> x.initFromUBL (aCC));

    if (aItem.hasClassifiedTaxCategoryEntries ())
      classifiedTaxCategory (x -> x.initFromUBL (aItem.getClassifiedTaxCategoryAtIndex (0)));

    return this;
  }

  @Nullable
  public String description ()
  {
    return m_sDescription;
  }

  @NonNull
  public PeppolViDATDD090ItemBuilder description (@Nullable final String s)
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
  public PeppolViDATDD090ItemBuilder name (@Nullable final String s)
  {
    m_sName = s;
    return this;
  }

  @NonNull
  @ReturnsMutableObject
  public ICommonsList <CommodityClassification> commodityClassifications ()
  {
    return m_aCommodityClassifications;
  }

  @NonNull
  public PeppolViDATDD090ItemBuilder commodityClassifications (@Nullable final ICommonsList <CommodityClassification> a)
  {
    m_aCommodityClassifications.setAll (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090ItemBuilder addCommodityClassification (@Nullable final CommodityClassification a)
  {
    if (a != null)
      m_aCommodityClassifications.add (a);
    return this;
  }

  @NonNull
  public PeppolViDATDD090ItemBuilder addCommodityClassification (@Nullable final PeppolViDATDD090CommodityClassificationBuilder a)
  {
    return addCommodityClassification (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD090ItemBuilder addCommodityClassification (@NonNull final Consumer <PeppolViDATDD090CommodityClassificationBuilder> aBuilderConsumer)
  {
    final PeppolViDATDD090CommodityClassificationBuilder aBuilder = new PeppolViDATDD090CommodityClassificationBuilder ();
    aBuilderConsumer.accept (aBuilder);
    return addCommodityClassification (aBuilder);
  }

  @Nullable
  public ClassifiedTaxCategory classifiedTaxCategory ()
  {
    return m_aClassifiedTaxCategory;
  }

  @NonNull
  public PeppolViDATDD090ItemBuilder classifiedTaxCategory (@Nullable final ClassifiedTaxCategory a)
  {
    m_aClassifiedTaxCategory = a;
    return this;
  }

  @NonNull
  public PeppolViDATDD090ItemBuilder classifiedTaxCategory (@Nullable final PeppolViDATDD090ClassifiedTaxCategoryBuilder a)
  {
    return classifiedTaxCategory (a == null ? null : a.build ());
  }

  @NonNull
  public PeppolViDATDD090ItemBuilder classifiedTaxCategory (@NonNull final Consumer <PeppolViDATDD090ClassifiedTaxCategoryBuilder> aBuilderConsumer)
  {
    final PeppolViDATDD090ClassifiedTaxCategoryBuilder aBuilder = new PeppolViDATDD090ClassifiedTaxCategoryBuilder ();
    aBuilderConsumer.accept (aBuilder);
    return classifiedTaxCategory (aBuilder);
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 0.9.0 Item builder: ";

    // m_sDescription is optional
    if (StringHelper.isEmpty (m_sName))
    {
      aCondLog.error (sErrorPrefix + "Name is missing");
      aReportedDocsErrs.inc ();
    }
    // m_aCommodityClassifications may be empty
    if (m_aClassifiedTaxCategory == null)
    {
      aCondLog.error (sErrorPrefix + "ClassifiedTaxCategory is missing");
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
  public Item build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD Item cannot be build.");
      return null;
    }

    final Item ret = new Item ();
    ret.setDescription (m_sDescription);
    ret.setName (m_sName);
    ret.setCommodityClassification (m_aCommodityClassifications);
    ret.setClassifiedTaxCategory (m_aClassifiedTaxCategory);
    return ret;
  }
}
