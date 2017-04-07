package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapperImpl;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.testdata.builders.data.ApplicationData;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Generates an Application for a Competition.  Additionally generates finances for each Organisationn on the Application
 */
public class ApplicationDataBuilder extends BaseDataBuilder<ApplicationData, ApplicationDataBuilder> {

    public ApplicationDataBuilder withCompetition(CompetitionResource competition) {
        return with(data -> data.setCompetition(competition));
    }

    public ApplicationDataBuilder withBasicDetails(UserResource leadApplicant, String applicationName, String researchCategory, boolean resubmission) {

        return with(data -> {

            doAs(leadApplicant, () -> {

                ApplicationResource created = applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(
                        applicationName, data.getCompetition().getId(), leadApplicant.getId()).
                        getSuccessObjectOrThrowException();

                ResearchCategoryMapper researchCategoryMapper = new ResearchCategoryMapperImpl();
                ResearchCategory category = researchCategoryRepository.findByName(researchCategory);
                created.setResearchCategory(researchCategoryMapper.mapToResource(category));
                created.setResubmission(resubmission);
                created = applicationService.saveApplicationDetails(created.getId(), created)
                        .getSuccessObjectOrThrowException();

                data.setLeadApplicant(leadApplicant);
                data.setApplication(created);
            });
        });
    }

    public ApplicationDataBuilder withInnovationArea(String innovationAreaName) {
        return asLeadApplicant(data -> {
                if (innovationAreaName.equals("NOT_APPLICABLE")) {
                    applicationInnovationAreaService.setNoInnovationAreaApplies(data.getApplication().getId());
                } else if (!innovationAreaName.isEmpty()) {
                    InnovationArea innovationArea = innovationAreaRepository.findByName(innovationAreaName);
                    applicationInnovationAreaService.setInnovationArea(data.getApplication().getId(), innovationArea.getId());
                }
            });
    }

    public ApplicationDataBuilder markApplicationDetailsComplete(boolean markAsComplete) {
        return asLeadApplicant(data -> {
            if (markAsComplete) {
                QuestionResource questionResource = simpleFindFirst(questionService.findByCompetition(data
                                .getCompetition()
                                .getId())
                                .getSuccessObjectOrThrowException(),
                        x -> "Application details".equals(x.getName())).get();

                questionService.markAsComplete(new QuestionApplicationCompositeId(questionResource.getId(), data
                                .getApplication()
                                .getId()),
                        retrieveLeadApplicant(data.getApplication().getId()).getId())
                        .getSuccessObjectOrThrowException();
            }
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

            List<Organisation> organisations = organisationRepository.findByUsersId(collaborator.getId());
            Organisation organisation = organisations.get(0);

            ApplicationInviteResource singleInvite = doInviteCollaborator(data, organisation.getName(),
                    Optional.of(collaborator.getId()), collaborator.getEmail(), collaborator.getName(), Optional.empty());

            doAs(systemRegistrar(), () -> inviteService.acceptInvite(singleInvite.getHash(), collaborator.getId()));
        });
    }

    public ApplicationDataBuilder inviteCollaboratorNotYetRegistered(String email, String hash, String name, InviteStatus status, String organisationName) {

        return asLeadApplicant(data -> {
            Organisation organisation = retrieveOrganisationByName(organisationName);
            Optional<Long> organisationId = organisation != null ? Optional.of(organisation.getId()) : Optional.empty();
            doInviteCollaborator(data, organisationName, Optional.empty(), email, name, Optional.of(hash));
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
                applicationService.updateApplicationStatus(data.getApplication().getId(), ApplicationStatus.OPEN).
                        getSuccessObjectOrThrowException());
    }

    public ApplicationDataBuilder submitApplication() {

        return asLeadApplicant(data -> {
            applicationService.updateApplicationStatus(data.getApplication().getId(), ApplicationStatus.SUBMITTED).
                    getSuccessObjectOrThrowException();

            applicationService.saveApplicationSubmitDateTime(data.getApplication().getId(), LocalDateTime.now()).getSuccessObjectOrThrowException();
            applicationService.sendNotificationApplicationSubmitted(data.getApplication().getId()).getSuccessObjectOrThrowException();
        });
    }

    private ApplicationInviteResource doInviteCollaborator(ApplicationData data, String organisationName, Optional<Long> userId, String email, String name, Optional<String> hash) {

        ApplicationInviteResourceBuilder baseApplicationInviteBuilder =
                userId.map(id -> newApplicationInviteResource().withUsers(id)).orElse(newApplicationInviteResource());

        List<Organisation> organisations = organisationRepository.findByUsersId(data.getLeadApplicant().getId());
        Organisation leadOrganisation = organisations.get(0);

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
                withOrganisationName(organisationName).
                withInviteResources(applicationInvite).
                build()).getSuccessObjectOrThrowException();

        testService.flushAndClearSession();

        List<InviteOrganisationResource> invites = inviteService.getInvitesByApplication(data.getApplication().getId()).getSuccessObjectOrThrowException();

        InviteOrganisationResource newInvite = simpleFindFirst(invites, i -> simpleFindFirst(i.getInviteResources(), r -> r.getEmail().equals(email)).isPresent()).get();
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
