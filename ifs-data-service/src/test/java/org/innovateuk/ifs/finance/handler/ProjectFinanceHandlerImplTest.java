package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Tests for ProjectFinanceHandler methods (duh!)
 */
public class ProjectFinanceHandlerImplTest extends BaseUnitTestMocksTest {
    @InjectMocks
    ProjectFinanceHandler handler = new ProjectFinanceHandlerImpl();

    @Mock
    private OrganisationFinanceDefaultHandler organisationFinanceDefaultHandlerMock;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test(){
        Long projectId = 1L;
        Long organisationId = 2L;

        Organisation organisation = newOrganisation().withId(organisationId).withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        ProjectFinanceResourceId projectFinanceResourceId = new ProjectFinanceResourceId(projectId, organisationId);
        ProjectFinance projectFinance = newProjectFinance().withOrganisation(organisation).build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withOrganisation(organisationId).build();

        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectFinanceResourceId.getProjectId(), projectFinanceResourceId.getOrganisationId())).thenReturn(projectFinance);
        when(projectFinanceMapperMock.mapToResource(projectFinance)).thenReturn(projectFinanceResource);
        when(organisationRepositoryMock.findOne(organisationId)).thenReturn(organisation);
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(anyLong())).thenReturn(organisationFinanceDefaultHandlerMock);

        ServiceResult<ProjectFinanceResource> result = handler.getProjectOrganisationFinances(projectFinanceResourceId);
        assertTrue(result.isSuccess());
        assertEquals(result.getSuccessObject(), projectFinanceResource);
    }
}
