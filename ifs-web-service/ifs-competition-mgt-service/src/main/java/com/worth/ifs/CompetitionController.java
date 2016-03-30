package com.worth.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CompetitionController {
    private static final Log LOG = LogFactory.getLog(CompetitionController.class);

    @RequestMapping("/competition/{competitionId}")
    public String displayCompetitionInfo(){
        LOG.warn("Show competition info ");
        return "test-comp-mgt";
    }
}
