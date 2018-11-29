package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.file.domain.FileType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link CompetitionDocument}s.
 */
public class ProjectDocumentBuilder extends BaseBuilder<CompetitionDocument, ProjectDocumentBuilder> {

    public static ProjectDocumentBuilder newCompetitionProjectDocument() {
        return new ProjectDocumentBuilder(emptyList()).with(uniqueIds());
    }

    private ProjectDocumentBuilder(List<BiConsumer<Integer, CompetitionDocument>> multiActions) {
        super(multiActions);
    }

    @Override
    protected ProjectDocumentBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionDocument>> actions) {
        return new ProjectDocumentBuilder(actions);
    }

    @Override
    protected CompetitionDocument createInitial() {
        return createDefault(CompetitionDocument.class);
    }

    public ProjectDocumentBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public ProjectDocumentBuilder withCompetition(Competition... competitions) {
        return withArray((competition, p) -> setField("competition", competition, p), competitions);
    }

    public ProjectDocumentBuilder withFileTypes(List<FileType>... fileTypesList) {
        return withArray((fileTypes, p) -> setField("fileTypes", fileTypes, p), fileTypesList);
    }

    public ProjectDocumentBuilder withTitle(String... titles) {
        return withArray((title, p) -> setField("title", title, p), titles);
    }

    public ProjectDocumentBuilder withGuidance(String... guidanceList) {
        return withArray((guidance, p) -> setField("guidance", guidance, p), guidanceList);
    }
}