package org.dyploma.algorithm.amadeus;

import org.dyploma.search.place.PlaceInSearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//mozna usunac
public class PlacesPermutationUtils {

    public static <T> List<List<T>> generateAllRecursive(int n, List<T> elements, T start, T end) {
        List<List<T>> permutations = new ArrayList<>();

        if (n == 1) {
            elements.add(0, start);
            elements.add(end);
            permutations.add(new ArrayList<>(elements));
        } else {
            for (int i = 0; i < n - 1; i++) {
                permutations.addAll(generateAllRecursive(n - 1, elements, start, end));
                if (n % 2 == 0) {
                    Collections.swap(elements, i, n - 1);
                } else {
                    Collections.swap(elements, 0, n - 1);
                }
            }
            permutations.addAll(generateAllRecursive(n - 1, elements, start, end)); // Dodajemy końcowe permutacje
        }

        return permutations;
    }
}
