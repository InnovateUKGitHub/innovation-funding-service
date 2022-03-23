package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.negate;

public class TermsAndConditionsReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    private final List<TermsAndConditionsRowReadOnlyViewModel> partners;
    private final boolean includeFundingRules;
    private final boolean displayCompleteStatus;
    private final String termsAndConditionsTerminology;
    private final boolean thirdPartyProcurementCompetition;
    private final String thirdPartyProcurementHeader;
    private final boolean isOfgemCompetition;
    private final CompetitionThirdPartyConfigResource thirdPartyConfig;

    public TermsAndConditionsReadOnlyViewModel(ApplicationReadOnlyData data,
                                               QuestionResource question,
                                               boolean includeFundingRules,
                                               List<TermsAndConditionsRowReadOnlyViewModel> partners,
                                               String termsAndConditionsTerminology,
                                               boolean thirdPartyProcurementCompetition,
                                               String thirdPartyProcurementHeader,
                                               boolean isOfgemCompetition,
                                               CompetitionThirdPartyConfigResource thirdPartyConfig) {
        super(data, question);
        this.partners = partners;
        this.includeFundingRules = includeFundingRules;
        this.displayCompleteStatus = !data.getApplication().isSubmitted() && !data.getCompetition().isExpressionOfInterest();
        this.termsAndConditionsTerminology = termsAndConditionsTerminology;
        this.thirdPartyProcurementCompetition = thirdPartyProcurementCompetition;
        this.thirdPartyProcurementHeader = thirdPartyProcurementHeader;
        this.isOfgemCompetition = isOfgemCompetition;
        this.thirdPartyConfig = thirdPartyConfig;
    }

    @Override
    public String getFragment() {
        return "terms-and-conditions";
    }

    @Override
    public boolean shouldDisplayMarkAsComplete() {
        return false;
    }

    @Override
    public boolean shouldDisplayActions() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return partners.stream().allMatch(TermsAndConditionsRowReadOnlyViewModel::isAccepted);
    }

    @Override
    public boolean isDisplayCompleteStatus() {
        return displayCompleteStatus;
    }

    public boolean isIncludeFundingRules() {
        return includeFundingRules;
    }

    public String getTermsAndConditionsTerminology() {
        return termsAndConditionsTerminology;
    }

    public List<TermsAndConditionsRowReadOnlyViewModel> getPartners() {
        return partners;
    }

    public List<TermsAndConditionsRowReadOnlyViewModel> getNonAcceptedPartners() {
        return partners.stream()
                .filter(negate(TermsAndConditionsRowReadOnlyViewModel::isAccepted))
                .collect(Collectors.toList());
    }

    public boolean isThirdPartyProcurementCompetition() {
        return thirdPartyProcurementCompetition || isOfgemCompetition;
    }

    public String getThirdPartyProcurementHeader() {
        return thirdPartyProcurementHeader;
    }

    public String getAccordionSectionId() {
        return "terms-and-conditions";
    }

    public boolean isOfgemCompetition() {
        return isOfgemCompetition;
    }

    public CompetitionThirdPartyConfigResource getThirdPartyConfig() {
        return thirdPartyConfig;
    }
}