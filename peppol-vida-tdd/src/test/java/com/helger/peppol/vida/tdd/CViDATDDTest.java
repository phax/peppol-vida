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

import static org.junit.Assert.assertEquals;

import java.time.Month;

import org.junit.Test;

import com.helger.datetime.helper.PDTFactory;

/**
 * Test class for class {@link CViDATDD}.
 *
 * @author Philip Helger
 */
public final class CViDATDDTest
{
  /**
   * The test vectors of the Peppol ViDA TDD v1.1.0 specification, chapter "Invoice UUID
   * calculation".
   */
  @Test
  public void testCreateInvoiceUUIDSpecTestVectors ()
  {
    assertEquals ("62fd54e5-8689-57a6-95a7-600c86734ea0",
                  CViDATDD.createInvoiceUUID ("DE811569869",
                                              "380",
                                              "INV-2025-0046",
                                              PDTFactory.createLocalDate (2025, Month.FEBRUARY, 10)).toString ());
    assertEquals ("81270c84-bfb1-5a12-8314-8887ee7d3e27",
                  CViDATDD.createInvoiceUUID ("BE0477472701",
                                              "380",
                                              "INV-2025-0001",
                                              PDTFactory.createLocalDate (2025, Month.JANUARY, 10)).toString ());
    assertEquals ("936d1327-0863-5632-ac82-30001c004e01",
                  CViDATDD.createInvoiceUUID ("DE811569869",
                                              "389",
                                              "INV-2025-0046",
                                              PDTFactory.createLocalDate (2025, Month.FEBRUARY, 10)).toString ());
    assertEquals ("9a787cd0-ef99-5bd1-ad5d-daf020699cf2",
                  CViDATDD.createInvoiceUUID ("FR40303265045",
                                              "261",
                                              "CN-2025-0003",
                                              PDTFactory.createLocalDate (2025, Month.FEBRUARY, 5)).toString ());
  }

  /**
   * Only leading and trailing whitespace is removed - internal whitespace and casing are preserved.
   */
  @Test
  public void testCreateInvoiceUUIDNormalization ()
  {
    final String sExpected = CViDATDD.createInvoiceUUID ("DE811569869",
                                                         "380",
                                                         "INV-2025-0046",
                                                         PDTFactory.createLocalDate (2025, Month.FEBRUARY, 10))
                                     .toString ();
    assertEquals (sExpected,
                  CViDATDD.createInvoiceUUID ("\n        DE811569869\n      ",
                                              "  380  ",
                                              "\tINV-2025-0046 ",
                                              PDTFactory.createLocalDate (2025, Month.FEBRUARY, 10)).toString ());

    // An invoice number containing spaces must not be collapsed
    assertEquals ("a5bcb919-b2b9-535c-9c19-60a869f7615d",
                  CViDATDD.createInvoiceUUID ("ATU99887766",
                                              "380",
                                              "NW-HP-001 - AT-DK",
                                              PDTFactory.createLocalDate (2026, Month.FEBRUARY, 10)).toString ());
  }
}
