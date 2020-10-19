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
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype.FundingTypeTemplate;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.template.CompetitionTemplate;
import org.innovateuk.ifs.competitionsetup.domain.AssessorCountOption;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.competitionsetup.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;

/**
 * Service that can create Competition template copies
 */
@Service
public class CompetitionSetupTemplateServiceImpl implements CompetitionSetupTemplateService {

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

    @Autowired
    private QuestionPriorityOrderService questionPriorityOrderService;

    private Map<CompetitionTypeEnum, CompetitionTemplate> templates;
    private Map<FundingType, FundingTypeTemplate> fundingTypeTemplates;

    @Autowired
    public void setCompetitionTemplates(List<CompetitionTemplate> templateBeans) {
        templates = templateBeans.stream()
                .collect(toMap(CompetitionTemplate::type, Function.identity()));
    }

    @Autowired
    public void setFundingTypeTemplates(List<FundingTypeTemplate> templateBeans) {
        fundingTypeTemplates = templateBeans.stream()
                .collect(toMap(FundingTypeTemplate::type, Function.identity()));
    }

    @Override
    public ServiceResult<Competition> initializeCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId) {
        Optional<CompetitionType> competitionType = competitionTypeRepository.findById(competitionTypeId);

        if (!competitionType.isPresent()) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        Optional<Competition> competitionOptional = competitionRepository.findById(competitionId);
        if (!competitionOptional.isPresent() || competitionIsNotInSetupState(competitionOptional.get())) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        Competition competition = competitionOptional.get();

        competition.setCompetitionType(competitionType.get());
        setDefaultAssessorPayAndCountAndAverageAssessorScore(competition);
        setDefaultOrganisationConfig(competition);
        setDefaultApplicationConfig(competition);

        setDefaultProjectDocuments(competition);

        CompetitionTemplate template = templates.get(competition.getCompetitionTypeEnum());
        FundingTypeTemplate fundingTypeTemplate = fundingTypeTemplates.get(competition.getFundingType());

        List<SectionBuilder> sectionBuilders = template.sections();
        sectionBuilders = fundingTypeTemplate.sections(sectionBuilders);
        competition = fundingTypeTemplate.initialiseFinanceTypes(competition);
        competition = fundingTypeTemplate.initialiseProjectSetupColumns(competition);
        competition.setSections(sectionBuilders.stream().map(SectionBuilder::build).collect(Collectors.toList()));
        template.copyTemplatePropertiesToCompetition(competition);
        competition = fundingTypeTemplate.overrideTermsAndConditions(competition);

        questionPriorityOrderService.persistAndPrioritiseSections(competition, competition.getSections(), null);
        return serviceSuccess(competitionRepository.save(competition));
    }

    private void setDefaultOrganisationConfig(Competition competition) {
        if (competition.getCompetitionOrganisationConfig() == null) {
            CompetitionOrganisationConfig competitionOrganisationConfig = new CompetitionOrganisationConfig();
            competitionOrganisationConfig.setCompetition(competition);
            competition.setCompetitionOrganisationConfig(competitionOrganisationConfig);
        }
    }

    private void setDefaultApplicationConfig(Competition competition) {
        if (competition.getCompetitionApplicationConfig() == null) {
            CompetitionApplicationConfig competitionApplicationConfig = new CompetitionApplicationConfig();
            competitionApplicationConfig.setCompetition(competition);
            competition.setCompetitionApplicationConfig(competitionApplicationConfig);
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
                "<p>Guidance on completing a collaboration agreement can be found on the <a target=\"_blank\" href=\"http://www.ipo.gov.uk/lambert\">Lambert Agreement website</a>.</p>\n" +
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

    private Competition setDefaultAssessorPayAndCountAndAverageAssessorScore(Competition competition) {

        if (competition.getCompetitionAssessmentConfig() == null) {
            CompetitionAssessmentConfig competitionAssessmentConfig = new CompetitionAssessmentConfig();
            competitionAssessmentConfig.setCompetition(competition);

            Optional<AssessorCountOption> defaultAssessorOption = assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competition.getCompetitionType().getId());
            defaultAssessorOption.ifPresent(assessorCountOption -> competitionAssessmentConfig.setAssessorCount(assessorCountOption.getOptionValue()));
            if (!competition.isKtp()) {
                competitionAssessmentConfig.setAssessorPay(CompetitionSetupServiceImpl.DEFAULT_ASSESSOR_PAY);
            }
            competition.setCompetitionAssessmentConfig(competitionAssessmentConfig);
        }

        return competition;
    }

    private boolean competitionIsNotInSetupState(Competition competition) {
        return !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP);
    }

}
