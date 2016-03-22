/*
 * Copyright 2016 Yetamine
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Collection extensions.
 *
 * <h1>Fluent interface extensions</h1>
 *
 * While Java Collections Framework is great, there are some tasks where its
 * interfaces are not fluent enough. The typical example is initialization of
 * collection instances, especially constants.
 *
 * <p>
 * To cope with such cases, this package provides a set of adapters that provide
 * fluent interface extensions in addition to the usual methods. The design of
 * the adapters consists of two parts: one part provides just the extensions,
 * while the other combines the extended interface with the extensions. Then
 * implementations of the adapters are made quite using the default methods.
 * Making custom adapters is therefore easy if ever needed. The split allows
 * making mixins as well.
 */
package net.yetamine.lang.collections;
