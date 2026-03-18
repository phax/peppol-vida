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

import org.junit.Test;

import com.helger.io.resource.ClassPathResource;
import com.helger.peppol.vida.tdd.testfiles.PeppolViDATestFiles;

/**
 * Test class for class {@link PeppolViDATDD100Marshaller}.
 *
 * @author Philip Helger
 */
public final class PeppolViDATDD090MarshallerTest
{
  @Test
  public void testBasic10 ()
  {
    final PeppolViDATDD100Marshaller m = new PeppolViDATDD100Marshaller ();
    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllGoodTDD100Files ())
      assertNotNull ("Failed to read " + aRes.getPath (), m.read (aRes));
    for (final ClassPathResource aRes : PeppolViDATestFiles.getAllSchematronBadTDD100Files ())
      assertNotNull ("Failed to read " + aRes.getPath (), m.read (aRes));
  }
}
