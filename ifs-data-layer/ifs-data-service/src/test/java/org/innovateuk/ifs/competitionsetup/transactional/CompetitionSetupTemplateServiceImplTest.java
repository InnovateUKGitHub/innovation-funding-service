package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype.GrantTemplate;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype.KtpTemplate;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype.LoanTemplate;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.template.ProgrammeTemplate;
import org.innovateuk.ifs.competitionsetup.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.resource.CompetitionTypeEnum.PROGRAMME;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompetitionSetupTemplateServiceImplTest extends BaseServiceUnitTest<CompetitionSetupTemplateServiceImpl> {

    public CompetitionSetupTemplateServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupTemplateServiceImpl();
    }

    @Mock
    private CompetitionTypeRepository competitionTypeRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private AssessorCountOptionRepository assessorCountOptionRepository;

    @Mock
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Mock
    private CompetitionDocumentConfigRepository competitionDocumentConfigRepository;

    @Mock
    private FileTypeRepository fileTypeRepository;

    @Mock
    private ProgrammeTemplate programmeTemplate;

    @Mock
    private LoanTemplate loanTemplate;

    @Mock
    private GrantTemplate grantTemplate;

    @Mock
    private KtpTemplate ktpTemplate;

    @Mock
    private QuestionPriorityOrderService questionPriorityOrderService;

    @Before
    public void setup() {
        when(programmeTemplate.type()).thenReturn(PROGRAMME);
        when(loanTemplate.type()).thenReturn(FundingType.LOAN);
        when(grantTemplate.type()).thenReturn(FundingType.GRANT);
        when(ktpTemplate.type()).thenReturn(FundingType.KTP);
        service.setCompetitionTemplates(newArrayList(programmeTemplate));
        service.setFundingTypeTemplates(newArrayList(loanTemplate, grantTemplate, ktpTemplate));
    }

    @Test
    public void initializeCompetitionByCompetitionTemplate_competitionTypeCantBeFoundShouldResultException() {
        CompetitionType competitionType = newCompetitionType().withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withId(3L).build();

        when(competitionTypeRepository.findById(competitionType.getId())).thenReturn(Optional.empty());
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_NOT_EDITABLE));
    }

    @Test
    public void initializeCompetitionByCompetitionTemplate_competitionCantBeFoundShouldResultInServiceFailure() {
        CompetitionType competitionType = newCompetitionType().withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withId(3L).build();

        when(competitionTypeRepository.findById(competitionType.getId())).thenReturn(Optional.of(competitionType));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.empty());
        when(assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_NOT_EDITABLE));
    }

    @Test
    public void initializeCompetitionByCompetitionTemplate_competitionNotInCompetitionSetupShouldResultInServiceFailure() {
        CompetitionType competitionType = newCompetitionType().withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).withId(3L).build();

        when(competitionTypeRepository.findById(competitionType.getId())).thenReturn(Optional.of(competitionType));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_NOT_EDITABLE));
    }

    @Test
    public void initializeCompetitionByCompetitionTemplate() {
        CompetitionType competitionType = newCompetitionType()
                .withName(PROGRAMME.getText())
                .withId(1L)
                .build();

        Competition competition = newCompetition()
                .withId(3L)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withFundingType(FundingType.GRANT)
                .build();

        when(programmeTemplate.sections()).thenReturn(newArrayList(aSection()));
        when(grantTemplate.sections(any())).thenReturn(newArrayList(aSection()));
        when(grantTemplate.initialiseFinanceTypes(any())).thenReturn(competition);
        when(grantTemplate.initialiseProjectSetupColumns(any())).thenReturn(competition);
        when(grantTemplate.overrideTermsAndConditions(any())).thenReturn(competition);
        when(competitionTypeRepository.findById(competitionType.getId())).thenReturn(Optional.of(competitionType));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId()))
                .thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());

        verify(programmeTemplate).copyTemplatePropertiesToCompetition(competition);
    }

    @Test
    public void initializeCompetitionByCompetitionTemplate_fundingTypeTermsAndConditions() {
        GrantTermsAndConditions fundingTypeTerms = new GrantTermsAndConditions();
        CompetitionType competitionType = newCompetitionType()
                .withName(PROGRAMME.getText())
                .withId(1L)
                .build();

        Competition competition = newCompetition()
                .withId(3L)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withFundingType(FundingType.LOAN)
                .build();

        when(grantTermsAndConditionsRepository.getLatestForFundingType(FundingType.LOAN)).thenReturn(fundingTypeTerms);
        when(programmeTemplate.sections()).thenReturn(newArrayList(aSection()));
        when(loanTemplate.sections(any())).thenReturn(newArrayList(aSection()));
        when(loanTemplate.initialiseFinanceTypes(any())).thenReturn(competition);
        when(loanTemplate.initialiseProjectSetupColumns(any())).thenReturn(competition);
        when(loanTemplate.overrideTermsAndConditions(any())).thenReturn(competition);
        when(competitionTypeRepository.findById(competitionType.getId())).thenReturn(Optional.of(competitionType));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId()))
                .thenReturn(Optional.empty());
        when(competitionRepository.save(competition)).thenReturn(competition);

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());

        verify(programmeTemplate).copyTemplatePropertiesToCompetition(competition);
//        assertEquals(result.getSuccess().getTermsAndConditions(), fundingTypeTerms);
    }

    @Test
    public void ktpFundingTypeDefaultsAssessorFinanceView() {
        GrantTermsAndConditions fundingTypeTerms = new GrantTermsAndConditions();
        CompetitionType competitionType = newCompetitionType()
                .withName(PROGRAMME.getText())
                .withId(1L)
                .build();

        Competition competition = newCompetition()
                .withId(3L)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withFundingType(FundingType.KTP)
                .build();

        when(grantTermsAndConditionsRepository.getLatestForFundingType(FundingType.LOAN)).thenReturn(fundingTypeTerms);
        when(programmeTemplate.sections()).thenReturn(newArrayList(aSection()));
        when(ktpTemplate.sections(any())).thenReturn(newArrayList(aSection()));
        when(ktpTemplate.initialiseFinanceTypes(any())).thenReturn(competition);
        when(ktpTemplate.initialiseProjectSetupColumns(any())).thenReturn(competition);
        when(ktpTemplate.overrideTermsAndConditions(any())).thenReturn(competition);
        when(competitionTypeRepository.findById(competitionType.getId())).thenReturn(Optional.of(competitionType));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId()))
                .thenReturn(Optional.empty());
        when(competitionRepository.save(competition)).thenReturn(competition);

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());
        assertEquals(AssessorFinanceView.ALL, result.getSuccess().getCompetitionAssessmentConfig().getAssessorFinanceView());
    }
}