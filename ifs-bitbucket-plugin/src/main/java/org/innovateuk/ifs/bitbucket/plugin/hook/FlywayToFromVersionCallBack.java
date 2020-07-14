package org.innovateuk.ifs.bitbucket.plugin.hook;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that gets called back with the Flyway versions from the current branch and that to be merged to and determines
 * whether the merge should go ahead.
 */
public class FlywayToFromVersionCallBack {

    private static final FlywayVersionComparator FLYWAY_VERSION_COMPARATOR = new FlywayVersionComparator();

    /**
     * List of Flyway patch numbers and file names on the branch being pulled to.
     */
    private List<Pair<String, List<Integer>>> toVersions = new ArrayList<>();

    /**
     * List of Flyway patch numbers and file names on the branch which the pull request came from.
     */
    private List<Pair<String, List<Integer>>> fromVersions = new ArrayList<>();

    private final List<String> errors = new ArrayList<>();

    public void onTo(final List<Pair<String, List<Integer>>> sortedVersions) {
        toVersions = sortedVersions;
        compareVersions();
    }

    public void onFrom(final List<Pair<String, List<Integer>>> sortedVersions) {
        fromVersions = sortedVersions;
        compareVersions();
    }

    void compareVersions() {
        if (!toVersions.isEmpty() && !fromVersions.isEmpty()) {
            final List<Pair<String, List<Integer>>> newFromVersions = new ArrayList<>(fromVersions);
            newFromVersions.removeAll(toVersions);
            final Pair<String, List<Integer>> toMaxVersion = toVersions.get(toVersions.size() -1);
            if(!newFromVersions.isEmpty()) {
                final Pair<String, List<Integer>> newFromMin = newFromVersions.get(0);
                if (FLYWAY_VERSION_COMPARATOR.compare(toMaxVersion.getValue(), newFromMin.getValue()) >= 0) {
                    errors.add("Flyway patch level error, please increase the level of the flyway patches " +
                            "in the incoming branch to be greater than the patch " + toMaxVersion.getKey() + ". Currently the " +
                            "lowest incoming patch is " + newFromMin.getKey());
                }
            }
        }
    }

    public List<String> getErrors() {
        return errors;
    }
}
