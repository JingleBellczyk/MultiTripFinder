package org.dyploma.airport;

import java.util.Map;

public class NameTransliterator {

    private static final Map<String, String> diacriticMap = Map.ofEntries(
            Map.entry("ü", "ue"),
            Map.entry("ä", "ae"),
            Map.entry("ö", "oe"),
            Map.entry("ß", "ss"),
            Map.entry("é", "e"),
            Map.entry("è", "e"),
            Map.entry("á", "a"),
            Map.entry("í", "i"),
            Map.entry("ó", "o"),
            Map.entry("ú", "u"),
            Map.entry("à", "a"),
            Map.entry("ç", "c"),
            Map.entry("ñ", "n"),
            Map.entry("š", "s"),
            Map.entry("č", "c"),
            Map.entry("ž", "z"),
            Map.entry("ň", "n"),
            Map.entry("ť", "t"),
            Map.entry("ď", "d"),
            Map.entry("ĺ", "l"),
            Map.entry("ł", "l")
    );


    public static String toLatinAlphabet(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            String currentChar = String.valueOf(input.charAt(i));

            result.append(diacriticMap.getOrDefault(currentChar, currentChar));
        }

        return result.toString();
    }
}
