package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapperImpl;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.testdata.builders.data.ApplicationData;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.testdata.builders.QuestionResponseDataBuilder.newApplicationQuestionResponseData;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;


/**
 * Generates an Application for a Competition.  Additionally generates finances for each Organisation on the Application
 */
public class ApplicationDataBuilder extends BaseDataBuilder<ApplicationData, ApplicationDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDataBuilder.class);

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
                    applicationInnovationAreaService.setInnovationArea(data.getApplication().getId(), innovationArea.getId())
                            .getSuccessObjectOrThrowException();
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
                applicationService.updateApplicationState(data.getApplication().getId(), ApplicationState.OPEN).
                        getSuccessObjectOrThrowException());
    }

    public ApplicationDataBuilder submitApplication() {

        return asLeadApplicant(data -> {
            applicationService.updateApplicationState(data.getApplication().getId(), ApplicationState.SUBMITTED).
                    getSuccessObjectOrThrowException();

            applicationService.saveApplicationSubmitDateTime(data.getApplication().getId(), ZonedDateTime.now()).getSuccessObjectOrThrowException();
            applicationService.sendNotificationApplicationSubmitted(data.getApplication().getId()).getSuccessObjectOrThrowException();
        });
    }

    public ApplicationDataBuilder markApplicationIneligible(String ineligibleReason) {
        return asCompAdmin(data ->  {
            IneligibleOutcomeResource reason = new IneligibleOutcomeResource(ineligibleReason);
            applicationService.markAsIneligible(data.getApplication().getId(), ineligibleOutcomeMapper.mapToDomain(reason));
        });
    }

    public ApplicationDataBuilder informApplicationIneligible() {
        return asCompAdmin(data -> {
            ApplicationIneligibleSendResource resource = new ApplicationIneligibleSendResource("subject", "content");
            applicationService.informIneligible(data.getApplication().getId(), resource);
        });
    }

    private ApplicationInviteResource doInviteCollaborator(ApplicationData data, String organisationName, Optional<Long> userId, String email, String name, Optional<String> hash) {
        Long applicationId = 1L;

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
                build(), applicationId).getSuccessObjectOrThrowException();

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
            UnaryOperator<QuestionResponseDataBuilder>... responseBuilders) {

        return withQuestionResponses(asList(responseBuilders));
    }

    public ApplicationDataBuilder withQuestionResponses(
            List<UnaryOperator<QuestionResponseDataBuilder>> responseBuilders) {

        return with(data -> {
            QuestionResponseDataBuilder baseBuilder =
                    newApplicationQuestionResponseData(serviceLocator).withApplication(data.getApplication());

            responseBuilders.forEach(builder -> builder.apply(baseBuilder).build());
        });
    }

    /**
     * Generate a default set of responses to the basic application questions (project summary, scope, etc) for
     * applications that need responses but don't have any specific values in the application-questions.csv
     */
    public ApplicationDataBuilder withDefaultQuestionResponses() {

        return with(data -> {

            QuestionResponseDataBuilder baseBuilder =
                    newApplicationQuestionResponseData(serviceLocator).withApplication(data.getApplication());

            List<QuestionResource> competitionQuestions = retrieveQuestionsByCompetitionId(data.getCompetition().getId());

            List<QuestionResource> questionsToAnswer = simpleFilter(competitionQuestions,
                    q -> !q.getMultipleStatuses() && q.getMarkAsCompletedEnabled() && !"Application details".equals(q.getName()));

            List<QuestionResponseDataBuilder> responseBuilders = simpleMap(questionsToAnswer, question -> {

                QuestionResponseDataBuilder responseBuilder = baseBuilder.
                        forQuestion(question.getName()).
                        withAssignee(data.getLeadApplicant().getEmail()).
                        withAnswer("This is the applicant response for " + question.getName().toLowerCase() + ".", data.getLeadApplicant().getEmail());

                List<FormInputResource> formInputs = retrieveFormInputsByQuestionId(question);

                if (formInputs.stream().anyMatch(fi -> fi.getType().equals(FormInputType.FILEUPLOAD))) {

                    String fileUploadName = (data.getApplication().getName() + "-" + question.getShortName().toLowerCase() + ".pdf")
                            .toLowerCase().replace(' ', '-') ;

                    responseBuilder = responseBuilder.withFileUploads(singletonList(fileUploadName), data.getLeadApplicant().getEmail());
                }

                return responseBuilder.markAsComplete();
            });

            responseBuilders.forEach(QuestionResponseDataBuilder::build);
        });
    }

    @Override
    protected void postProcess(int index, ApplicationData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Application '{}'", instance.getApplication().getName());
    }
}
