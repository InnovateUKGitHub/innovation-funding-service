package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class TermsAndConditionsReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    private final ApplicationTermsViewModel applicationTermsViewModel;
    private final ApplicationTermsPartnerViewModel applicationTermsPartnerViewModel;
    private final boolean displayCompleteStatus;
    private final boolean assessment;
    private final String termsAndConditionsTerminology;

    public TermsAndConditionsReadOnlyViewModel(ApplicationReadOnlyData data,
                                               QuestionResource question,
                                               ApplicationTermsViewModel applicationTermsViewModel,
                                               ApplicationTermsPartnerViewModel applicationTermsPartnerViewModel,
                                               boolean assessment,
                                               String termsAndConditionsTerminology) {
        super(data, question);
        this.applicationTermsViewModel = applicationTermsViewModel;
        this.applicationTermsPartnerViewModel = applicationTermsPartnerViewModel;
        this.displayCompleteStatus = !data.getCompetition().isExpressionOfInterest(); // do not show status for EoI competitions
        this.assessment = assessment;
        this.termsAndConditionsTerminology = termsAndConditionsTerminology;
    }

    @Override
    public String getFragment() {
        return assessment ? "terms-and-conditions-assessment" : "terms-and-conditions";
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
        return applicationTermsViewModel.isTermsAcceptedByAllOrganisations();
    }

    public ApplicationTermsViewModel getApplicationTermsViewModel() {
        return applicationTermsViewModel;
    }

    public ApplicationTermsPartnerViewModel getApplicationTermsPartnerViewModel() {
        return applicationTermsPartnerViewModel;
    }

    public boolean isAssessment() {
        return assessment;
    }

    @Override
    public boolean isDisplayCompleteStatus() {
        return displayCompleteStatus;
    }

    public String getTermsAndConditionsTerminology() {
        return termsAndConditionsTerminology;
    }
}