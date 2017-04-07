package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ApprovalType;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class
ProjectBuilder extends BaseBuilder<Project, ProjectBuilder> {

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

    public ProjectBuilder withCollaborationAgreement(FileEntry collaborationAgreement) {
        return with (project -> project.setCollaborationAgreement(collaborationAgreement));
    }

    public ProjectBuilder withExploitationPlan(FileEntry exploitationPlan) {
        return with (project -> project.setExploitationPlan(exploitationPlan));
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

    public ProjectBuilder withProjectUsers(List<ProjectUser>... projectUsers){
        return withArray((users, project) -> project.setProjectUsers(users), projectUsers);
    }

    public ProjectBuilder withDateSubmitted(ZonedDateTime... dates) {
        return withArray((date, project) -> project.setSpendProfileSubmittedDate(date), dates);
    }


    public ProjectBuilder withPartnerOrganisations(List<PartnerOrganisation>... partnerOrganisations) {
        return withArray((orgs, project) -> project.setPartnerOrganisations(orgs), partnerOrganisations);
    }

    public ProjectBuilder withOtherDocumentsApproved(ApprovalType... approved){
        return withArray((approval, project) -> project.setOtherDocumentsApproved(approval), approved);
    }

    public ProjectBuilder withOfferSubmittedDate(ZonedDateTime... dates){
        return withArray ((date, project) -> project.setOfferSubmittedDate(date), dates);
    }

    public ProjectBuilder withOtherDocumentsSubmittedDate(ZonedDateTime date) {
        return with (project -> project.setDocumentsSubmittedDate(date));
    }

    public ProjectBuilder withSpendProfileSubmittedDate(ZonedDateTime date) {
        return with (project -> project.setSpendProfileSubmittedDate(date));
    }

    @Override
    protected void postProcess(int index, Project project) {

        // add Hibernate-style backlinks
        project.getProjectUsers().forEach(pu -> {
            setField("project", project, pu);
        });

        project.getPartnerOrganisations().forEach(org -> {
            setField("project", project, org);
        });
    }
}
