package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.project.resource.ProjectPartnerStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ProjectPartnerStatusResourceBuilder extends BaseBuilder<ProjectPartnerStatusResource, ProjectPartnerStatusResourceBuilder> {

    private ProjectPartnerStatusResourceBuilder(List<BiConsumer<Integer, ProjectPartnerStatusResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectPartnerStatusResourceBuilder newProjectPartnerStatusResource() {
        return new ProjectPartnerStatusResourceBuilder(emptyList());
    }

    @Override
    protected ProjectPartnerStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectPartnerStatusResource>> actions) {
        return new ProjectPartnerStatusResourceBuilder(actions);
    }

    @Override
    protected ProjectPartnerStatusResource createInitial() {
        return new ProjectPartnerStatusResource();
    }

    public ProjectPartnerStatusResourceBuilder withBankDetailsStatus(ProjectActivityStates... bankDetailsStatuses){
        return withArray((bankDetailsStatus, partnerStatus) -> partnerStatus.setBankDetailsStatus(bankDetailsStatus), bankDetailsStatuses);
    }

    public ProjectPartnerStatusResourceBuilder withFinanceChecksStatus(ProjectActivityStates... financeChecksStatuses){
        return withArray((financeChecksStatus, partnerStatus) -> partnerStatus.setFinanceChecksStatus(financeChecksStatus), financeChecksStatuses);
    }

    public ProjectPartnerStatusResourceBuilder withGrantOfferStatus(ProjectActivityStates... grantOfferStatuses){
        return withArray((grantOfferStatus, partnerStatus) -> partnerStatus.setGrantOfferLetterStatus(grantOfferStatus), grantOfferStatuses);
    }

    public ProjectPartnerStatusResourceBuilder withMonitoringOfficerStatus(ProjectActivityStates... monitoringOfficerStatuses){
        return withArray((monitoringOfficerStatus, partnerStatus) -> partnerStatus.setMonitoringOfficerStatus(monitoringOfficerStatus), monitoringOfficerStatuses);
    }

    public ProjectPartnerStatusResourceBuilder withOtherDocumentsStatus(ProjectActivityStates... otherDocumentsStatuses){
        return withArray((otherDocumentsStatus, partnerStatus) -> partnerStatus.setOtherDocumentsStatus(otherDocumentsStatus), otherDocumentsStatuses);
    }

    public ProjectPartnerStatusResourceBuilder withProjectDetailsStatus(ProjectActivityStates... projectDetailsStatuses){
        return withArray((projectDetailsStatus, partnerStatus) -> partnerStatus.setProjectDetailsStatus(projectDetailsStatus), projectDetailsStatuses);
    }

    public ProjectPartnerStatusResourceBuilder withSpendProfileStatus(ProjectActivityStates... spendProfileStatuses){
        return withArray((spendProfileStatus, partnerStatus) -> partnerStatus.setSpendProfileStatus(spendProfileStatus), spendProfileStatuses);
    }
}
