package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionDocumentResourceBuilder extends BaseBuilder<CompetitionDocumentResource, CompetitionDocumentResourceBuilder> {

    private CompetitionDocumentResourceBuilder(List<BiConsumer<Integer, CompetitionDocumentResource>> multiActions) {
        super(multiActions);
    }

    public static CompetitionDocumentResourceBuilder neCompetitionDocumentResource() {
        return new CompetitionDocumentResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CompetitionDocumentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionDocumentResource>> actions) {
        return new CompetitionDocumentResourceBuilder(actions);
    }

    @Override
    protected CompetitionDocumentResource createInitial() {
        return new CompetitionDocumentResource();
    }

    public CompetitionDocumentResourceBuilder withId(Long... ids) {
        return withArray((id, competitionDocumentResource) -> setField("id", id, competitionDocumentResource), ids);
    }

    public CompetitionDocumentResourceBuilder withCompetition(Long... competitionIds) {
        return withArray((competitionId, competitionDocumentResource) -> setField("competition", competitionId, competitionDocumentResource), competitionIds);
    }

    public CompetitionDocumentResourceBuilder withTitle(String... titles) {
        return withArray((title, competitionDocumentResource) -> setField("title", title, competitionDocumentResource), titles);
    }

    public CompetitionDocumentResourceBuilder withGuidance(String... guidances) {
        return withArray((guidance, competitionDocumentResource) -> setField("guidance", guidance, competitionDocumentResource), guidances);
    }

    public CompetitionDocumentResourceBuilder withEditable(Boolean... editableFlags) {
        return withArray((editable, competitionDocumentResource) -> setField("editable", editable, competitionDocumentResource), editableFlags);
    }

    public CompetitionDocumentResourceBuilder withEnabled(Boolean... enabledFlags) {
        return withArray((enabled, competitionDocumentResource) -> setField("enabled", enabled, competitionDocumentResource), enabledFlags);
    }

    public CompetitionDocumentResourceBuilder withFileType(List<Long>... fileTypesLists) {
        return withArray((fileTypes, competitionDocumentResource) -> setField("fileTypes", fileTypes, competitionDocumentResource), fileTypesLists);
    }
}

