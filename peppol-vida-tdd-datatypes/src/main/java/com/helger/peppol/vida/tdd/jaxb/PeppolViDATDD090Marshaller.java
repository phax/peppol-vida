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
package com.helger.peppol.vida.tdd.jaxb;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.resource.ClassPathResource;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.peppol.vida.tdd.v090.ObjectFactory;
import com.helger.peppol.vida.tdd.v090.TaxDataType;
import com.helger.xml.namespace.MapBasedNamespaceContext;

/**
 * This is the reader and writer for Peppol ViDA pilot TDD 0.9.0 documents. This class may be
 * derived to override protected methods from {@link GenericJAXBMarshaller}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class PeppolViDATDD090Marshaller extends GenericJAXBMarshaller <TaxDataType>
{
  @NonNull
  @ReturnsMutableCopy
  private static ICommonsList <ClassPathResource> _getAllXSDs ()
  {
    return new CommonsArrayList <> (CPeppolViDATDD.TDD_XSD_0_9_0_CBC,
                                    CPeppolViDATDD.TDD_XSD_0_9_0_CAC,
                                    CPeppolViDATDD.TDD_XSD_0_9_0);
  }

  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public static MapBasedNamespaceContext createNamespaceContext ()
  {
    final MapBasedNamespaceContext ret = new MapBasedNamespaceContext ();
    ret.addMapping ("pxs", CPeppolViDATDD.TDD_XSD_0_9_0_NS);
    ret.addMapping ("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
    ret.addMapping ("cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
    return ret;
  }

  /**
   * Constructor
   */
  public PeppolViDATDD090Marshaller ()
  {
    super (TaxDataType.class, _getAllXSDs (), new ObjectFactory ()::createTaxData);
    setNamespaceContext (createNamespaceContext ());
  }
}
