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
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules.SubsidyControlTemplate;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype.*;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.template.HorizonEuropeGuaranteeTemplate;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.template.ProgrammeTemplate;
import org.innovateuk.ifs.competitionsetup.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.horizon.domain.CompetitionHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.domain.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.mapper.CompetitionHorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.repository.CompetitionHorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.transactional.HorizonWorkProgrammeService;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.resource.CompetitionTypeEnum.HORIZON_EUROPE_GUARANTEE;
import static org.innovateuk.ifs.competition.resource.CompetitionTypeEnum.PROGRAMME;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;
import static org.innovateuk.ifs.horizon.builder.HorizonWorkProgrammeBuilder.newHorizonWorkProgramme;
import static org.junit.Assert.*;
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
    private HorizonEuropeGuaranteeTemplate horizonEuropeGuaranteeTemplate;

    @Mock
    private LoanTemplate loanTemplate;

    @Mock
    private GrantTemplate grantTemplate;

    @Mock
    private KtpTemplate ktpTemplate;

    @Mock
    private ProcurementTemplate procurementTemplate;

    @Mock
    private HecpTemplate hecpTemplate;

    @Mock
    private SubsidyControlTemplate subsidyControlTemplate;
    @Mock
    private QuestionPriorityOrderService questionPriorityOrderService;

    private ArgumentCaptor<Competition> competitionArgumentCaptor;

    @Mock
    private HorizonWorkProgrammeService horizonWorkProgrammeService;

    @Mock
    private CompetitionHorizonWorkProgrammeMapper competitionHorizonWorkProgrammeMapper;

    @Mock
    private CompetitionHorizonWorkProgrammeRepository competitionHorizonWorkProgrammeRepository;

    @Before
    public void setup() {
        when(programmeTemplate.type()).thenReturn(PROGRAMME);
        when(horizonEuropeGuaranteeTemplate.type()).thenReturn(HORIZON_EUROPE_GUARANTEE);
        when(loanTemplate.type()).thenReturn(FundingType.LOAN);
        when(grantTemplate.type()).thenReturn(FundingType.GRANT);
        when(ktpTemplate.type()).thenReturn(FundingType.KTP);
        when(procurementTemplate.type()).thenReturn(FundingType.PROCUREMENT);
        when(hecpTemplate.type()).thenReturn(FundingType.HECP);
        when(subsidyControlTemplate.type()).thenReturn(FundingRules.SUBSIDY_CONTROL);
        service.setCompetitionTemplates(newArrayList(programmeTemplate, horizonEuropeGuaranteeTemplate));
        service.setFundingTypeTemplates(newArrayList(loanTemplate, grantTemplate, ktpTemplate, procurementTemplate, hecpTemplate));
        service.setFundingRulesTemplates(newArrayList(subsidyControlTemplate));
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
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .build();

        when(programmeTemplate.sections()).thenReturn(newArrayList(aSection()));
        when(grantTemplate.sections(any())).thenReturn(newArrayList(aSection()));
        when(grantTemplate.initialiseFinanceTypes(any())).thenReturn(competition);
        when(grantTemplate.initialiseProjectSetupColumns(any())).thenReturn(competition);
        when(grantTemplate.overrideTermsAndConditions(any())).thenReturn(competition);
        when(grantTemplate.setGolTemplate(any())).thenReturn(competition);
        when(competitionTypeRepository.findById(competitionType.getId())).thenReturn(Optional.of(competitionType));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId()))
                .thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());

        verify(programmeTemplate).copyTemplatePropertiesToCompetition(competition);
        verify(subsidyControlTemplate).sections(any(), any());
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
        when(loanTemplate.setGolTemplate(any())).thenReturn(competition);
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
    public void initializeCompetitionByCompetitionTemplate_EDI() {
        ReflectionTestUtils.setField(service, "ediUpdateToggle", true);

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
        when(loanTemplate.setGolTemplate(any())).thenReturn(competition);
        when(competitionTypeRepository.findById(competitionType.getId())).thenReturn(Optional.of(competitionType));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId()))
                .thenReturn(Optional.empty());
        when(competitionRepository.save(competition)).thenReturn(competition);

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());


    }

    @Test
    public void initializeCompetitionByCompetitionTemplate_competitionThirdPartyConfig() {
        competitionArgumentCaptor = ArgumentCaptor.forClass(Competition.class);

        CompetitionType competitionType = newCompetitionType()
                .withName(PROGRAMME.getText())
                .withId(1L)
                .build();

        Competition competition = newCompetition()
                .withId(3L)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withFundingType(FundingType.PROCUREMENT)
                .withFundingRules(FundingRules.NOT_AID)
                .build();

        when(programmeTemplate.sections()).thenReturn(newArrayList(aSection()));
        when(procurementTemplate.sections(any())).thenReturn(newArrayList(aSection()));
        when(procurementTemplate.initialiseFinanceTypes(any())).thenReturn(competition);
        when(procurementTemplate.initialiseProjectSetupColumns(any())).thenReturn(competition);
        when(procurementTemplate.overrideTermsAndConditions(any())).thenReturn(competition);
        when(procurementTemplate.setGolTemplate(any())).thenReturn(competition);
        when(competitionTypeRepository.findById(competitionType.getId())).thenReturn(Optional.of(competitionType));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId()))
                .thenReturn(Optional.empty());
        when(competitionRepository.save(competition)).thenReturn(competition);

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());

        verify(competitionRepository).save(competitionArgumentCaptor.capture());

        Competition toBeCreatedCompetition = competitionArgumentCaptor.getValue();

        assertNotNull(toBeCreatedCompetition);
        assertNull(toBeCreatedCompetition.getCompetitionThirdPartyConfig());

        verify(programmeTemplate).copyTemplatePropertiesToCompetition(competition);
    }

    @Test
    public void initializeCompetitionByCompetitionTemplate_competitionHorizonEuropeConfig() {
        competitionArgumentCaptor = ArgumentCaptor.forClass(Competition.class);

        CompetitionType competitionType = newCompetitionType()
                .withName(HORIZON_EUROPE_GUARANTEE.getText())
                .withId(1L)
                .build();

        Competition competition = newCompetition()
                .withId(3L)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withFundingType(FundingType.HECP)
                .withFundingRules(FundingRules.NOT_AID)
                .build();

        HorizonWorkProgramme horizonWorkProgramme = newHorizonWorkProgramme()
                .withId(1L)
                .withName("CL2")
                .withEnabled(true)
                .build();


        CompetitionHorizonWorkProgramme competitionHorizonWorkProgramme = new CompetitionHorizonWorkProgramme();
        competitionHorizonWorkProgramme.setCompetitionId(competition.getId());
        competitionHorizonWorkProgramme.setWorkProgramme(horizonWorkProgramme);

        when(horizonEuropeGuaranteeTemplate.sections()).thenReturn(newArrayList(aSection()));
        when(hecpTemplate.sections(any())).thenReturn(newArrayList(aSection()));
        when(hecpTemplate.initialiseFinanceTypes(any())).thenReturn(competition);
        when(hecpTemplate.initialiseProjectSetupColumns(any())).thenReturn(competition);
        when(hecpTemplate.overrideTermsAndConditions(any())).thenReturn(competition);
        when(hecpTemplate.setGolTemplate(any())).thenReturn(competition);
        when(competitionTypeRepository.findById(competitionType.getId())).thenReturn(Optional.of(competitionType));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId()))
                .thenReturn(Optional.empty());
        when(horizonWorkProgrammeService.initWorkProgrammesForCompetition(competition.getId())).thenReturn(ServiceResult.serviceSuccess());
        when(competitionHorizonWorkProgrammeMapper.mapIdAndWorkProgrammeToDomain(competition.getId(), horizonWorkProgramme)).thenReturn(competitionHorizonWorkProgramme);

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());

        verify(competitionRepository).save(competitionArgumentCaptor.capture());

        Competition toBeCreatedCompetition = competitionArgumentCaptor.getValue();
        assertNotNull(toBeCreatedCompetition);

        verify(horizonEuropeGuaranteeTemplate).copyTemplatePropertiesToCompetition(competition);
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
        when(ktpTemplate.setGolTemplate(any())).thenReturn(competition);
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