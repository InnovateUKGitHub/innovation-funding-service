package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.project.status.resource.ProjectStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

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

    public ProjectStatusResourceBuilder withNumberOfPartners(Integer... numberOfPartnersList) {
        return withArray((numberOfPartners, psr) -> setField("numberOfPartners", numberOfPartners, psr), numberOfPartnersList);
    }

    public ProjectStatusResourceBuilder withProjectLeadOrganisationName(String... projectLeadOrganisationNames) {
        return withArray((projectLeadOrganisationName, psr) -> setField("projectLeadOrganisationName", projectLeadOrganisationName, psr), projectLeadOrganisationNames);
    }

    public ProjectStatusResourceBuilder withProjectDetailStatus(ProjectActivityStates... projectDetailsStatuses) {
        return withArray((projectDetailsStatus, psr) -> setField("projectDetailsStatus", projectDetailsStatus, psr), projectDetailsStatuses);
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

    public ProjectStatusResourceBuilder withOtherDocumentsStatus(ProjectActivityStates... otherDocumentsStatuses) {
        return withArray((otherDocumentsStatus, psr) -> setField("otherDocumentsStatus", otherDocumentsStatus, psr), otherDocumentsStatuses);
    }

    public ProjectStatusResourceBuilder withGrantOfferLetterStatus(ProjectActivityStates... grantOfferLetterStatuses) {
        return withArray((grantOfferLetterStatus, psr) -> setField("grantOfferLetterStatus", grantOfferLetterStatus, psr), grantOfferLetterStatuses);
    }
}
