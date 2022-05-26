package org.innovateuk.ifs.application.forms.sections.hecpcosts.populator;

import org.innovateuk.ifs.application.forms.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.stereotype.Component;

@Component
public class HorizonEuropeGuaranteeCostsFormPopulator {

    private final ApplicationFinanceRestService applicationFinanceRestService;

    public HorizonEuropeGuaranteeCostsFormPopulator(ApplicationFinanceRestService applicationFinanceRestService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    public HorizonEuropeGuaranteeCostsForm populate(long applicationId, long organisationId) {
        ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();

        HorizonEuropeGuaranteeCostsForm form = new HorizonEuropeGuaranteeCostsForm();

        form.setLabour(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR).getTotal().toBigInteger());
        form.setOverhead(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS).getTotal().toBigInteger());
        form.setEquipment(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.EQUIPMENT).getTotal().toBigInteger());
        form.setOtherGoods(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_GOODS).getTotal().toBigInteger());
        form.setSubcontracting(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.SUBCONTRACTING_COSTS).getTotal().toBigInteger());
        form.setTravel(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.TRAVEL).getTotal().toBigInteger());
        form.setOther(applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_COSTS).getTotal().toBigInteger());

        return form;
    }
}