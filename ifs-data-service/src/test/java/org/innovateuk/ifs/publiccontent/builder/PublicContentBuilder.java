package org.innovateuk.ifs.publiccontent.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.publiccontent.domain.ContentSection;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class PublicContentBuilder extends BaseBuilder<PublicContent, PublicContentBuilder> {


    private PublicContentBuilder(List<BiConsumer<Integer, PublicContent>> newMultiActions) {
        super(newMultiActions);
    }

    public static PublicContentBuilder newPublicContent() {
        return new PublicContentBuilder(emptyList()).with(uniqueIds());
    }

    public PublicContentBuilder withCompetitionId(Long competitionId) {
        return with(publicContent -> setField("competitionId", competitionId, publicContent));
    }

    public PublicContentBuilder withId(Long id) {
        return with(publicContent -> setField("id", id, publicContent));
    }

    public PublicContentBuilder withPublishDate(ZonedDateTime publishDate) {
        return with(publicContent -> setField("publishDate", publishDate, publicContent));
    }

    public PublicContentBuilder withShortDescription(String shortDescription) {
        return with(publicContent -> setField("shortDescription", shortDescription, publicContent));
    }

    public PublicContentBuilder withProjectFundingRange(String projectFundingRange) {
        return with(publicContent -> setField("projectFundingRange", projectFundingRange, publicContent));
    }

    public PublicContentBuilder withEligibilitySummary(String eligibilitySummary) {
        return with(publicContent -> setField("eligibilitySummary", eligibilitySummary, publicContent));
    }

    public PublicContentBuilder withProjectSize(String projectSize) {
        return with(publicContent -> setField("projectSize", projectSize, publicContent));
    }

    public PublicContentBuilder withSummary(String summary) {
        return with(publicContent -> setField("summary", summary, publicContent));
    }

    public PublicContentBuilder withFundingType(FundingType fundingType) {
        return with(publicContent -> setField("fundingType", fundingType, publicContent));
    }

    public PublicContentBuilder withContentSections(List<ContentSection> contentSections) {
        return with(publicContent -> setField("contentSections", contentSections, publicContent));
    }

    public PublicContentBuilder withKeywords(List<Keyword> keywords) {
        return with(publicContent -> setField("keywords", keywords, publicContent));
    }

    @Override
    protected PublicContentBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PublicContent>> actions) {
        return new PublicContentBuilder(actions);
    }

    @Override
    protected PublicContent createInitial() {
        return new PublicContent();
    }


}
