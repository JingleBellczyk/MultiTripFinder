package org.dyploma.algorithm.algorithmGoogleAmadeus;

import lombok.Data;
import org.dyploma.algorithm.algorithmGoogleAmadeus.comparators.SolutionComparator;
import org.dyploma.trip.domain.Trip;

/**
 * This class represents a solution for a trip, containing information about
 * transfer time, cost, and the associated trip details. It is used for evaluating
 * and selecting the best solutions based on specific optimization criteria.
 */
@Data
public class Solution {

    private int transferTime;
    private double cost;
    private Trip trip;
    private final SolutionComparator comparator;

    public Solution(int transferTime, double cost, Trip trip, SolutionComparator comparator) {
        this.transferTime = transferTime;
        this.cost = cost;
        this.trip = trip;
        this.comparator = comparator;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "transferTime=" + transferTime +
                ", cost=" + cost +
                '}';
    }
}

