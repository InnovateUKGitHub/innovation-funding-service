package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ProjectInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ProjectInviteResourceBuilder extends BaseBuilder<ProjectInviteResource, ProjectInviteResourceBuilder> {

    private ProjectInviteResourceBuilder(List<BiConsumer<Integer, ProjectInviteResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectInviteResourceBuilder newProjectInviteResource() {
        return new ProjectInviteResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectInviteResource>> actions) {
        return new ProjectInviteResourceBuilder(actions);
    }

    public ProjectInviteResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public ProjectInviteResourceBuilder withName(String... name) {
        return withArraySetFieldByReflection("name", name);
    }

    public ProjectInviteResourceBuilder withNameConfirmed(String... nameConfirmed) {
        return withArraySetFieldByReflection("nameConfirmed", nameConfirmed);
    }

    public ProjectInviteResourceBuilder withEmail(String... email) {
        return withArraySetFieldByReflection("email", email);
    }

    public ProjectInviteResourceBuilder withProject(Long... projectId) {
        return withArraySetFieldByReflection("project", projectId);
    }

    public ProjectInviteResourceBuilder withApplicationId(Long... applicationId) {
        return withArraySetFieldByReflection("applicationId", applicationId);
    }

    public ProjectInviteResourceBuilder withOrganisation(Long... organisationId) {
        return withArraySetFieldByReflection("organisation", organisationId);
    }

    public ProjectInviteResourceBuilder withProjectName(String... projectName) {
        return withArraySetFieldByReflection("projectName", projectName);
    }

    public ProjectInviteResourceBuilder withHash(String... hash) {
        return withArraySetFieldByReflection("hash", hash);
    }

    public ProjectInviteResourceBuilder withStatus(InviteStatus... inviteStatus) {
        return withArraySetFieldByReflection("status", inviteStatus);
    }

    public ProjectInviteResourceBuilder withLeadOrganisation(Long... leadOrganisationId) {
        return withArraySetFieldByReflection("leadOrganisationId", leadOrganisationId);
    }

    public ProjectInviteResourceBuilder withLeadOrganisation(String... leadOrganisationName) {
        return withArraySetFieldByReflection("leadOrganisation", leadOrganisationName);
    }

    public ProjectInviteResourceBuilder withOrganisationName(String... organisationName) {
        return withArraySetFieldByReflection("organisationName", organisationName);
    }

    public ProjectInviteResourceBuilder withLeadApplicant(String... leadApplicantName) {
        return withArraySetFieldByReflection("leadApplicant", leadApplicantName);
    }

    public ProjectInviteResourceBuilder withCompetitionName(String... competitionName) {
        return withArraySetFieldByReflection("competitionName", competitionName);
    }

    public ProjectInviteResourceBuilder withUser(Long... userId) {
        return withArraySetFieldByReflection("user", userId);
    }

    @Override
    protected ProjectInviteResource createInitial() {
        return new ProjectInviteResource();
    }
}
