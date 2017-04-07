package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class PublicContentResourceBuilder extends BaseBuilder<PublicContentResource, PublicContentResourceBuilder> {

    private PublicContentResourceBuilder(List<BiConsumer<Integer, PublicContentResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static PublicContentResourceBuilder newPublicContentResource() {
        return new PublicContentResourceBuilder(emptyList()).with(uniqueIds());
    }

    public PublicContentResourceBuilder withCompetitionId(Long competitionId) {
        return with(publicContent -> setField("competitionId", competitionId, publicContent));
    }

    public PublicContentResourceBuilder withPublishDate(ZonedDateTime publishDate) {
        return with(publicContent -> setField("publishDate", publishDate, publicContent));
    }

    public PublicContentResourceBuilder withShortDescription(String shortDescription) {
        return with(publicContent -> setField("shortDescription", shortDescription, publicContent));
    }

    public PublicContentResourceBuilder withProjectFundingRange(String projectFundingRange) {
        return with(publicContent -> setField("projectFundingRange", projectFundingRange, publicContent));
    }

    public PublicContentResourceBuilder withEligibilitySummary(String eligibilitySummary) {
        return with(publicContent -> setField("eligibilitySummary", eligibilitySummary, publicContent));
    }

    public PublicContentResourceBuilder withProjectSize(String projectSize) {
        return with(publicContent -> setField("projectSize", projectSize, publicContent));
    }

    public PublicContentResourceBuilder withSummary(String summary) {
        return with(publicContent -> setField("summary", summary, publicContent));
    }

    public PublicContentResourceBuilder withFundingType(FundingType fundingType) {
        return with(publicContent -> setField("fundingType", fundingType, publicContent));
    }

    public PublicContentResourceBuilder withContentSections(List<PublicContentSectionResource> contentSections) {
        return with(publicContent -> setField("contentSections", contentSections, publicContent));
    }

    public PublicContentResourceBuilder withContentEvents(List<ContentEventResource> contentEvents) {
        return with(publicContent -> setField("contentEvents", contentEvents, publicContent));
    }

    public PublicContentResourceBuilder withKeywords(List<String> keywords) {
        return with(publicContent -> setField("keywords", keywords, publicContent));
    }

    @Override
    protected PublicContentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PublicContentResource>> actions) {
        return new PublicContentResourceBuilder(actions);
    }

    @Override
    protected PublicContentResource createInitial() {
        return new PublicContentResource();
    }
}
