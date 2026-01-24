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
package com.helger.peppol.vida.tdd.validate;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.exception.InitializationException;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.sch.SchematronResourceSCH;

/**
 * This class contains the Schematron resources for validating Peppol ViDA pilot TDD documents.
 *
 * @author Philip Helger
 */
@Immutable
public final class PeppolViDATDDValidator
{
  public static final String SCH_VIDA_TDD_090_PATH = "external/schematron/0.9.0/Peppol-ViDA-TDD.sch";

  private static final ISchematronResource VIDA_TDD_090 = SchematronResourceSCH.fromClassPath (SCH_VIDA_TDD_090_PATH);

  static
  {
    for (final ISchematronResource aSch : new ISchematronResource [] { VIDA_TDD_090 })
      if (!aSch.isValidSchematron ())
        throw new InitializationException ("Schematron in " + aSch.getResource ().getPath () + " is invalid");
  }

  private PeppolViDATDDValidator ()
  {}

  /**
   * @return Schematron ViDA pilot TDD v0.9.0
   */
  @NonNull
  public static ISchematronResource getSchematronViDA_TDD_090 ()
  {
    return VIDA_TDD_090;
  }
}
