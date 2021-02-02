package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.util.List;

public class TermsAndConditionsReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    private final List<TermsAndConditionsRowReadOnlyViewModel> partners;
    private final boolean includeFundingRules;
    private final boolean displayCompleteStatus;
    private final String termsAndConditionsTerminology;

    public TermsAndConditionsReadOnlyViewModel(ApplicationReadOnlyData data,
                                               QuestionResource question,
                                               boolean includeFundingRules,
                                               List<TermsAndConditionsRowReadOnlyViewModel> partners,
                                               String termsAndConditionsTerminology) {
        super(data, question);
        this.partners = partners;
        this.includeFundingRules = includeFundingRules;
        this.displayCompleteStatus = !data.getApplication().isSubmitted() && !data.getCompetition().isExpressionOfInterest();
        this.termsAndConditionsTerminology = termsAndConditionsTerminology;
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
}