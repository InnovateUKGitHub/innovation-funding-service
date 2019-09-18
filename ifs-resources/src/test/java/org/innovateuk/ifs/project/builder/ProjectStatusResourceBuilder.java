package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class ProjectStatusResourceBuilder extends BaseBuilder<ProjectStatusResource, ProjectStatusResourceBuilder> {

    public ProjectStatusResourceBuilder(List<BiConsumer<Integer, ProjectStatusResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ProjectStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectStatusResource>> actions) {
        return new ProjectStatusResourceBuilder(actions);
    }

    @Override
    protected ProjectStatusResource createInitial() {
        return new ProjectStatusResource();
    }

    public static ProjectStatusResourceBuilder newProjectStatusResource(){
        return new ProjectStatusResourceBuilder(emptyList());
    }

    public ProjectStatusResourceBuilder withProjectTitles(String ... projectTitles) {
        return withArray((projectTitle, psr) -> setField("projectTitle", projectTitle, psr), projectTitles);
    }

    public ProjectStatusResourceBuilder withProjectNumber(Long... projectNumbers) {
        return withArray((projectNumber, psr) -> setField("projectNumber", projectNumber, psr), projectNumbers);
    }

    public ProjectStatusResourceBuilder withApplicationNumber(Long... applicationNumbers) {
        return withArray((applicationNumber, psr) -> setField("applicationNumber", applicationNumber, psr), applicationNumbers);
    }

    public ProjectStatusResourceBuilder withNumberOfPartners(Integer... numberOfPartnersList) {
        return withArray((numberOfPartners, psr) -> setField("numberOfPartners", numberOfPartners, psr), numberOfPartnersList);
    }

    public ProjectStatusResourceBuilder withProjectLeadOrganisationName(String... projectLeadOrganisationNames) {
        return withArray((projectLeadOrganisationName, psr) -> setField("projectLeadOrganisationName", projectLeadOrganisationName, psr), projectLeadOrganisationNames);
    }

    public ProjectStatusResourceBuilder withProjectDetailStatus(ProjectActivityStates... projectDetailsStatuses) {
        return withArray((projectDetailsStatus, psr) -> setField("projectDetailsStatus", projectDetailsStatus, psr), projectDetailsStatuses);
    }

    public ProjectStatusResourceBuilder withProjectTeamStatus(ProjectActivityStates... projectTeamStatuses) {
        return withArray((projectTeamStatus, psr) -> setField("projectTeamStatus", projectTeamStatus, psr), projectTeamStatuses);
    }

    public ProjectStatusResourceBuilder withBankDetailsStatus(ProjectActivityStates... bankDetailsStatuses) {
        return withArray((bankDetailsStatus, psr) -> setField("bankDetailsStatus", bankDetailsStatus, psr), bankDetailsStatuses);
    }

    public ProjectStatusResourceBuilder withFinanceChecksStatus(ProjectActivityStates... financeChecksStatuses) {
        return withArray((financeChecksStatus, psr) -> setField("financeChecksStatus", financeChecksStatus, psr), financeChecksStatuses);
    }

    public ProjectStatusResourceBuilder withSpendProfileStatus(ProjectActivityStates... spendProfileStatuses) {
        return withArray((spendProfileStatus, psr) -> setField("spendProfileStatus", spendProfileStatus, psr), spendProfileStatuses);
    }

    public ProjectStatusResourceBuilder withMonitoringOfficerStatus(ProjectActivityStates... monitoringOfficerStatuses) {
        return withArray((monitoringOfficerStatus, psr) -> setField("monitoringOfficerStatus", monitoringOfficerStatus, psr), monitoringOfficerStatuses);
    }

    public ProjectStatusResourceBuilder withDocumentsStatus(ProjectActivityStates... documentsStatuses) {
        return withArray((documentsStatus, psr) -> setField("documentsStatus", documentsStatus, psr), documentsStatuses);
    }

    public ProjectStatusResourceBuilder withGrantOfferLetterStatus(ProjectActivityStates... grantOfferLetterStatuses) {
        return withArray((grantOfferLetterStatus, psr) -> setField("grantOfferLetterStatus", grantOfferLetterStatus, psr), grantOfferLetterStatuses);
    }

    public ProjectStatusResourceBuilder withProjectSetupCompleteStatus(ProjectActivityStates... projectSetupCompleteStatuses) {
        return withArray((projectSetupCompleteStatus, psr) -> setField("projectSetupCompleteStatus", projectSetupCompleteStatus, psr), projectSetupCompleteStatuses);
    }

    public ProjectStatusResourceBuilder withProjectState(ProjectState... projectState) {
        return withArraySetFieldByReflection("projectState", projectState);
    }
}
