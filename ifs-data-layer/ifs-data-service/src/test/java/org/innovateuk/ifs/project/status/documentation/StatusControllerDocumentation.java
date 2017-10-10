package org.innovateuk.ifs.project.status.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.controller.StatusController;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ProjectDocs.projectStatusResourceFields;
import static org.innovateuk.ifs.documentation.ProjectStatusDocs.competitionProjectsStatusResourceFields;
import static org.innovateuk.ifs.documentation.ProjectTeamStatusDocs.projectTeamStatusResourceFields;
import static org.innovateuk.ifs.project.builder.CompetitionProjectsStatusResourceBuilder.newCompetitionProjectsStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectStatusResourceBuilder.newProjectStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatusControllerDocumentation extends BaseControllerMockMVCTest<StatusController> {

    @Test
    public void getCompetitionStatus() throws Exception {
        Long competitionId = 1L;
        CompetitionProjectsStatusResource competitionProjectsStatusResource = newCompetitionProjectsStatusResource().
                withCompetitionName("ABC").
                withCompetitionNumber(competitionId).
                withProjectStatusResources(newProjectStatusResource().
                        withProjectNumber(1L, 2L, 3L).
                        withProjectTitles("Project ABC", "Project PMQ", "Project XYZ").
                        withProjectLeadOrganisationName("Hive IT").
                        withNumberOfPartners(3, 3, 3).
                        withProjectDetailStatus(COMPLETE, PENDING, COMPLETE).
                        withMonitoringOfficerStatus(PENDING, PENDING, COMPLETE).
                        withBankDetailsStatus(PENDING, NOT_REQUIRED, COMPLETE).
                        withFinanceChecksStatus(PENDING, NOT_STARTED, COMPLETE).
                        withSpendProfileStatus(PENDING, ACTION_REQUIRED, COMPLETE).
                        withOtherDocumentsStatus(PENDING, PENDING, COMPLETE).
                        withGrantOfferLetterStatus(PENDING, PENDING, PENDING).
                        build(3)).
                build();

        when(statusServiceMock.getCompetitionStatus(competitionId)).thenReturn(serviceSuccess(competitionProjectsStatusResource));

        mockMvc.perform(get("/project/competition/{id}", competitionId))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition for which project status details are being requested")
                        ),
                        responseFields(competitionProjectsStatusResourceFields)
                ));
    }

    @Test
    public void getTeamStatus() throws Exception {
        ProjectTeamStatusResource projectTeamStatusResource = buildTeamStatus();
        when(statusServiceMock.getProjectTeamStatus(123L, Optional.empty())).thenReturn(serviceSuccess(projectTeamStatusResource));
        mockMvc.perform(get("/project/{projectId}/team-status", 123L)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(projectTeamStatusResource))).
                andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project that the Project Users are being requested from")
                        ),
                        responseFields(projectTeamStatusResourceFields)));
    }

    @Test
    public void getTeamStatusWithFilterByUserId() throws Exception {
        ProjectTeamStatusResource projectTeamStatusResource = buildTeamStatus();
        when(statusServiceMock.getProjectTeamStatus(123L, Optional.of(456L))).thenReturn(serviceSuccess(projectTeamStatusResource));
        mockMvc.perform(get("/project/{projectId}/team-status", 123L).
                param("filterByUserId", "456")).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(projectTeamStatusResource))).
                andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project that the Project Users are being requested from")
                        ),
                        requestParameters(
                                parameterWithName("filterByUserId").description("Optional id of a user with which the partner organisations " +
                                        "will be filtered by, such that the non-lead partner organisations will only include organisations that " +
                                        "this user is a partner in")
                        ),
                        responseFields(projectTeamStatusResourceFields)));
    }

    @Test
    public void getStatus() throws Exception {
        Long projectId = 1L;
        ProjectStatusResource projectStatusResource = newProjectStatusResource().build();

        when(statusServiceMock.getProjectStatusByProjectId(projectId)).thenReturn(serviceSuccess(projectStatusResource));

        mockMvc.perform(get("/project/{id}/status", projectId))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the project that is being requested")
                        ),
                        responseFields(projectStatusResourceFields)
                ));
    }

    private ProjectTeamStatusResource buildTeamStatus(){
        ProjectPartnerStatusResource projectLeadStatusResource = newProjectPartnerStatusResource().withIsLeadPartner(true).build();
        List<ProjectPartnerStatusResource> partnerStatuses = newProjectPartnerStatusResource().build(3);

        projectLeadStatusResource.setName("Nomensa");
        partnerStatuses.get(0).setName("Acme Corp");
        partnerStatuses.get(1).setName("Hive IT");
        partnerStatuses.get(2).setName("Worth IT Systems");

        projectLeadStatusResource.setSpendProfileStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(0).setSpendProfileStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(1).setSpendProfileStatus(ProjectActivityStates.NOT_STARTED);
        partnerStatuses.get(2).setSpendProfileStatus(PENDING);

        projectLeadStatusResource.setBankDetailsStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(0).setBankDetailsStatus(PENDING);
        partnerStatuses.get(1).setBankDetailsStatus(ProjectActivityStates.NOT_REQUIRED);
        partnerStatuses.get(2).setBankDetailsStatus(ProjectActivityStates.NOT_STARTED);

        projectLeadStatusResource.setOtherDocumentsStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(0).setOtherDocumentsStatus(PENDING);
        partnerStatuses.get(1).setOtherDocumentsStatus(PENDING);
        partnerStatuses.get(2).setOtherDocumentsStatus(ProjectActivityStates.COMPLETE);

        projectLeadStatusResource.setProjectDetailsStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(0).setProjectDetailsStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(1).setProjectDetailsStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(2).setProjectDetailsStatus(ProjectActivityStates.COMPLETE);

        projectLeadStatusResource.setFinanceChecksStatus(PENDING);
        partnerStatuses.get(0).setFinanceChecksStatus(PENDING);
        partnerStatuses.get(1).setFinanceChecksStatus(PENDING);
        partnerStatuses.get(2).setFinanceChecksStatus(PENDING);

        projectLeadStatusResource.setMonitoringOfficerStatus(PENDING);
        partnerStatuses.get(0).setMonitoringOfficerStatus(ProjectActivityStates.COMPLETE);
        partnerStatuses.get(1).setMonitoringOfficerStatus(PENDING);
        partnerStatuses.get(2).setMonitoringOfficerStatus(ProjectActivityStates.COMPLETE);

        projectLeadStatusResource.setGrantOfferLetterStatus(PENDING);
        partnerStatuses.get(0).setGrantOfferLetterStatus(PENDING);
        partnerStatuses.get(1).setGrantOfferLetterStatus(PENDING);
        partnerStatuses.get(2).setGrantOfferLetterStatus(PENDING);

        return newProjectTeamStatusResource().withPartnerStatuses(partnerStatuses).build();
    }

    @Override
    protected StatusController supplyControllerUnderTest() {
        return new StatusController();
    }
}
