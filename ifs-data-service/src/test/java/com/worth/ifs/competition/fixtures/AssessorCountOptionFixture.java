package com.worth.ifs.competition.fixtures;

import com.worth.ifs.competition.domain.AssessorCountOption;
import com.worth.ifs.competition.resource.AssessorCountOptionResource;

import java.util.ArrayList;
import java.util.List;
import static com.worth.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static com.worth.ifs.competition.builder.AssessorCountOptionBuilder.newAssessorCountOption;
import static com.worth.ifs.competition.builder.AssessorCountOptionResourceBuilder.newAssessorCountOptionResource;

/**
 * Class to used to generate fixture for the testing.
 */
public class AssessorCountOptionFixture {

    public static List<AssessorCountOptionResource> programmeAssessorOptionResourcesList() {
        List<AssessorCountOptionResource> expectedList = new ArrayList<>();

        AssessorCountOptionResource oneAssessorOption = newAssessorCountOptionResource().withId(1L)
                .withAssessorOptionName("1").withAssessorOptionValue(1).withCompetitionType(1L).withDefaultOption(Boolean.FALSE).build();
        AssessorCountOptionResource threeAssessorsOption = newAssessorCountOptionResource().withId(2L)
                .withAssessorOptionName("3").withAssessorOptionValue(3).withCompetitionType(1L).withDefaultOption(Boolean.TRUE).build();
        AssessorCountOptionResource fiveAssessorsOption = newAssessorCountOptionResource().withId(3L)
                .withAssessorOptionName("5").withAssessorOptionValue(5).withCompetitionType(1L).withDefaultOption(Boolean.FALSE).build();

        expectedList.add(oneAssessorOption);
        expectedList.add(threeAssessorsOption);
        expectedList.add(fiveAssessorsOption);

        return expectedList;
    }

    public static List<AssessorCountOption> programmeAssessorOptionsList() {
        List<AssessorCountOption> expectedList = new ArrayList<>();
        AssessorCountOption oneAssessorOption = newAssessorCountOption().withId(1L)
                .withAssessorOptionName("1").withAssessorOptionValue(1).withCompetitionType(newCompetitionType().withId(1L).build()).withDefaultOption(Boolean.FALSE).build();
        AssessorCountOption threeAssessorsOption = newAssessorCountOption().withId(2L)
                .withAssessorOptionName("3").withAssessorOptionValue(3).withCompetitionType(newCompetitionType().withId(1L).build()).withDefaultOption(Boolean.TRUE).build();
        AssessorCountOption fiveAssessorsOption = newAssessorCountOption().withId(3L)
                .withAssessorOptionName("5").withAssessorOptionValue(5).withCompetitionType(newCompetitionType().withId(1L).build()).withDefaultOption(Boolean.FALSE).build();

        expectedList.add(oneAssessorOption);
        expectedList.add(threeAssessorsOption);
        expectedList.add(fiveAssessorsOption);

        return expectedList;
    }
}
