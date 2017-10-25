package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.junit.Test;

public class SectionTemplatePersistorServiceImplTest extends BaseServiceUnitTest<SectionTemplatePersistorServiceImpl> {
    public SectionTemplatePersistorServiceImpl supplyServiceUnderTest() {
        return new SectionTemplatePersistorServiceImpl();
    }

    @Test
    public void persistByEntity() throws Exception {
    }

    @Test
    public void persistByPrecedingEntity() throws Exception {
    }

    @Test
    public void deleteEntityById() throws Exception {
    }

    @Test
    public void cleanForPrecedingEntity() throws Exception {
    }
    /*
    @Test
    public void copyFromCompetitionTypeTemplateAssessorCountAndPay() {
        long typeId = 4L;
        long competitionId = 2L;
        CompetitionType competitionType = newCompetitionType().withId(typeId).build();
        Competition competition = newCompetition().build();
        Competition competitionTemplate = newCompetition()
                .withCompetitionType(competitionType)
                .withSections(newSection()
                        .withSectionType(SectionType.GENERAL)
                        .withQuestions(newQuestion()
                                .withFormInputs(newFormInput()
                                        .build(2)
                                ).build(2)
                        ).build(2)
                ).build();

        competitionType.setTemplate(competitionTemplate);

        AssessorCountOption assessorOption = newAssessorCountOption().withId(1L)
                .withAssessorOptionName("1").withAssessorOptionValue(1).withDefaultOption(Boolean.TRUE).build();

        when(competitionRepository.findById(competitionId)).thenReturn(competition);
        when(competitionTypeRepository.findOne(typeId)).thenReturn(competitionType);
        when(competitionTypeAssessorOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(typeId)).thenReturn(Optional.of(assessorOption));
        ServiceResult<Void> result = service.copyFromCompetitionTypeTemplate(competitionId, typeId);

        assertTrue(result.isSuccess());
        assertEquals(competition.getCompetitionType(), competitionType);
        assertEquals(Integer.valueOf(1), competition.getAssessorCount());
        assertEquals(CompetitionSetupServiceImpl.DEFAULT_ASSESSOR_PAY, competition.getAssessorPay());
    }

    @Test
    public void copyFromCompetitionTypeTemplateAssessorCountAndPayWithNoDefault() {
        long typeId = 4L;
        long competitionId = 2L;
        CompetitionType competitionType = newCompetitionType().withId(typeId).build();
        Competition competition = newCompetition().build();
        Competition competitionTemplate = newCompetition()
                .withCompetitionType(competitionType)
                .withSections(newSection()
                        .withSectionType(SectionType.GENERAL)
                        .withQuestions(newQuestion()
                                .withFormInputs(newFormInput()
                                        .build(2)
                                ).build(2)
                        ).build(2)
                ).build();

        competitionType.setTemplate(competitionTemplate);

        when(competitionRepository.findById(competitionId)).thenReturn(competition);
        when(competitionTypeRepository.findOne(typeId)).thenReturn(competitionType);
        when(competitionTypeAssessorOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(typeId)).thenReturn(Optional.empty());
        ServiceResult<Void> result = service.copyFromCompetitionTypeTemplate(competitionId, typeId);

        assertTrue(result.isSuccess());
        assertNull(competition.getAssessorCount());
        assertEquals(CompetitionSetupServiceImpl.DEFAULT_ASSESSOR_PAY, competition.getAssessorPay());
    }
    */
}