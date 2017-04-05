package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class PublicContentItemResourceBuilder extends BaseBuilder<PublicContentItemResource, PublicContentItemResourceBuilder> {

    private PublicContentItemResourceBuilder(List<BiConsumer<Integer, PublicContentItemResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static PublicContentItemResourceBuilder newPublicContentItemResource() {
        return new PublicContentItemResourceBuilder(emptyList());
    }

    public PublicContentItemResourceBuilder withContentSection(PublicContentResource... publicContentResources) {
        return withArray((publicContentItem, publicContentResource) -> setField("publicContentResource", publicContentItem, publicContentResource), publicContentResources);
    }

    public PublicContentItemResourceBuilder withCompetitionTitle(String competitionTitle) {
        return with(publicContentItem -> setField("competitionTitle", competitionTitle, publicContentItem));
    }

    public PublicContentItemResourceBuilder withCompetitionOpenDate(LocalDateTime competitionOpenDate) {
        return with(publicContentItem -> setField("competitionOpenDate", competitionOpenDate, publicContentItem));
    }

    public PublicContentItemResourceBuilder withCompetitionCloseDate(LocalDateTime competitionCloseDate) {
        return with(publicContentItem -> setField("competitionCloseDate", competitionCloseDate, publicContentItem));
    }

    public PublicContentItemResourceBuilder withNonIfsUrl(String nonIfsUrl) {
        return with(publicContentItem -> setField("nonIfsUrl", nonIfsUrl, publicContentItem));
    }

    public PublicContentItemResourceBuilder withNonIfs(Boolean nonIfs) {
        return with(publicContentItem -> setField("nonIfs", nonIfs, publicContentItem));
    }

    public PublicContentItemResourceBuilder withSetupComplete(Boolean setupComplete) {
        return with(publicContentItem -> setField("setupComplete", setupComplete, publicContentItem));
    }

    @Override
    protected PublicContentItemResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PublicContentItemResource>> actions) {
        return new PublicContentItemResourceBuilder(actions);
    }

    @Override
    protected PublicContentItemResource createInitial() {
        return new PublicContentItemResource();
    }
}
