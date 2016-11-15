package com.worth.ifs.competition.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionTypeAssessorOptionResourceListType;
import static com.worth.ifs.competition.builder.CompetitionTypeAssessorOptionResourceBuilder.newCompetitionTypeAssessorOptionResource;
import static org.junit.Assert.assertSame;

public class CompetitionTypeAssessorOptionsRestServiceImplTest extends BaseRestServiceUnitTest<CompetitionTypeAssessorOptionsRestServiceImpl> {

    private static final String COMPETITION_TYPE_ASSESSOR_OPTION_URL = "/competition-type-assessor-options";

    @Override
    protected CompetitionTypeAssessorOptionsRestServiceImpl registerRestServiceUnderTest() {
        final CompetitionTypeAssessorOptionsRestServiceImpl competitionTypeAssessorOptionsRestService = new CompetitionTypeAssessorOptionsRestServiceImpl();
        competitionTypeAssessorOptionsRestService.setDataRestServiceUrl(COMPETITION_TYPE_ASSESSOR_OPTION_URL);
        return competitionTypeAssessorOptionsRestService;
    }

    @Test
    public void testFindAllByCompetitionType() throws Exception {
        List<CompetitionTypeAssessorOptionResource> expectedList = new ArrayList<>();

        CompetitionTypeAssessorOptionResource oneAssessorOption = newCompetitionTypeAssessorOptionResource().withId(1L)
                .withAssessorOptionName("1").withAssessorOptionValue(1).build();
        CompetitionTypeAssessorOptionResource threeAssessorsOption = newCompetitionTypeAssessorOptionResource().withId(2L)
                .withAssessorOptionName("3").withAssessorOptionValue(3).build();
        CompetitionTypeAssessorOptionResource fiveAssessorsOption = newCompetitionTypeAssessorOptionResource().withId(3L)
                .withAssessorOptionName("5").withAssessorOptionValue(5).build();

        expectedList.add(oneAssessorOption);
        expectedList.add(threeAssessorsOption);
        expectedList.add(fiveAssessorsOption);


        setupGetWithRestResultExpectations(COMPETITION_TYPE_ASSESSOR_OPTION_URL + "/1", competitionTypeAssessorOptionResourceListType(), expectedList);
        List<CompetitionTypeAssessorOptionResource> responseList = service.findAllByCompetitionType(1L).getSuccessObject();
        assertSame(expectedList, responseList);
    }

}