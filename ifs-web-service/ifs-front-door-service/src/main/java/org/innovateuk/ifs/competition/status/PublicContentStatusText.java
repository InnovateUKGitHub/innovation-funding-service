package org.innovateuk.ifs.competition.status;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;

import java.time.ZonedDateTime;
import java.util.function.Predicate;

/**
 * Enum containing Competition Public Content status texts and corresponding predicates.
 */
public enum PublicContentStatusText {
    OPENING_SOON("Opening soon", "Opens", openingDateIsInFuture()),
    OPEN_NOW("Open now", "Opened", openingDateIsInPastAndClosingDateIsAtLeastTwoWeeksAway()),
    CLOSING_SOON("Closing soon", "Opened", openingDateIsInPastAndClosingDateIsLessThanTwoWeeksAway());

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

    public static Predicate<PublicContentItemResource> openingDateIsInFuture() {
        return content -> content.getCompetitionOpenDate().isAfter(ZonedDateTime.now());
    }

    public static Predicate<PublicContentItemResource> openingDateIsInPastAndClosingDateIsAtLeastTwoWeeksAway() {
        return content -> content.getCompetitionOpenDate().isBefore(ZonedDateTime.now()) &&
                content.getCompetitionCloseDate().isAfter(ZonedDateTime.now().plusDays(14));
    }

    public static Predicate<PublicContentItemResource> openingDateIsInPastAndClosingDateIsLessThanTwoWeeksAway() {
        return content -> content.getCompetitionOpenDate().isBefore(ZonedDateTime.now()) &&
                (content.getCompetitionCloseDate().isBefore(ZonedDateTime.now().plusDays(14)) || content.getCompetitionCloseDate().equals(ZonedDateTime.now().plusDays(14)));
    }
}