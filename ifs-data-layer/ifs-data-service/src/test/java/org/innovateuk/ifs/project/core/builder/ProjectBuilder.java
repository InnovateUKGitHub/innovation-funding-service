package org.innovateuk.ifs.project.core.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.financereviewer.domain.FinanceReviewer;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.resource.ApprovalType;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ProjectBuilder extends BaseBuilder<Project, ProjectBuilder> {

    private ProjectBuilder(List<BiConsumer<Integer, Project>> multiActions) {
        super(multiActions);
    }

    public static ProjectBuilder newProject() {
        return new ProjectBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Project>> actions) {
        return new ProjectBuilder(actions);
    }

    @Override
    protected Project createInitial() {
        return new Project();
    }

    public ProjectBuilder withId(Long... ids) {
        return withArray((id, project) -> setField("id", id, project), ids);
    }

    public ProjectBuilder withTargetStartDate(LocalDate... dates) {
        return withArray((date, project) -> project.setTargetStartDate(date), dates);
    }

    public ProjectBuilder withAddress(Address... address) {
        return withArray((add, project) -> project.setAddress(add), address);
    }

    public ProjectBuilder withSignedGrantOfferLetter(FileEntry grantOfferLetter) {
        return with (project -> project.setSignedGrantOfferLetter(grantOfferLetter));
    }

    public ProjectBuilder withGrantOfferLetter(FileEntry grantOfferLetter) {
        return with (project -> project.setGrantOfferLetter(grantOfferLetter));
    }

    public ProjectBuilder withAdditionalContractFile(FileEntry additionalContractFile) {
        return with (project -> project.setAdditionalContractFile(additionalContractFile));
    }

    public ProjectBuilder withDuration(Long... durations) {
        return withArray((duration, project) -> project.setDurationInMonths(duration), durations);
    }

    public ProjectBuilder withName(String... names) {
        return withArray((name, project) -> project.setName(name), names);
    }

    public ProjectBuilder withApplication(Application... application){
        return withArray((app, project) -> project.setApplication(app), application);
    }

    @SafeVarargs
    public final  ProjectBuilder withProjectUsers(List<ProjectUser>... projectUsers){
        return withArray((users, project) -> project.setProjectUsers(users), projectUsers);
    }

    @SafeVarargs
    public final ProjectBuilder withProjectDocuments(List<ProjectDocument>... projectDocuments){
        return withArray((projectDocument, project) -> project.setProjectDocuments(projectDocument), projectDocuments);
    }

    public ProjectBuilder withDateSubmitted(ZonedDateTime... dates) {
        return withArray((date, project) -> project.setSpendProfileSubmittedDate(date), dates);
    }

    @SafeVarargs
    public final ProjectBuilder withPartnerOrganisations(List<PartnerOrganisation>... partnerOrganisations) {
        return withArray((orgs, project) -> project.setPartnerOrganisations(orgs), partnerOrganisations);
    }

    public ProjectBuilder withOtherDocumentsApproved(ApprovalType... approved){
        return withArray((approval, project) -> project.setOtherDocumentsApproved(approval), approved);
    }

    public ProjectBuilder withOfferSubmittedDate(ZonedDateTime... dates){
        return withArray ((date, project) -> project.setOfferSubmittedDate(date), dates);
    }

    public ProjectBuilder withSpendProfileSubmittedDate(ZonedDateTime date) {
        return with (project -> project.setSpendProfileSubmittedDate(date));
    }

    public ProjectBuilder withProjectMonitoringOfficer(MonitoringOfficer... projectMonitoringOfficers) {
        return withArray((projectMonitoringOfficer, project) -> project.setProjectMonitoringOfficer(projectMonitoringOfficer), projectMonitoringOfficers);
    }

    public ProjectBuilder withFinanceReviewer(FinanceReviewer... financeReviewers) {
        return withArray((financeReviewer, project) -> project.setFinanceReviewer(financeReviewer), financeReviewers);
    }

    public ProjectBuilder withProjectProcess(ProjectProcess... projectProcesses) {
        return withArraySetFieldByReflection("projectProcess", projectProcesses);
    }

    @Override
    protected void postProcess(int index, Project project) {

        // add Hibernate-style backlinks
        project.getProjectUsers().forEach(pu -> setField("project", project, pu));

        project.getPartnerOrganisations().forEach(org -> setField("project", project, org));

        project.getProjectMonitoringOfficer().ifPresent(mo -> setField("project", project, mo));

        Optional.ofNullable(project.getProjectProcess()).ifPresent(projectProcess -> projectProcess.setTarget(project));
    }
}