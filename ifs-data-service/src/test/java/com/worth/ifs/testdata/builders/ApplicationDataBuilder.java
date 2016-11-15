package com.worth.ifs.testdata.builders;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.invite.builder.ApplicationInviteResourceBuilder;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.testdata.builders.data.ApplicationData;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.UserResource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static com.worth.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Arrays.asList;
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

    public ApplicationDataBuilder withQuestionResponse(String questionName, String value, String answeredBy) {
        return withQuestionResponses(builder -> builder.forQuestion(questionName).withAnswer(value, answeredBy));
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

            Organisation organisation = retrieveOrganisationById(collaborator.getOrganisations().get(0));

            ApplicationInviteResource singleInvite = doInviteCollaborator(data, Optional.of(organisation.getId()), organisation.getName(),
                    Optional.of(collaborator.getId()), collaborator.getEmail(), collaborator.getName(), Optional.empty());

            doAs(systemRegistrar(), () -> inviteService.acceptInvite(singleInvite.getHash(), collaborator.getId()));
        });
    }

    public ApplicationDataBuilder inviteCollaboratorNotYetRegistered(String email, String hash, String name, InviteStatus status, String organisationName) {

        return asLeadApplicant(data -> {
            Organisation organisation = retrieveOrganisationByName(organisationName);
            Optional<Long> organisationId = organisation != null ? Optional.of(organisation.getId()) : Optional.empty();
            doInviteCollaborator(data, organisationId, organisationName, Optional.empty(), email, name, Optional.of(hash));
        });
    }

    public ApplicationDataBuilder withFinances(UnaryOperator<ApplicationFinanceDataBuilder>... builderFns) {
        return withFinances(asList(builderFns));
    }

    public ApplicationDataBuilder withFinances(List<UnaryOperator<ApplicationFinanceDataBuilder>> builderFns) {

        return with(data -> {

            ApplicationFinanceDataBuilder baseFinanceBuilder = ApplicationFinanceDataBuilder.newApplicationFinanceData(serviceLocator).
                    withApplication(data.getApplication()).
                    withCompetition(data.getCompetition());

            builderFns.forEach(fn -> fn.apply(baseFinanceBuilder).build());

        });
    }

    public ApplicationDataBuilder beginApplication() {

        return asLeadApplicant(data ->
                applicationService.updateApplicationStatus(data.getApplication().getId(), ApplicationStatusConstants.OPEN.getId()).
                        getSuccessObjectOrThrowException());
    }

    public ApplicationDataBuilder submitApplication() {

        return asLeadApplicant(data -> {

            applicationService.updateApplicationStatus(data.getApplication().getId(), ApplicationStatusConstants.SUBMITTED.getId()).
                    getSuccessObjectOrThrowException();

            applicationService.saveApplicationSubmitDateTime(data.getApplication().getId(), LocalDateTime.now()).getSuccessObjectOrThrowException();
            applicationService.sendNotificationApplicationSubmitted(data.getApplication().getId()).getSuccessObjectOrThrowException();
        });
    }

    private ApplicationInviteResource doInviteCollaborator(ApplicationData data, Optional<Long> organisationId, String organisationName, Optional<Long> userId, String email, String name, Optional<String> hash) {

        ApplicationInviteResourceBuilder baseApplicationInviteBuilder =
                userId.map(id -> newApplicationInviteResource().withUsers(id)).orElse(newApplicationInviteResource());

        Organisation leadOrganisation = retrieveOrganisationById(data.getLeadApplicant().getOrganisations().get(0));

        List<ApplicationInviteResource> applicationInvite = baseApplicationInviteBuilder.
                withApplication(data.getApplication().getId()).
                withName(name).
                withEmail(email).
                withLeadApplicant(data.getLeadApplicant().getName()).
                withLeadApplicantEmail(data.getLeadApplicant().getEmail()).
                withLeadOrganisation(leadOrganisation.getName()).
                withCompetitionId(data.getCompetition().getId()).
                build(1);

        inviteService.createApplicationInvites(newInviteOrganisationResource().
                withOrganisation(organisationId.map(id -> id).orElse(null)).
                withOrganisationName(organisationName).
                withInviteResources(applicationInvite).
                build()).getSuccessObjectOrThrowException();

        Set<InviteOrganisationResource> invites = inviteService.getInvitesByApplication(data.getApplication().getId()).getSuccessObjectOrThrowException();

        InviteOrganisationResource newInvite = simpleFindFirst(new ArrayList<>(invites), i -> simpleFindFirst(i.getInviteResources(), r -> r.getEmail().equals(email)).isPresent()).get();
        ApplicationInviteResource usersInvite = simpleFindFirst(newInvite.getInviteResources(), r -> r.getEmail().equals(email)).get();

        hash.ifPresent(h -> {

            ApplicationInvite saved = applicationInviteRepository.findOne(usersInvite.getId());
            saved.setHash(h);
            applicationInviteRepository.save(saved);

            usersInvite.setHash(h);
        });

        return usersInvite;
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

    public ApplicationDataBuilder withQuestionResponses(
            UnaryOperator<ResponseDataBuilder>... responseBuilders) {

        return withQuestionResponses(asList(responseBuilders));
    }

    public ApplicationDataBuilder withQuestionResponses(
            List<UnaryOperator<ResponseDataBuilder>> responseBuilders) {

        return with(data -> {
            ResponseDataBuilder baseBuilder =
                    ResponseDataBuilder.newApplicationQuestionResponseData(serviceLocator).withApplication(data.getApplication());

            responseBuilders.forEach(builder -> builder.apply(baseBuilder).build());
        });
    }
}
