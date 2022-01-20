package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public abstract class CompetitionSearchResultItemBuilder<T extends CompetitionSearchResultItem, B> extends
        BaseBuilder<T, B> {

    protected CompetitionSearchResultItemBuilder(List<BiConsumer<Integer, T>> newMultiActions) {
        super(newMultiActions);
    }

    public B withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public B withName(String... names) {
        return withArray((name, object) -> setField("name", name, object), names);
    }

    public B withCompetitionStatus(CompetitionStatus... competitionStatuses) {
        return withArray((status, competition) -> setField("competitionStatus", status, competition), competitionStatuses);
    }

    public B withCompetitionTypeName(String... competitionTypeNames) {
        return withArray((competitionTypeName, competition) -> setField("competitionTypeName", competitionTypeName, competition), competitionTypeNames);
    }

    public B withInnovationAreaNames(Set<String>... innovationAreaNames) {
        return withArray((innovationAreaName, competition) -> setField("innovationAreaNames", innovationAreaName, competition), innovationAreaNames);
    }

}
