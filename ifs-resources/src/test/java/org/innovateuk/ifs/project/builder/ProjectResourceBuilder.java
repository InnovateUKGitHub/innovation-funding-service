package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ProjectResourceBuilder extends BaseBuilder<ProjectResource, ProjectResourceBuilder> {

    private ProjectResourceBuilder(List<BiConsumer<Integer, ProjectResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectResourceBuilder newProjectResource() {
        return new ProjectResourceBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedNames("Project "));
    }

    @Override
    protected ProjectResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectResource>> actions) {
        return new ProjectResourceBuilder(actions);
    }

    @Override
    protected ProjectResource createInitial() {
        return new ProjectResource();
    }

    public ProjectResourceBuilder withId(Long... ids) {
        return withArray((id, project) -> setField("id", id, project), ids);
    }

    public ProjectResourceBuilder withName(String... name){
        return withArray((n, project) -> project.setName(n), name);
    }

    public ProjectResourceBuilder withApplication(ApplicationResource applicationResource){
        return with(project -> project.setApplication(applicationResource.getId()));
    }

    public ProjectResourceBuilder withApplication(Long... application){
        return withArray((applicationId, project) -> project.setApplication(applicationId), application);
    }

    public ProjectResourceBuilder withTargetStartDate(LocalDate... dates) {
        return withArray((date, project) -> project.setTargetStartDate(date), dates);
    }

    public ProjectResourceBuilder withAddress(AddressResource address) {
        return with(project -> project.setAddress(address));
    }

    public ProjectResourceBuilder withCollaborationAgreement(Long collaborationAgreement) {
        return with (project -> project.setCollaborationAgreement(collaborationAgreement));
    }

    public ProjectResourceBuilder withExploitationPlan(Long exploitationPlan) {
        return with (project -> project.setExploitationPlan(exploitationPlan));
    }

    public ProjectResourceBuilder withSignedGrantOfferLetter(Long grantOfferLetter) {
        return with (project -> project.setSignedGrantOfferLetter(grantOfferLetter));
    }

    public ProjectResourceBuilder withGrantOfferLetter(Long grantOfferLetter) {
        return with (project -> project.setGrantOfferLetter(grantOfferLetter));
    }

    public ProjectResourceBuilder withAdditionalContractFile(Long additionalContractFile) {
        return with (project -> project.setAdditionalContractFile(additionalContractFile));
    }

    public ProjectResourceBuilder withOtherDocumentsApproved(ApprovalType otherDocumentsApproved) {
        return with(project -> project.setOtherDocumentsApproved(otherDocumentsApproved));
    }

    public ProjectResourceBuilder withDocumentsSubmittedDate(ZonedDateTime documentsSubmittedDate) {
        return with(project -> project.setDocumentsSubmittedDate(documentsSubmittedDate));
    }

    public ProjectResourceBuilder withProjectUsers(List<Long>... projectUsers) {
        return withArray((userList, project) -> project.setProjectUsers(userList), projectUsers);
    }

    public ProjectResourceBuilder withDuration(Long... durations) {
        return withArray((duration, project) -> project.setDurationInMonths(duration), durations);
    }
}
