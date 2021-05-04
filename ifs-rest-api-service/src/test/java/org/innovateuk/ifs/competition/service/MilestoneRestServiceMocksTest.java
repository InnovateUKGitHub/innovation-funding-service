package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.milestoneResourceListType;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class MilestoneRestServiceMocksTest extends BaseRestServiceUnitTest<MilestoneRestServiceImpl> {

    private static final String milestonesRestURL = "/milestone";
    private static final Long competitionId = 1L;
    private static final Long newCompetitionId = 2L;
    private static final Long newAssessmentPeriodId = 3L;

    @Override
    protected MilestoneRestServiceImpl registerRestServiceUnderTest() {
        return new MilestoneRestServiceImpl();
    }

    @Test
    public void getAllPublicMilestonesByCompetitionId() {
        List<MilestoneResource> returnedResponse = new ArrayList<>();
        returnedResponse.add(getOpenDateMilestone());

        setupGetWithRestResultAnonymousExpectations(milestonesRestURL + "/" + competitionId + "/public", milestoneResourceListType(), returnedResponse, OK);
        List<MilestoneResource> response = service.getAllPublicMilestonesByCompetitionId(competitionId).getSuccess();
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void getAllMilestonesByCompetitionId() {
        List<MilestoneResource> returnedResponse = new ArrayList<>();
        returnedResponse.add(getOpenDateMilestone());

        setupGetWithRestResultExpectations(milestonesRestURL + "/" + competitionId, milestoneResourceListType(), returnedResponse);
        List<MilestoneResource> response = service.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void getMilestoneByTypeAndCompetitionId() {
        MilestoneResource returnedResponse = getBriefingEventMilestone();
        MilestoneType type = MilestoneType.BRIEFING_EVENT;

        setupGetWithRestResultExpectations(milestonesRestURL + "/" + competitionId + "/get-by-type?type=" + type, MilestoneResource.class, returnedResponse);

        MilestoneResource response = service.getMilestoneByTypeAndCompetitionId(type, competitionId).getSuccess();

        assertNotNull(response);
        Assert.assertEquals(returnedResponse, response);
    }

    @Test
    public void createMilestone() {
        MilestoneResource milestoneResource = new MilestoneResource();
        milestoneResource.setId(3L);
        milestoneResource.setType(MilestoneType.OPEN_DATE);
        milestoneResource.setCompetitionId(newCompetitionId);

        setupPostWithRestResultExpectations(milestonesRestURL, MilestoneResource.class, milestoneResource, milestoneResource, CREATED);

        MilestoneResource response = service.create(milestoneResource).getSuccess();
        assertNotNull(response);
        Assert.assertEquals(milestoneResource, response);
    }

    @Test
    public void updateMilestones() {

        List<MilestoneResource> returnedResponse = new ArrayList<>();
        returnedResponse.add(getOpenDateMilestone());

        setupGetWithRestResultExpectations(milestonesRestURL + "/" + competitionId, milestoneResourceListType(), returnedResponse);
        List<MilestoneResource> response = service.getAllMilestonesByCompetitionId(competitionId).getSuccess();

        assertNotNull(response);
        assertEquals(returnedResponse, response);

        MilestoneResource milestone = response.get(0);
        milestone.setDate(milestone.getDate().plusDays(7));
        response.set(0, milestone);

        setupPutWithRestResultExpectations(milestonesRestURL + "/many", Void.class, response, null, OK);
        service.updateMilestones(response);
        setupPutWithRestResultVerifications(milestonesRestURL + "/many", Void.class, response);
    }

    @Test
    public void updateMilestone() {
        MilestoneResource returnedResponse = getBriefingEventMilestone();
        MilestoneType type = MilestoneType.BRIEFING_EVENT;

        setupGetWithRestResultExpectations(milestonesRestURL + "/" + competitionId + "/get-by-type?type=" + type, MilestoneResource.class, returnedResponse);
        MilestoneResource response = service.getMilestoneByTypeAndCompetitionId(type, competitionId).getSuccess();

        assertNotNull(response);
        Assert.assertEquals(returnedResponse, response);

        ZonedDateTime date = ZonedDateTime.now();
        response.setDate(date);

        setupPutWithRestResultExpectations(milestonesRestURL + "/", Void.class, response, null, OK);
        service.updateMilestone(response);
        setupPutWithRestResultVerifications(milestonesRestURL + "/", Void.class, response);
    }

    @Test
    public void updateCompletionStage() {

        String url = milestonesRestURL + "/competition/" + competitionId + "/completion-stage?completionStage=" +
                CompetitionCompletionStage.PROJECT_SETUP.name();

        setupPutWithRestResultExpectations(url, OK);

        RestResult<Void> result = service.updateCompletionStage(competitionId, CompetitionCompletionStage.PROJECT_SETUP);

        assertTrue(result.isSuccess());

        setupPutWithRestResultVerifications(url);
    }

    private MilestoneResource getOpenDateMilestone() {
        return milestone(1L, MilestoneType.OPEN_DATE, ZonedDateTime.now(), competitionId);

    }

    private MilestoneResource getBriefingEventMilestone() {
        return milestone(1L, MilestoneType.BRIEFING_EVENT, ZonedDateTime.of(2026,3,15,9,0,0,0,ZoneId.systemDefault()), competitionId);
    }

    private MilestoneResource milestone(Long id, MilestoneType type, ZonedDateTime date, Long competitionId) {
        MilestoneResource milestone = new MilestoneResource();
        milestone.setId(id);
        milestone.setType(type);
        milestone.setDate(date);
        milestone.setCompetitionId(competitionId);
        return milestone;
    }
}

