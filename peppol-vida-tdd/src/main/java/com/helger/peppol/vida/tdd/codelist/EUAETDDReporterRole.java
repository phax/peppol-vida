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
package com.helger.peppol.vida.tdd.codelist;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

/**
 * UAE TDD Document Scope.
 *
 * @author Philip Helger
 */
public enum EUAETDDReporterRole implements IHasID <String>
{
  /**
   * The party, usually the seller, who is issuing and sending the reported document such as Invoice
   * or Credit Note.
   */
  SENDER ("01"),
  /**
   * The party, usually the buyer, who has received the reported document such as Invoice or Credit
   * Note.
   */
  RECEIVER ("02");

  private final String m_sID;

  EUAETDDReporterRole (@NonNull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nullable
  public static EUAETDDReporterRole getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EUAETDDReporterRole.class, sID);
  }
}
