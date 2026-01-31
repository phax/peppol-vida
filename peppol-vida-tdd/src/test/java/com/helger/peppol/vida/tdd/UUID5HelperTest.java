package com.helger.peppol.vida.tdd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

/**
 * Test class for class {@link UUID5Helper}
 *
 * @author Philip Helger
 */
public final class UUID5HelperTest
{
  @Test
  public void testSpecificCase1 ()
  {
    final UUID aNSUUID = UUID.fromString ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    assertNotNull (aNSUUID);
    final UUID aUUID5 = UUID5Helper.fromUTF8 (aNSUUID, "380 33445566 2026-01-13 5060012349998 0088");
    assertNotNull (aUUID5);
    assertEquals ("8aeba72d-2253-57fe-86f0-7c35648eb808", aUUID5.toString ());
  }
}
