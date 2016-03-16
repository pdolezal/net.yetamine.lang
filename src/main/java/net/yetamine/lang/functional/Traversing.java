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

package net.yetamine.lang.functional;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A specialized {@link Function} variant designed for {@code null}-safe
 * traversal through data structures like trees and other kinds of graphs.
 *
 * <p>
 * This interface allows to use the pattern that following example demonstrates.
 * Let's have a hierarchical structure of configuration data with named entries,
 * that form natually a tree structure (e.g., from JSON, XML or another format):
 *
 * <pre>
 * interface Node {
 *     Node child(String name);
 *     String value();
 * }
 * </pre>
 *
 * Getting a value from a node in depth is not very comfortable without using
 * the pattern described further:
 *
 * <pre>
 * // Let's get the address and port to listen at from network/listen/address
 * // and network/listen.port entries:
 *
 * final String address;
 * final int port;
 *
 * Node current = configuration.child("network");
 * if (current != null) {
 *     current = current.child("listen");
 *     if (child != null) {
 *         Node node = current.child("address");
 *         if (node != null) {
 *            address = node.value();
 *         } else {
 *            address = "localhost";
 *         }
 *
 *         node = current.child("port");
 *         if (node != null) {
 *             port = Integer.valueOf(node.value());
 *         } else {
 *             port = DEFAULT_PORT;
 *         }
 *     }
 * }
 * </pre>
 *
 * Implementing a utility method, that gets a path in the structure and finds
 * the node if it exists, is definitely a solution for many cases, but solves
 * just one particular problem. Still, there is the problem of missing values,
 * getting and transforming them etc., specially when the node interface does
 * not offer rich support for that.
 *
 * <p>
 * A solution is falling back to {@link Optional} and {@link Function}, which
 * are ubiquitous, and rather adapting the use of a lean node interface than
 * requiring the node interface to solve everything. This is especially useful
 * in cases like the example above where particular entries in a possibly much
 * richer structure are well-known and the code accesses them using constants.
 * The constants could be more than just the names of the nodes: a node-specific
 * access strategies.
 *
 * <p>
 * The example above could be solved then in the following way:
 *
 * <pre>
 * static final Traversing&gt;Node, Node&lt; NETWORK = n -&lt; Optional.ofNullable(n.child("network"));
 * static final Traversing&gt;Node, Node&lt; LISTEN = n -&lt; Optional.ofNullable(n.child("listen"));
 * static final Traversing&gt;Node, String&lt; ADDRESS = n -&lt; Optional.ofNullable(n.child("address")).map(Node::value);
 * static final Traversing&gt;Node, Integer&lt; PORT = n -&lt; Optional.ofNullable(n.child("port")).map(Node::value).map(Integer::valueOf);
 * </pre>
 *
 * Of course, instead of such ad-hoc definitions, a set of definitions for the
 * most commonly used types could be pre-defined. For instance, the {@code PORT}
 * constant could be defined as:
 *
 * <pre>
 * static final Traversing&gt;Node, Integer&lt; PORT = integerValue("port");
 *
 * // Where following definition of 'integerValue' would be in the scope:
 * static Traversing&gt;Node, Integer&lt; integerValue(String name) {
 *     return node -&lt; Optional.ofNullable(node.child(name)).map(Node::value).map(Integer::valueOf);
 * }
 * </pre>
 *
 * Finally, when having such constants, the original code snippet for retrieving
 * the address and port may look like this:
 *
 * <pre>
 * final Optional&gt;Node&lt; listen = NETWORK.apply(configuration).flatMap(LISTEN);
 * final String address = listen.flatMap(ADDRESS).orElse("localhost");
 * final int port = listen.flatMap(PORT).orElse(DEFAULT_PORT);
 * </pre>
 *
 * Hence, the traversal through the structure can be performed easily as a
 * sequence of {@link Optional#flatMap(Function)} with elements of the path as
 * the mapping functions: {@code root.flatMap(node1).flatMap(node2)}. And this
 * is the actual sense of this interface: to enable this pattern and indicate
 * for an implementation that the implementation supports and encourages it.
 *
 * @param <C>
 *            the structure or container to traverse through using this instance
 * @param <V>
 *            the value provided as the result of asking the container for
 *            traversal
 */
@FunctionalInterface
public interface Traversing<C, V> extends Function<C, Optional<V>> {

    /**
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    Optional<V> apply(C t);

    /**
     * Makes an instance that uses this instance to get a result to map then
     * with the given mapping function.
     *
     * <p>
     * The implementation is equivalent to {@code apply(t).map(f)} where
     * {@code t} is the parameter of resulting {@link Traversing} instance.
     *
     * @param <T>
     *            the type of the value provided by the resulting
     *            {@link Traversing} instance
     * @param f
     *            the given mapping function. It must not be {@code null}.
     *
     * @return a mapping composition of this instance and the given function
     */
    default <T> Traversing<C, T> map(Function<? super V, ? extends T> f) {
        Objects.requireNonNull(f);
        return t -> apply(t).map(f);
    }
}
