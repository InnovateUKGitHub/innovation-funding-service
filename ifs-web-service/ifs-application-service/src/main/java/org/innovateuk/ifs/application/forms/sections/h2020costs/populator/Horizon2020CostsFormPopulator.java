package org.innovateuk.ifs.application.forms.sections.h2020costs.populator;

import org.innovateuk.ifs.application.forms.sections.h2020costs.form.Horizon2020CostsForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.stereotype.Component;

@Component
public class Horizon2020CostsFormPopulator {

    private final ApplicationFinanceRestService applicationFinanceRestService;

    public Horizon2020CostsFormPopulator(ApplicationFinanceRestService applicationFinanceRestService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    public Horizon2020CostsForm populate(long applicationId, long organisationId) {
        ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();

        Horizon2020CostsForm form = new Horizon2020CostsForm();

        form.setLabour(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR).getTotal());
        form.setOverhead(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR).getTotal());
        form.setMaterial(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR).getTotal());
        form.setCapital(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR).getTotal());
        form.setSubcontracting(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR).getTotal());
        form.setTravel(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR).getTotal());
        form.setOther(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR).getTotal());

        return form;
    }
}
