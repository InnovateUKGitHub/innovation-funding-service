package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class ApplicationResearchCategoryPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationResearchCategoryPopulator populator;

    @Test
    public void populate() throws Exception {

        Long questionId = 1L;
        Long applicationId = 2L;
        Long organisationId = 3L;
        String competitionName = "COMP_NAME";
        UserResource user = newUserResource().build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource().build();

        List<ProcessRoleResource> userApplicationRoles = newProcessRoleResource()
                .withApplication(applicationId)
                .withRole(newRoleResource().withName(UserApplicationRole.LEAD_APPLICANT.getRoleName()).build(),
                        newRoleResource().withName(UserApplicationRole.COLLABORATOR.getRoleName()).build(),
                        newRoleResource().withName(UserApplicationRole.LEAD_APPLICANT.getRoleName()).build())
                .withOrganisation(3L, 4L, 5L)
                .withUser(user)
                .build(3);

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(newApplicationResource().withCompetitionName(competitionName).build()));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(3)));
        when(financeService.getApplicationFinanceDetails(user.getId(), applicationId, organisationId)).thenReturn(applicationFinanceResource);
        when(processRoleService.findProcessRolesByApplicationId(applicationId)).thenReturn(userApplicationRoles);
        when(organisationService.getOrganisationById(anyLong())).thenReturn(organisation);

        ResearchCategoryViewModel researchCategoryViewModel = populator.populate(applicationId, questionId, user.getId());

        assertEquals(questionId, researchCategoryViewModel.getQuestionId());
        assertEquals(applicationId, researchCategoryViewModel.getApplicationId());
        assertEquals(researchCategoryViewModel.getCurrentCompetitionName(), competitionName);
        assertEquals(researchCategoryViewModel.getAvailableResearchCategories().size(), 3L);
        assertEquals(researchCategoryViewModel.getHasApplicationFinances(), true);
    }
}