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

/**
 * Test class for class {@link PeppolViDATDD110Marshaller}.
 *
 * @author Philip Helger
 */
public final class PeppolViDATDD110MarshallerTest
{
  @Test
  public void testBasic ()
  {
    final PeppolViDATDD110Marshaller m = new PeppolViDATDD110Marshaller ();
    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllGoodTDD110Files ())
      assertNotNull ("Failed to read " + aRes.getPath (), m.read (aRes));
    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllSchematronBadTDD110Files ())
      assertNotNull ("Failed to read " + aRes.getPath (), m.read (aRes));
  }

  /**
   * A TDD v1.0.0 document has no Invoice Transmission UUID (TDT-018) and can therefore not be read
   * with the TDD v1.1.0 marshaller.
   */
  @Test
  @SuppressWarnings ("deprecation")
  public void testCannotReadTDD100 ()
  {
    final ClassPathResource aRes = PeppolViDATestFiles.getAllGoodTDD100Files ().getFirstOrNull ();
    assertNotNull (aRes);
    assertNull (new PeppolViDATDD110Marshaller ().read (aRes));
  }
}
