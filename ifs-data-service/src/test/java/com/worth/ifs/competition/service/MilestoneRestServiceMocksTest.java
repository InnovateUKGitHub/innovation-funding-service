package com.worth.ifs.competition.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.competition.resource.MilestoneResource;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.milestoneResourceListType;
import static java.util.Arrays.asList;

/**
 *
 */
public class MilestoneRestServiceMocksTest extends BaseRestServiceUnitTest<MilestoneRestServiceImpl> {

    @Override
    protected MilestoneRestServiceImpl registerRestServiceUnderTest() {
        MilestoneRestServiceImpl milestoneService = new MilestoneRestServiceImpl();
        return milestoneService;
    }

    @Test
    public void test_getAllDatesByCompetitionId() {
        List<MilestoneResource> returnedResponse = asList(new MilestoneResource());
        setupGetWithRestResultExpectations("/milestone/" + 7L, milestoneResourceListType(), returnedResponse);
        List<MilestoneResource> response = service.getAllDatesByCompetitionId(7L).getSuccessObject();
        assertNotNull(response);
    }
}
