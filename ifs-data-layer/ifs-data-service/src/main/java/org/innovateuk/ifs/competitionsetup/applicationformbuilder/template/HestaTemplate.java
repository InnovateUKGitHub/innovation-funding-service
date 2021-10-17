package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionAssessmentConfig;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.NO_FINANCES;

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
        competition.setGrantClaimMaximums(commonBuilders.getDefaultGrantClaimMaximums());
        competition.setAcademicGrantPercentage(100);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(60);
        competition.setCollaborationLevel(CollaborationLevel.SINGLE_OR_COLLABORATIVE);
        competition.setUseResubmissionQuestion(FALSE);
        competition.setApplicationFinanceType(NO_FINANCES);
        competition.setResubmission(FALSE);
        competition.setHasAssessmentStage(FALSE);
        competition.setHasAssessmentPanel(FALSE);
        competition.setHasInterviewStage(FALSE);
        competition.setCompetitionAssessmentConfig(competitionAssessmentConfig);
        competition.setStateAid(FALSE);

        return competition;
    }
}
