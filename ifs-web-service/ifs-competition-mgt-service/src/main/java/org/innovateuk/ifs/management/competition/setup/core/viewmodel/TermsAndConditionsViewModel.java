package org.innovateuk.ifs.management.competition.setup.core.viewmodel;

import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;

import java.util.List;

public class TermsAndConditionsViewModel extends CompetitionSetupViewModel {

    private final List<GrantTermsAndConditionsResource> termsAndConditionsList;

    private final GrantTermsAndConditionsResource currentTermsAndConditions;
    private final GrantTermsAndConditionsResource currentStateAidTermsAndConditions;

    private final boolean termsAndConditionsDocUploaded;
    private final boolean includeStateAid;
    private final boolean stateAidPage;
    private  String projectCostGuidanceLink;

    public TermsAndConditionsViewModel(GeneralSetupViewModel generalSetupViewModel,
                                       List<GrantTermsAndConditionsResource> termsAndConditionsList,
                                       GrantTermsAndConditionsResource currentTermsAndConditions,
                                       GrantTermsAndConditionsResource currentStateAidTermsAndConditions,
                                       boolean termsAndConditionsDocUploaded,
                                       boolean includeStateAid, boolean stateAidPage,
                                       String projectCostGuidanceLink) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.termsAndConditionsList = termsAndConditionsList;
        this.currentTermsAndConditions = currentTermsAndConditions;
        this.currentStateAidTermsAndConditions = currentStateAidTermsAndConditions;
        this.termsAndConditionsDocUploaded = termsAndConditionsDocUploaded;
        this.includeStateAid = includeStateAid;
        this.stateAidPage = stateAidPage;
        this.projectCostGuidanceLink = projectCostGuidanceLink;
    }

    public List<GrantTermsAndConditionsResource> getTermsAndConditionsList() {
        return termsAndConditionsList;
    }

    public GrantTermsAndConditionsResource getCurrentTermsAndConditions() {
        return currentTermsAndConditions;
    }

    public GrantTermsAndConditionsResource getCurrentStateAidTermsAndConditions() {
        return currentStateAidTermsAndConditions;
    }

    public boolean isTermsAndConditionsDocUploaded() {
        return termsAndConditionsDocUploaded;
    }

    public boolean isIncludeStateAid() {
        return includeStateAid;
    }

    public boolean isStateAidPage() {
        return stateAidPage;
    }

    public String getProjectCostGuidanceLink() {
        return projectCostGuidanceLink;
    }

    public void setProjectCostGuidanceLink(String projectCostGuidanceLink) {
        this.projectCostGuidanceLink = projectCostGuidanceLink;
    }
}