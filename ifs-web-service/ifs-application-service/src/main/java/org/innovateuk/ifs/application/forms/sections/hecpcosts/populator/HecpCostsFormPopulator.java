package org.innovateuk.ifs.application.forms.sections.h2020costs.populator;

import org.innovateuk.ifs.application.forms.sections.h2020costs.form.Horizon2020CostsForm;
import org.innovateuk.ifs.application.forms.sections.h2020costs.form.HorizonEuropeCostsForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.stereotype.Component;

@Component
public class HecpCostsFormPopulator {

    private final ApplicationFinanceRestService applicationFinanceRestService;

    public HecpCostsFormPopulator(ApplicationFinanceRestService applicationFinanceRestService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    public HorizonEuropeCostsForm populate(long applicationId, long organisationId) {
        ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();

        HorizonEuropeCostsForm form = new HorizonEuropeCostsForm();

        form.setLabour(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR).getTotal().toBigInteger());
        form.setOverhead(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS).getTotal().toBigInteger());
        form.setMaterial(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.MATERIALS).getTotal().toBigInteger());
        form.setCapital(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.CAPITAL_USAGE).getTotal().toBigInteger());
        form.setSubcontracting(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.SUBCONTRACTING_COSTS).getTotal().toBigInteger());
        form.setTravel(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.TRAVEL).getTotal().toBigInteger());
        form.setOther(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_COSTS).getTotal().toBigInteger());

        return form;
    }
}