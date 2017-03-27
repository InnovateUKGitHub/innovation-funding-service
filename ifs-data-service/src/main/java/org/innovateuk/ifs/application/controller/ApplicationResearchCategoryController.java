package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationResearchCategoryService;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that exposes functionality for linking an {@link Application} to an {@link ResearchCategory}.
 */
@RestController
@RequestMapping("/applicationResearchCategory")
public class ApplicationResearchCategoryController {

    @Autowired
    private ApplicationResearchCategoryService applicationResearchCategoryService;

    @Autowired
    private ApplicationMapper applicationMapper;

    @PostMapping("/researchCategory/{applicationId}")
    public RestResult<ApplicationResource> setResearchCategory(@PathVariable("applicationId") final Long applicationId, @RequestBody Long researchCategoryId) {
        return applicationResearchCategoryService.setResearchCategory(applicationId, researchCategoryId).toGetResponse();
    }
}
