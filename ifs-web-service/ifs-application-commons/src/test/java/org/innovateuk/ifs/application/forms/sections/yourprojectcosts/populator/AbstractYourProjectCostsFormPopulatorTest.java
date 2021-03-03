package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AbstractYourProjectCostsFormPopulatorTest {

    @Mock
    private OverheadFileRestService overheadFileRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @InjectMocks
    private AbstractYourProjectCostsFormPopulator target = new AbstractYourProjectCostsFormPopulator() {
        @Override
        protected BaseFinanceResource getFinanceResource(long targetId, long organisationId) {
            return newApplicationFinanceResource().withIndustrialCosts().build();
        }

        @Override
        protected boolean shouldAddEmptyRow() {
            return true;
        }

        @Override
        protected Optional<FileEntryResource> overheadFile(long costId) {
            return overheadFileRestService.getOverheadFileDetails(costId).getOptionalSuccessObject();
        }
    };

    @Test
    public void populate() {
        when(overheadFileRestService.getOverheadFileDetails(any())).thenReturn(RestResult.restSuccess(newFileEntryResource().withName("filename").build()));

        OrganisationResource organisationResource = newOrganisationResource()
                .withId(2L)
                .withOrganisationType(1L)
                .build();
        when(organisationRestService.getOrganisationById(organisationResource.getId())).thenReturn(restSuccess(organisationResource));

        YourProjectCostsForm form = target.populateForm( 1L, organisationResource.getId());

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
        Assert.assertNotNull(form.getJustificationForm());
        Assert.assertEquals(3, form.getProcurementOverheadRows().size());
        Assert.assertEquals(false, form.getVatForm().getRegistered());
        Assert.assertEquals(2, form.getAssociateSalaryCostRows().size());
        Assert.assertEquals(5, (int)form.getAssociateSalaryCostRows().get("1").getDuration());
        Assert.assertEquals(5, (int)form.getAssociateDevelopmentCostRows().get("1").getDuration());
        Assert.assertEquals(10, (int)form.getAssociateSalaryCostRows().get("2").getDuration());
        Assert.assertEquals(10, (int)form.getAssociateDevelopmentCostRows().get("2").getDuration());
        Assert.assertEquals(2, form.getAssociateDevelopmentCostRows().size());
        Assert.assertEquals(3, form.getConsumableCostRows().size());
        Assert.assertEquals(2, form.getKnowledgeBaseCostRows().size());
        Assert.assertEquals(3, form.getAssociateSupportCostRows().size());
        Assert.assertEquals(3, form.getEstateCostRows().size());
        Assert.assertEquals(3, form.getTravelRows().size());
        Assert.assertEquals(3, form.getKtpTravelCostRows().size());
        Assert.assertNotNull(form.getAdditionalCompanyCostForm().getAssociateSalary());
    }
}
