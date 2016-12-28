package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;

import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.milestoneResourceListType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MilestoneRestServiceMocksTest extends BaseRestServiceUnitTest<MilestoneRestServiceImpl> {

    private static final String milestonesRestURL = "/milestone";
    private static final Long competitionId = 1L;
    private static final Long newCompetitionId = 2L;

    @Override
    protected MilestoneRestServiceImpl registerRestServiceUnderTest() {
        MilestoneRestServiceImpl milestoneService = new MilestoneRestServiceImpl();
        return milestoneService;
    }

    @Test
    public void test_getAllMilestonesByCompetitionId() {
        List<MilestoneResource> returnedResponse = new ArrayList<>();
        returnedResponse.add(getOpenDateMilestone());

        setupGetWithRestResultExpectations(milestonesRestURL + "/" + competitionId, milestoneResourceListType(), returnedResponse);
        List<MilestoneResource> response = service.getAllMilestonesByCompetitionId(competitionId).getSuccessObject();
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getMilestoneByTypeAndCompetitionId() {
        MilestoneResource returnedResponse = getBriefingEventMilestone();
        MilestoneType type = MilestoneType.BRIEFING_EVENT;

        setupGetWithRestResultExpectations(milestonesRestURL + "/" + competitionId + "/getByType?type=" + type, MilestoneResource.class, returnedResponse);

        MilestoneResource response = service.getMilestoneByTypeAndCompetitionId(type, competitionId).getSuccessObject();

        assertNotNull(response);
        Assert.assertEquals(returnedResponse, response);
    }

    @Test
    public void test_createMilestone() {
        MilestoneResource milestone = new MilestoneResource();
        milestone.setId(3L);
        milestone.setType(MilestoneType.OPEN_DATE);
        milestone.setCompetition(newCompetitionId);

        setupPostWithRestResultExpectations(milestonesRestURL + "/" + newCompetitionId, MilestoneResource.class, MilestoneType.OPEN_DATE, milestone, HttpStatus.OK);

        MilestoneResource response = service.create(MilestoneType.OPEN_DATE, newCompetitionId).getSuccessObject();
        assertNotNull(response);
        Assert.assertEquals(milestone, response);
    }

    @Test
    public void test_updateMilestones() {

        List<MilestoneResource> returnedResponse = new ArrayList<>();
        returnedResponse.add(getOpenDateMilestone());

        setupGetWithRestResultExpectations(milestonesRestURL + "/" + competitionId, milestoneResourceListType(), returnedResponse);
        List<MilestoneResource> response = service.getAllMilestonesByCompetitionId(competitionId).getSuccessObject();

        assertNotNull(response);
        assertEquals(returnedResponse, response);

        MilestoneResource milestone = response.get(0);
        milestone.setDate(milestone.getDate().plusDays(7));
        response.set(0, milestone);

        setupPutWithRestResultExpectations(milestonesRestURL + "/many", Void.class, response, null, HttpStatus.OK);
        service.updateMilestones(response);
        setupPutWithRestResultVerifications(milestonesRestURL + "/many", Void.class, response);
    }

    @Test
    public void test_updateMilestone() {

        MilestoneResource returnedResponse = getBriefingEventMilestone();
        MilestoneType type = MilestoneType.BRIEFING_EVENT;

        setupGetWithRestResultExpectations(milestonesRestURL + "/" + competitionId + "/getByType?type=" + type, MilestoneResource.class, returnedResponse);
        MilestoneResource response = service.getMilestoneByTypeAndCompetitionId(type, competitionId).getSuccessObject();

        assertNotNull(response);
        Assert.assertEquals(returnedResponse, response);

        LocalDateTime date = LocalDateTime.now();
        response.setDate(date);

        setupPutWithRestResultExpectations(milestonesRestURL + "/", Void.class, response, null, HttpStatus.OK);
        service.updateMilestone(response);
        setupPutWithRestResultVerifications(milestonesRestURL + "/", Void.class, response);
    }

    private MilestoneResource getOpenDateMilestone() {
        return milestone(1L, MilestoneType.OPEN_DATE, LocalDateTime.now(), competitionId);

    }

    private MilestoneResource getBriefingEventMilestone() {
        return milestone(1L, MilestoneType.BRIEFING_EVENT, LocalDateTime.of(2026,3,15,9,0), competitionId);
    }

    private MilestoneResource milestone(Long id, MilestoneType type, LocalDateTime date, Long competitionId) {
        MilestoneResource milestone = new MilestoneResource();
        milestone.setId(id);
        milestone.setType(type);
        milestone.setDate(date);
        milestone.setCompetition(competitionId);
        return milestone;
    }
}

