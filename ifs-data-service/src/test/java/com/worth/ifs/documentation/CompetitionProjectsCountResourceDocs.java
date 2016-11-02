package com.worth.ifs.documentation;

import com.worth.ifs.competition.builder.CompetitionProjectsCountResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.competition.builder.CompetitionProjectsCountResourceBuilder.newCompetitionProjectsCountResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionProjectsCountResourceDocs {
    public static final FieldDescriptor[] competitionProjectsCountResourceFields = {
            fieldWithPath("competitionId").description("The competition id"),
            fieldWithPath("numProjects").description("The number of projects for a competition in project set up")
    };

    public static final CompetitionProjectsCountResourceBuilder competitionProjectsCountResourceBuilder = newCompetitionProjectsCountResource()
            .withCompetitionId(1L)
            .withNumProjects(10);
}
