package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.innovateuk.ifs.testdata.builders.data.QuestionnaireResponseData;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


import static java.util.Collections.emptyList;

public class QuestionnaireResponseDataBuilder extends BaseDataBuilder<QuestionnaireResponseData, QuestionnaireResponseDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionnaireResponseDataBuilder.class);

    public QuestionnaireResponseDataBuilder withCompetition(CompetitionResource competition) {
        return with(data -> data.setCompetition(competition));
    }

    public QuestionnaireResponseDataBuilder  withApplication(ApplicationResource application){
        return with(data -> data.setApplication(application));
    }

    public QuestionnaireResponseDataBuilder withOrganisationName(String organisationName){
        return with(data -> data.setOrganisationName(organisationName));
    }

    public QuestionnaireResponseDataBuilder organisationName(String organisationName){
        return with(data -> data.setOrganisationName(organisationName));
    }

    public QuestionnaireResponseDataBuilder organisationName(ApplicationResource application){
        return with(data -> data.setApplication(application));
    }

    public QuestionnaireResponseDataBuilder withUser(String email) {
        return withUser(retrieveUserByEmail(email));
    }

    public QuestionnaireResponseDataBuilder withUser(UserResource user) {
        return with(data -> data.setUser(user));
    }

    public QuestionnaireResponseDataBuilder  withQuestionSetup(QuestionSetupType questionSetupType){
        return with(data -> data.setQuestionSetupType(questionSetupType));
    }

    public QuestionnaireResponseDataBuilder withSelectedOptions(List<String> options){
        return with(data -> data.setSelectedOptions(options));
    }

    private Long getQuestionnaireId(QuestionnaireResponseData data){
        List<QuestionResource> questions = questionService.findByCompetition(data.getCompetition().getId()).getSuccess();
        return questions.stream()
                .filter(question -> data.getQuestionSetupType().equals(question.getQuestionSetupType()))
                .findFirst()
                .get()
                .getQuestionnaireId();
    }

    private QuestionnaireResponseResource createQuestionnaireResponseToQuestionnaire(Long questionnaireId){
        QuestionnaireResponseResource questionnaireResponse = new QuestionnaireResponseResource();
        questionnaireResponse.setQuestionnaire(questionnaireId);
        return questionnaireResponseService.create(questionnaireResponse).getSuccess();
    }

    public QuestionnaireResponseDataBuilder withQuestionnaireResponse() {
        return doAsUser(data -> {
            Long questionnaireId = getQuestionnaireId(data);
            QuestionnaireResource questionnaire = questionnaireService.get(questionnaireId).getSuccess();
            List<QuestionnaireQuestionResource> questions = questionnaireQuestionService.get(questionnaire.getQuestions()).getSuccess();
            QuestionnaireResponseResource  questionnaireResponse = createQuestionnaireResponseToQuestionnaire(questionnaireId);
            data.setQuestionnaireResponseUuid(questionnaireResponse.getId()); // We might need this later.
            QuestionnaireQuestionResource firstQuestion = questions.get(0);
            QuestionnaireDecisionImplementation outcome = fillInQuestionnaire(questionnaireResponse.getId(), firstQuestion, data.getSelectedOptions());
            data.setOutcome(outcome);
        });
    }

    private QuestionnaireDecisionImplementation fillInQuestionnaire(String questionnaireResponseId, QuestionnaireQuestionResource currentQuestion, List<String> selectedOptions){
        return questionnaireOptionService.get(currentQuestion.getOptions()).getSuccess().stream()
                .filter(option -> option.getText().equals(selectedOptions.get(0)))
                .findFirst()
                .map(option -> {
                    // Create the response.
                    QuestionnaireQuestionResponseResource questionResponse = new QuestionnaireQuestionResponseResource();
                    questionResponse.setQuestionnaireResponse(questionnaireResponseId);
                    questionResponse.setQuestion(currentQuestion.getId());
                    questionResponse.setOption(option.getId());
                    questionnaireQuestionResponseService.create(questionResponse);
                    // Go to the next question or return the result.
                    if (selectedOptions.size() == 1){
                        return questionnaireTextOutcomeService.get(option.getDecision()).getSuccess().getImplementation();
                    }
                    else {
                        QuestionnaireQuestionResource nexQuestion = questionnaireQuestionService.get(option.getDecision()).getSuccess();
                        return fillInQuestionnaire(questionnaireResponseId, nexQuestion, selectedOptions.subList(1, selectedOptions.size()));
                    }
                }).get();
    }

    private QuestionnaireResponseDataBuilder doAsUser(Consumer<QuestionnaireResponseData> action) {
        return with(data -> doAs(data.getUser(), () -> action.accept(data)));
    }

    public static QuestionnaireResponseDataBuilder newQuestionnaireResponseDataBuilder(ServiceLocator serviceLocator) {
        return new QuestionnaireResponseDataBuilder(emptyList(), serviceLocator);
    }

    private QuestionnaireResponseDataBuilder(List<BiConsumer<Integer, QuestionnaireResponseData>> multiActions,
                                             ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected QuestionnaireResponseDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionnaireResponseData>> actions) {
        return new QuestionnaireResponseDataBuilder(actions, serviceLocator);
    }

    @Override
    protected QuestionnaireResponseData createInitial() {
        return new QuestionnaireResponseData();
    }
}
