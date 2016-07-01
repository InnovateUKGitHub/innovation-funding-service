package com.worth.ifs.bitbucket.plugin.hook;

import com.atlassian.bitbucket.scm.pull.MergeRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that gets called back with the Flyway versions from the current branch and that to be merged to and determines
 * whether the merge should go ahead.
 */
public class FlywayToFromVersionCallBack {

    private static FlywayVersionComparator FLYWAY_VERSION_COMPARATOR = new FlywayVersionComparator();
    private final MergeRequest request;

    /**
     * List of Flyway patch numbers and file names on the branch being pulled to.
     */
    private List<Pair<String, List<Integer>>> toVersions = new ArrayList<Pair<String, List<Integer>>>();

    /**
     * List of Flyway patch numbers and file names on the branch which the pull request came from.
     */
    private List<Pair<String, List<Integer>>> fromVersions = new ArrayList<Pair<String, List<Integer>>>();

    public FlywayToFromVersionCallBack(final MergeRequest request) {
        this.request = request;
    }

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
            final List<Pair<String, List<Integer>>> newFromVersions = new ArrayList<Pair<String, List<Integer>>>(fromVersions);
            newFromVersions.removeAll(toVersions);
            final Pair<String, List<Integer>> toMaxVersion = toVersions.get(toVersions.size() -1);
            if(!newFromVersions.isEmpty()) {
                final Pair<String, List<Integer>> newFromMin = newFromVersions.get(0);
                if (FLYWAY_VERSION_COMPARATOR.compare(toMaxVersion.getValue(), newFromMin.getValue()) >= 0) {
                    setErrors("Flyway patching number error, please up the patching level. Max current level: " + toMaxVersion.getKey());
                }
            }
        }
    }

    void setErrors(final String error) {
        request.veto(error, error);
    }
}