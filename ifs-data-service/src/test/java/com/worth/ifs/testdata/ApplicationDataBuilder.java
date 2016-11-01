package com.worth.ifs.testdata;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.user.resource.UserResource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.worth.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Collections.emptyList;


public class ApplicationDataBuilder extends BaseDataBuilder<ApplicationData, ApplicationDataBuilder> {

    public ApplicationDataBuilder withCompetition(CompetitionResource competition) {
        return with(data -> data.setCompetition(competition));
    }

    public ApplicationDataBuilder withBasicDetails(UserResource leadApplicant, String applicationName) {

        return with(data -> {

            doAs(leadApplicant, () -> {

                ApplicationResource created = applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(
                        applicationName, data.getCompetition().getId(), leadApplicant.getId()).
                        getSuccessObjectOrThrowException();

                data.setLeadApplicant(leadApplicant);
                data.setApplication(created);
            });
        });
    }

    public ApplicationDataBuilder withStartDate(LocalDate startDate) {
        return asLeadApplicant(data -> doApplicationDetailsUpdate(data, application -> application.setStartDate(startDate)));
    }

    public ApplicationDataBuilder withDurationInMonths(long durationInMonths) {
        return asLeadApplicant(data -> doApplicationDetailsUpdate(data, application ->
                application.setDurationInMonths(durationInMonths)));
    }

    public ApplicationDataBuilder inviteCollaborator(UserResource collaborator) {

        return asLeadApplicant(data -> {

            inviteService.createApplicationInvites(newInviteOrganisationResource().
                    withOrganisation(collaborator.getOrganisations().get(0)).
                    withInviteResources(newApplicationInviteResource().
                            withUsers(collaborator.getId()).
                            withApplication(data.getApplication().getId()).
                            withName(collaborator.getFirstName()).
                            withEmail(collaborator.getEmail()).
                            build(1)).
                    build()).getSuccessObjectOrThrowException();

            Set<InviteOrganisationResource> invites = inviteService.getInvitesByApplication(data.getApplication().getId()).getSuccessObjectOrThrowException();

            InviteOrganisationResource newInvite = simpleFindFirst(new ArrayList<>(invites), i -> simpleFindFirst(i.getInviteResources(), r -> r.getEmail().equals(collaborator.getEmail())).isPresent()).get();
            ApplicationInviteResource singleInvite = simpleFindFirst(newInvite.getInviteResources(), r -> r.getEmail().equals(collaborator.getEmail())).get();

            doAs(systemRegistrar(), () -> inviteService.acceptInvite(singleInvite.getHash(), collaborator.getId()));
        });
    }

    public ApplicationDataBuilder submitApplication() {

        return asLeadApplicant(data ->
            applicationService.updateApplicationStatus(data.getApplication().getId(), ApplicationStatusConstants.SUBMITTED.getId()));
    }

    private ApplicationDataBuilder asLeadApplicant(Consumer<ApplicationData> action) {
        return with(data -> doAs(data.getLeadApplicant(), () -> action.accept(data)));
    }

    private void doApplicationDetailsUpdate(ApplicationData data, Consumer<ApplicationResource> updateFn) {

        ApplicationResource application =
                applicationService.getApplicationById(data.getApplication().getId()).getSuccessObjectOrThrowException();

        updateFn.accept(application);

        ApplicationResource updated = applicationService.saveApplicationDetails(application.getId(), application).
                getSuccessObjectOrThrowException();

        data.setApplication(updated);
    }

    public static ApplicationDataBuilder newApplicationData(ServiceLocator serviceLocator) {

        return new ApplicationDataBuilder(emptyList(), serviceLocator);
    }

    private ApplicationDataBuilder(List<BiConsumer<Integer, ApplicationData>> multiActions,
                                   ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected ApplicationDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationData>> actions) {
        return new ApplicationDataBuilder(actions, serviceLocator);
    }

    @Override
    protected ApplicationData createInitial() {
        return new ApplicationData();
    }
}
