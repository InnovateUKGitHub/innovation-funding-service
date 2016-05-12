package com.worth.ifs.application.model;

import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.QuestionStatusRestService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Services {
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private ProcessRoleService processRoleService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private FormInputService formInputService;
    @Autowired
    private FormInputResponseService formInputResponseService;
    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    public ApplicationService getApplicationService() {
        return applicationService;
    }

    public CompetitionService getCompetitionService() {
        return competitionService;
    }

    public ProcessRoleService getProcessRoleService() {
        return processRoleService;
    }

    public OrganisationService getOrganisationService() {
        return organisationService;
    }

    public UserService getUserService() {
        return userService;
    }

    public QuestionService getQuestionService() {
        return questionService;
    }

    public InviteRestService getInviteRestService() {
        return inviteRestService;
    }

    public SectionService getSectionService() {
        return sectionService;
    }

    public FormInputService getFormInputService() {
        return formInputService;
    }

    public FormInputResponseService getFormInputResponseService() {
        return formInputResponseService;
    }

    public QuestionStatusRestService getQuestionStatusRestService() {
        return questionStatusRestService;
    }
}
