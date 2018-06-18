package org.innovateuk.ifs.application.areas.populator;

import org.innovateuk.ifs.application.areas.form.ResearchCategoryForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.springframework.stereotype.Component;

@Component
public class ApplicationResearchCategoryFormPopulator {

    public ResearchCategoryForm populate(ApplicationResource applicationResource,
                                         ResearchCategoryForm researchCategoryForm) {
        if (applicationResource.getResearchCategory() != null) {
            researchCategoryForm.setResearchCategory(applicationResource.getResearchCategory().getId());
        }

        return researchCategoryForm;
    }
}
