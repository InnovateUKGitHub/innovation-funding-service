package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
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
    private CompetitionRestService competitionRestService;

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

        when(competitionRestService.getCompetitionById(1L)).thenReturn(restSuccess(competitionResource));

        final CompetitionResource found = service.getById(1L);
        assertEquals(Long.valueOf(1L), found.getId());
    }

    @Test
    public void getAllCompetitions() throws Exception {
        CompetitionResource comp1 = newCompetitionResource().withName("Competition 1").withId(1L).build();

        CompetitionResource comp2 = newCompetitionResource().withName("Competition 2").withId(2L).build();

        final List<CompetitionResource> expected = new ArrayList<>(asList(comp1, comp2));
        when(competitionRestService.getAll()).thenReturn(restSuccess(expected));

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
        when(competitionRestService.getAll()).thenReturn(restSuccess(expected));

        final List<CompetitionResource> found = service.getAllCompetitionsNotInSetup();
        assertEquals(1, found.size());
        assertEquals(Long.valueOf(2L), found.get(0).getId());
    }

    @Test
    public void findUnsuccessfulApplications() throws Exception {
        Long competitionId = 1L;
        int pageNumber = 0;
        int pageSize = 20;
        String sortField = "id";

        ApplicationPageResource applicationPage = new ApplicationPageResource();
        when(competitionRestService.findUnsuccessfulApplications(competitionId, pageNumber, pageSize, sortField)).thenReturn(restSuccess(applicationPage));

        ApplicationPageResource result = service.findUnsuccessfulApplications(competitionId, pageNumber, pageSize, sortField);
        assertEquals(applicationPage, result);
        verify(competitionRestService, only()).findUnsuccessfulApplications(competitionId, pageNumber, pageSize, sortField);
    }

    @Test
    public void getAllCompetitionTypes() throws Exception {
        CompetitionTypeResource type1 = newCompetitionTypeResource().withStateAid(false).withName("Type 1").withId(1L).build();

        CompetitionTypeResource type2 = newCompetitionTypeResource().withStateAid(false).withName("Type 2").withId(2L).build();

        final List<CompetitionTypeResource> expected = new ArrayList<>(asList(type1, type2));

        when(competitionRestService.getCompetitionTypes()).thenReturn(restSuccess(expected));

        final List<CompetitionTypeResource> found = service.getAllCompetitionTypes();
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals(Long.valueOf(2L), found.get(1).getId());
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
        when(competitionRestService.closeAssessment(competitionId)).thenReturn(restSuccess());

        service.closeAssessment(competitionId);
        verify(competitionRestService, only()).closeAssessment(competitionId);
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
    public void findInnovationLeads() throws Exception {
        Long competitionId = 1L;
        List<UserResource> userResources = Collections.emptyList();
        when(competitionRestService.findInnovationLeads(competitionId)).thenReturn(restSuccess(userResources));

        List<UserResource> result = service.findInnovationLeads(competitionId);
        assertEquals(userResources, result);
        verify(competitionRestService, only()).findInnovationLeads(competitionId);
    }

    @Test
    public void addInnovationLead() throws Exception {
        Long competitionId = 1L;
        Long innovationLeadUserId = 2L;
        when(competitionRestService.addInnovationLead(competitionId, innovationLeadUserId)).thenReturn(restSuccess());

        service.addInnovationLead(competitionId, innovationLeadUserId);
        verify(competitionRestService, only()).addInnovationLead(competitionId, innovationLeadUserId);
    }

    @Test
    public void removeInnovationLead() throws Exception {
        Long competitionId = 1L;
        Long innovationLeadUserId = 2L;
        when(competitionRestService.removeInnovationLead(competitionId, innovationLeadUserId)).thenReturn(restSuccess());

        service.removeInnovationLead(competitionId, innovationLeadUserId);
        verify(competitionRestService, only()).removeInnovationLead(competitionId, innovationLeadUserId);
    }

}
