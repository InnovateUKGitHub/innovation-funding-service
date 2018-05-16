package org.innovateuk.ifs.fundingdecision.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.fundingdecision.controller.ApplicationFundingDecisionController;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.FundingDecision.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.documentation.FundingNotificationResourceDocs.fundingNotificationResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

public class ApplicationFundingDecisionControllerDocumentation extends BaseControllerMockMVCTest<ApplicationFundingDecisionController> {

    @Mock
    private ApplicationFundingService applicationFundingServiceMock;

    @Mock
    private ApplicationService applicationServiceMock;

    @Mock
    private CompetitionService competitionServiceMock;

    @Mock
    private ProjectService projectServiceMock;

    @Override
    protected ApplicationFundingDecisionController supplyControllerUnderTest() {
        return new ApplicationFundingDecisionController();
    }
    
    @Test
    public void saveFundingDecisionData() throws Exception {
        Long competitionId = 1L;
        Map<Long, FundingDecision> decision = MapFunctions.asMap(1L, FUNDED, 2L, UNFUNDED);

        when(applicationFundingServiceMock.saveFundingDecisionData(competitionId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/applicationfunding/1")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(objectMapper.writeValueAsString(decision)))
        		.andDo( document("applicationfunding/{method-name}"));
    }

    @Test
    public void sendNotifications() throws Exception {
        Map<Long, FundingDecision> decisions = MapFunctions.asMap(1L, FUNDED, 2L, UNFUNDED, 3L, ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);
        ApplicationResource application = newApplicationResource().withCompetition(4L).build();
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionTypeName("Programme")
                .build();

        when(applicationServiceMock.getApplicationById(1L)).thenReturn(serviceSuccess(application));
        when(competitionServiceMock.getCompetitionById(4L)).thenReturn(serviceSuccess(competition));
        when(projectServiceMock.createProjectsFromFundingDecisions(decisions)).thenReturn(serviceSuccess());
        when(applicationFundingServiceMock.notifyApplicantsOfFundingDecisions(notification)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/sendNotifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andDo( document("applicationfunding/{method-name}",
                        requestFields(fundingNotificationResourceFields)
                ));
    }
}
