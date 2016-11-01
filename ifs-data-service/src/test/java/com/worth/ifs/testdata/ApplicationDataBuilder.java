package com.worth.ifs.testdata;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.user.resource.UserResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

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

    public ApplicationDataBuilder inviteCollaborator(UserResource collaborator) {

        return with(data -> {

            doAs(data.getLeadApplicant(), () -> {

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
        });
    }

    public ApplicationDataBuilder submitApplication() {

        return with(data -> {

            doAs(data.getLeadApplicant(), () -> {

                applicationService.updateApplicationStatus(data.getApplication().getId(), ApplicationStatusConstants.SUBMITTED.getId());
            });
        });
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
