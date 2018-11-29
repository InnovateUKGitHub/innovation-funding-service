package org.innovateuk.ifs.sil.grant.resource;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GrantBuilder extends BaseBuilder<Grant, GrantBuilder> {
    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2018, 1,2);
    private static final ZonedDateTime DEFAULT_GOL_DATE = ZonedDateTime
            .of(LocalDate.of(2018, 3,4), LocalTime.MIDNIGHT, ZoneId.of("GMT"));
    private static String SPECIAL_CHARACTERS = "!@£$%^&*(){}[];:<>,./?`~|";
    private static final int LONG_STRING_REPEAT = 5;
    private static final int CONTACT_ID_START = 1_000;

    private String name;
    private String orgType = "Business";
    private String orgProjectRole = "lead";
    private String contactRole = "finance_contact";
    private int participantCount = 1;
    private int costCategoryCount = 2;
    private int durationInMonths = 12;
    private int applicationId = 1;
    private Integer defaultCompetitionCode = 9;
    private long total = 50_000;
    private BigDecimal overheadRate = BigDecimal.valueOf(50);
    private BigDecimal awardRate =  BigDecimal.valueOf(30);
    private BigDecimal capLimit =  BigDecimal.valueOf(80);
    private List<String> costCategories;
    private LocalDate startDate;
    private ZonedDateTime grantOfferLetterDate;
    private boolean specialCharacters;
    private boolean longStrings;

    private GrantBuilder(List<BiConsumer<Integer, Grant>> multiActions) {
        super(multiActions);
    }

    public static GrantBuilder newGrant() {
        return new GrantBuilder(Collections.emptyList());
    }

    public Grant build() {
        return with(grant -> {
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
            grant.setDuration(durationInMonths);
            grant.setId(applicationId);
            if (defaultCompetitionCode != null) {
                grant.setCompetitionCode(defaultCompetitionCode);
            }
        }).superBuild();
    }

    private Grant superBuild() {
        return super.build();
    }

    @Override
    protected GrantBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Grant>> actions) {
        GrantBuilder builder = new GrantBuilder(actions);
        builder.defaultCompetitionCode = defaultCompetitionCode;
        return builder;
    }

    @Override
    protected Grant createInitial() {
        return new Grant();
    }

    private String createString(String body) {
        return name + " " + body + " " + (specialCharacters ? SPECIAL_CHARACTERS : "")
                + (longStrings ?
                    StringUtils.repeat(specialCharacters ? SPECIAL_CHARACTERS : " 123456789", LONG_STRING_REPEAT) : "");
    }

    public GrantBuilder name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return name;
    }

    public GrantBuilder withCompetitionId(Long... competitionIds) {
        defaultCompetitionCode = null;
        return withArray((id, grant) -> grant.setCompetitionCode(id), competitionIds);
    }

    public GrantBuilder withParticipantCount(int participantCount) {
        this.participantCount = participantCount;
        return this;
    }

    public GrantBuilder withCount(int count) {
        this.participantCount = count;
        this.costCategoryCount = count;
        return this;
    }

    public GrantBuilder withTotal(long total) {
        this.total = total;
        return this;
    }

    public GrantBuilder withSpecialCharacters(boolean specialCharacters) {
        this.specialCharacters = specialCharacters;
        return this;
    }

    public GrantBuilder withLongStrings(boolean longStrings) {
        this.longStrings = longStrings;
        return this;
    }

    private Participant createParticipant(int i) {
        Participant participant = new Participant();
        participant.setContactEmail("participant-" + i + "test.com");
        participant.setForecasts(createForecasts(BigDecimal.valueOf(total)
                .divide(BigDecimal.valueOf(participantCount), 6, BigDecimal.ROUND_UP)));
        participant.setContactRole(contactRole);
        participant.setOrgProjectRole(orgProjectRole);
        participant.setId(i);
        participant.setContactId(CONTACT_ID_START + 1);
        participant.setOrgType(orgType);
        participant.setCapLimit(capLimit);
        participant.setOverheadRate(overheadRate);
        participant.setAwardRate(awardRate);
        return participant;
    }

    private Collection<Forecast> createForecasts(BigDecimal participantTotal) {
        BigDecimal value = participantTotal.divide(BigDecimal.valueOf(durationInMonths * costCategories.size()),
                6, BigDecimal.ROUND_UP);
        return costCategories.stream().map(category -> {
                    Collection<Period> periods = createPeriods(durationInMonths, value);
                    return new Forecast()
                            .costCategory(category)
                            .cost(periods.stream().mapToLong(Period::getValue).sum())
                            .periods(periods);
                }).collect(Collectors.toList());
    }

    private static Collection<Period> createPeriods(int durationInMonths, BigDecimal value) {
        return Stream.iterate(0, i -> i + 1).limit(durationInMonths)
                .map(i -> new Period().month(i + 1).value(value.longValue()))
                .collect(Collectors.toList());
    }
}
