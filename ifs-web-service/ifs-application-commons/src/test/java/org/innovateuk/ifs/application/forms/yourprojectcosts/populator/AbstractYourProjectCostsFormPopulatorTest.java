package org.innovateuk.ifs.application.forms.yourprojectcosts.populator;

import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.finance.builder.BaseFinanceResourceBuilder;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;

@RunWith(MockitoJUnitRunner.class)
public class AbstractYourProjectCostsFormPopulatorTest {

    @InjectMocks
    private AbstractYourProjectCostsFormPopulator target = new AbstractYourProjectCostsFormPopulator() {
        @Override
        protected BaseFinanceResource getFinanceResource(long targetId, long organisationId) {
            return newApplicationFinanceResource().withFinanceOrganisationDetails(BaseFinanceResourceBuilder.INDUSTRIAL_FINANCES).build();
        }

        @Override
        protected boolean shouldAddEmptyRow() {
            return true;
        }
    };

    @Test
    public void populate() {
        YourProjectCostsForm form = new YourProjectCostsForm();

        target.populateForm(form, 1L, 2L);

        Assert.assertEquals((Integer) 250, form.getLabour().getWorkingDaysPerYear());
        Assert.assertEquals(3, form.getLabour().getRows().size());

        Assert.assertEquals((Long) 1L, form.getOverhead().getCostId());
        Assert.assertEquals(OverheadRateType.TOTAL, form.getOverhead().getRateType());
        Assert.assertEquals("filename", form.getOverhead().getFilename());
        Assert.assertEquals((Integer) 1000, form.getOverhead().getTotalSpreadsheet());

        Assert.assertEquals(3, form.getMaterialRows().size());
        Assert.assertEquals(3, form.getCapitalUsageRows().size());
        Assert.assertEquals(3, form.getSubcontractingRows().size());
        Assert.assertEquals(3, form.getTravelRows().size());
        Assert.assertEquals(3, form.getOtherRows().size());
    }

}
