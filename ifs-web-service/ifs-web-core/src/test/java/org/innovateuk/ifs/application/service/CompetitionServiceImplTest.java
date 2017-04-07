package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestService;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.AssessorCountOptionResourceBuilder.newAssessorCountOptionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeResourceBuilder.newCompetitionTypeResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.mockito.Mockito.*;


/**
 * Test Class for all functionality in {@link CompetitionServiceImpl}
 */
public class CompetitionServiceImplTest extends BaseServiceUnitTest<CompetitionService> {

    @Mock
    private CompetitionsRestService competitionsRestService;

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    @Mock
    private AssessorCountOptionsRestService assessorCountOptionsRestService;

    @Override
    protected CompetitionService supplyServiceUnderTest() {
        return new CompetitionServiceImpl();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void getById() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().withId(1L).build();

        when(competitionsRestService.getCompetitionById(1L)).thenReturn(restSuccess(competitionResource));

        final CompetitionResource found = service.getById(1L);
        assertEquals(Long.valueOf(1L), found.getId());
    }

    @Test
    public void create() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().withId(1L).build();

        when(competitionsRestService.create()).thenReturn(restSuccess(competitionResource));

        final CompetitionResource found = service.create();
        assertEquals(Long.valueOf(1L), found.getId());
    }


    @Test
    public void createNonIfs() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().withId(1L).build();

        when(competitionsRestService.createNonIfs()).thenReturn(restSuccess(competitionResource));

        final CompetitionResource found = service.createNonIfs();
        assertEquals(Long.valueOf(1L), found.getId());
    }

    @Test
    public void getAllCompetitions() throws Exception {
        CompetitionResource comp1 = newCompetitionResource().withName("Competition 1").withId(1L).build();

        CompetitionResource comp2 = newCompetitionResource().withName("Competition 2").withId(2L).build();

        final List<CompetitionResource> expected = new ArrayList<>(asList(comp1, comp2));
        when(competitionsRestService.getAll()).thenReturn(restSuccess(expected));

        final List<CompetitionResource> found = service.getAllCompetitions();
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals(Long.valueOf(2L), found.get(1).getId());
    }

    @Test
    public void getAllCompetitionsNotInSetup() throws Exception {
        CompetitionResource comp1 = newCompetitionResource().withName("Competition 1").withId(1L).withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        CompetitionResource comp2 = newCompetitionResource().withName("Competition 2").withId(2L).build();

        final List<CompetitionResource> expected = new ArrayList<>(asList(comp1, comp2));
        when(competitionsRestService.getAll()).thenReturn(restSuccess(expected));

        final List<CompetitionResource> found = service.getAllCompetitionsNotInSetup();
        assertEquals(1, found.size());
        assertEquals(Long.valueOf(2L), found.get(0).getId());
    }

    @Test
    public void getAllCompetitionTypes() throws Exception {
        CompetitionTypeResource type1 = newCompetitionTypeResource().withStateAid(false).withName("Type 1").withId(1L).build();

        CompetitionTypeResource type2 = newCompetitionTypeResource().withStateAid(false).withName("Type 2").withId(2L).build();

        final List<CompetitionTypeResource> expected = new ArrayList<>(asList(type1, type2));

        when(competitionsRestService.getCompetitionTypes()).thenReturn(restSuccess(expected));

        final List<CompetitionTypeResource> found = service.getAllCompetitionTypes();
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals(Long.valueOf(2L), found.get(1).getId());
    }

    @Test
    public void generateCompetitionCode() throws Exception {
        final String expected = "201606-01";
        when(competitionsRestService.generateCompetitionCode(1L, ZonedDateTime.of(2016, 6, 16, 0, 0, 0, 0, ZoneId.systemDefault()))).thenReturn(restSuccess(expected));

        final String found = service.generateCompetitionCode(1L, ZonedDateTime.of(2016, 6, 16, 0, 0, 0, 0, ZoneId.systemDefault()));
        assertEquals(expected, found);
    }


    @Test
    public void initApplicationFormByCompetitionType() throws Exception {
        Long competitionId = Long.MAX_VALUE;
        Long competitionTypeId = Long.MIN_VALUE;
        when(competitionsRestService.initApplicationForm(competitionId, competitionTypeId)).thenReturn(restSuccess());

        service.initApplicationFormByCompetitionType(competitionId, competitionTypeId);
    }

    @Test
    public void markAsSetup() throws Exception {
        Long competitionId = Long.MAX_VALUE;
        when(competitionsRestService.markAsSetup(competitionId)).thenReturn(restSuccess());

        service.markAsSetup(competitionId);
    }

    @Test
    public void returnToSetup() throws Exception {
        Long competitionId = Long.MAX_VALUE;
        when(competitionsRestService.returnToSetup(competitionId)).thenReturn(restSuccess());

        service.returnToSetup(competitionId);
    }

    @Test
    public void testGetAssessorOptionsForCompetitionType() throws Exception {
        AssessorCountOptionResource expectedResource = newAssessorCountOptionResource()
                .withId(1L).withAssessorOptionName("1").withAssessorOptionValue(1).withDefaultOption(Boolean.FALSE).build();

        final List<AssessorCountOptionResource> expectedList = new ArrayList<>(asList(expectedResource));

        when(assessorCountOptionsRestService.findAllByCompetitionType(1L)).thenReturn(restSuccess(expectedList));

        final List<AssessorCountOptionResource> found = service.getAssessorOptionsForCompetitionType(1L);
        assertEquals(1, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
    }

    @Test
    public void closeAssessment() throws Exception {
        Long competitionId = Long.MAX_VALUE;
        when(competitionsRestService.closeAssessment(competitionId)).thenReturn(restSuccess());

        service.closeAssessment(competitionId);
        verify(competitionsRestService, only()).closeAssessment(competitionId);
    }

    @Test
    public void notifyAssessors() throws Exception {
        Long competitionId = Long.MAX_VALUE;
        when(competitionsRestService.notifyAssessors(competitionId)).thenReturn(restSuccess());

        service.notifyAssessors(competitionId);
        verify(competitionsRestService, only()).notifyAssessors(competitionId);
    }

    @Test
    public void getPublicContentOfCompetition() throws Exception {
        Long competitionId = 12314L;
        PublicContentItemResource expected = newPublicContentItemResource().build();

        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(expected));

        PublicContentItemResource result = service.getPublicContentOfCompetition(competitionId);

        assertEquals(expected, result);
    }

    @Test
    public void releaseFeedback() throws Exception {
        Long competitionId = Long.MAX_VALUE;
        when(competitionsRestService.releaseFeedback(competitionId)).thenReturn(restSuccess());

        service.releaseFeedback(competitionId);
        verify(competitionsRestService, only()).releaseFeedback(competitionId);
    }
}
