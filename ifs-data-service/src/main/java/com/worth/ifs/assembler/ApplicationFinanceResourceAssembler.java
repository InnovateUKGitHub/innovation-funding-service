package com.worth.ifs.assembler;

import com.worth.ifs.controller.ApplicationFinanceController;
import com.worth.ifs.domain.ApplicationFinance;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.repository.CostCategoryRepository;
import com.worth.ifs.resource.ApplicationFinanceResource;
import com.worth.ifs.resource.CostCategoryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationFinanceResourceAssembler extends ResourceAssemblerSupport<ApplicationFinance, ApplicationFinanceResource> {

    @Autowired
    EntityLinks entityLinks;

    @Autowired
    CostCategoryRepository costCategoryRepository;

    @Autowired
    CostCategoryResourceAssembler costCategoryResourceAssembler;

    public ApplicationFinanceResourceAssembler() {
        super(ApplicationFinanceController.class, ApplicationFinanceResource.class);
    }

    @Override
    public ApplicationFinanceResource toResource(ApplicationFinance applicationFinance) {
        ApplicationFinanceResource applicationFinanceResource = createResourceWithId(applicationFinance.getId(), applicationFinance);

        applicationFinanceResource.setApplicationId(applicationFinance.getApplication().getId());
        applicationFinanceResource.setOrganisation(applicationFinance.getOrganisation());
        List<CostCategory> costCategories = costCategoryRepository.findByApplicationFinanceId(applicationFinance.getId());
        List<CostCategoryResource> costCategoryResources = costCategoryResourceAssembler.getCostCategories(costCategories);
        applicationFinanceResource.setCostCategoryResources(costCategoryResources);
        return applicationFinanceResource;
    }

}
