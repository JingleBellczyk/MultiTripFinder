package org.dyploma.algorithm.algorithmGoogleAmadeus.comparators;

import org.dyploma.algorithm.algorithmGoogleAmadeus.Solution;

public class TransferTimeComparator implements SolutionComparator {
    @Override
    public int compare(Solution s1, Solution s2) {
        return Integer.compare(s2.getTransferTime(), s1.getTransferTime());
    }
}
