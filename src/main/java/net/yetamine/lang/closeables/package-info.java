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
 * Extensions and tools for managing resources.
 *
 * <p>
 * A <i>resource</i> is an object which needs explicit release when not needed
 * anymore in order to conserve system resources or even in order to something
 * happen (e.g., changes are reflected in the outer world). While the language
 * supports resources by <i>try-with-resources</i> statement, it does not care
 * about resources created on demand or legacy resources that do not inherit
 * from {@link java.lang.AutoCloseable}. This package aims to address both
 * problems, i.e., it provides adapting any resource to be usable with the
 * built-in <i>try-with-resources</i> statement and to create resources on
 * demand.
 */
package net.yetamine.lang.closeables;
