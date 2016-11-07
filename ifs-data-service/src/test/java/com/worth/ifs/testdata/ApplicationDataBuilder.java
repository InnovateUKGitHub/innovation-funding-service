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
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.worth.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static com.worth.ifs.testdata.ApplicationFinanceDataBuilder.newApplicationFinanceData;
import static com.worth.ifs.testdata.ResponseDataBuilder.newApplicationQuestionResponseData;
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

    public ApplicationDataBuilder withFinances(Function<ApplicationFinanceDataBuilder, ApplicationFinanceDataBuilder>... builderFns) {
        return withFinances(asList(builderFns));
    }

    public ApplicationDataBuilder withFinances(List<Function<ApplicationFinanceDataBuilder, ApplicationFinanceDataBuilder>> builderFns) {

        return with(data -> {

            ApplicationFinanceDataBuilder baseFinanceBuilder = newApplicationFinanceData(serviceLocator).
                    withApplication(data.getApplication()).
                    withCompetition(data.getCompetition());

            builderFns.forEach(fn -> fn.apply(baseFinanceBuilder).build());

        });
    }

    public ApplicationDataBuilder openApplication() {

        return asLeadApplicant(data ->
                applicationService.updateApplicationStatus(data.getApplication().getId(), ApplicationStatusConstants.OPEN.getId()));
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

    public ApplicationDataBuilder withQuestionResponses(
            UnaryOperator<ResponseDataBuilder>... responseBuilders) {

        return withQuestionResponses(asList(responseBuilders));
    }

    public ApplicationDataBuilder withQuestionResponses(
            List<UnaryOperator<ResponseDataBuilder>> responseBuilders) {

        return with(data -> {
            ResponseDataBuilder baseBuilder =
                    newApplicationQuestionResponseData(serviceLocator).withApplication(data.getApplication());

            responseBuilders.forEach(builder -> builder.apply(baseBuilder).build());
        });
    }
}
