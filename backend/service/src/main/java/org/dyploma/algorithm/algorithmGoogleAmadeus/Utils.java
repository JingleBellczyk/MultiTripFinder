package org.dyploma.algorithm.algorithmGoogleAmadeus;

import org.dyploma.trip.domain.Trip;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    private static final int MAX_BEST_SOLUTIONS_SIZE = 5;

    /**
     * this method prepare data before permutations
     */
    public static List<List<PlacesWithAirport>> permutePlacesWithAirport(List<PlacesWithAirport> placesWithAirportTable) {

        if (placesWithAirportTable.size() == 2) {
            List<List<PlacesWithAirport>> permutations = new ArrayList<>();
            permutations.add(placesWithAirportTable);
            return permutations;
        }

        PlacesWithAirport startPlace = placesWithAirportTable.get(0);
        PlacesWithAirport endPlace = placesWithAirportTable.get(placesWithAirportTable.size() - 1);
        List<PlacesWithAirport> middleSection = placesWithAirportTable.subList(1, placesWithAirportTable.size() - 1);

        return generateAllRecursive(middleSection.size(), middleSection, startPlace, endPlace);
    }

    /**
     * Generates all possible permutations of the given elements,
     * with start and end elements added to each permutation.
     *
     * @param n the number of elements to permute
     * @param elements the elements to be permuted
     * @param start an element to add at the beginning of each permutation
     * @param end an element to add at the end of each permutation
     * @param <T> the type of elements being permuted
     * @return a list of permutations with the start and end elements included
     */
    public static <T> List<List<T>> generateAllRecursive(int n, List<T> elements, T start, T end) {
        List<List<T>> permutations = new ArrayList<>();

        if (n == 1) {
            List<T> elementsResult = new ArrayList<>(elements);
            elementsResult.add(0, start);
            elementsResult.add(end);

            permutations.add(new ArrayList<>(elementsResult));
        } else {
            for (int i = 0; i < n - 1; i++) {
                permutations.addAll(generateAllRecursive(n - 1, elements, start, end));
                if (n % 2 == 0) {
                    Collections.swap(elements, i, n - 1);
                } else {
                    Collections.swap(elements, 0, n - 1);
                }
            }
            permutations.addAll(generateAllRecursive(n - 1, elements, start, end));
        }

        return permutations;
    }

    /**
     * Utility methods for working with date and time. This includes:
     * - Converting a LocalDate to a LocalDateTime set to the start of the day.
     * - Adding a specified number of hours to a LocalDateTime.
     * - Calculating the duration between two LocalDateTime instances in hours or minutes.
     */

    public static LocalDateTime convertToLocalDateTime(LocalDate date) {

        return date.atStartOfDay();
    }

    public static LocalDateTime addHoursToDate(LocalDateTime dateTime, long hoursToAdd) {

        return dateTime.plusHours(hoursToAdd);
    }

    public static int calculateDurationInHours(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return (int) Duration.between(startLocalDateTime, endLocalDateTime).toHours();
    }

    public static int calculateDurationInMinutes(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return (int) Duration.between(startLocalDateTime, endLocalDateTime).toMinutes();
    }


    /**
     * Methods for operations on queue with best solutions
     * - addSolution - adds solution in correct place in queue
     * - compareSolutions - compares solutions based on comparator
     */
    public static void addSolution(PriorityQueue<Solution> bestSolutions, Solution newSolution) {
        if (bestSolutions.size() < MAX_BEST_SOLUTIONS_SIZE) {
            bestSolutions.add(newSolution);
        } else {
            Solution worstSolution = bestSolutions.peek();
            if (worstSolution != null && compareSolutions(bestSolutions, newSolution, worstSolution) > 0) {
                bestSolutions.poll();
                bestSolutions.add(newSolution);
            }
        }
    }

    private static int compareSolutions(PriorityQueue<Solution> queue, Solution newSolution, Solution worstSolution) {

        Comparator<? super Solution> comparator = queue.comparator();
        if (comparator != null) {
            return comparator.compare(newSolution, worstSolution);
        } else {
            throw new IllegalStateException("Queue does not have a comparator");
        }
    }

    public static List<Trip> getTripsFromQueue(PriorityQueue<Solution> queue) {

        return queue.stream()
                .sorted(queue.comparator().reversed())
                .map(solution -> solution.getTrip())
                .collect(Collectors.toList());
    }

    public static double getCurrentWorstCost(PriorityQueue<Solution> queue) {

        if (queue.isEmpty()) {
            return -1.0;
        }
        Solution worstSolution = queue.peek();
        return worstSolution.getCost();
    }

    public static int getCurrentWorstTransferTime(PriorityQueue<Solution> queue) {

        if (queue.isEmpty()) {
            return -1;
        }

        Solution worstSolution = queue.peek();
        return worstSolution.getTransferTime();
    }
}