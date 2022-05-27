package org.innovateuk.ifs.application.forms.sections.horizoneuropeguaranteecosts.populator;

import org.innovateuk.ifs.application.forms.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.application.forms.sections.hecpcosts.populator.HorizonEuropeGuaranteeCostsFormPopulator;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HorizonEuropeGuaranteeCostsFormPopulatorTest {

    private static final long APPLICATION_ID = 1L;
    private static final long ORGANISATION_ID = 2L;

    @InjectMocks
    private HorizonEuropeGuaranteeCostsFormPopulator populator;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Test
    public void populate() {
        ApplicationFinanceResource finance = newApplicationFinanceResource().withIndustrialCosts().build();
        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        HorizonEuropeGuaranteeCostsForm form = populator.populate(APPLICATION_ID, ORGANISATION_ID);

        assertEquals(form.getPersonnel(), finance.getFinanceOrganisationDetails().get(FinanceRowType.PERSONNEL).getTotal().toBigInteger());
        assertEquals(form.getOverhead(), finance.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS).getTotal().toBigInteger());
        assertEquals(form.getMaterial(), finance.getFinanceOrganisationDetails().get(FinanceRowType.MATERIALS).getTotal().toBigInteger());
        assertEquals(form.getCapital(), finance.getFinanceOrganisationDetails().get(FinanceRowType.CAPITAL_USAGE).getTotal().toBigInteger());
        assertEquals(form.getSubcontracting(), finance.getFinanceOrganisationDetails().get(FinanceRowType.SUBCONTRACTING_COSTS).getTotal().toBigInteger());
        assertEquals(form.getTravel(), finance.getFinanceOrganisationDetails().get(FinanceRowType.TRAVEL).getTotal().toBigInteger());
        assertEquals(form.getOther(), finance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_COSTS).getTotal().toBigInteger());
    }
}
