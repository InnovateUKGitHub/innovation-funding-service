package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.GuidanceRowBuilder.aGuidanceRow;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;

@Component
public class DirectAwardTemplate implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.DIRECT_AWARD;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        if (FundingRules.SUBSIDY_CONTROL == competition.getFundingRules()) {
            competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Innovate UK - Subsidy control"));
            competition.setOtherFundingRulesTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Innovate UK"));
        } else {
            competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Innovate UK"));
        }

        competition.setAcademicGrantPercentage(100);
        competition.setAlwaysOpen(true);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(36);
        return competition;
    }

    @Override
    public List<SectionBuilder> sections() {
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationTeam(),
                                applicationDetails(),
                                researchCategory(),
                                equalityDiversityAndInclusion(),
                                projectSummary(),
                                publicDescription(),
                                scope()
                        )),
                applicationQuestions()
                        .withQuestions(newArrayList(
                                directAwardDefaultQuestion()
                        )),
                finances(),
                termsAndConditions()
        );
    }

    public static QuestionBuilder directAwardDefaultQuestion() {
        return genericQuestion()
                .withShortName("Direct award placeholder question")
                .withName("Direct award placeholder question")
                .withDescription("Direct award placeholder question description");
    }

    public static QuestionBuilder scope() {
        return aQuestion()
                .withShortName("Scope")
                .withName("How does your project align with the scope of this award?")
                .withDescription("If your application doesn't align with the scope, we will not assess it.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.SCOPE)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(true)
                                .withGuidanceTitle("What should I include in the project scope?")
                                .withGuidanceAnswer("<p>It is important that you read the following guidance.</p><p>To show how your project aligns with the scope of this award, you need to:</p><ul class=\"list-bullet\">         <li>read the award brief in full</li><li>understand the background, challenge and scope of the award</li><li>address the research objectives in your application</li><li>match your project's objectives and activities to these</li></ul> <p>Once you have submitted your application, you should not change this section unless:</p><ul class=\"list-bullet\">         <li>we ask you to provide more information</li><li>we ask you to make it clearer</li></ul>")
                                .withWordCount(400),
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(true)
                                .withDescription("Is the application in scope?"),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_RESEARCH_CATEGORY)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(true)
                                .withDescription("Please select the research category for this project"),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(true)
                                .withGuidanceTitle("Guidance for assessing scope")
                                .withGuidanceAnswer("You should still assess this application even if you think that it is not in scope. Your answer should be based upon the following:")
                                .withWordCount(100)
                                .withGuidanceRows(newArrayList(
                                        aGuidanceRow()
                                                .withSubject("Yes")
                                                .withJustification("The application contains the following: Is the consortia business led? Are there two or more partners to the collaboration? Does it meet the scope of the award as defined in the award brief?"),
                                        aGuidanceRow()
                                                .withSubject("No")
                                                .withJustification("One or more of the above requirements have not been satisfied.")
                                ))
                ));
    }
}
