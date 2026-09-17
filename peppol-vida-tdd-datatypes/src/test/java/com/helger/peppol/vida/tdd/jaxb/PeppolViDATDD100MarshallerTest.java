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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.io.resource.ClassPathResource;
import com.helger.peppol.vida.tdd.testfiles.PeppolViDATestFiles;
import com.helger.peppol.vida.tdd.v2026_03_18.TaxDataType;

/**
 * Test class for the deprecated class {@link PeppolViDATDD100Marshaller}.
 *
 * @author Philip Helger
 */
@SuppressWarnings ("deprecation")
public final class PeppolViDATDD100MarshallerTest
{
  @Test
  public void testBasic ()
  {
    final PeppolViDATDD100Marshaller m = new PeppolViDATDD100Marshaller ();
    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllGoodTDD100Files ())
    {
      final TaxDataType aTDD = m.read (aRes);
      assertNotNull ("Failed to read " + aRes.getPath (), aTDD);
      assertNotNull (m.getAsString (aTDD));
    }
  }

  /**
   * A TDD v1.1.0 document has an Invoice Transmission UUID (TDT-018), which the TDD v1.0.0 XSD does
   * not know.
   */
  @Test
  public void testCannotReadTDD110 ()
  {
    final ClassPathResource aRes = PeppolViDATestFiles.getAllGoodTDD110Files ().getFirstOrNull ();
    assertNotNull (aRes);
    assertNull (new PeppolViDATDD100Marshaller ().read (aRes));
  }
}
