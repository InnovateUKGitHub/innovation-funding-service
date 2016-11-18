package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.resource.ApplicationInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
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
        return withArray((id, applicationInviteResource) -> setField("id", id, applicationInviteResource), ids);
    }

    public ApplicationInviteResourceBuilder withLeadApplicant(String... leadApplicants) {
        return withArray((leadApplicant, applicationInviteResource) -> setField("leadApplicant", leadApplicant, applicationInviteResource), leadApplicants);
    }

    public ApplicationInviteResourceBuilder withLeadApplicantEmail(String... leadApplicantEmails) {
        return withArray((leadApplicantEmail, applicationInviteResource) -> setField("leadApplicantEmail", leadApplicantEmail, applicationInviteResource), leadApplicantEmails);
    }

    public ApplicationInviteResourceBuilder withName(String... names) {
        return withArray((name, applicationInviteResource) -> setField("name", name, applicationInviteResource), names);
    }

    public ApplicationInviteResourceBuilder withNameConfirmed(String... namesConfirmed) {
        return withArray((nameConfirmed, applicationInviteResource) -> setField("nameConfirmed", nameConfirmed, applicationInviteResource), namesConfirmed);
    }

    public ApplicationInviteResourceBuilder withApplication(Long... applications) {
        return withArray((reason, applicationInviteResource) -> setField("application", reason, applicationInviteResource), applications);
    }

    public ApplicationInviteResourceBuilder withUsers(Long... users) {
        return withArray((user, applicationInviteResource) -> setField("user", user, applicationInviteResource), users);
    }

    public ApplicationInviteResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArray((competitionId, applicationInviteResource) -> setField("competitionId", competitionId, applicationInviteResource), competitionIds);
    }

    public ApplicationInviteResourceBuilder withStatus(InviteStatus... statuses) {
        return withArray((status, applicationInviteResource) -> setField("status", status, applicationInviteResource), statuses);
    }

    @Override
    protected void postProcess(int index, ApplicationInviteResource instance) {
        super.postProcess(index, instance);
    }
}