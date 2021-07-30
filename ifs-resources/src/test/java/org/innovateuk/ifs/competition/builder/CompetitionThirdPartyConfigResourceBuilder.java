package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionThirdPartyConfigResourceBuilder extends BaseBuilder<CompetitionThirdPartyConfigResource, CompetitionThirdPartyConfigResourceBuilder> {

    private CompetitionThirdPartyConfigResourceBuilder (List<BiConsumer<Integer, CompetitionThirdPartyConfigResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionThirdPartyConfigResourceBuilder newCompetitionThirdPartyConfigResource() {
        return new CompetitionThirdPartyConfigResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionThirdPartyConfigResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionThirdPartyConfigResource>> actions) {
        return new CompetitionThirdPartyConfigResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CompetitionThirdPartyConfigResource createInitial() {
        return new CompetitionThirdPartyConfigResource();
    }

    public CompetitionThirdPartyConfigResourceBuilder withTermsAndConditionsLabel(String... termsAndConditionsLabel) {
        return withArraySetFieldByReflection("termsAndConditionsLabel", termsAndConditionsLabel);
    }

    public CompetitionThirdPartyConfigResourceBuilder withTermsAndConditionsGuidance(String... termsAndConditionsGuidance) {
        return withArraySetFieldByReflection("termsAndConditionsGuidance", termsAndConditionsGuidance);
    }

    public CompetitionThirdPartyConfigResourceBuilder withProjectCostGuidanceUrl(String... projectCostGuidanceUrl) {
        return withArraySetFieldByReflection("projectCostGuidanceUrl", projectCostGuidanceUrl);
    }
}
