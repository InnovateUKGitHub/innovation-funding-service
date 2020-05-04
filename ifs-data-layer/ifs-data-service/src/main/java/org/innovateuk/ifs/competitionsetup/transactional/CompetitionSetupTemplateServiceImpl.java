package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.*;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.repository.CompetitionAssessmentConfigRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.template.CompetitionTemplatePersistorImpl;
import org.innovateuk.ifs.competitionsetup.domain.AssessorCountOption;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.competitionsetup.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.competitionsetup.util.CompetitionInitialiser.initialiseFinanceTypes;
import static org.innovateuk.ifs.competitionsetup.util.CompetitionInitialiser.initialiseProjectSetupColumns;

/**
 * Service that can create Competition template copies
 */
@Service
public class CompetitionSetupTemplateServiceImpl implements CompetitionSetupTemplateService {

    @Autowired
    private CompetitionTemplatePersistorImpl competitionTemplatePersistor;

    @Autowired
    private AssessorCountOptionRepository assessorCountOptionRepository;

    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private CompetitionDocumentConfigRepository competitionDocumentConfigRepository;

    @Autowired
    private CompetitionAssessmentConfigRepository competitionAssessmentConfigRepository;

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Override
    public ServiceResult<Competition> initializeCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId) {
        Optional<CompetitionType> competitionType = competitionTypeRepository.findById(competitionTypeId);

        if (!competitionType.isPresent()) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        Competition template = competitionType.get().getTemplate();
        if (template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        Optional<Competition> competitionOptional = competitionRepository.findById(competitionId);
        if (!competitionOptional.isPresent() || competitionIsNotInSetupState(competitionOptional.get())) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        Competition competition = competitionOptional.get();

        competition.setCompetitionType(competitionType.get());
        setDefaultAssessorPayAndCountAndAverageAssessorScore(competition);
        setDefaultOrganisationConfig(competition);

        competitionTemplatePersistor.cleanByEntityId(competitionId);

        copyTemplatePropertiesToCompetition(template, competition);

        overrideTermsAndConditionsForNonGrantCompetitions(competition);

        setDefaultProjectDocuments(competition);

        initialiseFinanceTypes(competition);
        initialiseProjectSetupColumns(competition);

        return serviceSuccess(competitionTemplatePersistor.persistByEntity(competition));
    }

    private void setDefaultOrganisationConfig(Competition competition) {
        if (competition.getCompetitionOrganisationConfig() == null) {
            CompetitionOrganisationConfig competitionOrganisationConfig = new CompetitionOrganisationConfig();
            competitionOrganisationConfig.setCompetition(competition);
            competition.setCompetitionOrganisationConfig(competitionOrganisationConfig);
        }
    }

    private void setDefaultProjectDocuments(Competition competition) {
        if (competitionDocumentConfigRepository.findByCompetitionId(competition.getId()).isEmpty()) {
            FileType pdfFileType = fileTypeRepository.findByName("PDF");
            createCollaborationAgreement(competition, singletonList(pdfFileType));
            createExploitationPlan(competition, singletonList(pdfFileType));
        }
    }

    private void createCollaborationAgreement(Competition competition, List<FileType> fileTypes) {
        competitionDocumentConfigRepository.save(new CompetitionDocument(competition, COLLABORATION_AGREEMENT_TITLE, "<p>The collaboration agreement covers how the consortium will work together on the project and exploit its results. It must be signed by all partners.</p>\n" +
                "\n" +
                "<p>Please allow enough time to complete this document before your project start date.</p>\n" +
                "\n" +
                "<p>Guidance on completing a collaboration agreement can be found on the <a target=\"_blank\" href=\"http://www.ipo.gov.uk/lambert\">Lambert Agreement website(opens in a new window)</a>.</p>\n" +
                "\n" +
                "<p>Your collaboration agreement must be:</p>\n" +
                "<ul class=\"list-bullet\"><li>in portable document format (PDF)</li>\n" +
                "<li>legible at 100% magnification</li>\n" +
                "<li>less than 10MB in file size</li></ul>",
                false, competition.isGrant(), fileTypes));
    }

    private void createExploitationPlan(Competition competition, List<FileType> fileTypes) {
        competitionDocumentConfigRepository.save(new CompetitionDocument(competition, "Exploitation plan", "<p>This is a confirmation of your overall plan, setting out the business case for your project. This plan will change during the lifetime of the project.</p>\n" +
                "\n" +
                "<p>It should also describe partner activities that will exploit the results of the project so that:</p>\n" +
                "<ul class=\"list-bullet\"><li>changes in the commercial environment can be monitored and accounted for</li>\n" +
                "<li>adequate resources are committed to exploitation</li>\n" +
                "<li>exploitation can be monitored by the stakeholders</li></ul>\n" +
                "\n" +
                "<p>You can download an <a href=\"/files/exploitation_plan.doc\" class=\"govuk-link\">exploitation plan template</a>.</p>\n" +
                "\n" +
                "<p>The uploaded exploitation plan must be:</p>\n" +
                "<ul class=\"list-bullet\"><li>in portable document format (PDF)</li>\n" +
                "<li>legible at 100% magnification</li>\n" +
                "<li>less than 10MB in file size</li></ul>",
                false, competition.isGrant(), fileTypes));
    }

    private void overrideTermsAndConditionsForNonGrantCompetitions(Competition populatedCompetition) {
        if (populatedCompetition.getFundingType() != FundingType.GRANT) {
            GrantTermsAndConditions grantTermsAndConditions =
                    grantTermsAndConditionsRepository.getLatestForFundingType(populatedCompetition.getFundingType());
            populatedCompetition.setTermsAndConditions(grantTermsAndConditions);
        }
    }

    private Competition copyTemplatePropertiesToCompetition(Competition template, Competition competition) {
        competition.setSections(new ArrayList<>(template.getSections()));
        competition.setGrantClaimMaximums(new ArrayList<>(template.getGrantClaimMaximums()));
        competition.setTermsAndConditions(template.getTermsAndConditions());
        competition.setAcademicGrantPercentage(template.getAcademicGrantPercentage());
        competition.setMinProjectDuration(template.getMinProjectDuration());
        competition.setMaxProjectDuration(template.getMaxProjectDuration());
        competition.setApplicationFinanceType(template.getApplicationFinanceType());
        return competition;
    }

    private Competition setDefaultAssessorPayAndCountAndAverageAssessorScore(Competition competition) {

        if (competition.getCompetitionAssessmentConfig() == null) {
            CompetitionAssessmentConfig competitionAssessmentConfig = new CompetitionAssessmentConfig();
            competitionAssessmentConfig.setCompetition(competition);

            Optional<AssessorCountOption> defaultAssessorOption = assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competition.getCompetitionType().getId());
            defaultAssessorOption.ifPresent(assessorCountOption -> competitionAssessmentConfig.setAssessorCount(assessorCountOption.getOptionValue()));
            competitionAssessmentConfig.setAssessorPay(CompetitionSetupServiceImpl.DEFAULT_ASSESSOR_PAY);
            competition.setCompetitionAssessmentConfig(competitionAssessmentConfig);
        }

        return competition;
    }

    private boolean competitionIsNotInSetupState(Competition competition) {
        return !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP);
    }

}
