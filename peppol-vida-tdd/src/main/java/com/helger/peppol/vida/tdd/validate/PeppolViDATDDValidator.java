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
package com.helger.peppol.vida.tdd.validate;

import java.util.Locale;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.version.Version;
import com.helger.diver.api.coord.DVRCoordinate;
import com.helger.diver.api.version.DVRVersion;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;
import com.helger.peppol.vida.tdd.jaxb.CPeppolViDATDD;
import com.helger.peppol.vida.tdd.jaxb.PeppolViDATDD100Marshaller;
import com.helger.peppol.vida.tdd.jaxb.PeppolViDATDD110Marshaller;
import com.helger.phive.api.execute.ValidationExecutionManager;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.api.executorset.ValidationExecutorSetRegistry;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.phive.api.validity.IValidityDeterminator;
import com.helger.phive.xml.executorset.VesXmlBuilder;
import com.helger.phive.xml.schematron.ValidationExecutorSchematronBuilder;
import com.helger.phive.xml.source.IValidationSourceXML;
import com.helger.phive.xml.source.ValidationSourceXML;
import com.helger.xml.namespace.MapBasedNamespaceContext;

/**
 * This class contains the Schematron resources for validating Peppol ViDA pilot TDD documents.
 *
 * @author Philip Helger
 */
@Immutable
public final class PeppolViDATDDValidator
{
  @NonNull
  private static ClassLoader _getCL ()
  {
    return CPeppolViDATDD.class.getClassLoader ();
  }

  public static final String GROUP_ID = "org.peppol.taxdata";
  public static final String ARTIFACT_ID = "vida";

  /**
   * VES ID for the Peppol ViDA Pilot TDD v1.0.0 rules.
   *
   * @deprecated Since 0.11.0 - use {@link #VID_TDD_VIDA_110} instead. It is only kept around to be
   *             able to validate legacy documents.
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  public static final DVRCoordinate VID_TDD_VIDA_100 = new DVRCoordinate (GROUP_ID,
                                                                          ARTIFACT_ID,
                                                                          DVRVersion.of (new Version (1, 0, 0)));
  private static final String PREFIX_100 = "external/schematron/2026-06-02/";

  /**
   * @deprecated Since 0.11.0 - use {@link #XSLT_CEN_TDD_110} instead
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  public static final IReadableResource XSLT_CEN_TDD_100 = new ClassPathResource (PREFIX_100 + "CEN-EN16931-UBL.xslt",
                                                                                  _getCL ());
  /**
   * @deprecated Since 0.11.0 - use {@link #XSLT_BILLING_TDD_110} instead
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  public static final IReadableResource XSLT_BILLING_TDD_100 = new ClassPathResource (PREFIX_100 +
                                                                                      "PEPPOL-EN16931-UBL.xslt",
                                                                                      _getCL ());
  /**
   * @deprecated Since 0.11.0 - use {@link #XSLT_VIDA_TDD_110} instead
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  public static final IReadableResource XSLT_VIDA_TDD_100 = new ClassPathResource (PREFIX_100 + "Peppol-ViDA-TDD.xslt",
                                                                                   _getCL ());

  // Aligned with phive-rules
  public static final DVRCoordinate VID_TDD_VIDA_110 = new DVRCoordinate (GROUP_ID,
                                                                          ARTIFACT_ID,
                                                                          DVRVersion.of (new Version (1, 1, 0)));
  private static final String PREFIX_110 = "external/schematron/2026-09-14/";
  public static final IReadableResource XSLT_CEN_TDD_110 = new ClassPathResource (PREFIX_110 + "CEN-EN16931-UBL.xslt",
                                                                                  _getCL ());
  public static final IReadableResource XSLT_BILLING_TDD_110 = new ClassPathResource (PREFIX_110 +
                                                                                      "PEPPOL-EN16931-UBL.xslt",
                                                                                      _getCL ());
  public static final IReadableResource XSLT_VIDA_TDD_110 = new ClassPathResource (PREFIX_110 + "Peppol-ViDA-TDD.xslt",
                                                                                   _getCL ());

  public static final ValidationExecutorSetRegistry <IValidationSourceXML> VES_REGISTRY = new ValidationExecutorSetRegistry <> ();

  static
  {
    final MapBasedNamespaceContext aNsCtx = PeppolViDATDD110Marshaller.createNamespaceContext ();

    // TDD v1.0.0 - deprecated, only kept around to be able to validate legacy documents
    VesXmlBuilder.builder ()
                 .vesID (VID_TDD_VIDA_100)
                 .displayName ("Peppol ViDA Pilot TDD 1.0.0")
                 .deprecated ()
                 .addXSD (PeppolViDATDD100Marshaller.getAllXSDs ())
                 .addSchematron (ValidationExecutorSchematronBuilder.xslt2 (XSLT_CEN_TDD_100)
                                                                    .namespaceContext (aNsCtx)
                                                                    .build ())
                 .addSchematron (ValidationExecutorSchematronBuilder.xslt2 (XSLT_BILLING_TDD_100)
                                                                    .namespaceContext (aNsCtx)
                                                                    .build ())
                 .addSchematron (ValidationExecutorSchematronBuilder.xslt2 (XSLT_VIDA_TDD_100)
                                                                    .namespaceContext (aNsCtx)
                                                                    .build ())
                 .registerInto (VES_REGISTRY);

    VesXmlBuilder.builder ()
                 .vesID (VID_TDD_VIDA_110)
                 .displayName ("Peppol ViDA Pilot TDD 1.1.0")
                 .notDeprecated ()
                 .addXSD (PeppolViDATDD110Marshaller.getAllXSDs ())
                 .addSchematron (ValidationExecutorSchematronBuilder.xslt2 (XSLT_CEN_TDD_110)
                                                                    .namespaceContext (aNsCtx)
                                                                    .build ())
                 .addSchematron (ValidationExecutorSchematronBuilder.xslt2 (XSLT_BILLING_TDD_110)
                                                                    .namespaceContext (aNsCtx)
                                                                    .build ())
                 .addSchematron (ValidationExecutorSchematronBuilder.xslt2 (XSLT_VIDA_TDD_110)
                                                                    .namespaceContext (aNsCtx)
                                                                    .build ())
                 .registerInto (VES_REGISTRY);
  }

  private PeppolViDATDDValidator ()
  {}

  /**
   * Validate against Schematron ViDA Pilot TDD v1.0.0 rules
   *
   * @param aXmlRes
   *        The XML resource to use. May not be <code>null</code>.
   * @return The Validation result list. Never <code>null</code>.
   * @deprecated Since 0.11.0 - use {@link #validateViDA_TDD_110(IReadableResource)} instead. It is
   *             only kept around to be able to validate legacy documents.
   */
  @Deprecated (since = "0.11.0", forRemoval = true)
  @NonNull
  public static ValidationResultList validateViDA_TDD_100 (@NonNull final IReadableResource aXmlRes)
  {
    final IValidationExecutorSet <IValidationSourceXML> aExecutors = VES_REGISTRY.getOfID (VID_TDD_VIDA_100);
    final IValidationSourceXML aSource = ValidationSourceXML.create (aXmlRes);
    return ValidationExecutionManager.executeValidation (IValidityDeterminator.createDefault (),
                                                         aExecutors,
                                                         aSource,
                                                         Locale.US);
  }

  /**
   * Validate against Schematron ViDA Pilot TDD v1.1.0 rules
   *
   * @param aXmlRes
   *        The XML resource to use. May not be <code>null</code>.
   * @return The Validation result list. Never <code>null</code>.
   */
  @NonNull
  public static ValidationResultList validateViDA_TDD_110 (@NonNull final IReadableResource aXmlRes)
  {
    final IValidationExecutorSet <IValidationSourceXML> aExecutors = VES_REGISTRY.getOfID (VID_TDD_VIDA_110);
    final IValidationSourceXML aSource = ValidationSourceXML.create (aXmlRes);
    return ValidationExecutionManager.executeValidation (IValidityDeterminator.createDefault (),
                                                         aExecutors,
                                                         aSource,
                                                         Locale.US);
  }
}
