package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competitionsetup.utils.CompetitionSpecialSectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Provides an API for AJAX calls to retrieve {@link InnovationAreaResource}s for a sector.
 */
@Controller
@RequestMapping("/competition/setup")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = InnovationAreaAjaxController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class InnovationAreaAjaxController {
    @Autowired
    private CategoryRestService categoryRestService;

    /* AJAX Function */
    @GetMapping("/getInnovationArea/{innovationSectorId}")
    @ResponseBody
    public List<InnovationAreaResource> getInnovationAreas(@PathVariable("innovationSectorId") Long innovationSectorId) {

        if (CompetitionSpecialSectors.isOpenSector().test(innovationSectorId)) {
            List<InnovationAreaResource> returningList = categoryRestService.getInnovationAreas().getSuccessObjectOrThrowException();
            returningList.add(0, createAllInnovationArea());

            return returningList;
        } else {
            return categoryRestService.getInnovationAreasBySector(innovationSectorId).getSuccessObjectOrThrowException();
        }
    }

    private InnovationAreaResource createAllInnovationArea() {
        InnovationAreaResource innovationArea = new InnovationAreaResource();
        innovationArea.setId(-1L);
        innovationArea.setName("All");

        return innovationArea;
    }
}
