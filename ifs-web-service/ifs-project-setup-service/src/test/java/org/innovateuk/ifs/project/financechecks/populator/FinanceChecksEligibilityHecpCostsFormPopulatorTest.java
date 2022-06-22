package org.innovateuk.ifs.project.financechecks.populator;

import org.innovateuk.ifs.application.forms.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinanceChecksEligibilityHecpCostsFormPopulatorTest {
    private static final long PROJECT_ID = 1L;
    private static final long ORGANISATION_ID = 2L;

    @InjectMocks
    private FinanceChecksEligibilityHecpCostsFormPopulator populator;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Test
    public void populate() {
        ProjectFinanceResource finance = newProjectFinanceResource().withIndustrialCosts().build();
        when(projectFinanceRestService.getProjectFinance(PROJECT_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        HorizonEuropeGuaranteeCostsForm form = populator.populate(PROJECT_ID, ORGANISATION_ID);

        assertEquals(form.getPersonnel(), finance.getFinanceOrganisationDetails().get(FinanceRowType.PERSONNEL).getTotal().toBigInteger());
        assertEquals(form.getHecpIndirectCosts(), finance.getFinanceOrganisationDetails().get(FinanceRowType.HECP_INDIRECT_COSTS).getTotal().toBigInteger());
        assertEquals(form.getEquipment(), finance.getFinanceOrganisationDetails().get(FinanceRowType.EQUIPMENT).getTotal().toBigInteger());
        assertEquals(form.getOtherGoods(), finance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_GOODS).getTotal().toBigInteger());
        assertEquals(form.getSubcontracting(), finance.getFinanceOrganisationDetails().get(FinanceRowType.SUBCONTRACTING_COSTS).getTotal().toBigInteger());
        assertEquals(form.getTravel(), finance.getFinanceOrganisationDetails().get(FinanceRowType.TRAVEL).getTotal().toBigInteger());
        assertEquals(form.getOther(), finance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_COSTS).getTotal().toBigInteger());
    }
}
