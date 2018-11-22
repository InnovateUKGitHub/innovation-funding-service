package org.innovateuk.ifs.sil.grant.controller;

import org.innovateuk.ifs.sil.grant.resource.Forecast;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.sil.grant.resource.Period;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class GrantBuilder {
    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2018, 1,2);
    private static final ZonedDateTime DEFAULT_GOL_DATE = ZonedDateTime
            .of(LocalDate.of(2018, 3,4), LocalTime.MIDNIGHT, ZoneId.of("GMT"));
    private static String SPECIAL_CHARACTERS = "!@£$%^&*(){}[];:<>,./?`~|";

    private Grant grant = new Grant();
    private String name;
    private int participantCount = 1;
    private int costCategoryCount = 2;
    private int durationInMonths = 12;
    private long total = 50_000;
    private List<String> costCategories;
    private LocalDate startDate;
    private ZonedDateTime grantOfferLetterDate;
    private boolean specialCharacters;
    private boolean longStrings;

    Grant build() {
        costCategories = Stream.iterate(0, i -> i + 1).limit(costCategoryCount)
            .map(i -> i == 0 ? "Overheads" : "Other " + i)
            .collect(Collectors.toList());

        grant.setParticipants(
                Stream.iterate(0, i -> i + 1)
                        .limit(participantCount)
                        .map(this::createParticipant)
                        .collect(Collectors.toSet()));

        grant.setGrantOfferLetterDate(grantOfferLetterDate != null ? grantOfferLetterDate : DEFAULT_GOL_DATE);
        grant.setStartDate(startDate != null ? startDate : DEFAULT_START_DATE);
        grant.setTitle(createString("title"));
        grant.setSummary(createString("summary"));
        grant.setPublicDescription(createString("public description"));
        return grant;
    }

    private String createString(String body) {
        return name + " " + body + " " + (specialCharacters ? SPECIAL_CHARACTERS : "");
    }

    GrantBuilder name(String name) {
        this.name = name;
        return this;
    }

    String name() {
        return name;
    }

    GrantBuilder withParticipantCount(int participantCount) {
        this.participantCount = participantCount;
        return this;
    }

    GrantBuilder withCount(int count) {
        this.participantCount = count;
        this.costCategoryCount = count;
        return this;
    }

    GrantBuilder withTotal(long total) {
        this.total = total;
        return this;
    }

    GrantBuilder withSpecialCharacters(boolean specialCharacters) {
        this.specialCharacters = specialCharacters;
        return this;
    }

    GrantBuilder withLongStrings(boolean longStrings) {
        this.longStrings = longStrings;
        return this;
    }

    private Participant createParticipant(int i) {
        Participant participant = new Participant();
        participant.setContactEmail("participant-" + i + "test.com");
        participant.setForecasts(createForecasts(BigDecimal.valueOf(total)
                .divide(BigDecimal.valueOf(participantCount), 6, BigDecimal.ROUND_UP)));
        return participant;
    }

    private Set<Forecast> createForecasts(BigDecimal participantTotal) {
        BigDecimal value = participantTotal.divide(BigDecimal.valueOf(durationInMonths * costCategories.size()),
                6, BigDecimal.ROUND_UP);
        return costCategories.stream().map(category ->
                new Forecast().costCategory(category).periods(createPeriods(durationInMonths, value)))
                .collect(Collectors.toSet());
    }

    private static Set<Period> createPeriods(int durationInMonths, BigDecimal value) {
        return Stream.iterate(0, i -> i + 1).limit(durationInMonths)
                .map(i -> new Period().month(i + 1).value(value.longValue()))
                .collect(Collectors.toSet());
    }
}
