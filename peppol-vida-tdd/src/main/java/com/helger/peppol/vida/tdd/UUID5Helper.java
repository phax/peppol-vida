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

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;

/**
 * This class contains static methods that leverage {@link java.util.UUID} and
 * {@link java.security.MessageDigest} to create version-5 UUIDs with full namespace support.
 * <p>
 * The UUID class provided by java.util is suitable as a datatype for UUIDs of any version, but
 * lacks methods for creating version 5 (SHA-1 based) UUIDs. Its implementation of version 3 (MD5
 * based) UUIDs also lacks build-in namespace support.
 * <p>
 * This class was informed by <a href="http://www.ietf.org/rfc/rfc4122.txt">RFC 4122</a>. Since RFC
 * 4122 is vague on how a 160-bit hash is turned into the 122 free bits of a UUID (6 bits being used
 * for version and variant information), this class was modelled after java.util.UUID's type-3
 * implementation and validated against the D language's phobos library
 * <a href="http://dlang.org/phobos/std_uuid.html">std.uuid</a>, which in turn was modelled after
 * the Boost project's
 * <a href="http://www.boost.org/doc/libs/1_42_0/libs/uuid/uuid.html" >boost.uuid</a>; and also
 * validated against the Python language's
 * <a href="http://docs.python.org/2/library/uuid.html">uuid</a> library.<br>
 * <br>
 * Source:
 * https://github.com/rootsdev/polygenea/blob/master/java/src/org/rootsdev/polygenea/UUID5.java
 *
 * @see java.util.UUID
 * @see java.security.MessageDigest
 * @author Luther Tychonievich. Released into the public domain. I would consider it a courtesy if
 *         you cite me if you benefit from this code.
 * @author Philip Helger
 */
@Immutable
public final class UUID5Helper
{
  /**
   * The following namespace is a type-4 UUID from the Peppol ViDA Pilot solution architecture
   */
  public static final UUID PEPPOL_VIDA_NAMESPACE = UUID.fromString ("e0bc4ac8-b025-46e5-a76d-0c893fc3027e");

  private UUID5Helper ()
  {}

  /**
   * A helper method for making UUID objects, which in java store longs not bytes
   *
   * @param aSrc
   *        An array of bytes having at least offset+8 elements
   * @param nOfs
   *        Where to start extracting a long
   * @param aByteOrder
   *        either ByteOrder.BIG_ENDIAN or ByteOrder.LITTLE_ENDIAN
   * @return a long, the specified endianness of which matches the bytes in src[offset,offset+8]
   */
  private static long _peekLong (final byte @NonNull [] aSrc, final int nOfs, @NonNull final ByteOrder aByteOrder)
  {
    long ret = 0;
    if (aByteOrder == ByteOrder.BIG_ENDIAN)
    {
      for (int i = nOfs; i < nOfs + 8; i += 1)
      {
        ret <<= 8;
        ret |= aSrc[i] & 0xffL;
      }
    }
    else
    {
      for (int i = nOfs + 7; i >= nOfs; i -= 1)
      {
        ret <<= 8;
        ret |= aSrc[i] & 0xffL;
      }
    }
    return ret;
  }

  /**
   * A private method from UUID pulled out here so we have access to it.
   *
   * @param hash
   *        A 16 (or more) byte array to be the basis of the UUID
   * @param version
   *        The version number to replace 4 bits of the hash (the variant code will replace 2 more
   *        bits))
   * @return A UUID object
   */
  private static UUID _makeUUID (final byte [] hash, final int version)
  {
    long msb = _peekLong (hash, 0, ByteOrder.BIG_ENDIAN);
    long lsb = _peekLong (hash, 8, ByteOrder.BIG_ENDIAN);
    // Set the version field
    msb &= ~(0xfL << 12);
    msb |= ((long) version) << 12;
    // Set the variant field to 2
    lsb &= ~(0x3L << 62);
    lsb |= 2L << 62;
    return new UUID (msb, lsb);
  }

  /**
   * Similar to UUID.nameUUIDFromBytes, but does version 5 (sha-1) not version 3 (md5)
   *
   * @param aNameBytes
   *        The bytes to use as the "name" of this hash
   * @return the UUID object and never <code>null</code>.
   */
  @NonNull
  public static UUID fromBytes (final byte @NonNull [] aNameBytes)
  {
    ValueEnforcer.notNull (aNameBytes, "Name");

    try
    {
      final MessageDigest aMD = MessageDigest.getInstance ("SHA-1");
      return _makeUUID (aMD.digest (aNameBytes), 5);
    }
    catch (final NoSuchAlgorithmException ex)
    {
      throw new IllegalStateException (ex);
    }
  }

  /**
   * A helper method for writing uuid objects, which in java store longs not bytes
   *
   * @param nData
   *        A long to write into the dest array
   * @param aDest
   *        An array of bytes having at least offset+8 elements
   * @param nOfs
   *        Where to start writing a long
   * @param aByteOrder
   *        either ByteOrder.BIG_ENDIAN or ByteOrder.LITTLE_ENDIAN
   */
  private static void _putLong (final long nData,
                                final byte @NonNull [] aDest,
                                final int nOfs,
                                @NonNull final ByteOrder aByteOrder)
  {
    long nRealData = nData;
    if (aByteOrder == ByteOrder.BIG_ENDIAN)
    {
      for (int i = nOfs + 7; i >= nOfs; i -= 1)
      {
        aDest[i] = (byte) (nRealData & 0xff);
        nRealData >>= 8;
      }
    }
    else
    {
      for (int i = nOfs; i < nOfs + 8; i += 1)
      {
        aDest[i] = (byte) (nRealData & 0xff);
        nRealData >>= 8;
      }
    }
  }

  /**
   * A helper method for reading uuid objects, which in java store longs not bytes
   *
   * @param nData
   *        a long to convert to bytes
   * @param aByteOrder
   *        either ByteOrder.BIG_ENDIAN or ByteOrder.LITTLE_ENDIAN
   * @return an array of 8 bytes
   */
  private static byte [] _asBytes (final long nData, final ByteOrder aByteOrder)
  {
    final byte [] ans = new byte [8];
    _putLong (nData, ans, 0, aByteOrder);
    return ans;
  }

  /**
   * Similar to UUID.nameUUIDFromBytes, but does version 5 (sha-1) not version 3 (md5) and uses a
   * namespace
   *
   * @param aNamespace
   *        The namespace to use for this UUID. If null, uses 00000000-0000-0000-0000-000000000000
   * @param aNameBytes
   *        The bytes to use as the "name" of this hash
   * @return the UUID object
   */
  public static UUID fromBytes (@Nullable final UUID aNamespace, final byte @NonNull [] aNameBytes)
  {
    ValueEnforcer.notNull (aNameBytes, "NameBytes");

    try
    {
      final MessageDigest aMD = MessageDigest.getInstance ("SHA-1");
      if (aNamespace == null)
      {
        aMD.update (new byte [16]);
      }
      else
      {
        aMD.update (_asBytes (aNamespace.getMostSignificantBits (), ByteOrder.BIG_ENDIAN));
        aMD.update (_asBytes (aNamespace.getLeastSignificantBits (), ByteOrder.BIG_ENDIAN));
      }
      return _makeUUID (aMD.digest (aNameBytes), 5);
    }
    catch (final NoSuchAlgorithmException e)
    {
      throw new AssertionError (e);
    }
  }

  /**
   * Similar to UUID.nameUUIDFromBytes, but does version 5 (sha-1) not version 3 (md5)
   *
   * @param sName
   *        The string to be encoded in utf-8 to get the bytes to hash
   * @return the UUID object
   */
  @NonNull
  public static UUID fromUTF8 (@NonNull final String sName)
  {
    return fromBytes (sName.getBytes (StandardCharsets.UTF_8));
  }

  /**
   * Similar to UUID.nameUUIDFromBytes, but does version 5 (sha-1) not version 3 (md5) and uses a
   * namespace
   *
   * @param aNamespace
   *        The namespace to use for this UUID. If null, uses 00000000-0000-0000-0000-000000000000
   * @param sName
   *        The string to be encoded in utf-8 to get the bytes to hash
   * @return the UUID object
   */
  public static UUID fromUTF8 (@Nullable final UUID aNamespace, final String sName)
  {
    return fromBytes (aNamespace, sName.getBytes (StandardCharsets.UTF_8));
  }
}
