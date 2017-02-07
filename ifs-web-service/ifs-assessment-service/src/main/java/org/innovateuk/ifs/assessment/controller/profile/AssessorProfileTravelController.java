package org.innovateuk.ifs.assessment.controller.profile;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to manage the Assessor Profile Travel and subsistence rates page
 */
@Controller
@RequestMapping("/profile/travel")
@PreAuthorize("hasAuthority('assessor')")
public class AssessorProfileTravelController {

    @RequestMapping(method = RequestMethod.GET)
    public String getTravelAndSubsistence() {
        return "profile/travel";
    }
}
