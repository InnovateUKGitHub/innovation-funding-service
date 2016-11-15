package com.worth.ifs.competition.fixtures;

import com.worth.ifs.competition.domain.CompetitionTypeAssessorOption;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.competition.builder.CompetitionTypeAssessorOptionBuilder.newCompetitionTypeAssessorOption;
import static com.worth.ifs.competition.builder.CompetitionTypeAssessorOptionResourceBuilder.newCompetitionTypeAssessorOptionResource;

/**
 * Class to used to generate fixture for the testing.
 */
public class CompetitionTypeAssessorOptionFixture {

    public static List<CompetitionTypeAssessorOptionResource> programmeAssessorOptionResourcesList() {
        List<CompetitionTypeAssessorOptionResource> expectedList = new ArrayList<>();

        CompetitionTypeAssessorOptionResource oneAssessorOption = newCompetitionTypeAssessorOptionResource().withId(1L)
                .withAssessorOptionName("1").withAssessorOptionValue(1).withCompetitionTypeId(1L).withDefaultOption(Boolean.FALSE).build();
        CompetitionTypeAssessorOptionResource threeAssessorsOption = newCompetitionTypeAssessorOptionResource().withId(2L)
                .withAssessorOptionName("3").withAssessorOptionValue(3).withCompetitionTypeId(1L).withDefaultOption(Boolean.TRUE).build();
        CompetitionTypeAssessorOptionResource fiveAssessorsOption = newCompetitionTypeAssessorOptionResource().withId(3L)
                .withAssessorOptionName("5").withAssessorOptionValue(5).withCompetitionTypeId(1L).withDefaultOption(Boolean.FALSE).build();

        expectedList.add(oneAssessorOption);
        expectedList.add(threeAssessorsOption);
        expectedList.add(fiveAssessorsOption);

        return expectedList;
    }

    public static List<CompetitionTypeAssessorOption> programmeAssessorOptionsList() {
        List<CompetitionTypeAssessorOption> expectedList = new ArrayList<>();

        CompetitionTypeAssessorOption oneAssessorOption = newCompetitionTypeAssessorOption().withId(1L)
                .withAssessorOptionName("1").withAssessorOptionValue(1).withCompetitionTypeId(1L).withDefaultOption(Boolean.FALSE).build();
        CompetitionTypeAssessorOption threeAssessorsOption = newCompetitionTypeAssessorOption().withId(2L)
                .withAssessorOptionName("3").withAssessorOptionValue(3).withCompetitionTypeId(1L).withDefaultOption(Boolean.TRUE).build();
        CompetitionTypeAssessorOption fiveAssessorsOption = newCompetitionTypeAssessorOption().withId(3L)
                .withAssessorOptionName("5").withAssessorOptionValue(5).withCompetitionTypeId(1L).withDefaultOption(Boolean.FALSE).build();

        expectedList.add(oneAssessorOption);
        expectedList.add(threeAssessorsOption);
        expectedList.add(fiveAssessorsOption);

        return expectedList;
    }
}
