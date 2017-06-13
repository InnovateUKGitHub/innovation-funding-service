package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;

import java.util.function.Predicate;

/**
 * Enum containing Competition Public Content status texts and corresponding predicates.
 */
public enum PublicContentStatusText {
    OPENING_SOON("Opening soon", "Opens", PublicContentStatusPredicate.openingDateIsInFuture),
    OPEN_NOW("Open now", "Opened", PublicContentStatusPredicate.openingDateIsInPastAndClosingDateIsAtLeastTwoWeeksAway),
    CLOSING_SOON("Closing soon", "Opened", PublicContentStatusPredicate.openingDateIsInPastAndClosingDateIsLessThanTwoWeeksAway);

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
}