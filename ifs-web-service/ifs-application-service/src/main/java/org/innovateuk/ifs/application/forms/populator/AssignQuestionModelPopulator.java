package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.application.forms.viewmodel.AssignQuestionViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssignQuestionModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private QuestionRestService questionRestService;

    public AssignQuestionViewModel populateModel(long questionId, long applicationId) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(application.getId()).getSuccess();
        QuestionResource question = questionRestService.findById(questionId).getSuccess();

        return new AssignQuestionViewModel(application,
                                           processRoles,
                                           question);
    }
}
