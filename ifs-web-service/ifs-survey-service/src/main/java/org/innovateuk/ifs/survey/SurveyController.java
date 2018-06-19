package org.innovateuk.ifs.survey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/survey")
@SecuredBySpring(value = "Controller", description = "TODO" , securedType = SurveyController.class)
public class SurveyController {

    private static final Log LOG = LogFactory.getLog(SurveyController.class);

    @GetMapping
    public String view() {
        LOG.debug("Survey View");
        return "survey/survey";
    }
}
