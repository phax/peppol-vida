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

import java.util.UUID;

import com.helger.annotation.concurrent.Immutable;

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
}
