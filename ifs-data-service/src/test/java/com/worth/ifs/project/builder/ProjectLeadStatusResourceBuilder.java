package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.project.resource.ProjectLeadStatusResource;
import com.worth.ifs.user.resource.OrganisationTypeEnum;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ProjectLeadStatusResourceBuilder extends BaseBuilder<ProjectLeadStatusResource, ProjectLeadStatusResourceBuilder> {

    private ProjectLeadStatusResourceBuilder(List<BiConsumer<Integer, ProjectLeadStatusResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectLeadStatusResourceBuilder newProjectLeadStatusResource() {
        return new ProjectLeadStatusResourceBuilder(emptyList());
    }

    @Override
    protected ProjectLeadStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectLeadStatusResource>> actions) {
        return new ProjectLeadStatusResourceBuilder(actions);
    }

    @Override
    protected ProjectLeadStatusResource createInitial() {
        return newInstance(ProjectLeadStatusResource.class);
    }

    public ProjectLeadStatusResourceBuilder withName(String... names){
        return withArray((name, partnerStatus) -> partnerStatus.setName(name), names);
    }

    public ProjectLeadStatusResourceBuilder withFinanceContactStatus(ProjectActivityStates... financeContactStatuses) {
        return withArray((financeContactStatus, partnerStatus) -> partnerStatus.setFinanceContactStatus(financeContactStatus), financeContactStatuses);
    }

    public ProjectLeadStatusResourceBuilder withBankDetailsStatus(ProjectActivityStates... bankDetailsStatuses){
        return withArray((bankDetailsStatus, partnerStatus) -> partnerStatus.setBankDetailsStatus(bankDetailsStatus), bankDetailsStatuses);
    }

    public ProjectLeadStatusResourceBuilder withFinanceChecksStatus(ProjectActivityStates... financeChecksStatuses){
        return withArray((financeChecksStatus, partnerStatus) -> partnerStatus.setFinanceChecksStatus(financeChecksStatus), financeChecksStatuses);
    }

    public ProjectLeadStatusResourceBuilder withGrantOfferStatus(ProjectActivityStates... grantOfferStatuses){
        return withArray((grantOfferStatus, partnerStatus) -> partnerStatus.setGrantOfferLetterStatus(grantOfferStatus), grantOfferStatuses);
    }

    public ProjectLeadStatusResourceBuilder withMonitoringOfficerStatus(ProjectActivityStates... monitoringOfficerStatuses){
        return withArray((monitoringOfficerStatus, partnerStatus) -> partnerStatus.setMonitoringOfficerStatus(monitoringOfficerStatus), monitoringOfficerStatuses);
    }

    public ProjectLeadStatusResourceBuilder withOtherDocumentsStatus(ProjectActivityStates... otherDocumentsStatuses){
        return withArray((otherDocumentsStatus, partnerStatus) -> partnerStatus.setOtherDocumentsStatus(otherDocumentsStatus), otherDocumentsStatuses);
    }

    public ProjectLeadStatusResourceBuilder withProjectDetailsStatus(ProjectActivityStates... projectDetailsStatuses){
        return withArray((projectDetailsStatus, partnerStatus) -> partnerStatus.setProjectDetailsStatus(projectDetailsStatus), projectDetailsStatuses);
    }

    public ProjectLeadStatusResourceBuilder withSpendProfileStatus(ProjectActivityStates... spendProfileStatuses){
        return withArray((spendProfileStatus, partnerStatus) -> partnerStatus.setSpendProfileStatus(spendProfileStatus), spendProfileStatuses);
    }

    public ProjectLeadStatusResourceBuilder withOrganisationType(OrganisationTypeEnum... organisationTypes){
        return withArray((organisationType, partnerStatus) -> partnerStatus.setOrganisationType(organisationType), organisationTypes);
    }

    public ProjectLeadStatusResourceBuilder withOrganisationId(Long... organisationIds){
        return withArray((organisationId, partnerStatus) -> partnerStatus.setOrganisationId(organisationId), organisationIds);
    }
}
