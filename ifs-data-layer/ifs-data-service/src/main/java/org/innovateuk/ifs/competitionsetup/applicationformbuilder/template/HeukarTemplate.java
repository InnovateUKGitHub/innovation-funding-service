package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionAssessmentConfig;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.NO_FINANCES;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;

@Component
public class HeukarTemplate implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private OrganisationTypeRepository organisationTypeRepository;

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.HEUKAR;
    }

    @Override
    public Competition initialiseOrganisationConfig(Competition competition) {
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

        competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Horizon Europe UK Application Registration"));
        competition.setLeadApplicantTypes(organisationTypesList);
        competition.setAcademicGrantPercentage(100);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(84);
        competition.setCollaborationLevel(CollaborationLevel.SINGLE_OR_COLLABORATIVE);
        competition.setUseResubmissionQuestion(false);
        competition.setApplicationFinanceType(NO_FINANCES);
        competition.setResubmission(false);
        competition.setHasAssessmentStage(false);
        competition.setHasAssessmentPanel(false);
        competition.setHasInterviewStage(false);
        competition.setCompetitionAssessmentConfig(competitionAssessmentConfig);
        competition.setFundingRules(FundingRules.NOT_AID);

        return competition;
    }

    @Override
    public List<SectionBuilder> sections() {
        QuestionBuilder scopeQuestion = scope();
        scopeQuestion.getFormInputs().stream()
                .filter(fi -> fi.getScope().equals(FormInputScope.ASSESSMENT))
                .forEach(fi -> fi.withActive(false));

        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationTeam(),
                                applicationDetails(),
                                equalityDiversityAndInclusion(),
                                projectSummary(),
                                publicDescription(),
                                scopeQuestion
                        )),
                applicationQuestions()
                        .withQuestions(newArrayList(
                                question()
                        )),
                termsAndConditions()
        );
    }

    public static QuestionBuilder question() {
        QuestionBuilder question = genericQuestion();
        question.getFormInputs().stream()
                .filter(fi -> fi.getScope().equals(FormInputScope.ASSESSMENT))
                .forEach(fi -> fi.withActive(false));

        question.withShortName("A HEUKAR question");
        question.withName("Title");
        question.withDescription("Subtitle");
        question.getFormInputs().stream()
                .filter(fi -> fi.getScope().equals(FormInputScope.APPLICATION))
                .forEach(fi -> fi.withGuidanceTitle("Guidance title"));

        question.getFormInputs().stream()
                .filter(fi -> fi.getScope().equals(FormInputScope.APPLICATION))
                .forEach(fi -> fi.withGuidanceAnswer("Guidance answer"));

        return question;
    }
}