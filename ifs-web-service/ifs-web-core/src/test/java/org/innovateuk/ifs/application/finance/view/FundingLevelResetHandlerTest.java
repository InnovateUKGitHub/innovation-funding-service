package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

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

    @Test
    public void tetsResetFundingAndMarkAsIncomplete() {

        ApplicationFinanceResource applicationFinanceResource = ApplicationFinanceResourceBuilder.newApplicationFinanceResource().withApplication(1L).build();
        UserResource user = UserResourceBuilder.newUserResource().withId(2L).build();
        List<ProcessRoleResource> processRoles = ProcessRoleResourceBuilder.newProcessRoleResource().withApplication(1L).withUser(user).build(2);

        when(processRoleService.getByApplicationId(1L)).thenReturn(processRoles);

        target.resetFundingAndMarkAsIncomplete(applicationFinanceResource, 1L, 2L);
    }

    @Test
    public void resetFundingLevelAndMarkAsIncompleteForAllCollaborators() {

        target.resetFundingLevelAndMarkAsIncompleteForAllCollaborators(1l, 1L);
    }

}