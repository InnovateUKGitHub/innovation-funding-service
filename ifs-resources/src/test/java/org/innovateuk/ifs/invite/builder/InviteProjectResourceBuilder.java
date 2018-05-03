package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class InviteProjectResourceBuilder extends BaseBuilder<InviteProjectResource, InviteProjectResourceBuilder> {

    private InviteProjectResourceBuilder(List<BiConsumer<Integer, InviteProjectResource>> multiActions) {
        super(multiActions);
    }

    public static InviteProjectResourceBuilder newInviteProjectResource() {
        return new InviteProjectResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InviteProjectResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InviteProjectResource>> actions) {
        return new InviteProjectResourceBuilder(actions);
    }

    public InviteProjectResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public InviteProjectResourceBuilder withName(String... name) {
        return withArraySetFieldByReflection("name", name);
    }

    public InviteProjectResourceBuilder withNameConfirmed(String... nameConfirmed) {
        return withArraySetFieldByReflection("nameConfirmed", nameConfirmed);
    }

    public InviteProjectResourceBuilder withEmail(String... email) {
        return withArraySetFieldByReflection("email", email);
    }

    public InviteProjectResourceBuilder withProject(Long... projectId) {
        return withArraySetFieldByReflection("project", projectId);
    }

    public InviteProjectResourceBuilder withApplicationId(Long... applicationId) {
        return withArraySetFieldByReflection("applicationId", applicationId);
    }

    public InviteProjectResourceBuilder withOrganisation(Long... organisationId) {
        return withArraySetFieldByReflection("organisation", organisationId);
    }

    public InviteProjectResourceBuilder withProjectName(String... projectName) {
        return withArraySetFieldByReflection("projectName", projectName);
    }

    public InviteProjectResourceBuilder withHash(String... hash) {
        return withArraySetFieldByReflection("hash", hash);
    }

    public InviteProjectResourceBuilder withStatus(InviteStatus... inviteStatus) {
        return withArraySetFieldByReflection("status", inviteStatus);
    }

    public InviteProjectResourceBuilder withLeadOrganisation(Long... leadOrganisationId) {
        return withArraySetFieldByReflection("leadOrganisationId", leadOrganisationId);
    }

    public InviteProjectResourceBuilder withLeadOrganisation(String... leadOrganisationName) {
        return withArraySetFieldByReflection("leadOrganisation", leadOrganisationName);
    }

    public InviteProjectResourceBuilder withOrganisationName(String... organisationName) {
        return withArraySetFieldByReflection("organisationName", organisationName);
    }

    public InviteProjectResourceBuilder withLeadApplicant(String... leadApplicantName) {
        return withArraySetFieldByReflection("leadApplicant", leadApplicantName);
    }

    public InviteProjectResourceBuilder withCompetitionName(String... competitionName) {
        return withArraySetFieldByReflection("competitionName", competitionName);
    }

    public InviteProjectResourceBuilder withUser(Long... userId) {
        return withArraySetFieldByReflection("user", userId);
    }

    @Override
    protected InviteProjectResource createInitial() {
        return new InviteProjectResource();
    }
}
