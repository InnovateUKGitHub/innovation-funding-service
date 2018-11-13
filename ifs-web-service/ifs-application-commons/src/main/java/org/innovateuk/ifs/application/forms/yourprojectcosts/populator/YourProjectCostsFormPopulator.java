package org.innovateuk.ifs.application.forms.yourprojectcosts.populator;

import org.innovateuk.ifs.application.forms.yourprojectcosts.form.*;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm.EMPTY_ROW_ID;
import static org.innovateuk.ifs.util.CollectionFunctions.toLinkedMap;

@Component
public class YourProjectCostsFormPopulator {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private ApplicationService applicationService;

    public void populateForm(YourProjectCostsForm form, long applicationId, UserResource user, Optional<Long> organisationId) {
        OrganisationResource organisation = organisationId.map(organisationRestService::getOrganisationById).map(RestResult::getSuccess)
                .orElseGet(() -> organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess());
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();

        form.setWorkingDaysPerYear(workingDaysPerYear(finance));
        form.setOverhead(overhead(finance));

        form.setLabourCosts(labourCosts(finance));
        form.setCapitalUsageRows(capitalUsageRows(finance));
        form.setMaterialRows(materialRows(finance));
        form.setOtherRows(otherRows(finance));
        form.setSubcontractingRows(subcontractingRows(finance));
        form.setTravelRows(travelRows(finance));

    }

    private OverheadForm overhead(ApplicationFinanceResource finance) {
        OverheadCostCategory costCategory = (OverheadCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS);
        Overhead overhead = costCategory.getCosts().stream().findFirst().map(Overhead.class::cast).orElseGet(Overhead::new);
        return new OverheadForm(overhead);
    }

    private Integer workingDaysPerYear(ApplicationFinanceResource finance) {
        LabourCostCategory costCategory = (LabourCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR);
        return costCategory.getWorkingDaysPerYear();
    }

    private Map<String, LabourRowForm> labourCosts(ApplicationFinanceResource finance) {
        LabourCostCategory costCategory = (LabourCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR);
        //Calculate totals using working days
        costCategory.calculateTotal();
        Map<String, LabourRowForm> rows = costCategory.getCosts().stream()
                .map(LabourCost.class::cast)
                .map(LabourRowForm::new)
                .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));

        rows.put(EMPTY_ROW_ID, new LabourRowForm());
        return rows;
    }

    private Map<String, MaterialRowForm> materialRows(ApplicationFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.MATERIALS);
        Map<String, MaterialRowForm> rows = costCategory.getCosts().stream()
                .map(Materials.class::cast)
                .map(MaterialRowForm::new)
                .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
        rows.put(EMPTY_ROW_ID, new MaterialRowForm());
        return rows;
    }

    private Map<String, CapitalUsageRowForm> capitalUsageRows(ApplicationFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.CAPITAL_USAGE);
        Map<String, CapitalUsageRowForm> rows = costCategory.getCosts().stream()
                .map(CapitalUsage.class::cast)
                .map(CapitalUsageRowForm::new)
                .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
        rows.put(EMPTY_ROW_ID, new CapitalUsageRowForm());
        return rows;
    }

    private Map<String, OtherCostRowForm> otherRows(ApplicationFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_COSTS);
        Map<String, OtherCostRowForm> rows = costCategory.getCosts().stream()
                .map(OtherCost.class::cast)
                .map(OtherCostRowForm::new)
                .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
        rows.put(EMPTY_ROW_ID, new OtherCostRowForm());
        return rows;
    }

    private Map<String, SubcontractingRowForm> subcontractingRows(ApplicationFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.SUBCONTRACTING_COSTS);
        Map<String, SubcontractingRowForm> rows = costCategory.getCosts().stream()
                .map(SubContractingCost.class::cast)
                .map(SubcontractingRowForm::new)
                .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
        rows.put(EMPTY_ROW_ID, new SubcontractingRowForm());
        return rows;
    }

    private Map<String, TravelRowForm> travelRows(ApplicationFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.TRAVEL);
        Map<String, TravelRowForm> rows = costCategory.getCosts().stream()
                .map(TravelCost.class::cast)
                .map(TravelRowForm::new)
                .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
        rows.put(EMPTY_ROW_ID, new TravelRowForm());
        return rows;
    }

}
