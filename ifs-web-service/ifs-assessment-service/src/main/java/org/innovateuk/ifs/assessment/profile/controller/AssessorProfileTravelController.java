package org.innovateuk.ifs.assessment.profile.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to manage the Assessor Profile Travel and subsistence rates page
 */
@Controller
@RequestMapping("/profile/travel")
@PreAuthorize("hasAuthority('assessor')")
public class AssessorProfileTravelController {

    @GetMapping
    public String getTravelAndSubsistence() {
        return "profile/travel";
    }
}
