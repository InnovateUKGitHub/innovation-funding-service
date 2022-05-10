package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.LabourRowForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private AbstractYourProjectCostsFormPopulator targetForThirdPartyOfgem = new AbstractYourProjectCostsFormPopulator() {
        @Override
        protected BaseFinanceResource getFinanceResource(long targetId, long organisationId) {
            return newApplicationFinanceResource().withIndustrialCostsForThirdPartyOfgem().build();
        }

        @Override
        protected boolean shouldAddEmptyRow() {
            return true;
        }

        @Override
        protected Optional<FileEntryResource> overheadFile(long costId) {
            return Optional.empty();
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

        YourProjectCostsForm form = target.populateForm( 1L, organisationResource.getId(), false);

        assertEquals((Integer) 250, form.getLabour().getWorkingDaysPerYear());
        assertEquals(3, form.getLabour().getRows().size());

        assertEquals((Long) 1L, form.getOverhead().getCostId());
        assertEquals(OverheadRateType.TOTAL, form.getOverhead().getRateType());
        assertEquals("filename", form.getOverhead().getFilename());
        assertEquals((Integer) 1000, form.getOverhead().getTotalSpreadsheet());

        assertEquals(3, form.getMaterialRows().size());
        assertEquals(3, form.getCapitalUsageRows().size());
        assertEquals(3, form.getSubcontractingRows().size());
        assertEquals(3, form.getTravelRows().size());
        assertEquals(3, form.getOtherRows().size());
        assertNotNull(form.getJustificationForm());
        assertEquals(3, form.getProcurementOverheadRows().size());
        assertEquals(false, form.getVatForm().getRegistered());
        assertEquals(2, form.getAssociateSalaryCostRows().size());
        assertEquals(5, (int)form.getAssociateSalaryCostRows().get("1").getDuration());
        assertEquals(5, (int)form.getAssociateDevelopmentCostRows().get("1").getDuration());
        assertEquals(10, (int)form.getAssociateSalaryCostRows().get("2").getDuration());
        assertEquals(10, (int)form.getAssociateDevelopmentCostRows().get("2").getDuration());
        assertEquals(2, form.getAssociateDevelopmentCostRows().size());
        assertEquals(3, form.getConsumableCostRows().size());
        assertEquals(2, form.getKnowledgeBaseCostRows().size());
        assertEquals(3, form.getAssociateSupportCostRows().size());
        assertEquals(3, form.getEstateCostRows().size());
        assertEquals(3, form.getTravelRows().size());
        assertEquals(3, form.getKtpTravelCostRows().size());
        assertNotNull(form.getAdditionalCompanyCostForm().getAssociateSalary());
        assertFalse(form.isThirdPartyOfgem());
    }

    @Test
    public void populate_for_thirdPartyOfgem() {
        when(overheadFileRestService.getOverheadFileDetails(any())).thenReturn(RestResult.restSuccess(newFileEntryResource().withName("filename").build()));

        OrganisationResource organisationResource = newOrganisationResource()
                .withId(2L)
                .withOrganisationType(1L)
                .build();
        when(organisationRestService.getOrganisationById(organisationResource.getId())).thenReturn(restSuccess(organisationResource));

        YourProjectCostsForm form = targetForThirdPartyOfgem.populateForm( 1L, organisationResource.getId(), true);

        assertEquals(0, form.getLabour().getWorkingDaysPerYear().intValue());
        assertEquals(2, form.getLabour().getRows().size());

        LabourRowForm labourRowForm = form.getLabour().getRows().get("1");

        assertNotNull(labourRowForm);
        assertEquals(BigDecimal.TEN,labourRowForm.getRate());
        assertEquals(100,labourRowForm.getDays().intValue());
        assertEquals(new BigDecimal(1000),labourRowForm.getTotal());
        assertTrue(labourRowForm.getThirdPartyOfgem());

        assertTrue(form.isThirdPartyOfgem());
    }
}
