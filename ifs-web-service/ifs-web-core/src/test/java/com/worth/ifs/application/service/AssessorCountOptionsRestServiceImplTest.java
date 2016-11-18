package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.competition.resource.AssessorCountOptionResource;
import com.worth.ifs.competition.service.AssessorCountOptionsRestServiceImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.assessorCountOptionResourceListType;
import static com.worth.ifs.competition.builder.AssessorCountOptionResourceBuilder.newAssessorCountOptionResource;
import static org.junit.Assert.assertSame;

public class AssessorCountOptionsRestServiceImplTest extends BaseRestServiceUnitTest<AssessorCountOptionsRestServiceImpl> {

    private static final String COMPETITION_TYPE_ASSESSOR_OPTION_URL = "/assessor-count-options";

    @Override
    protected AssessorCountOptionsRestServiceImpl registerRestServiceUnderTest() {
        final AssessorCountOptionsRestServiceImpl competitionTypeAssessorOptionsRestService = new AssessorCountOptionsRestServiceImpl();
        competitionTypeAssessorOptionsRestService.setDataRestServiceUrl(COMPETITION_TYPE_ASSESSOR_OPTION_URL);
        return competitionTypeAssessorOptionsRestService;
    }

    @Test
    public void testFindAllByCompetitionType() throws Exception {
        List<AssessorCountOptionResource> expectedList = new ArrayList<>();

        AssessorCountOptionResource oneAssessorOption = AssessorCountOptionResourceBuilder.newAssessorCountOptionResource().withId(1L)
                .withAssessorOptionName("1").withAssessorOptionValue(1).build();
        AssessorCountOptionResource threeAssessorsOption = AssessorCountOptionResourceBuilder.newAssessorCountOptionResource().withId(2L)
                .withAssessorOptionName("3").withAssessorOptionValue(3).build();
        AssessorCountOptionResource fiveAssessorsOption = AssessorCountOptionResourceBuilder.newAssessorCountOptionResource().withId(3L)
                .withAssessorOptionName("5").withAssessorOptionValue(5).build();

        expectedList.add(oneAssessorOption);
        expectedList.add(threeAssessorsOption);
        expectedList.add(fiveAssessorsOption);


        setupGetWithRestResultExpectations(COMPETITION_TYPE_ASSESSOR_OPTION_URL + "/1", assessorCountOptionResourceListType(), expectedList);
        List<AssessorCountOptionResource> responseList = service.findAllByCompetitionType(1L).getSuccessObject();
        assertSame(expectedList, responseList);
    }

}