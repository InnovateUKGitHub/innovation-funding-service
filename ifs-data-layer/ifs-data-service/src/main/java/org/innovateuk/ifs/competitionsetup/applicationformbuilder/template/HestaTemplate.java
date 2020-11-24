package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionAssessmentConfig;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationTypeRepository;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.NO_FINANCES;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.GuidanceRowBuilder.aGuidanceRow;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aDefaultAssessedQuestion;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;

@Component
public class HestaTemplate implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private OrganisationTypeRepository organisationTypeRepository;

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.HESTA;
    }

    @Override
    public Competition initializeOrganisationConfig(Competition competition) {
        if (competition.getCompetitionOrganisationConfig() == null) {
            CompetitionOrganisationConfig competitionOrganisationConfig = new CompetitionOrganisationConfig();
            competitionOrganisationConfig.setCompetition(competition);
            competition.setCompetitionOrganisationConfig(competitionOrganisationConfig);
            competitionOrganisationConfig.setInternationalLeadOrganisationAllowed(TRUE);
            competitionOrganisationConfig.setInternationalOrganisationsAllowed(TRUE);
        }

        return competition;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        Iterable<OrganisationType> organisationTypes = organisationTypeRepository.findAll();
        List<OrganisationType> organisationTypesList = Lists.newArrayList(organisationTypes);

        CompetitionAssessmentConfig competitionAssessmentConfig = new CompetitionAssessmentConfig();
        competitionAssessmentConfig.setCompetition(competition);
        competitionAssessmentConfig.setAssessorCount(0);
        competitionAssessmentConfig.setHasAssessmentPanel(false);
        competitionAssessmentConfig.setHasInterviewStage(false);
        competitionAssessmentConfig.setIncludeAverageAssessorScoreInNotifications(false);
        competitionAssessmentConfig.setAssessorFinanceView(AssessorFinanceView.OVERVIEW);

        competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Hesta"));
        competition.setLeadApplicantTypes(organisationTypesList);
        competition.setGrantClaimMaximums(commonBuilders.getDefaultGrantClaimMaximums());
        competition.setAcademicGrantPercentage(100);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(60);
        competition.setCollaborationLevel(CollaborationLevel.SINGLE_OR_COLLABORATIVE);
        competition.setUseResubmissionQuestion(false);
        competition.setApplicationFinanceType(NO_FINANCES);
        competition.setResubmission(false);
        competition.setHasAssessmentStage(false);
        competition.setHasAssessmentPanel(false);
        competition.setHasInterviewStage(false);
        competition.setCompetitionAssessmentConfig(competitionAssessmentConfig);

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
                                genericQuestion()
                        )),
                termsAndConditions()
        );
    }

    public static QuestionBuilder genericQuestion() {
        return aDefaultAssessedQuestion()
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(Function.identity(),
                                assessorInputBuilder ->
                                        assessorInputBuilder
                                                .withActive(false)
                                                .withGuidanceRows(newArrayList(
                                                aGuidanceRow()
                                                        .withSubject("9,10"),
                                                aGuidanceRow()
                                                        .withSubject("7,8"),
                                                aGuidanceRow()
                                                        .withSubject("5,6"),
                                                aGuidanceRow()
                                                        .withSubject("3,4"),
                                                aGuidanceRow()
                                                        .withSubject("1,2")
                                        ))
                        )
                );
    }

    public static QuestionBuilder scope() {
        return aQuestion()
                .withShortName("Scope")
                .withName("How does your project align with the scope of this competition?")
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
                                .withGuidanceAnswer("<p>It is important that you read the following guidance.</p><p>To show how your project aligns with the scope of this competition, you need to:</p><ul class=\"list-bullet\">         <li>read the competition brief in full</li><li>understand the background, challenge and scope of the competition</li><li>address the research objectives in your application</li><li>match your project's objectives and activities to these</li></ul> <p>Once you have submitted your application, you should not change this section unless:</p><ul class=\"list-bullet\">         <li>we ask you to provide more information</li><li>we ask you to make it clearer</li></ul>")
                                .withWordCount(400),
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false)
                                .withDescription("Is the application in scope?"),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_RESEARCH_CATEGORY)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false)
                                .withDescription("Please select the research category for this project"),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false)
                                .withGuidanceTitle("Guidance for assessing scope")
                                .withGuidanceAnswer("You should still assess this application even if you think that it is not in scope. Your answer should be based upon the following:")
                                .withWordCount(100)
                                .withGuidanceRows(newArrayList(
                                        aGuidanceRow()
                                                .withSubject("Yes")
                                                .withJustification("The application contains the following: Is the consortia business led? Are there two or more partners to the collaboration? Does it meet the scope of the competition as defined in the competition brief?"),
                                        aGuidanceRow()
                                                .withSubject("No")
                                                .withJustification("One or more of the above requirements have not been satisfied.")
                                ))
                ));
    }
}