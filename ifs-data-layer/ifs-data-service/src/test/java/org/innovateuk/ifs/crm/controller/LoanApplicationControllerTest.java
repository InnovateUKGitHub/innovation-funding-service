package org.innovateuk.ifs.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.crm.transactional.SilMessageRecordingService;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionStatus;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.sil.SilPayloadKeyType;
import org.innovateuk.ifs.sil.SilPayloadType;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplicationStatus;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoanApplicationControllerTest extends BaseControllerMockMVCTest<LoanApplicationController> {

    public static final String APPLICATION_PAYLOAD = "{\"questionSetupType\":\"LOAN_BUSINESS_AND_FINANCIAL_INFORMATION\",\"completionStatus\":\"Complete\",\"completionDate\":\"2022-05-12T09:42:07.403057Z\"}";
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
    @Mock
    private ActivityLogService activityLogService;
    @Mock
    private SilMessageRecordingService silMessagingService;

    @Mock
    private ObjectMapper objectMapper;

    @Override
    protected LoanApplicationController supplyControllerUnderTest() {
        return new LoanApplicationController();
    }

    @Before
   public void  setup(){
        when(objectMapper.writer()).thenReturn(new ObjectMapper().writer());
        doNothing().when(silMessagingService).recordSilMessage(SilPayloadType.APPLICATION_UPDATE, SilPayloadKeyType.APPLICATION_ID,"1", APPLICATION_PAYLOAD, null);

    }

    @Test
    public void updateApplicationSuccess() throws Exception {
        long applicationId = 1L;
        UserResource user = newUserResource().withId(1L).build();
        ProcessRoleResource processRole = newProcessRoleResource().withId(1L).build();
        CompetitionResource competition = newCompetitionResource().withId(1L).withFundingType(FundingType.LOAN).build();
        QuestionResource question = newQuestionResource().withId(1L).withQuestionSetupType(LOAN_BUSINESS_AND_FINANCIAL_INFORMATION).build();
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(question.getId(), applicationId);

        SilLoanApplicationStatus silStatus = new SilLoanApplicationStatus();
        silStatus.setCompletionStatus(QuestionStatus.COMPLETE);
        silStatus.setQuestionSetupType(LOAN_BUSINESS_AND_FINANCIAL_INFORMATION);
        silStatus.setCompletionDate(ZonedDateTime.now(ZoneId.of("UTC")));

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId))
                .thenReturn(serviceSuccess(processRole));
        when(competitionService.getCompetitionByApplicationId(applicationId))
                .thenReturn(serviceSuccess(competition));
        when(questionService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), silStatus.getQuestionSetupType()))
                .thenReturn(serviceSuccess(question));
        when(questionService.getQuestionById(ids.questionId)).thenReturn(serviceSuccess(question));
        when(questionStatusService.markAsCompleteNoValidate(ids,processRole.getId())).thenReturn(serviceSuccess());
        when(questionStatusService.markAsComplete(ids, processRole.getId(), silStatus.getCompletionDate()))
                .thenReturn(serviceSuccess(Collections.emptyList()));

        mockMvc.perform(patch("/application-update/{applicationId}", applicationId).contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateApplicationFailWhenIncorrectProcessRoleIdIsPassed() throws Exception {

        long applicationId = 1L;
        UserResource user = newUserResource().withId(1L).build();
        ProcessRoleResource processRole = newProcessRoleResource().withId(5L).build();
        CompetitionResource competition = newCompetitionResource().withId(1L).withFundingType(FundingType.LOAN).build();
        QuestionResource question = newQuestionResource().withId(1L).withQuestionSetupType(LOAN_BUSINESS_AND_FINANCIAL_INFORMATION).build();
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(question.getId(), applicationId);

        SilLoanApplicationStatus silStatus = new SilLoanApplicationStatus();
        silStatus.setCompletionStatus(QuestionStatus.COMPLETE);
        silStatus.setQuestionSetupType(LOAN_BUSINESS_AND_FINANCIAL_INFORMATION);
        silStatus.setCompletionDate(ZonedDateTime.now(ZoneId.of("UTC")));

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);



        when(usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId))
                .thenReturn(serviceSuccess(processRole));
        when(competitionService.getCompetitionByApplicationId(applicationId))
                .thenReturn(serviceSuccess(competition));
        when(questionService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), silStatus.getQuestionSetupType()))
                .thenReturn(serviceSuccess(question));
        when(questionService.getQuestionById(ids.questionId)).thenReturn(serviceSuccess(question));
        when(questionStatusService.markAsCompleteNoValidate(ids, processRole.getId())).thenReturn(serviceFailure(GENERAL_NOT_FOUND));
        when(questionStatusService.markAsComplete(ids, processRole.getId(), silStatus.getCompletionDate()))
                .thenReturn(serviceSuccess(Collections.emptyList()));

        mockMvc.perform(patch("/application-update/{applicationId}", applicationId).contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateApplicationIncomplete() throws Exception {
        long applicationId = 1L;
        UserResource user = newUserResource().withId(1L).build();
        ProcessRoleResource processRole = newProcessRoleResource().withId(1L).build();
        CompetitionResource competition = newCompetitionResource().withId(1L).withFundingType(FundingType.LOAN).build();
        QuestionResource question = newQuestionResource().withId(1L).withQuestionSetupType(LOAN_BUSINESS_AND_FINANCIAL_INFORMATION).build();
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(question.getId(), applicationId);

        SilLoanApplicationStatus silStatus = new SilLoanApplicationStatus();
        silStatus.setCompletionStatus(QuestionStatus.INCOMPLETE);
        silStatus.setQuestionSetupType(LOAN_BUSINESS_AND_FINANCIAL_INFORMATION);
        silStatus.setCompletionDate(ZonedDateTime.now(ZoneId.of("UTC")));

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId))
                .thenReturn(serviceSuccess(processRole));
        when(competitionService.getCompetitionByApplicationId(applicationId))
                .thenReturn(serviceSuccess(competition));
        when(questionService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), silStatus.getQuestionSetupType()))
                .thenReturn(serviceSuccess(question));
        when(questionService.getQuestionById(ids.questionId)).thenReturn(serviceSuccess(question));
        when(questionStatusService.markAsCompleteNoValidate(ids, user.getId())).thenReturn(serviceSuccess());
        when(questionStatusService.markAsInComplete(ids, processRole.getId()))
                .thenReturn(serviceSuccess(Collections.emptyList()));

        mockMvc.perform(patch("/application-update/{applicationId}", applicationId).contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    @Test
    public void updateApplicationUnauthorized() throws Exception {
        long applicationId = 1L;
        SilLoanApplicationStatus silStatus = new SilLoanApplicationStatus();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(null);

        mockMvc.perform(patch("/application-update/{applicationId}", applicationId).contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andExpect(status().isUnauthorized());

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
        silStatus.setCompletionStatus(QuestionStatus.COMPLETE);
        silStatus.setQuestionSetupType(LOAN_BUSINESS_AND_FINANCIAL_INFORMATION);
        silStatus.setCompletionDate(ZonedDateTime.now(ZoneId.of("UTC")));

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(usersRolesService.getProcessRoleByUserIdAndApplicationId(user.getId(), applicationId))
                .thenReturn(serviceSuccess(processRole));
        when(competitionService.getCompetitionByApplicationId(applicationId))
                .thenReturn(serviceSuccess(competition));
        when(questionService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), silStatus.getQuestionSetupType()))
                .thenReturn(serviceSuccess(question));
        when(questionStatusService.markAsComplete(ids, processRole.getId(), silStatus.getCompletionDate()))
                .thenReturn(serviceSuccess(Collections.emptyList()));

        mockMvc.perform(patch("/application-update/{applicationId}", applicationId).contentType(APPLICATION_JSON).content(toJson(silStatus)))
                .andExpect(status().isForbidden());
    }
}
