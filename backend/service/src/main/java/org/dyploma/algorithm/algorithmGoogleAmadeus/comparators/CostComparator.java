package org.dyploma.algorithm.algorithmGoogleAmadeus.comparators;

import org.dyploma.algorithm.algorithmGoogleAmadeus.Solution;

public class CostComparator implements SolutionComparator {
    @Override
    public int compare(Solution s1, Solution s2) {
        return Double.compare(s2.getCost(), s1.getCost());
    }
}
