package com.worth.ifs.bitbucket.plugin.hook;

import com.atlassian.bitbucket.content.AbstractContentTreeCallback;
import com.atlassian.bitbucket.content.ContentTreeNode;
import com.atlassian.bitbucket.content.ContentTreeSummary;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.util.Collections.sort;
import static org.apache.commons.lang3.tuple.Pair.of;

/**
 * Callback that is called when Bitbucket iterates over a source control file system.
 * Aggregates and orders any Flyway scripts it encounters, and calls another callback when it is done with these.
 */
public class FlywayVersionContentTreeCallback extends AbstractContentTreeCallback {

    private final List<Pair<String, List<Integer>>> versionNumbers = new ArrayList<Pair<String, List<Integer>>>();
    private final Consumer<List<Pair<String, List<Integer>>>> callBack;
    private static final String FLYWAY_MAJOR_PATCH = "V([0-9]+)";
    private static final Pattern FLYWAY_MAJOR_PATCH_PATTERN = Pattern.compile(FLYWAY_MAJOR_PATCH);
    private static final String FLYWAY_MINOR_PATCH = "(?:_([0-9]+))";
    private static final Pattern FLYWAY_MINOR_PATCH_PATTERN = Pattern.compile(FLYWAY_MINOR_PATCH);
    private static final String FLYWAY_END_PATCH = "__.*\\.sql\\z";
    private static final Pattern FLYWAY_PATCH_PATTERN = Pattern.compile(FLYWAY_MAJOR_PATCH + FLYWAY_MINOR_PATCH + "*" + FLYWAY_END_PATCH);
    private static final Comparator<Pair<String, List<Integer>>> FLYWAY_VERSION_COMPARATOR = new Comparator<Pair<String, List<Integer>>>() {
        @Override
        public int compare(Pair<String, List<Integer>> o1, Pair<String, List<Integer>> o2) {
            return new FlywayVersionComparator().compare(o1.getValue(), o2.getValue());
        }
    };

    public FlywayVersionContentTreeCallback(final Consumer<List<Pair<String, List<Integer>>>> callBack) {
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
        final List<Pair<String, List<Integer>>> sorted = sortAndFilter(versionNumbers);
        callBack.accept(sorted);
    }

    static List<Pair<String, List<Integer>>> sortAndFilter(final List<Pair<String, List<Integer>>> unsorted) {
        final List<Pair<String, List<Integer>>> sorted = new ArrayList<Pair<String, List<Integer>>>(unsorted);
        final Iterator<Pair<String, List<Integer>>> iterator = sorted.iterator();
        while (iterator.hasNext()) {
            final Pair<String, List<Integer>> toRemove = iterator.next();
            if (toRemove.getValue().isEmpty()) {
                iterator.remove();
            }
        }
        sort(sorted, FLYWAY_VERSION_COMPARATOR);
        return sorted;
    }

    static Pair<String, List<Integer>> versionFromName(final String name) {
        final List<Integer> version = new ArrayList<Integer>();
        final Matcher matcher = FLYWAY_PATCH_PATTERN.matcher(name);
        if (matcher.find()) {
            final Matcher major = FLYWAY_MAJOR_PATCH_PATTERN.matcher(name);
            major.find();
            version.add(parseInt(major.group(1)));
            for (final Matcher minor = FLYWAY_MINOR_PATCH_PATTERN.matcher(name); minor.find(); ) {
                version.add(parseInt(minor.group(1)));
            }
        }
        return of(name, version);
    }
}
