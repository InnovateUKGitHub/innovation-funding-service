package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.innovateuk.ifs.testdata.builders.data.SubsidyBasisData;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;
import static org.innovateuk.ifs.questionnaire.resource.QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_TRUE;

public class SubsidyBasisDataBuilder extends BaseDataBuilder<SubsidyBasisData, SubsidyBasisDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(SubsidyBasisDataBuilder.class);


    public SubsidyBasisDataBuilder withApplication(ApplicationResource application) {
        return with(data -> data.setApplication(application));
    }

    public SubsidyBasisDataBuilder withCompetition(CompetitionResource competition) {
        return with(data -> data.setCompetition(competition));
    }

    public SubsidyBasisDataBuilder withOrganisation(String organisationName) {
        return with(data -> data.setOrganisation(retrieveOrganisationResourceByName(organisationName)));
    }

    public SubsidyBasisDataBuilder withOrganisation(OrganisationResource organisation) {
        return with(data -> data.setOrganisation(organisation));
    }

    public SubsidyBasisDataBuilder withUser(String email) {
        return withUser(retrieveUserByEmail(email));
    }

    public SubsidyBasisDataBuilder withUser(UserResource user) {
        return with(data -> data.setUser(user));
    }

    public SubsidyBasisDataBuilder withSelectedOptions(List<String> options){
        return with(data -> data.setSelectedOptions(options));
    }

    private Long getSubsidyBasisQuestionnaireId(SubsidyBasisData data){
        List<QuestionResource> questions = questionService.findByCompetition(data.getCompetition().getId()).getSuccess();
        QuestionResource subsidyBasisQuestion = questions.stream().filter(question -> SUBSIDY_BASIS.equals(question.getQuestionSetupType())).findFirst().get();
        return subsidyBasisQuestion.getQuestionnaireId();
    }

    private QuestionnaireResponseResource createQuestionnaireResponseToQuestionnaire(Long questionnaireId){
        QuestionnaireResponseResource questionnaireResponse = new QuestionnaireResponseResource();
        questionnaireResponse.setQuestionnaire(questionnaireId);
        questionnaireResponse.setId("dff4555a-ba9b-4910-9d5e-3d84c099829a"); // TODO 
        return questionnaireResponseService.create(questionnaireResponse).getSuccess(); }

    public SubsidyBasisDataBuilder withSubsidyBasis() {
        return doAsUser(data -> {
            Long subsidyBasisQuestionnaireId = getSubsidyBasisQuestionnaireId(data);
            QuestionnaireResource subsidyBasisQuestionnaire = questionnaireService.get(subsidyBasisQuestionnaireId).getSuccess();
            List<QuestionnaireQuestionResource> subsidyBasisQuestions = questionnaireQuestionService.get(subsidyBasisQuestionnaire.getQuestions()).getSuccess();
            QuestionnaireResponseResource  subsidyBasisQuestionnaireResponse = createQuestionnaireResponseToQuestionnaire(subsidyBasisQuestionnaireId);
            QuestionnaireQuestionResource firstQuestion = subsidyBasisQuestions.get(0);
            QuestionnaireDecisionImplementation subsidyBasisOutcome = fillInQuestionnaire(subsidyBasisQuestionnaireResponse.getId(), firstQuestion, data.getSelectedOptions());

            ApplicationFinanceResource finance = financeService.findApplicationFinanceByApplicationIdAndOrganisation(data.getApplication().getId(), data.getOrganisation().getId()).getSuccess();
            finance.setNorthernIrelandDeclaration(SET_NORTHERN_IRELAND_DECLARATION_TRUE.equals(subsidyBasisOutcome));
            financeService.updateApplicationFinance(finance.getId(), finance);
        });
    }

    private QuestionnaireDecisionImplementation fillInQuestionnaire(String questionnaireResponseId, QuestionnaireQuestionResource currentQuestion, List<String> selectedOptions){
        return questionnaireOptionService.get(currentQuestion.getOptions()).getSuccess().stream()
                .filter(option -> option.getText().equals(selectedOptions.get(0)))
                .findFirst()
                .map(option -> {
                    // Create the response.
                    QuestionnaireQuestionResponseResource subsidyBasisQuestionResponse = new QuestionnaireQuestionResponseResource();
                    subsidyBasisQuestionResponse.setQuestionnaireResponse(questionnaireResponseId);
                    subsidyBasisQuestionResponse.setQuestion(currentQuestion.getId());
                    subsidyBasisQuestionResponse.setOption(currentQuestion.getId());
                    questionnaireQuestionResponseService.create(subsidyBasisQuestionResponse);
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

    @Override
    protected void postProcess(int index, SubsidyBasisData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Subsidy Basis for Application '{}', Organisation '{}'", instance.getApplication().getName(), instance.getOrganisation().getName());
    }
}
