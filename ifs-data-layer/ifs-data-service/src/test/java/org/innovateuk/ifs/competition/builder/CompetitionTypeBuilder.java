package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CompetitionTypeBuilder extends BaseBuilder<CompetitionType, CompetitionTypeBuilder> {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private CompetitionTypeBuilder(List<BiConsumer<Integer, CompetitionType>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionTypeBuilder newCompetitionType() {
        return new CompetitionTypeBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("CompetitionType "));
    }

    public CompetitionTypeBuilder withCompetitions(List<Competition> competitions) {
        return with(competitionType -> competitionType.setCompetitions(competitions));
    }

    public CompetitionTypeBuilder withName(String name) {
        return with(competitionType -> setField("name", name, competitionType));
    }

    public CompetitionTypeBuilder withStateAid(Boolean stateAid) {
        return with(competitionType -> setField("stateAid", stateAid, competitionType));
    }

    @Override
    protected CompetitionTypeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionType>> actions) {
        return new CompetitionTypeBuilder(actions);
    }

    @Override
    protected CompetitionType createInitial() {
        return new CompetitionType();
    }

    public CompetitionTypeBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

}
