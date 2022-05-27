package org.innovateuk.ifs.project.financechecks.populator;

import org.innovateuk.ifs.application.forms.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.stereotype.Component;

@Component
public class FinanceChecksEligibilityHecpCostsFormPopulator {

    private final ProjectFinanceRestService projectFinanceRestService;

    public FinanceChecksEligibilityHecpCostsFormPopulator(ProjectFinanceRestService projectFinanceRestService) {
        this.projectFinanceRestService = projectFinanceRestService;
    }

    public HorizonEuropeGuaranteeCostsForm populate(long projectId, long organisationId) {
        ProjectFinanceResource projectFinance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();

        HorizonEuropeGuaranteeCostsForm form = new HorizonEuropeGuaranteeCostsForm();

        form.setPersonnel(projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.PERSONNEL).getTotal().toBigInteger());
        form.setHecpIndirectCosts(projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.HECP_INDIRECT_COSTS).getTotal().toBigInteger());
        form.setEquipment(projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.EQUIPMENT).getTotal().toBigInteger());
        form.setOtherGoods(projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_GOODS).getTotal().toBigInteger());
        form.setSubcontracting(projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.SUBCONTRACTING_COSTS).getTotal().toBigInteger());
        form.setTravel(projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.TRAVEL).getTotal().toBigInteger());
        form.setOther(projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_COSTS).getTotal().toBigInteger());

        return form;
    }
}