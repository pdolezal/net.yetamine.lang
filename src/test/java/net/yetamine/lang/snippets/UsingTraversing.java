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

package net.yetamine.lang.snippets;

import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.yetamine.lang.functional.Traversing;

/**
 * This class explains and demonstrates the sense of the {@link Traversing}
 * interface.
 *
 * <p>
 * This demo shows how an application can retrieve particular well-known
 * configuration entries from a structured configuration object. For the
 * simplicity, the configuration is made just as a tree of maps, but using JSON
 * instead would be very similar and based on the same principles (because it's
 * a tree of maps in its very core).
 */
public final class UsingTraversing {

    private static final int DEFAULT_PORT = 80;

    /**
     * Tests processing the given configuration.
     *
     * <p>
     * See the implementation of the other parts within the class to understand
     * the whole idean and the details.
     *
     * @param configuration
     *            the configuration object. It must not be {@code null}.
     */
    @Test(dataProvider = "configuration")
    public void demo(Node configuration) {
        // Retrive the connection address. This statement shows the full form of
        // a traversal through several nodes and the error handling for the case
        // of missing value. Thanks to Optional, there is no problem with missing
        // intermediate nodes. At the end, orElseThrow handles any missing entry.
        final String connectToAddress = NETWORK.apply(configuration)
                .flatMap(CONNECT)   // Get connection section
                .flatMap(ADDRESS)   // Get the address
                .orElseThrow(() -> new IllegalArgumentException("Missing connection address."));

        // The same as above, just using a default when the entry is missing
        final int connectToPort = NETWORK.apply(configuration).flatMap(CONNECT).flatMap(PORT).orElse(DEFAULT_PORT);

        // Of course, it is usually useless to repeate the identical path traversal.
        // Here, the node with the desired leaf entries is cached and all reads are
        // done on the cached object easily. Since we have default values for all
        // entries, we don't throw any exception, rather fill the defaults instead.
        final Optional<Node> listen = NETWORK.apply(configuration).flatMap(LISTEN);
        final String listenAtAddress = listen.flatMap(ADDRESS).orElse("localhost");
        final int listenAtPort = listen.flatMap(PORT).orElse(DEFAULT_PORT);

        // Now listen and connect
        Assert.assertEquals(connectToAddress, "yetamine.net");
        Assert.assertEquals(connectToPort, DEFAULT_PORT);

        Assert.assertEquals(listenAtAddress, "192.168.0.1");
        Assert.assertEquals(listenAtPort, 8080);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "configuration")
    public static Object[][] configuration() {
        // @formatter:off
        final Node result = new Node()
            .put("network", new Node()
                .put("listen", new Node()
                    .put("address", new Node("192.168.0.1"))
                    .put("port", new Node("8080"))
                )

                .put("connect", new Node()
                    .put("address", new Node("yetamine.net"))
                )
            );
        // @formatter:on

        return new Object[][] { { result } };
    }

    /**
     * A class representing a node of the hierarchical configuration.
     *
     * <p>
     * This class resembles much a JSON object and gives a hint how to apply the
     * pattern even to your favourite JSON library without any need to change it
     * or modify it. Notice that the interface is very basic; intentionally, to
     * emphasize the idea, it does not support retrieving any {@link Optional}
     * values, therefore the adaptation must take all the care: no help from the
     * {@link Node}, sorry.
     */
    static class Node {

        /** Children nodes of this node. */
        private final SortedMap<String, Node> children = new TreeMap<>();
        /** Value represented by this node, if any. */
        private String value;

        /**
         * Creates a new instance.
         *
         * @param representation
         *            the representation of this node's value. It must not be
         *            {@code null}.
         */
        public Node(String representation) {
            value = representation;
        }

        /**
         * Creates a new instance with no value.
         */
        public Node() {
            this(null);
        }

        /**
         * Sets the value of this node.
         *
         * @param representation
         *            the representation of the value
         *
         * @return this instance
         */
        public Node value(String representation) {
            value = representation;
            return this;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            final StringBuilder result = new StringBuilder();

            result.append('{');
            if (value != null) {
                result.append("value=").append(value);

                if (!children.isEmpty()) {
                    result.append(", ");
                }
            }

            if (!children.isEmpty()) {
                result.append("children=").append(children);
            }

            return result.append('}').toString();
        }

        /**
         * Returns the value of this node as a {@link String}.
         *
         * @return the value of this node as a {@link String}
         */
        public String string() {
            return value;
        }

        /**
         * Returns the value of this node as a {@link Integer}.
         *
         * @return the value of this node as a {@link Integer}
         *
         * @throws NumberFormatException
         *             if this node does not represent a number
         */
        public int integer() {
            return Integer.parseInt(value);
        }

        /**
         * Returns the live map with the children of this node.
         *
         * @return the live map with the children of this node
         */
        public SortedMap<String, Node> children() {
            return children;
        }

        /**
         * Puts a child into this node.
         *
         * @param name
         *            the name of the child. It must not be {@code null}.
         * @param child
         *            the child to put
         *
         * @return this instance
         */
        public Node put(String name, Node child) {
            children.put(name, child);
            return this;
        }
    }

    // Here are some factory methods for convenient definitions of constants
    // refering to nodes and values

    /**
     * Returns a {@link Traversing} instance for a {@link Node} of the given
     * name.
     *
     * @param name
     *            the name of the child node to find
     *
     * @return a {@link Traversing} instance for a {@link Node} of the given
     *         name
     */
    static Traversing<Node, Node> node(String name) {
        return Traversing.of(node -> node.children().get(name));
    }

    // Some example definitions of the node/value constants

    static final Traversing<Node, Node> NETWORK = node("network");
    static final Traversing<Node, Node> LISTEN = node("listen");
    static final Traversing<Node, Node> CONNECT = node("connect");
    static final Traversing<Node, String> ADDRESS = node("address").map(Node::string);
    static final Traversing<Node, Integer> PORT = node("port").map(Node::integer);
}
