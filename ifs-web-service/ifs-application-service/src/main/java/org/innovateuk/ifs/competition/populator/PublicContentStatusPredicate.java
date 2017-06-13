package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;

import java.time.ZonedDateTime;
import java.util.function.Predicate;

/**
 * Lists the predicates used to determine what state a {@link PublicContentItemResource} is in.
 */
public class PublicContentStatusPredicate {
    public static final Predicate<PublicContentItemResource> openingDateIsInFuture =
            content -> content.getCompetitionOpenDate().isAfter(ZonedDateTime.now());

    public static final Predicate<PublicContentItemResource> openingDateIsInPastAndClosingDateIsAtLeastTwoWeeksAway =
            content -> content.getCompetitionOpenDate().isBefore(ZonedDateTime.now()) &&
                    content.getCompetitionCloseDate().isAfter(ZonedDateTime.now().plusDays(14));

    public static final Predicate<PublicContentItemResource> openingDateIsInPastAndClosingDateIsLessThanTwoWeeksAway  =
            content -> content.getCompetitionCloseDate().isBefore(ZonedDateTime.now().plusDays(14))
                    || content.getCompetitionCloseDate().equals(ZonedDateTime.now().plusDays(14));
}
