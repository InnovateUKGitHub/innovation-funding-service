package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ProjectUserInviteResourceBuilder extends BaseBuilder<ProjectUserInviteResource, ProjectUserInviteResourceBuilder> {

    private ProjectUserInviteResourceBuilder(List<BiConsumer<Integer, ProjectUserInviteResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectUserInviteResourceBuilder newProjectUserInviteResource() {
        return new ProjectUserInviteResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectUserInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectUserInviteResource>> actions) {
        return new ProjectUserInviteResourceBuilder(actions);
    }

    public ProjectUserInviteResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public ProjectUserInviteResourceBuilder withName(String... name) {
        return withArraySetFieldByReflection("name", name);
    }

    public ProjectUserInviteResourceBuilder withNameConfirmed(String... nameConfirmed) {
        return withArraySetFieldByReflection("nameConfirmed", nameConfirmed);
    }

    public ProjectUserInviteResourceBuilder withEmail(String... email) {
        return withArraySetFieldByReflection("email", email);
    }

    public ProjectUserInviteResourceBuilder withProject(Long... projectId) {
        return withArraySetFieldByReflection("project", projectId);
    }

    public ProjectUserInviteResourceBuilder withApplicationId(Long... applicationId) {
        return withArraySetFieldByReflection("applicationId", applicationId);
    }

    public ProjectUserInviteResourceBuilder withOrganisation(Long... organisationId) {
        return withArraySetFieldByReflection("organisation", organisationId);
    }

    public ProjectUserInviteResourceBuilder withProjectName(String... projectName) {
        return withArraySetFieldByReflection("projectName", projectName);
    }

    public ProjectUserInviteResourceBuilder withHash(String... hash) {
        return withArraySetFieldByReflection("hash", hash);
    }

    public ProjectUserInviteResourceBuilder withStatus(InviteStatus... inviteStatus) {
        return withArraySetFieldByReflection("status", inviteStatus);
    }

    public ProjectUserInviteResourceBuilder withLeadOrganisation(Long... leadOrganisationId) {
        return withArraySetFieldByReflection("leadOrganisationId", leadOrganisationId);
    }

    public ProjectUserInviteResourceBuilder withLeadOrganisation(String... leadOrganisationName) {
        return withArraySetFieldByReflection("leadOrganisation", leadOrganisationName);
    }

    public ProjectUserInviteResourceBuilder withOrganisationName(String... organisationName) {
        return withArraySetFieldByReflection("organisationName", organisationName);
    }

    public ProjectUserInviteResourceBuilder withLeadApplicant(String... leadApplicantName) {
        return withArraySetFieldByReflection("leadApplicant", leadApplicantName);
    }

    public ProjectUserInviteResourceBuilder withCompetitionName(String... competitionName) {
        return withArraySetFieldByReflection("competitionName", competitionName);
    }

    public ProjectUserInviteResourceBuilder withUser(Long... userId) {
        return withArraySetFieldByReflection("user", userId);
    }

    @Override
    protected ProjectUserInviteResource createInitial() {
        return new ProjectUserInviteResource();
    }
}
