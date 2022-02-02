package org.innovateuk.ifs.async.generation;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * A holder of useful information about a Future and how it was created
 */
public class AsyncFutureDetails {

    private String futureName;
    private String threadName;
    private List<String> threadAncestry;
    private List<String> futureAncestry;

    public AsyncFutureDetails(String futureName, String threadName, List<String> threadAncestry, List<String> futureAncestry) {
        this.futureName = futureName;
        this.threadName = threadName;
        this.threadAncestry = threadAncestry;
        this.futureAncestry = futureAncestry;
    }

    public String getFutureName() {
        return futureName;
    }

    public String getThreadName() {
        return threadName;
    }

    public List<String> getThreadAncestry() {
        return threadAncestry;
    }

    public List<String> getFutureAncestry() {
        return futureAncestry;
    }

    public static AsyncFutureDetails topLevelThread() {
        return new AsyncFutureDetails("Top level", Thread.currentThread().getName(), emptyList(), emptyList());
    }
}
