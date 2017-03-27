package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ApplicationInviteResourceBuilder extends BaseBuilder<ApplicationInviteResource, ApplicationInviteResourceBuilder> {

    private ApplicationInviteResourceBuilder(List<BiConsumer<Integer, ApplicationInviteResource>> multiActions) {
        super(multiActions);
    }

    public static ApplicationInviteResourceBuilder newApplicationInviteResource() {
        return new ApplicationInviteResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationInviteResource createInitial() {
        return new ApplicationInviteResource();
    }

    @Override
    protected ApplicationInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationInviteResource>> actions) {
        return new ApplicationInviteResourceBuilder(actions);
    }

    public ApplicationInviteResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public ApplicationInviteResourceBuilder withLeadApplicant(String... leadApplicants) {
        return withArraySetFieldByReflection("leadApplicant", leadApplicants);
    }

    public ApplicationInviteResourceBuilder withLeadApplicantEmail(String... leadApplicantEmails) {
        return withArraySetFieldByReflection("leadApplicantEmail", leadApplicantEmails);
    }

    public ApplicationInviteResourceBuilder withLeadOrganisation(String... leadOrganisations) {
        return withArraySetFieldByReflection("leadOrganisation", leadOrganisations);
    }

    public ApplicationInviteResourceBuilder withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public ApplicationInviteResourceBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public ApplicationInviteResourceBuilder withNameConfirmed(String... namesConfirmed) {
        return withArraySetFieldByReflection("nameConfirmed", namesConfirmed);
    }

    public ApplicationInviteResourceBuilder withApplication(Long... applications) {
        return withArraySetFieldByReflection("application", applications);
    }

    public ApplicationInviteResourceBuilder withUsers(Long... users) {
        return withArraySetFieldByReflection("user", users);
    }

    public ApplicationInviteResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArraySetFieldByReflection("competitionId", competitionIds);
    }

    public ApplicationInviteResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public ApplicationInviteResourceBuilder withStatus(InviteStatus... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public ApplicationInviteResourceBuilder withHash(String... hashes) {
        return withArraySetFieldByReflection("hash", hashes);
    }

    public ApplicationInviteResourceBuilder withInviteOrganisation(Long... inviteOrganisations) {
        return withArraySetFieldByReflection("inviteOrganisation", inviteOrganisations);
    }

    public ApplicationInviteResourceBuilder withInviteOrganisationNameConfirmed(String... inviteOrganisationNameConfirmeds) {
        return withArraySetFieldByReflection("inviteOrganisationNameConfirmed", inviteOrganisationNameConfirmeds);
    }

    public ApplicationInviteResourceBuilder withInviteOrganisationName(String... inviteOrganisationNames) {
        return withArraySetFieldByReflection("inviteOrganisationName", inviteOrganisationNames);
    }

    @Override
    protected void postProcess(int index, ApplicationInviteResource instance) {
        super.postProcess(index, instance);
    }
}
