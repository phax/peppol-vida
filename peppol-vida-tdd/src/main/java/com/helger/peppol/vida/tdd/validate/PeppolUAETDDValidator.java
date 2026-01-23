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
 * This class contains the Schematron resources for validating Peppol UAE TDD documents.
 *
 * @author Philip Helger
 */
@Immutable
public final class PeppolUAETDDValidator
{
  @Deprecated (forRemoval = true, since = "0.9.1")
  public static final String SCH_UAE_TDD_100_PATH = "external/schematron/peppol-ae-tdd-1.0.0.sch";
  public static final String SCH_UAE_TDD_101_PATH = "external/schematron/peppol-ae-tdd-1.0.1.sch";

  @Deprecated (forRemoval = true, since = "0.9.1")
  private static final ISchematronResource UAE_TDD_100 = SchematronResourceSCH.fromClassPath (SCH_UAE_TDD_100_PATH);
  private static final ISchematronResource UAE_TDD_101 = SchematronResourceSCH.fromClassPath (SCH_UAE_TDD_101_PATH);

  static
  {
    for (final ISchematronResource aSch : new ISchematronResource [] { UAE_TDD_100, UAE_TDD_101 })
      if (!aSch.isValidSchematron ())
        throw new InitializationException ("Schematron in " + aSch.getResource ().getPath () + " is invalid");
  }

  private PeppolUAETDDValidator ()
  {}

  /**
   * @return Schematron UAE TDD v1.0.0
   */
  @NonNull
  @Deprecated (forRemoval = true, since = "0.9.1")
  public static ISchematronResource getSchematronUAE_TDD_100 ()
  {
    return UAE_TDD_100;
  }

  /**
   * @return Schematron UAE TDD v1.0.1
   */
  @NonNull
  public static ISchematronResource getSchematronUAE_TDD_101 ()
  {
    return UAE_TDD_101;
  }

  /**
   * @return Schematron UAE TDD v1.0.x
   */
  @NonNull
  public static ISchematronResource getSchematronUAE_TDD_10 ()
  {
    return getSchematronUAE_TDD_101 ();
  }
}
