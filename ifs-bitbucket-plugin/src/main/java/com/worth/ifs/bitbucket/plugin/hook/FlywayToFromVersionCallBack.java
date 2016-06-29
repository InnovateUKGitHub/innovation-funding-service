package com.worth.ifs.bitbucket.plugin.hook;

import com.atlassian.bitbucket.scm.pull.MergeRequest;

import java.util.ArrayList;
import java.util.List;

public class FlywayToFromVersionCallBack {

    private final MergeRequest request;
    private List<Integer> maxToVersion = new ArrayList<>();
    private List<Integer> minFromVersion = new ArrayList<>();
    private static FlywayVersionComparator FLYWAY_VERSION_COMPARATOR = new FlywayVersionComparator();

    public FlywayToFromVersionCallBack(final MergeRequest request) {
        this.request = request;
    }

    public void onTo(final List<List<Integer>> sortedVersions) {
        maxToVersion = sortedVersions.isEmpty() ? new ArrayList<>() : sortedVersions.get(sortedVersions.size() - 1);
        compareVersions();
    }

    public void onFrom(final List<List<Integer>> sortedVersions) {
        minFromVersion = sortedVersions.isEmpty() ? new ArrayList<>() : sortedVersions.get(0);
        compareVersions();
    }

    void compareVersions() {
        if (!maxToVersion.isEmpty() && !minFromVersion.isEmpty()) {
            if (FLYWAY_VERSION_COMPARATOR.compare(maxToVersion, minFromVersion) >= 0) {
                setErrors("Flyway patching number error, please up the patching level");
            }
        }
    }

    void setErrors(final String error) {
        request.veto(error, error);
    }
}