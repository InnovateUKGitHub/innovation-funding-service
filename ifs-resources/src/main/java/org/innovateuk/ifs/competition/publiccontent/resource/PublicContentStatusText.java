package org.innovateuk.ifs.competition.publiccontent.resource;

import java.time.ZonedDateTime;
import java.util.function.Predicate;

/**
 * Enum containing Competition Public Content status texts and corresponding predicates.
 */
public enum PublicContentStatusText {
    OPENING_SOON("Opening soon", "Opens", Predicates.openingDateIsInFuture),
    OPENING_NOW("Open now", "Opened", Predicates.openingDateIsInPastAndClosingDateIsAtLeastTwoWeeksAway),
    CLOSING_SOON("Closing soon", "Opened", Predicates.openingDateIsInPastAndClosingDateIsLessThanTwoWeeksAway);

    private final String header;
    private final String openTense;
    private final Predicate<PublicContentItemResource> predicate;

    PublicContentStatusText(String header, String openTense, Predicate<PublicContentItemResource> predicate) {
        this.header = header;
        this.openTense = openTense;
        this.predicate = predicate;
    }

    public Predicate<PublicContentItemResource> getPredicate() {
        return predicate;
    }

    public String getHeader() {
        return header;
    }

    public String getOpenTense() {
        return openTense;
    }

    private static class Predicates {
        public static final Predicate<PublicContentItemResource> openingDateIsInFuture =
                content -> content.getCompetitionOpenDate().isAfter(ZonedDateTime.now());

        public static final Predicate<PublicContentItemResource> openingDateIsInPastAndClosingDateIsAtLeastTwoWeeksAway =
                content -> content.getCompetitionOpenDate().isBefore(ZonedDateTime.now()) &&
                        content.getCompetitionCloseDate().isAfter(ZonedDateTime.now().plusDays(14));

        public static final Predicate<PublicContentItemResource> openingDateIsInPastAndClosingDateIsLessThanTwoWeeksAway  =
                content -> content.getCompetitionCloseDate().isBefore(ZonedDateTime.now().plusDays(14));
    }
}