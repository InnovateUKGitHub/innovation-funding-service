package org.innovateuk.ifs.application.assign.populator;

import org.innovateuk.ifs.application.assign.model.AssignQuestionViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;

@Component
public class AssignQuestionModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private QuestionRestService questionRestService;

    public AssignQuestionViewModel populateModel(long questionId, long applicationId) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        List<ProcessRoleResource> processRoles = processRoleRestService.findAssignableProcessRoles(application.getId()).getSuccess();
        QuestionResource question = questionRestService.findById(questionId).getSuccess();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("applicationId", singletonList(valueOf(applicationId)));
        params.put("questionId", singletonList(valueOf(questionId)));
        return new AssignQuestionViewModel(application,
                                           processRoles,
                                           question);
    }
}
