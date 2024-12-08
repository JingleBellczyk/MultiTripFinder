package org.dyploma.algorithm.algorithmGoogleAmadeus.comparators.factory;

import org.dyploma.algorithm.algorithmGoogleAmadeus.comparators.CostComparator;
import org.dyploma.algorithm.algorithmGoogleAmadeus.comparators.SolutionComparator;
import org.dyploma.algorithm.algorithmGoogleAmadeus.comparators.TransferTimeComparator;
import org.dyploma.search.criteria.CriteriaMode;

public class SolutionComparatorFactory {

    public static SolutionComparator getComparator(CriteriaMode criteriaMode) {
        return switch (criteriaMode) {
            case DURATION -> new TransferTimeComparator();
            case PRICE -> new CostComparator();
        };
    }
}