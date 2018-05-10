package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.template.CompetitionTemplatePersistorImpl;
import org.innovateuk.ifs.form.domain.Section;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.junit.Assert.*;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class CompetitionSetupTemplateServiceImplTest extends BaseServiceUnitTest<CompetitionSetupTemplateService>{
    public CompetitionSetupTemplateService supplyServiceUnderTest() {
        return new CompetitionSetupTemplateServiceImpl();
    }

    @Mock
    private CompetitionTypeRepository competitionTypeRepositoryMock;

    @Mock
    private CompetitionTemplatePersistorImpl competitionTemplatePersistorMock;

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_competitionTypeCantBeFoundShouldResultException() throws Exception {
        Competition competitionTemplate = newCompetition().withId(2L).build();
        CompetitionType competitionType = newCompetitionType().withTemplate(competitionTemplate).withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withId(3L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(null);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_NOT_EDITABLE));

        verifyZeroInteractions(competitionTemplatePersistorMock);
    }

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_competitionCantBeFoundShouldResultInServiceFailure() throws Exception {
        Competition competitionTemplate = newCompetition().withId(2L).build();
        CompetitionType competitionType = newCompetitionType().withTemplate(competitionTemplate).withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withId(3L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(null);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_NOT_EDITABLE));

        verifyZeroInteractions(competitionTemplatePersistorMock);
    }

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_competitionNotInCompetitionSetupShouldResultInServiceFailure() throws Exception {
        Competition competitionTemplate = newCompetition().withId(2L).build();
        CompetitionType competitionType = newCompetitionType().withTemplate(competitionTemplate).withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).withId(3L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_NOT_EDITABLE));

        verifyZeroInteractions(competitionTemplatePersistorMock);
    }

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_templateCantBeFoundShouldResultInServiceFailure() throws Exception {
        CompetitionType competitionType = newCompetitionType().withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withId(3L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());

        verifyZeroInteractions(competitionTemplatePersistorMock);
    }

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_competitionShouldBeCleanedAndPersistedWithTemplateSections() throws Exception {
        List<Section> templateSections = newSection().withId(1L, 2L, 3L).build(3);
        Competition competitionTemplate = newCompetition()
                .withId(2L)
                .withSections(templateSections)
                .build();
        CompetitionType competitionType = newCompetitionType()
                .withId(1L)
                .withTemplate(competitionTemplate)
                .build();
        Competition competition = newCompetition()
                .withId(3L)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();
        Competition expectedResult = newCompetition().withId(4L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(competitionTemplatePersistorMock.persistByEntity(competition)).thenReturn(expectedResult);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId()))
                .thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());
        assertEquals(expectedResult, result.getSuccess());

        InOrder inOrder = inOrder(competitionTemplatePersistorMock);
        inOrder.verify(competitionTemplatePersistorMock).cleanByEntityId(competition.getId());
        inOrder.verify(competitionTemplatePersistorMock).persistByEntity(refEq(competition));
    }

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_templatePropertiesAreCopied() throws Exception {

        List<Section> templateSections = newSection().withId(1L, 2L, 3L).build(3);

        GrantTermsAndConditions templateTermsAndConditions = new GrantTermsAndConditions();

        Competition competitionTemplate = newCompetition()
                .withId(2L)
                .withSections(templateSections)
                .withFullApplicationFinance(false)
                .withTermsAndConditions(templateTermsAndConditions)
                .withAcademicGrantPercentage(30)
                .build();
        CompetitionType competitionType = newCompetitionType()
                .withId(1L)
                .withTemplate(competitionTemplate)
                .build();
        Competition competition = newCompetition()
                .withId(3L)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(competitionTemplatePersistorMock.persistByEntity(competition))
                .thenReturn(newCompetition().withId(4L).build());
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId()))
                .thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());

        assertEquals(false, competition.isFullApplicationFinance());
        assertSame(templateTermsAndConditions, competition.getTermsAndConditions());
        assertSame(competitionTemplate.getAcademicGrantPercentage(), competition.getAcademicGrantPercentage());
    }
}