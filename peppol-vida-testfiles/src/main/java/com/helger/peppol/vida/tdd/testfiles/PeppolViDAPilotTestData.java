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
package com.helger.peppol.vida.tdd.testfiles;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.io.resource.ClassPathResource;

/**
 * A single test data package of the OpenPeppol ViDA Pilot Testing repository
 * (https://github.com/OpenPEPPOL/vida-pilot-testing/), consisting of the source Peppol BIS Billing
 * 3.0 document and the matching non-normative buy-side (C3) sample TDD.
 *
 * @author Philip Helger
 * @since 0.10.2
 */
@Immutable
public final class PeppolViDAPilotTestData
{
  private final String m_sScenarioID;
  private final String m_sJurisdictions;
  private final boolean m_bCreditNote;
  private final ClassPathResource m_aSourceRes;
  private final ClassPathResource m_aSampleTDDRes;

  public PeppolViDAPilotTestData (@NonNull @Nonempty final String sScenarioID,
                                  @NonNull @Nonempty final String sJurisdictions,
                                  final boolean bCreditNote,
                                  @NonNull final ClassPathResource aSourceRes,
                                  @NonNull final ClassPathResource aSampleTDDRes)
  {
    ValueEnforcer.notEmpty (sScenarioID, "ScenarioID");
    ValueEnforcer.notEmpty (sJurisdictions, "Jurisdictions");
    ValueEnforcer.notNull (aSourceRes, "SourceRes");
    ValueEnforcer.notNull (aSampleTDDRes, "SampleTDDRes");
    m_sScenarioID = sScenarioID;
    m_sJurisdictions = sJurisdictions;
    m_bCreditNote = bCreditNote;
    m_aSourceRes = aSourceRes;
    m_aSampleTDDRes = aSampleTDDRes;
  }

  /**
   * @return The test scenario identifier as defined in the Peppol ViDA Pilot Methodology and Testing
   *         document - e.g. <code>NW-HP-001</code>. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getScenarioID ()
  {
    return m_sScenarioID;
  }

  /**
   * @return The tax jurisdictions of seller and buyer, separated by a "-" - e.g.
   *         <code>AT-DK</code>. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getJurisdictions ()
  {
    return m_sJurisdictions;
  }

  /**
   * @return <code>true</code> if the source document is a UBL CreditNote, <code>false</code> if it
   *         is a UBL Invoice.
   */
  public boolean isCreditNote ()
  {
    return m_bCreditNote;
  }

  /**
   * @return The source Peppol BIS Billing 3.0 document. Never <code>null</code>.
   */
  @NonNull
  public ClassPathResource getSourceRes ()
  {
    return m_aSourceRes;
  }

  /**
   * @return The non-normative buy-side (C3) sample TDD matching the source document. Never
   *         <code>null</code>.
   */
  @NonNull
  public ClassPathResource getSampleTDDRes ()
  {
    return m_aSampleTDDRes;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ScenarioID", m_sScenarioID)
                                       .append ("Jurisdictions", m_sJurisdictions)
                                       .append ("CreditNote", m_bCreditNote)
                                       .append ("SourceRes", m_aSourceRes)
                                       .append ("SampleTDDRes", m_aSampleTDDRes)
                                       .getToString ();
  }
}
