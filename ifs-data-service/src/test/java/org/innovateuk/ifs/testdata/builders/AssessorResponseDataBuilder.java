package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Generates Assessor Responses for Assessments
 */
public class AssessorResponseDataBuilder extends BaseDataBuilder<Void, AssessorResponseDataBuilder> {

    public AssessorResponseDataBuilder withAssessorResponseData(String competitionName,
                                                                String applicationName,
                                                                String assessorEmail,
                                                                String shortName,
                                                                String description,
                                                                boolean isResearchCategory,
                                                                String value) {
        return with(data -> {
            UserResource assessor = retrieveUserByEmail(assessorEmail);
            ProcessRole processRole = retrieveAssessorByApplicationNameAndUser(applicationName, assessor);
            Competition competition = retrieveCompetitionByName(competitionName);

            Assessment assessment = assessmentRepository.findOneByParticipantId(processRole.getId());

            Question question = questionRepository.findByCompetitionId(competition.getId())
                    .stream()
                    .filter(x -> shortName.equals(x.getShortName()))
                    .findFirst()
                    .get();

            FormInput formInput = formInputRepository.findByQuestionIdAndScopeAndActiveTrueOrderByPriorityAsc(question.getId(), FormInputScope.ASSESSMENT)
                    .stream()
                    .filter(x -> x.getDescription().contains(description))
                    .findFirst()
                    .get();

            String realValue = value;
            if (isResearchCategory) {
                realValue = researchCategoryRepository.findByName(value).getId().toString();
            }

            AssessorFormInputResponseResource assessorFormInputResponse = new AssessorFormInputResponseResource();
            assessorFormInputResponse.setAssessment(assessment.getId());
            assessorFormInputResponse.setFormInput(formInput.getId());
            assessorFormInputResponse.setValue(realValue);

            doAs(assessor, () -> assessorFormInputResponseService.saveUpdatedFormInputResponse(
                    assessorFormInputResponseService
                            .updateFormInputResponse(assessorFormInputResponse)
                            .getSuccessObject()));
        });
    }

    private AssessorResponseDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                        ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    public static AssessorResponseDataBuilder newAssessorResponseData(ServiceLocator serviceLocator) {
        return new AssessorResponseDataBuilder(emptyList(), serviceLocator);
    }

    @Override
    protected AssessorResponseDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new AssessorResponseDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }
}
