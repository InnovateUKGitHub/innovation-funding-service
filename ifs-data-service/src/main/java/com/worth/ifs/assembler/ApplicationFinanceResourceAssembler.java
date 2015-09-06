package com.worth.ifs.assembler;

import com.worth.ifs.controller.ApplicationFinanceController;
import com.worth.ifs.domain.ApplicationFinance;
import com.worth.ifs.resource.ApplicationFinanceResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ApplicationFinanceResourceAssembler extends ResourceAssemblerSupport<ApplicationFinance, ApplicationFinanceResource> {

    @Autowired
    EntityLinks entityLinks;

    public ApplicationFinanceResourceAssembler() {
        super(ApplicationFinanceController.class, ApplicationFinanceResource.class);
    }

    @Override
    public ApplicationFinanceResource toResource(ApplicationFinance applicationFinance) {
        ApplicationFinanceResource applicationFinanceResource = createResourceWithId(applicationFinance.getId(), applicationFinance);

        applicationFinanceResource.setApplicationId(applicationFinance.getApplication().getId());
        applicationFinanceResource.setOrganisation(applicationFinance.getOrganisation());

        return applicationFinanceResource;
    }

}
