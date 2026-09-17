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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.uuid.UUID5Helper;

/**
 * Peppol ViDA pilot TDD constants.
 *
 * @author Philip Helger
 * @since 0.1.4
 */
@Immutable
public final class CViDATDD
{
  /**
   * The following namespace is a type-4 UUID from the Peppol ViDA Pilot solution architecture
   */
  public static final UUID PEPPOL_VIDA_NAMESPACE = UUID.fromString ("e0bc4ac8-b025-46e5-a76d-0c893fc3027e");

  private CViDATDD ()
  {}

  @NonNull
  private static String _trimmed (@Nullable final String s)
  {
    return s == null ? "" : s.trim ();
  }

  /**
   * Calculate the Invoice UUID (TDT-017) as defined in the Peppol ViDA TDD v1.1.0 specification,
   * chapter "Invoice UUID calculation". It is a version 5 UUID over {@link #PEPPOL_VIDA_NAMESPACE}
   * and a name built from the four provided values. Each value is stripped of leading and trailing
   * whitespace only - internal whitespace and casing are preserved - and the values are joined with
   * exactly one space character.
   *
   * @param sSellerVATIdentifier
   *        Seller VAT identifier (BT-31). May be <code>null</code>.
   * @param sDocumentTypeCode
   *        Invoice type code (BT-03). May be <code>null</code>.
   * @param sDocumentNumber
   *        Invoice number (BT-01). May be <code>null</code>.
   * @param aIssueDate
   *        Invoice issue date (BT-02). May be <code>null</code>. Formatted as "YYYY-MM-DD".
   * @return The calculated Invoice UUID. Never <code>null</code>.
   * @since 0.11.0
   */
  @NonNull
  public static UUID createInvoiceUUID (@Nullable final String sSellerVATIdentifier,
                                        @Nullable final String sDocumentTypeCode,
                                        @Nullable final String sDocumentNumber,
                                        @Nullable final LocalDate aIssueDate)
  {
    final String sName = _trimmed (sSellerVATIdentifier) +
                         ' ' +
                         _trimmed (sDocumentTypeCode) +
                         ' ' +
                         _trimmed (sDocumentNumber) +
                         ' ' +
                         (aIssueDate == null ? "" : DateTimeFormatter.ISO_LOCAL_DATE.format (aIssueDate));
    return UUID5Helper.fromUTF8 (PEPPOL_VIDA_NAMESPACE, sName);
  }
}
