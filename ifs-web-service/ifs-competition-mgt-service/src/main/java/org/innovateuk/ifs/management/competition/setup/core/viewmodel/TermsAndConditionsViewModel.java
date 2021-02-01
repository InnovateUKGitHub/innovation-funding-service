package org.innovateuk.ifs.management.competition.setup.core.viewmodel;

import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;

import java.util.List;

public class TermsAndConditionsViewModel extends CompetitionSetupViewModel {

    private final List<GrantTermsAndConditionsResource> termsAndConditionsList;

    private final GrantTermsAndConditionsResource currentTermsAndConditions;
    private final GrantTermsAndConditionsResource currentSubsidyControlTermsAndConditions;

    private final boolean termsAndConditionsDocUploaded;
    private final boolean includeSubsidyControl;
    private final boolean subsidyControlPage;

    public TermsAndConditionsViewModel(GeneralSetupViewModel generalSetupViewModel,
                                       List<GrantTermsAndConditionsResource> termsAndConditionsList,
                                       GrantTermsAndConditionsResource currentTermsAndConditions,
                                       GrantTermsAndConditionsResource currentSubsidyControlTermsAndConditions,
                                       boolean termsAndConditionsDocUploaded,
                                       boolean includeSubsidyControl, boolean subsidyControlPage) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.termsAndConditionsList = termsAndConditionsList;
        this.currentTermsAndConditions = currentTermsAndConditions;
        this.currentSubsidyControlTermsAndConditions = currentSubsidyControlTermsAndConditions;
        this.termsAndConditionsDocUploaded = termsAndConditionsDocUploaded;
        this.includeSubsidyControl = includeSubsidyControl;
        this.subsidyControlPage = subsidyControlPage;
    }

    public List<GrantTermsAndConditionsResource> getTermsAndConditionsList() {
        return termsAndConditionsList;
    }

    public GrantTermsAndConditionsResource getCurrentTermsAndConditions() {
        return currentTermsAndConditions;
    }

    public GrantTermsAndConditionsResource getCurrentSubsidyControlTermsAndConditions() {
        return currentSubsidyControlTermsAndConditions;
    }

    public boolean isTermsAndConditionsDocUploaded() {
        return termsAndConditionsDocUploaded;
    }

    public boolean isIncludeSubsidyControl() {
        return includeSubsidyControl;
    }

    public boolean isSubsidyControlPage() {
        return subsidyControlPage;
    }
}