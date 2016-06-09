package com.worth.ifs.application.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.category.service.CategoryRestService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competition.service.CompetitionsRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CompetitionServiceImplTest extends BaseServiceUnitTest<CompetitionService> {

    @Mock
    private CompetitionsRestService competitionsRestService;

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
    public void test_getById() throws Exception {
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setId(1L);


        when(competitionsRestService.getCompetitionById(1L)).thenReturn(restSuccess(competitionResource));

        final CompetitionResource found = service.getById(1L);
        assertEquals(Long.valueOf(1L), found.getId());
    }

    @Test
    public void test_create() throws Exception {
        CompetitionResource competitionResource = new CompetitionResource();
        competitionResource.setId(1L);

        when(competitionsRestService.create()).thenReturn(restSuccess(competitionResource));

        final CompetitionResource found = service.create();
        assertEquals(Long.valueOf(1L), found.getId());
    }

    @Test
    public void test_getAllCompetitions() throws Exception {
        CompetitionResource comp1 = new CompetitionResource();
        comp1.setName("Competition 1");
        comp1.setId(1L);

        CompetitionResource comp2 = new CompetitionResource();
        comp2.setName("Competition 2");
        comp2.setId(2L);

        final List<CompetitionResource> expected = new ArrayList<>(asList(comp1, comp2));
        when(competitionsRestService.getAll()).thenReturn(restSuccess(expected));

        final List<CompetitionResource> found = service.getAllCompetitions();
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals(Long.valueOf(2L), found.get(1).getId());
    }

    @Test
    public void test_getCompetitionSetupSectionsByCompetitionId() throws Exception {
        CompetitionSetupSectionResource section1 = new CompetitionSetupSectionResource();
        section1.setName("Section one");
        section1.setId(1L);

        CompetitionSetupSectionResource section2 = new CompetitionSetupSectionResource();
        section2.setName("Section two");
        section2.setId(2L);

        final List<CompetitionSetupSectionResource> expected = new ArrayList<>(asList(section1, section2));
        when(competitionsRestService.getSetupSections()).thenReturn(restSuccess(expected));

        final List<CompetitionSetupSectionResource> found = service.getCompetitionSetupSectionsByCompetitionId(1L);
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals(Long.valueOf(2L), found.get(1).getId());
    }


    @Test
    public void test_getCompletedCompetitionSetupSectionStatusesByCompetitionId() throws Exception {
        CompetitionSetupCompletedSectionResource completed1 = new CompetitionSetupCompletedSectionResource();
        completed1.setCompetition(1L);
        completed1.setCompetitionSetupSection(2L);
        completed1.setId(1L);

        CompetitionSetupCompletedSectionResource completed2 = new CompetitionSetupCompletedSectionResource();
        completed2.setCompetition(1L);
        completed2.setCompetitionSetupSection(3L);
        completed2.setId(2L);

        final List<CompetitionSetupCompletedSectionResource> expected = new ArrayList<>(asList(completed1, completed2));
        when(competitionsRestService.getCompletedSetupSections(1L)).thenReturn(restSuccess(expected));

        final List<Long> found = service.getCompletedCompetitionSetupSectionStatusesByCompetitionId(1L);
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(2L), found.get(0));
        assertEquals(Long.valueOf(3L), found.get(1));
    }


    @Test
    public void test_getAllCompetitionTypes() throws Exception {
        CompetitionTypeResource type1 = new CompetitionTypeResource();
        type1.setStateAid(false);
        type1.setName("Type 1");
        type1.setId(1L);

        CompetitionTypeResource type2 = new CompetitionTypeResource();
        type2.setStateAid(false);
        type2.setName("Type 2");
        type2.setId(2L);


        final List<CompetitionTypeResource> expected = new ArrayList<>(asList(type1, type2));

        when(competitionsRestService.getCompetitionTypes()).thenReturn(restSuccess(expected));

        final List<CompetitionTypeResource> found = service.getAllCompetitionTypes();
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals(Long.valueOf(2L), found.get(1).getId());
    }

    @Test
    public void test_generateCompetitionCode() throws Exception {
        final String expected = "201606-01";
        when(competitionsRestService.generateCompetitionCode(1L, LocalDateTime.of(2016, 06, 16, 0, 0, 0))).thenReturn(restSuccess(expected));

        final String found = service.generateCompetitionCode(1L, LocalDateTime.of(2016, 06, 16, 0, 0, 0));
        assertEquals(expected, found);
    }


}