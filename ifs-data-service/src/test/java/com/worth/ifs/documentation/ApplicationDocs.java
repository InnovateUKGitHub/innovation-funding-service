package com.worth.ifs.documentation;

import com.worth.ifs.application.builder.ApplicationResourceBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicationDocs {
    public static final FieldDescriptor[] applicationResourceFields = {
            fieldWithPath("id").description("Id of the application"),
            fieldWithPath("name").description("Name of the application"),
            fieldWithPath("startDate").description("Estimated timescales: project start date"),
            fieldWithPath("submittedDate").description("The date the applicant has submitted this application."),
            fieldWithPath("durationInMonths").description("Estimated timescales: project duration in months"),
            fieldWithPath("applicationStatus").description("ApplicationStatus Id"),
            fieldWithPath("applicationStatusName").description("ApplicationStatus name"),
            fieldWithPath("stateAidAgreed").description("Flag indicating if the user has accepted that they are eligible for state aid"),
            fieldWithPath("competition").description("Competition Id"),
            fieldWithPath("competitionName").description("Competition Name"),
            fieldWithPath("competitionStatus").description("Competition Status"),
            fieldWithPath("assessorFeedbackFileEntry").description("Uploaded Assessor Feedback for the Application"),
            fieldWithPath("completion").description("percentage of completion of the application"),
            fieldWithPath("resubmission").description("indicator that this application is a resubmission"),
            fieldWithPath("previousApplicationNumber").description("the application number of the previous submission"),
            fieldWithPath("previousApplicationTitle").description("the application title of the previous submission")

    };

    public static final ApplicationResourceBuilder applicationResourceBuilder = newApplicationResource()
            .withId(1L)
            .withName("application name")
            .withStartDate(LocalDate.now())
            .withSubmittedDate(LocalDateTime.now())
            .withDuration(1L)
            .withApplicationStatus(ApplicationStatusConstants.OPEN)
            .withCompetition(1L)
            .withCompetitionName("competition name")
            .withCompetitionStatus(CompetitionResource.Status.PROJECT_SETUP)
            .withAssessorFeedbackFileEntry(123L)
            .withCompletion(new BigDecimal(30L));
}
