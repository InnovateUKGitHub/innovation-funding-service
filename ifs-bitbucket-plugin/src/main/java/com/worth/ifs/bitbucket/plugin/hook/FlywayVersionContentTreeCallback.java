package com.worth.ifs.bitbucket.plugin.hook;

import com.atlassian.bitbucket.content.AbstractContentTreeCallback;
import com.atlassian.bitbucket.content.ContentTreeNode;
import com.atlassian.bitbucket.content.ContentTreeSummary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

public class FlywayVersionContentTreeCallback extends AbstractContentTreeCallback {

    private final List<List<Integer>> versionNumbers = new ArrayList<List<Integer>>();
    private final Consumer<List<List<Integer>>> callBack;
    private static final String FLYWAY_MAJOR_PATCH = "V([0-9]+)";
    private static final Pattern FLYWAY_MAJOR_PATCH_PATTERN = Pattern.compile(FLYWAY_MAJOR_PATCH);
    private static final String FLYWAY_MINOR_PATCH = "(?:_([0-9]+))";
    private static final Pattern FLYWAY_MINOR_PATCH_PATTERN = Pattern.compile(FLYWAY_MINOR_PATCH);
    private static final String FLYWAY_END_PATCH = "__.*\\.sql\\z";
    private static final Pattern FLYWAY_PATCH_PATTERN = Pattern.compile(FLYWAY_MAJOR_PATCH + FLYWAY_MINOR_PATCH + "*" + FLYWAY_END_PATCH);
    private static final Comparator<List<Integer>> FLYWAY_VERSION_COMPARATOR = new FlywayVersionComparator();

    public FlywayVersionContentTreeCallback(final Consumer<List<List<Integer>>> callBack) {
        this.callBack = callBack;
    }

    @Override
    public boolean onTreeNode(final ContentTreeNode node) throws IOException {
        final String name = node.getPath().getName();
        versionNumbers.add(versionFromName(name)); // Might be empty
        return true; // Keep going
    }

    @Override
    public void onEnd(final ContentTreeSummary summary) throws IOException {
        final List<List<Integer>> sorted = sortAndFilter(versionNumbers);
        callBack.accept(sorted);
    }

    static List<List<Integer>> sortAndFilter(final List<List<Integer>> unsorted){
        final List<List<Integer>> sorted = unsorted.stream().filter(l -> !l.isEmpty()).sorted(FLYWAY_VERSION_COMPARATOR).collect(toList());
        return sorted;
    }

    static List<Integer> versionFromName(final String name) {
        final List<Integer> version = new ArrayList<>();
        final Matcher matcher = FLYWAY_PATCH_PATTERN.matcher(name);
        if (matcher.find()) {
            final Matcher major = FLYWAY_MAJOR_PATCH_PATTERN.matcher(name);
            major.find();
            version.add(parseInt(major.group(1)));
            for (final Matcher minor = FLYWAY_MINOR_PATCH_PATTERN.matcher(name); minor.find(); ) {
                version.add(parseInt(minor.group(1)));
            }
        }
        return version;
    }
}
