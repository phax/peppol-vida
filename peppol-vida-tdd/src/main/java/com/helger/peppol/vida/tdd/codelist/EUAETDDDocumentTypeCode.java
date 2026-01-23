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
 * UAE TDD Document Type Code.
 *
 * @author Philip Helger
 */
public enum EUAETDDDocumentTypeCode implements IHasID <String>
{
  /** Submitting a Tax Data Document */
  SUBMIT ("S"),
  /** TDD is sent to revise a previously reported TDD. */
  RESUBMIT ("R"),
  /** A previously reported TDD is withdrawn. */
  WITHDRAW ("W"),
  /** Document failed validation. */
  FAILED ("F");

  private final String m_sID;

  EUAETDDDocumentTypeCode (@NonNull @Nonempty final String sID)
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
  public static EUAETDDDocumentTypeCode getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EUAETDDDocumentTypeCode.class, sID);
  }
}
