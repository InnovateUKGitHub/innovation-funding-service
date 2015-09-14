package com.worth.ifs.application;

import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ResponseService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.application.service.UserService;
import com.worth.ifs.security.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public abstract class AbstractApplicationController {

    @Autowired
    protected ResponseService responseService;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected SectionService sectionService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserAuthenticationService userAuthenticationService;

    @Autowired
    protected CostService costService;


    protected void assignQuestion(HttpServletRequest request, Long applicationId, Long userId) {
        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey("assign_question")){
            String assign = request.getParameter("assign_question");
            Long questionId = Long.valueOf(assign.split("_")[0]);
            Long assigneeId = Long.valueOf(assign.split("_")[1]);

            responseService.assignQuestion(applicationId, questionId, userId, assigneeId);
        }
    }

}
