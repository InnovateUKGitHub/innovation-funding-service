package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.user.domain.Profile;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorBuilder.newInnovationSector;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicationInnovationAreaServiceImplTest extends BaseServiceUnitTest<ApplicationInnovationAreaService> {
    @Mock
    private InnovationAreaRepository innovationAreaRepository;

    @Override
    protected ApplicationInnovationAreaService supplyServiceUnderTest() {
        return new ApplicationInnovationAreaServiceImpl();
    }

    @Test
    public void getAvailableInnovationAreas() throws Exception {
        Long applicationId = 1L;

        List<InnovationArea> innovationAreas = newInnovationArea()
                .withName("Innovation Area A", "Innovation Area B")
                .build(2);
        InnovationSector innovationSector = newInnovationSector().withChildren(innovationAreas).build();
        Competition competition = newCompetition().withInnovationSector(innovationSector).build();
        competition.addInnovationArea(innovationAreas.get(0));
        competition.addInnovationArea(innovationAreas.get(1));

        List<InnovationAreaResource> expectedInnovationAreas = newInnovationAreaResource()
                .withName("Innovation Area A", "Innovation Area B")
                .build(2);

        Application application = newApplication()
                .withId(1L)
                .withCompetition(competition).build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(innovationAreaMapperMock.mapToResource(innovationAreas)).thenReturn(expectedInnovationAreas);

        ServiceResult<List<InnovationAreaResource>> result = service.getAvailableInnovationAreas(applicationId);

        assertTrue(result.isSuccess());
        assertEquals(expectedInnovationAreas, result.getSuccessObject());
    }

    @Test
    public void getAvailableInnovationAreas_applicationDoesNotExistShouldResultInError() throws Exception {
        Long applicationId = 1L;

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(null);

        ServiceResult<List<InnovationAreaResource>> result = service.getAvailableInnovationAreas(1L);

        assertTrue(result.isFailure());
        assertEquals(result.getFailure().getErrors().get(0).getErrorKey(), CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey());
    }

    @Test
    public void getAvailableInnovationAreas_applicationHasNoAttachedCompetitionShouldResultInError() throws Exception {
        Long applicationId = 1L;

        Application application = newApplication()
                .withId(1L).build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);

        ServiceResult<List<InnovationAreaResource>> result = service.getAvailableInnovationAreas(1L);

        assertTrue(result.isFailure());
        assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void getAvailableInnovationAreas_applicationCompetitionHasNoAttachedSectorShouldResultInError() throws Exception {
        Long applicationId = 1L;

        Competition competition = newCompetition().build();

        Application application = newApplication()
                .withId(1L)
                .withCompetition(competition).build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);

        ServiceResult<List<InnovationAreaResource>> result = service.getAvailableInnovationAreas(1L);

        assertTrue(result.isFailure());
        assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void getAvailableInnovationAreas_applicationCompetitionSectorHasNoChildrenShouldResultInError() throws Exception {
        Long applicationId = 1L;

        InnovationSector innovationSector = newInnovationSector().build();
        Competition competition = newCompetition().withInnovationSector(innovationSector).build();

        Application application = newApplication()
                .withId(1L)
                .withCompetition(competition).build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);

        ServiceResult<List<InnovationAreaResource>> result = service.getAvailableInnovationAreas(1L);

        assertTrue(result.isFailure());
        assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void setApplicationInnovationArea() throws Exception {
        Long applicationId = 1L;

        List<InnovationArea> innovationAreas = newInnovationArea()
                .withName("Innovation Area A", "Innovation Area B")
                .build(2);
        InnovationSector innovationSector = newInnovationSector().withChildren(innovationAreas).build();
        Competition competition = newCompetition().withInnovationSector(innovationSector).build();

        competition.addInnovationArea(innovationAreas.get(0));
        competition.addInnovationArea(innovationAreas.get(1));

        Application application = newApplication().withId(applicationId).withCompetition(competition).build();

        Application expectedApplication = newApplication().withId(applicationId).withInnovationArea(innovationAreas.get(1)).build();
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(applicationRepositoryMock.save(expectedApplication)).thenReturn(expectedApplication);

        ServiceResult<ApplicationResource> result = service.setInnovationArea(applicationId, innovationAreas.get(0).getId());

        assertTrue(result.isSuccess());

        verify(applicationRepositoryMock, times(1)).save(any(Application.class));
    }

    @Test
    public void setApplicationInnovationArea_nonExistingApplicationShouldResultInError() throws Exception {
        Long applicationId = 1L;
        Long innovationAreaId = 2L;

        InnovationArea innovationArea = newInnovationArea().withId(innovationAreaId).build();

        Application expectedApplication = newApplication().withId(applicationId).withInnovationArea(innovationArea).build();
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(null);

        ServiceResult<ApplicationResource> result = service.setInnovationArea(applicationId ,innovationAreaId);

        assertTrue(result.isFailure());

        verify(applicationRepositoryMock, times(0)).save(any(Application.class));
        assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void setApplicationInnovationArea_nonExistingInnovationAreaShouldResultInError() throws Exception {
        Long applicationId = 1L;
        Long innovationAreaId = 2L;

        InnovationArea innovationArea = newInnovationArea().withId(innovationAreaId).build();
        Application application = newApplication().withId(applicationId).build();

        Application expectedApplication = newApplication().withId(applicationId).withInnovationArea(innovationArea).build();
        when(innovationAreaRepository.findOne(innovationAreaId)).thenReturn(null);
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(applicationRepositoryMock.save(expectedApplication)).thenReturn(expectedApplication);

        ServiceResult<ApplicationResource> result = service.setInnovationArea(applicationId ,innovationAreaId);

        assertTrue(result.isFailure());
        verify(applicationRepositoryMock, times(0)).save(any(Application.class));
        assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void setApplicationInnovationArea_nonAllowedInnovationAreaShouldResultInError() throws Exception {
        Long applicationId = 1L;
        Long innovationAreaIdOne = 1L;
        Long innovationAreaIdTwo = 2L;

        Long disallowedInnovationAreaId = 3L;

        List<InnovationArea> innovationAreas = newInnovationArea().withId(innovationAreaIdOne,innovationAreaIdTwo).build(2);

        InnovationSector innovationSector = newInnovationSector().withChildren(innovationAreas).build();
        Competition competition = newCompetition().withInnovationSector(innovationSector).build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).build();

        Application expectedApplication = newApplication().withId(applicationId).withInnovationArea(innovationAreas.get(1)).build();
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(applicationRepositoryMock.save(expectedApplication)).thenReturn(expectedApplication);

        ServiceResult<ApplicationResource> result = service.setInnovationArea(applicationId, disallowedInnovationAreaId);

        assertTrue(result.isFailure());

        verify(applicationRepositoryMock, times(0)).save(any(Application.class));
        assertEquals(CommonFailureKeys.GENERAL_FORBIDDEN.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void setApplicationNoInnovationAreaApplies() throws Exception {
        Long applicationId = 1L;

        Application application = newApplication().withId(applicationId).build();

        Application expectedApplication = newApplication().withId(applicationId).withNoInnovationAreaApplicable(true).build();
        ApplicationResource expectedApplicationResource = newApplicationResource().withId(applicationId).withNoInnovationAreaApplicable(true).build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(applicationRepositoryMock.save(any(Application.class))).thenReturn(expectedApplication);
        when(applicationMapperMock.mapToResource(expectedApplication)).thenReturn(expectedApplicationResource);

        ServiceResult<ApplicationResource> result = service.setNoInnovationAreaApplies(applicationId);

        assertTrue(result.isSuccess());
        assertNull(result.getSuccessObject().getInnovationArea());
        assertEquals(true, result.getSuccessObject().getNoInnovationAreaApplicable());

        verify(applicationRepositoryMock, times(1)).save(any(Application.class));
    }
}
