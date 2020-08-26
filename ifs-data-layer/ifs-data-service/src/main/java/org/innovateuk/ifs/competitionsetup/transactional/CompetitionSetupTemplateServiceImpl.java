package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.*;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.repository.*;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.template.CompetitionTemplatePersistorImpl;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.ProgrammeTemplate;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.SectionBuilder;
import org.innovateuk.ifs.competitionsetup.domain.AssessorCountOption;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.competitionsetup.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.competitionsetup.util.CompetitionInitialiser;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.GuidanceRow;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.*;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;

/**
 * Service that can create Competition template copies
 */
@Service
public class CompetitionSetupTemplateServiceImpl implements CompetitionSetupTemplateService {

    private static final String TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS = "Investor Partnerships terms and conditions";
    private static final String TERMS_AND_CONDITIONS_OTHER = "Award terms and conditions";

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

    @Autowired
    private CompetitionInitialiser competitionInitialiser;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private GuidanceRowRepository guidanceRowRepository;

    @Autowired
    private MultipleChoiceOptionRepository multipleChoiceOptionRepository;

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
        setDefaultApplicationConfig(competition);


        if (!competitionType.get().getId().equals(1L)) {
            competitionTemplatePersistor.cleanByEntityId(competitionId);
        }
        copyTemplatePropertiesToCompetition(template, competition);

        overrideTermsAndConditionsForNonGrantCompetitions(competition);

        overrideTermsAndConditionsTerminologyForInvestorPartnerships(competition);

        setDefaultProjectDocuments(competition);

        competitionInitialiser.initialiseFinanceTypes(competition);
        competitionInitialiser.initialiseProjectSetupColumns(competition);

        if (!competitionType.get().getId().equals(1L)) {
            return serviceSuccess(competitionTemplatePersistor.persistByEntity(competition));
        } else {
            competition.setSections(ProgrammeTemplate.sections().stream().map(SectionBuilder::build).collect(Collectors.toList()));
            setCompetitionOnSections(competition, competition.getSections());
            return serviceSuccess(competition);
        }
    }

    void setCompetitionOnSections(Competition competition, List<Section> sections) {
        int si = 0;
        for (Section section : sections) {
            setCompetitionOnSections(competition, section.getChildSections());
            section.setCompetition(competition);
            section.setPriority(si);
            si++;
            Section savedSection = sectionRepository.save(section);
            int qi = 0;
            for (Question question : section.getQuestions()) {
                question.setSection(savedSection);
                question.setCompetition(competition);
                question.setPriority(qi);
                qi++;
                if (section.getName().equals("Application questions")) {
                    question.setQuestionNumber(String.valueOf(qi));
                }
                Question savedQuestion = questionRepository.save(question);
                int fii = 0;
                for (FormInput fi : question.getFormInputs()) {
                    fi.setQuestion(savedQuestion);
                    fi.setCompetition(competition);
                    fi.setPriority(fii);
                    fii++;
                    fi.getMultipleChoiceOptions().forEach(mc -> {
                        mc.setFormInput(fi);
                    });
                    int gri = 0;
                    for (GuidanceRow gr : fi.getGuidanceRows()) {
                        gr.setFormInput(fi);
                        gr.setPriority(gri);
                        gri++;
                    }
                    formInputRepository.save(fi);
                    //TODO validators
                }
            }
        }
    }

    private void overrideTermsAndConditionsTerminologyForInvestorPartnerships(Competition competition) {

        Optional<Section> termsSection = competition.getSections().stream().filter(s -> s.isType(SectionType.TERMS_AND_CONDITIONS)).findAny();
        if (termsSection.isPresent()) {
            String termsToUse;
            if (FundingType.INVESTOR_PARTNERSHIPS == competition.getFundingType()) {
                termsToUse = TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS;
            } else {
                termsToUse = TERMS_AND_CONDITIONS_OTHER;
            }

            termsSection.get().getQuestions().forEach(q -> {
                q.setDescription(termsToUse);
                q.setName(termsToUse);
                q.setShortName(termsToUse);
            });
        }
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
