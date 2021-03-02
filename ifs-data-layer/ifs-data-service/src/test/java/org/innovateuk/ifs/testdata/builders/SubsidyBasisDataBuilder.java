package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.questionnaire.link.domain.ApplicationOrganisationQuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;
import org.innovateuk.ifs.testdata.builders.data.SubsidyBasisData;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

public class SubsidyBasisDataBuilder extends BaseDataBuilder<SubsidyBasisData, SubsidyBasisDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(SubsidyBasisDataBuilder.class);


    public SubsidyBasisDataBuilder withCompetition(CompetitionResource competition) {
        return with(data -> data.setCompetition(competition));
    }

    public SubsidyBasisDataBuilder withApplication(ApplicationResource application) {
        return with(data -> data.setApplication(application));
    }

    public SubsidyBasisDataBuilder withOrganisationName(String organisationName){
        return with(data -> data.setOrganisationName(organisationName));
    }

    public SubsidyBasisDataBuilder withQuestionnaireResponseUuid(String uuid){
        return with(data -> data.setQuestionnaireResponseUuid(uuid));
    }

    public SubsidyBasisDataBuilder withUser(String email) {
        return withUser(retrieveUserByEmail(email));
    }

    public SubsidyBasisDataBuilder withUser(UserResource user) {
        return with(data -> data.setUser(user));
    }


//
//    private QuestionnaireResponseResource createQuestionnaireResponseToQuestionnaire(Long questionnaireId, String uuid){
//        QuestionnaireResponseResource questionnaireResponse = new QuestionnaireResponseResource();
//        questionnaireResponse.setQuestionnaire(questionnaireId);
//        questionnaireResponse.setId(uuid);
//        return questionnaireResponseService.create(questionnaireResponse).getSuccess(); }

    public SubsidyBasisDataBuilder withSubsidyBasis() {
        return doAsUser(data -> {
            QuestionnaireResponse questionnaireResponse = questionnaireResponseRepository.findById(UUID.fromString(data.getQuestionnaireResponseUuid())).get();
            Application application = applicationRepository.findById(data.getApplication().getId()).get();
            Organisation organisation = getOrganisation(application.getId(), data.getOrganisationName());
            ApplicationOrganisationQuestionnaireResponse response = new ApplicationOrganisationQuestionnaireResponse();
            response.setApplication(application);
            response.setOrganisation(organisation);
            response.setQuestionnaireResponse(questionnaireResponse);
            applicationOrganisationQuestionnaireResponseRepository.save(response);
        });
    }

    private Organisation getOrganisation(long applicationId, String organsitionName){
        return processRoleRepository.findByApplicationId(applicationId).stream()
                .map(processRole -> processRole.getOrganisationId())
                .distinct()
                .map(organisationId -> organisationRepository.findById(organisationId).get())
                .filter(organisation -> organisation.getName().equals(organsitionName))
                .findFirst().get();
    }

//    private QuestionnaireDecisionImplementation fillInQuestionnaire(String questionnaireResponseId, QuestionnaireQuestionResource currentQuestion, List<String> selectedOptions){
//        return questionnaireOptionService.get(currentQuestion.getOptions()).getSuccess().stream()
//                .filter(option -> option.getText().equals(selectedOptions.get(0)))
//                .findFirst()
//                .map(option -> {
//                    // Create the response.
//                    QuestionnaireQuestionResponseResource questionResponse = new QuestionnaireQuestionResponseResource();
//                    questionResponse.setQuestionnaireResponse(questionnaireResponseId);
//                    questionResponse.setQuestion(currentQuestion.getId());
//                    questionResponse.setOption(currentQuestion.getId());
//                    questionnaireQuestionResponseService.create(questionResponse);
//                    // Go to the next question or return the result.
//                    if (selectedOptions.size() == 1){
//                        return questionnaireTextOutcomeService.get(option.getDecision()).getSuccess().getImplementation();
//                    }
//                    else {
//                        QuestionnaireQuestionResource nexQuestion = questionnaireQuestionService.get(option.getDecision()).getSuccess();
//                        return fillInQuestionnaire(questionnaireResponseId, nexQuestion, selectedOptions.subList(1, selectedOptions.size()));
//                    }
//                }).get();
//    }

    private SubsidyBasisDataBuilder doAsUser(Consumer<SubsidyBasisData> action) {
        return with(data -> doAs(data.getUser(), () -> action.accept(data)));
    }

    public static SubsidyBasisDataBuilder newSubsidyBasisDataBuilder(ServiceLocator serviceLocator) {
        return new SubsidyBasisDataBuilder(emptyList(), serviceLocator);
    }

    private SubsidyBasisDataBuilder(List<BiConsumer<Integer, SubsidyBasisData>> multiActions,
                                    ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected SubsidyBasisDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SubsidyBasisData>> actions) {
        return new SubsidyBasisDataBuilder(actions, serviceLocator);
    }

    @Override
    protected SubsidyBasisData createInitial() {
        return new SubsidyBasisData();
    }
}
