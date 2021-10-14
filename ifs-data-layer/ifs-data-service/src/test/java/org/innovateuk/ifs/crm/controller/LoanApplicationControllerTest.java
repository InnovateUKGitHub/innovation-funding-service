package org.innovateuk.ifs.crm.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplicationStatus;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class LoanApplicationControllerTest extends BaseControllerMockMVCTest<LoanApplicationController> {

    @Mock
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private UsersRolesService usersRolesService;
    @Mock
    private CompetitionService competitionService;
    @Mock
    private QuestionService questionService;
    @Mock
    private QuestionStatusService questionStatusService;

    @Override
    protected LoanApplicationController supplyControllerUnderTest() {
        return new LoanApplicationController();
    }

    @Test
    public void updateApplicationSuccess() throws Exception {
        long applicationId = 1L;
        UserResource user = newUserResource().withId(1L).build();
        ProcessRoleResource processRole = newProcessRoleResource().withId(1L).build();
        CompetitionResource competition = newCompetitionResource().withId(1L).withFundingType(FundingType.LOAN).build();
        QuestionResource question = newQuestionResource().withId(1L).build();
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(question.getId(), applicationId);

        SilLoanApplicationStatus silStatus = new SilLoanApplicationStatus();
        silStatus.setApplicationId(applicationId);
        silStatus.setCompletionStatus("Complete");
        silStatus.setQuestionSetupType(QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION);
        silStatus.setCompletionDate(ZonedDateTime.now(ZoneId.of("GMT")));
        silStatus.setCompletedBy("1");

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId))
                .thenReturn(ServiceResult.serviceSuccess(processRole));
        when(competitionService.getCompetitionByApplicationId(applicationId))
                .thenReturn(ServiceResult.serviceSuccess(competition));
        when(questionService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), silStatus.getQuestionSetupType()))
                .thenReturn(ServiceResult.serviceSuccess(question));
        when(questionStatusService.markAsComplete(ids, processRole.getId(), silStatus.getCompletionDate()))
                .thenReturn(ServiceResult.serviceSuccess(Collections.emptyList()));

        mockMvc.perform(post("/application-update/{applicationId}", applicationId).contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateApplicationUnauthorized() throws Exception {
        long applicationId = 1L;
        SilLoanApplicationStatus silStatus = new SilLoanApplicationStatus();
        silStatus.setApplicationId(applicationId);

        mockMvc.perform(post("/application-update/{applicationId}", applicationId).contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void updateApplicationIncompleteData() throws Exception {
        long applicationId = 1L;
        UserResource user = newUserResource().withId(1L).build();
        ProcessRoleResource processRole = newProcessRoleResource().withId(1L).build();
        SilLoanApplicationStatus silStatus = new SilLoanApplicationStatus();
        silStatus.setApplicationId(applicationId);

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId))
                .thenReturn(ServiceResult.serviceSuccess(processRole));

        mockMvc.perform(post("/application-update/{applicationId}", applicationId).contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void updateApplicationNotLoan() throws Exception {
        long applicationId = 1L;
        UserResource user = newUserResource().withId(1L).build();
        ProcessRoleResource processRole = newProcessRoleResource().withId(1L).build();
        CompetitionResource competition = newCompetitionResource().withId(1L).build();
        QuestionResource question = newQuestionResource().withId(1L).build();
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(question.getId(), applicationId);

        SilLoanApplicationStatus silStatus = new SilLoanApplicationStatus();
        silStatus.setApplicationId(applicationId);
        silStatus.setCompletionStatus("Complete");
        silStatus.setQuestionSetupType(QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION);
        silStatus.setCompletionDate(ZonedDateTime.now(ZoneId.of("GMT")));
        silStatus.setCompletedBy("1");

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId))
                .thenReturn(ServiceResult.serviceSuccess(processRole));
        when(competitionService.getCompetitionByApplicationId(applicationId))
                .thenReturn(ServiceResult.serviceSuccess(competition));
        when(questionService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), silStatus.getQuestionSetupType()))
                .thenReturn(ServiceResult.serviceSuccess(question));
        when(questionStatusService.markAsComplete(ids, processRole.getId(), silStatus.getCompletionDate()))
                .thenReturn(ServiceResult.serviceSuccess(Collections.emptyList()));

        mockMvc.perform(post("/application-update/{applicationId}", applicationId).contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andExpect(status().isForbidden());
    }
}
