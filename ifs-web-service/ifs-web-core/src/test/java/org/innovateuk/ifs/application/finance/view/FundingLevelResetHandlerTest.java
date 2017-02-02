package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.application.builder.SectionResourceBuilder;
import org.innovateuk.ifs.application.finance.service.FinanceRowService;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.when;

/**
 * Tests for FundingLevelResetHandler
 */
@RunWith(MockitoJUnitRunner.class)
public class FundingLevelResetHandlerTest {

    @InjectMocks
    private FundingLevelResetHandler target;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private SectionService sectionService;

    @Mock
    private QuestionService questionService;

    @Mock
    private FinanceRowService financeRowService;

    @Mock
    private FinanceService financeService;

    @Test
    public void tetsResetFundingAndMarkAsIncomplete() {

        Long applicationId = 1L;
        Long competitionId = 3L;
        Long userId = 2L;

        ApplicationFinanceResource applicationFinanceResource = ApplicationFinanceResourceBuilder.newApplicationFinanceResource().withApplication(applicationId).build();
        UserResource user = UserResourceBuilder.newUserResource().withId(userId).build();
        List<ProcessRoleResource> processRoles = ProcessRoleResourceBuilder.newProcessRoleResource().withApplication(applicationId).withUser(user).build(1);
        List<SectionResource> sectionResources = SectionResourceBuilder.newSectionResource().build(1);
        QuestionResource questionResource = QuestionResourceBuilder.newQuestionResource().build();

        when(processRoleService.getByApplicationId(1L)).thenReturn(processRoles);
        when(sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES)).thenReturn(sectionResources);
        when(questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.FINANCE)).thenReturn(RestResult.restSuccess(questionResource));

        target.resetFundingAndMarkAsIncomplete(applicationFinanceResource, competitionId, userId);

        Mockito.inOrder(sectionService).verify(sectionService, calls(1)).markAsInComplete(sectionResources.get(0).getId(),
                applicationId, processRoles.get(0).getId());
    }

    @Test
    public void resetFundingLevelAndMarkAsIncompleteForAllCollaborators() {

        Long applicationId = 1L;
        Long competitionId = 3L;
        Long userId = 2L;

        UserResource user = UserResourceBuilder.newUserResource().withId(userId).build();
        List<ProcessRoleResource> processRoles = ProcessRoleResourceBuilder.newProcessRoleResource().withApplication(applicationId).withUser(user).build(2);
        List<SectionResource> sectionResources = SectionResourceBuilder.newSectionResource().build(1);
        QuestionResource questionResource = QuestionResourceBuilder.newQuestionResource().build();
        List<ApplicationFinanceResource> applicationFinanceResources = ApplicationFinanceResourceBuilder.newApplicationFinanceResource().build(3);

        when(processRoleService.getByApplicationId(1L)).thenReturn(processRoles);
        when(sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES)).thenReturn(sectionResources);
        when(questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.FINANCE)).thenReturn(RestResult.restSuccess(questionResource));
        when(financeService.getApplicationFinanceDetails(applicationId)).thenReturn(applicationFinanceResources);

        target.resetFundingLevelAndMarkAsIncompleteForAllCollaborators(competitionId, applicationId);

        InOrder inOrder = Mockito.inOrder(sectionService, financeService);

        inOrder.verify(sectionService, calls(1)).markAsInComplete(sectionResources.get(0).getId(),applicationId, processRoles.get(0).getId());
        inOrder.verify(sectionService, calls(1)).markAsInComplete(sectionResources.get(0).getId(),applicationId, processRoles.get(1).getId());

        inOrder.verify(financeService, calls(1)).getApplicationFinanceDetails(applicationId);
    }
}