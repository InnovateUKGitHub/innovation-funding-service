package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestServiceImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessorCountOptionResourceListType;
import static org.innovateuk.ifs.competition.builder.AssessorCountOptionResourceBuilder.newAssessorCountOptionResource;
import static org.junit.Assert.assertSame;

public class AssessorCountOptionsRestServiceImplTest extends BaseRestServiceUnitTest<AssessorCountOptionsRestServiceImpl> {

    private static final String COMPETITION_TYPE_ASSESSOR_OPTION_URL = "/assessor-count-options";

    @Override
    protected AssessorCountOptionsRestServiceImpl registerRestServiceUnderTest() {
        final AssessorCountOptionsRestServiceImpl competitionTypeAssessorOptionsRestService = new AssessorCountOptionsRestServiceImpl();
        competitionTypeAssessorOptionsRestService.setServiceUrl(COMPETITION_TYPE_ASSESSOR_OPTION_URL);
        return competitionTypeAssessorOptionsRestService;
    }

    @Test
    public void testFindAllByCompetitionType() throws Exception {
        List<AssessorCountOptionResource> expectedList = new ArrayList<>();

        AssessorCountOptionResource oneAssessorOption = newAssessorCountOptionResource().withId(1L)
                .withAssessorOptionName("1").withAssessorOptionValue(1).build();
        AssessorCountOptionResource threeAssessorsOption = newAssessorCountOptionResource().withId(2L)
                .withAssessorOptionName("3").withAssessorOptionValue(3).build();
        AssessorCountOptionResource fiveAssessorsOption = newAssessorCountOptionResource().withId(3L)
                .withAssessorOptionName("5").withAssessorOptionValue(5).build();

        expectedList.add(oneAssessorOption);
        expectedList.add(threeAssessorsOption);
        expectedList.add(fiveAssessorsOption);


        setupGetWithRestResultExpectations(COMPETITION_TYPE_ASSESSOR_OPTION_URL + "/1", assessorCountOptionResourceListType(), expectedList);
        List<AssessorCountOptionResource> responseList = service.findAllByCompetitionType(1L).getSuccess();
        assertSame(expectedList, responseList);
    }

}
