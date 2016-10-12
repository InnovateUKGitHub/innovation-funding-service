package com.worth.ifs.assessment.model.profile;

import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileTermsViewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.ContractResource;
import com.worth.ifs.user.service.ContractRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Terms view.
 */
@Component
public class AssessorProfileTermsModelPopulator {
    @Autowired
    private ContractRestService contractRestService;

    public AssessorProfileTermsViewModel populateModel() {
        AssessorProfileTermsViewModel model = new AssessorProfileTermsViewModel();
        addContractHTML(model);
        return model;
    }

    private void addContractHTML(AssessorProfileTermsViewModel model) {
        ContractResource currentContract = contractRestService.getCurrentContract().getSuccessObjectOrThrowException();
        model.setTerms(currentContract.getText());
    }
}
