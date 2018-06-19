package org.innovateuk.ifs.survey;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A controller for users to complete the satisfaction survey after submitting an application.
 */
@Controller
@RequestMapping("/survey")
public class SurveyController {

    @GetMapping("/feedback")
    public String view() {
        return "survey/survey";
    }
}
