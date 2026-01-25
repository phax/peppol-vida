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
package com.helger.peppol.vida.tddv090;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringHelper;
import com.helger.peppol.vida.tdd.v090.cac.CardAccount;
import com.helger.peppol.vida.tdd.v090.cac.FinancialInstitutionBranch;
import com.helger.peppol.vida.tdd.v090.cac.PaymentMeans;
import com.helger.peppol.vida.tdd.v090.cac.PaymentMeans.PayeeFinancialAccount;
import com.helger.peppol.vida.tdd.v090.cbc.PaymentMeansCode;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CardAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.FinancialAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PaymentMeansCodeType;

/**
 * Builder for Peppol ViDA pilot TDD 0.9.0 sub element called "PaymentMeans".
 *
 * @author Philip Helger
 */
public class PeppolViDATDD090PaymentMeansBuilder implements IBuilder <PaymentMeans>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolViDATDD090PaymentMeansBuilder.class);

  private String m_sPaymentMeansCode;
  private String m_sPaymentMeansCodeName;
  private String m_sPaymentID;
  private String m_sCardPrimaryAccountNumberID;
  private String m_sCardNetworkID;
  private String m_sCardHolderName;
  private String m_sPayeeFinancialAccountID;
  private String m_sPayeeFinancialAccountIDScheme;
  private String m_sPayeeFinancialInstitutionBranchID;
  private String m_sPayeeFinancialInstitutionBranchIDScheme;
  // PaymentMandate has no type in 0.9.0

  public PeppolViDATDD090PaymentMeansBuilder ()
  {}

  /**
   * Set all fields from the provided UBL 2.1 object
   *
   * @param aObj
   *        The UBL object to read from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder initFromUBL (@NonNull final PaymentMeansType aObj)
  {
    ValueEnforcer.notNull (aObj, "PaymentMeans");

    final PaymentMeansCodeType aPMC = aObj.getPaymentMeansCode ();
    if (aPMC != null)
    {
      paymentMeansCode (aPMC.getValue ());
      paymentMeansCodeName (aPMC.getName ());
    }

    if (aObj.hasPaymentIDEntries ())
      paymentID (aObj.getPaymentIDAtIndex (0).getValue ());

    final CardAccountType aCA = aObj.getCardAccount ();
    if (aCA != null)
    {
      cardPrimaryAccountNumberID (aCA.getPrimaryAccountNumberIDValue ());
      cardNetworkID (aCA.getNetworkIDValue ());
      cardHolderName (aCA.getHolderNameValue ());
    }

    final FinancialAccountType aPFA = aObj.getPayeeFinancialAccount ();
    if (aPFA != null)
    {
      IDType aID = aPFA.getID ();
      if (aID != null)
      {
        payeeFinancialAccountID (aID.getValue ());
        payeeFinancialAccountIDScheme (aID.getSchemeID ());
      }

      final BranchType aFIB = aPFA.getFinancialInstitutionBranch ();
      if (aFIB != null)
      {
        aID = aFIB.getID ();
        if (aID != null)
        {
          payeeFinancialInstitutionBranchID (aID.getValue ());
          payeeFinancialInstitutionBranchIDScheme (aID.getSchemeID ());
        }
      }
    }

    return this;
  }

  @Nullable
  public String paymentMeansCode ()
  {
    return m_sPaymentMeansCode;
  }

  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder paymentMeansCode (@Nullable final String s)
  {
    m_sPaymentMeansCode = s;
    return this;
  }

  @Nullable
  public String paymentMeansCodeName ()
  {
    return m_sPaymentMeansCodeName;
  }

  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder paymentMeansCodeName (@Nullable final String s)
  {
    m_sPaymentMeansCodeName = s;
    return this;
  }

  @Nullable
  public String paymentID ()
  {
    return m_sPaymentID;
  }

  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder paymentID (@Nullable final String s)
  {
    m_sPaymentID = s;
    return this;
  }

  @Nullable
  public String cardPrimaryAccountNumberID ()
  {
    return m_sCardPrimaryAccountNumberID;
  }

  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder cardPrimaryAccountNumberID (@Nullable final String s)
  {
    m_sCardPrimaryAccountNumberID = s;
    return this;
  }

  @Nullable
  public String cardNetworkID ()
  {
    return m_sCardNetworkID;
  }

  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder cardNetworkID (@Nullable final String s)
  {
    m_sCardNetworkID = s;
    return this;
  }

  @Nullable
  public String cardHolderName ()
  {
    return m_sCardHolderName;
  }

  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder cardHolderName (@Nullable final String s)
  {
    m_sCardHolderName = s;
    return this;
  }

  @Nullable
  public String payeeFinancialAccountID ()
  {
    return m_sPayeeFinancialAccountID;
  }

  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder payeeFinancialAccountID (@Nullable final String s)
  {
    m_sPayeeFinancialAccountID = s;
    return this;
  }

  @Nullable
  public String payeeFinancialAccountIDScheme ()
  {
    return m_sPayeeFinancialAccountIDScheme;
  }

  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder payeeFinancialAccountIDScheme (@Nullable final String s)
  {
    m_sPayeeFinancialAccountIDScheme = s;
    return this;
  }

  @Nullable
  public String payeeFinancialInstitutionBranchID ()
  {
    return m_sPayeeFinancialInstitutionBranchID;
  }

  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder payeeFinancialInstitutionBranchID (@Nullable final String s)
  {
    m_sPayeeFinancialInstitutionBranchID = s;
    return this;
  }

  @Nullable
  public String payeeFinancialInstitutionBranchIDScheme ()
  {
    return m_sPayeeFinancialInstitutionBranchIDScheme;
  }

  @NonNull
  public PeppolViDATDD090PaymentMeansBuilder payeeFinancialInstitutionBranchIDScheme (@Nullable final String s)
  {
    m_sPayeeFinancialInstitutionBranchIDScheme = s;
    return this;
  }

  private boolean _isEveryRequiredFieldSet (final boolean bDoLogOnError, @NonNull final MutableInt aReportedDocsErrs)
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, bDoLogOnError);
    final String sErrorPrefix = "Error in Peppol ViDA pilot TDD 0.9.0 PaymentMeans builder: ";

    if (StringHelper.isEmpty (m_sPaymentMeansCode))
    {
      aCondLog.error (sErrorPrefix + "PaymentMeansCode is missing");
      aReportedDocsErrs.inc ();
    }
    // m_sPaymentMeansCodeName is optional
    // m_sPaymentID is optional
    // m_sCardPrimaryAccountNumberID is optional
    // m_sCardNetworkID is optional
    // m_sCardHolderName is optional
    // m_sPayeeFinancialAccountID is optional
    // m_sPayeeFinancialAccountIDScheme is optional
    // m_sPayeeFinancialInstitutionBranchID is optional
    // m_sPayeeFinancialInstitutionBranchIDScheme is optional

    return aReportedDocsErrs.intValue () == 0;
  }

  public boolean isEveryRequiredFieldSet (final boolean bDoLogOnError)
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    return _isEveryRequiredFieldSet (bDoLogOnError, aReportedDocErrs);
  }

  @Nullable
  public PaymentMeans build ()
  {
    final MutableInt aReportedDocErrs = new MutableInt (0);
    if (!_isEveryRequiredFieldSet (true, aReportedDocErrs))
    {
      LOGGER.error ("At least one mandatory field is not set and therefore the TDD PaymentMeans cannot be build.");
      return null;
    }

    final PaymentMeans ret = new PaymentMeans ();
    {
      final PaymentMeansCode aPMC = new PaymentMeansCode ();
      aPMC.setValue (m_sPaymentMeansCode);
      aPMC.setName (m_sPaymentMeansCodeName);
      ret.setPaymentMeansCode (aPMC);
    }
    ret.setPaymentID (m_sPaymentID);
    if (StringHelper.isNotEmpty (m_sCardPrimaryAccountNumberID))
    {
      final CardAccount aCA = new CardAccount ();
      aCA.setPrimaryAccountNumberID (m_sCardPrimaryAccountNumberID);
      aCA.setNetworkID (m_sCardNetworkID);
      aCA.setHolderName (m_sCardHolderName);
      ret.setCardAccount (aCA);
    }
    if (StringHelper.isNotEmpty (m_sPayeeFinancialAccountID))
    {
      final PayeeFinancialAccount aPFA = new PayeeFinancialAccount ();
      aPFA.setID (m_sPayeeFinancialAccountID).setSchemeID (m_sPayeeFinancialAccountIDScheme);
      if (StringHelper.isNotEmpty (m_sPayeeFinancialInstitutionBranchID))
      {
        final FinancialInstitutionBranch aFIB = new FinancialInstitutionBranch ();
        aFIB.setID (m_sPayeeFinancialInstitutionBranchID).setSchemeID (m_sPayeeFinancialInstitutionBranchIDScheme);
        aPFA.setFinancialInstitutionBranch (aFIB);
      }
      ret.setPayeeFinancialAccount (aPFA);
    }
    return ret;
  }
}
