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

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.io.resource.ClassPathResource;

/**
 * Contains all the constants for Peppol ViDA pilot TDD handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class CPeppolViDATDD
{
  @NonNull
  private static ClassLoader _getCL ()
  {
    return CPeppolViDATDD.class.getClassLoader ();
  }

  private static final String PATH_1_0_0 = "/external/schemas/2026-03-18/";
  /**
   * XML Schema resources for Peppol ViDA pilot TDD XSD 1.0.0
   *
   * @deprecated Since 0.11.0 - use {@link #TDD_XSD_1_1_0_PATH} instead. The XSD is only kept around
   *             to be able to validate legacy documents.
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  public static final String TDD_XSD_1_0_0_PATH = PATH_1_0_0 + "Peppol-ViDA-TDD.xsd";

  /**
   * XML Schema resources for Peppol ViDA pilot TDD XSD 1.0.0
   *
   * @deprecated Since 0.11.0 - use {@link #TDD_XSD_1_1_0} instead. The XSD is only kept around to
   *             be able to validate legacy documents.
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  public static final ClassPathResource TDD_XSD_1_0_0 = new ClassPathResource (TDD_XSD_1_0_0_PATH, _getCL ());

  /**
   * Namespace URI for Peppol ViDA pilot TDD XSD 1.0.0
   *
   * @deprecated Since 0.11.0 - use {@link #TDD_XSD_1_1_0_NS} instead. The value is identical,
   *             because the XML namespace was intentionally not changed between TDD 1.0.0 and
   *             1.1.0.
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  public static final String TDD_XSD_1_0_0_NS = "urn:peppol:schema:vida-taxdata:1.0";

  private static final String PATH_1_1_0 = "/external/schemas/2026-09-14/";
  /**
   * XML Schema resources for Peppol ViDA pilot TDD XSD 1.1.0
   */
  public static final String TDD_XSD_1_1_0_PATH = PATH_1_1_0 + "Peppol-ViDA-TDD.xsd";

  /**
   * XML Schema resources for Peppol ViDA pilot TDD XSD 1.1.0
   */
  public static final ClassPathResource TDD_XSD_1_1_0 = new ClassPathResource (TDD_XSD_1_1_0_PATH, _getCL ());

  /**
   * Namespace URI for Peppol ViDA pilot TDD XSD 1.1.0. The XML namespace was intentionally not
   * changed between TDD 1.0.0 and 1.1.0.
   */
  public static final String TDD_XSD_1_1_0_NS = "urn:peppol:schema:vida-taxdata:1.0";

  @PresentForCodeCoverage
  private static final CPeppolViDATDD INSTANCE = new CPeppolViDATDD ();

  private CPeppolViDATDD ()
  {}
}
