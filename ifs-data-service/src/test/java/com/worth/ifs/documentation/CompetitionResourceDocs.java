package com.worth.ifs.documentation;

import com.worth.ifs.competition.builder.CompetitionResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDateTime;

import static com.google.common.primitives.Longs.asList;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionResourceDocs {
    public static final FieldDescriptor[] competitionResourceFields = {
            fieldWithPath("id").description("Id of the competitionResource"),
            fieldWithPath("sections").description("List of section ids belonging to the competition"),
            fieldWithPath("name").description("name of the competition"),
            fieldWithPath("description").description("description of the competition"),
            fieldWithPath("startDate").description("date the competition opens for submissions"),
            fieldWithPath("endDate").description("date the submissions phase of the competition closes"),
            fieldWithPath("assessmentStartDate").description("date on which the assessments start"),
            fieldWithPath("assessmentEndDate").description("date on which all the assessments should be finished"),
            fieldWithPath("assessorFeedbackDate").description("date on which applicants can expect to receive feedback from the assessments"),
            fieldWithPath("competitionStatus").description("the current status of the competition"),
            fieldWithPath("maxResearchRatio").description("maximum ratio of research participation"),
            fieldWithPath("academicGrantPercentage").description("grant claim percentage for the academics")
    };

    public static final CompetitionResourceBuilder competitionResourceBuilder = newCompetitionResource()
            .withId(1L)
            .withSections(asList(1L, 2L, 3L))
            .withName("competition name")
            .withDescription("competition description")
            .withStartDate(LocalDateTime.now())
            .withEndDate(LocalDateTime.now().plusDays(30))
            .withAssessmentStartDate(LocalDateTime.now().plusDays(32))
            .withAssessmentEndDate(LocalDateTime.now().plusDays(44))
            .withAssessorFeedbackDate(LocalDateTime.now().plusDays(56))
            .withMaxResearchRatio(20)
            .withAcademicGrantClaimPercentage(100);
}
