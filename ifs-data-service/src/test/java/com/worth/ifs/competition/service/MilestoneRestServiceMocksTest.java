package com.worth.ifs.competition.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneResource.MilestoneName;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.worth.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.milestoneResourceListType;

/**
 *
 */
public class MilestoneRestServiceMocksTest extends BaseRestServiceUnitTest<MilestoneRestServiceImpl> {

    private static final String milestonesRestURL = "/milestone";

    @Override
    protected MilestoneRestServiceImpl registerRestServiceUnderTest() {
        MilestoneRestServiceImpl milestoneService = new MilestoneRestServiceImpl();
        return milestoneService;
    }

    @Test
    public void test_getAllDatesByCompetitionId() {
        List<MilestoneResource> returnedResponse = new ArrayList<>();
        returnedResponse.add(getMilestone());

        setupGetWithRestResultExpectations(milestonesRestURL + "/" + 1L, milestoneResourceListType(), returnedResponse);
        List<MilestoneResource> response = service.getAllDatesByCompetitionId(1L).getSuccessObject();
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_createMilestone() {

        MilestoneResource milestone = new MilestoneResource();
        milestone.setId(2L);
        milestone.setName(MilestoneResource.MilestoneName.OPEN_DATE);
        milestone.setCompetition(2L);

        setupPostWithRestResultExpectations(milestonesRestURL + "/" + 2L, MilestoneResource.class, MilestoneName.OPEN_DATE, milestone, HttpStatus.OK);

        MilestoneResource response = service.create(MilestoneName.OPEN_DATE, 2L).getSuccessObject();
        assertNotNull(response);
        assertEquals(milestone, response);
    }


    @Test
    public void test_update() {

        List<MilestoneResource> returnedResponse = new ArrayList<>();
        returnedResponse.add(getMilestone());

        setupGetWithRestResultExpectations(milestonesRestURL + "/" + 1L, milestoneResourceListType(), returnedResponse);
        List<MilestoneResource> response = service.getAllDatesByCompetitionId(1L).getSuccessObject();
        assertNotNull(response);
        assertEquals(returnedResponse, response);

        MilestoneResource milestone = response.get(0);
        milestone.setDate(milestone.getDate().plusDays(7));

        setupPutWithRestResultExpectations(milestonesRestURL + "/" + 1L, Void.class, response, null, HttpStatus.OK);

        service.update(response, 1L).getSuccessObject();
    }

    private MilestoneResource getMilestone() {
        MilestoneResource milestone = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneResource.MilestoneName.OPEN_DATE)
                .withDate(LocalDateTime.now())
                .withCompetitionId(1L).build();
        return milestone;
    }
}

