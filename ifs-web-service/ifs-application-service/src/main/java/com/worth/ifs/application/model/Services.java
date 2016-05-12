package com.worth.ifs.application.model;

import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Services {
    @Autowired
    ApplicationService applicationService;
    @Autowired
    CompetitionService competitionService;
    @Autowired
    ProcessRoleService processRoleService;
    @Autowired
    OrganisationService organisationService;
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    InviteRestService inviteRestService;
    @Autowired
    SectionService sectionService;
}
