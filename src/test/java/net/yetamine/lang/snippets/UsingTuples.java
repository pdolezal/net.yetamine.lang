/*
 * Copyright 2017 Yetamine
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

import java.util.Comparator;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.containers.tuples.Tuple2;
import net.yetamine.lang.functional.Single;

/**
 * A small demonstration of using tuples in a functional style.
 */
public final class UsingTuples {

    /**
     * Finds a word with the most vowels in an English text and recognizes if
     * there is only a single such word.
     */
    @Test
    public void vowels() {
        final Single<Tuple2<String, Integer>> wordWithMostVowels = Stream.of(TEXT.split("[ .,;-]"))
                .map(w -> Tuple2.of(w, vowels(w)))
                .collect(Single.collector(Single.optimum(Comparator.comparing(Tuple2::get2))));

        wordWithMostVowels.single().ifTrue(() -> {
            final Tuple2<String, Integer> word = wordWithMostVowels.get();
            Assert.assertEquals(word.get1(), "exercitation");
            Assert.assertEquals(word.get2(), Integer.valueOf(6));
            // Computes the per cent of vowels in the word's characters
            Assert.assertEquals(word.map((w, v) -> v * 100 / w.length()), Integer.valueOf(50));
        }).ifFalse(() -> {
            Assert.fail("There is a single word with most vowels.");
        }).ifUnknown(() -> {
            Assert.fail("There is a word with most vowels.");
        });
    }

    private static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod "
            + "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation "
            + "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in "
            + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non "
            + "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    /**
     * Counts vowels in English text.
     *
     * @param text
     *            the text to analyze. It must not be {@code null}.
     *
     * @return the number of vowels found
     */
    private static int vowels(String text) {
        return (int) text.chars().filter(c -> {
            switch (Character.toLowerCase(c)) {
                case 'a':
                case 'e':
                case 'i':
                case 'o':
                case 'u':
                    return true;

                default:
                    return false;
            }
        }).count();
    }
}
