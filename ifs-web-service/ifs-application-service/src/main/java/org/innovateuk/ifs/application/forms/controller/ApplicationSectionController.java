package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.application.ApplicationUrlHelper;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.SECTION_URL;

/**
 * This controller will handle all submit requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
public class ApplicationSectionController {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ApplicationUrlHelper applicationUrlHelper;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder', 'external_finance')")
    @GetMapping("/{sectionType}/{organisationId}")
    public String redirectToSectionManagement(@PathVariable SectionType sectionType,
                                              @PathVariable long applicationId,
                                              @PathVariable long organisationId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        List<SectionResource> sections = sectionRestService.getSectionsByCompetitionIdAndType(application.getCompetition(), sectionType).getSuccess();
        if (sections.size() == 1) {
            return redirect(applicationId, sections.get(0), organisationId);
        }
        return "redirect:/application/" + applicationId;
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{sectionType}")
    public String redirectToSection(@PathVariable SectionType sectionType,
                                    @PathVariable long applicationId,
                                    UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        List<SectionResource> sections = sectionRestService.getSectionsByCompetitionIdAndType(application.getCompetition(), sectionType).getSuccess();
        if (sections.size() == 1) {
            return redirect(applicationId, sections.get(0), user);
        }
        return "redirect:/application/" + applicationId;
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping(SECTION_URL + "{sectionId}")
    public String getSectionApplicant(@PathVariable long applicationId,
                                      @PathVariable long sectionId,
                                      UserResource user) {
        SectionResource section = sectionRestService.getById(sectionId).getSuccess();
        return redirect(applicationId, section, user);
    }

    @SecuredBySpring(value = "ApplicationSectionController", description = "Internal users can access the sections in the 'Your project Finances'")
    @PreAuthorize("hasAnyAuthority('support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    @GetMapping(SECTION_URL + "{sectionId}/{organisationId}")
    public String getSectionInternalUser(@PathVariable long applicationId,
                                         @PathVariable long sectionId,
                                         @PathVariable long organisationId) {
        SectionResource section = sectionRestService.getById(sectionId).getSuccess();
        return redirect(applicationId, section, organisationId);
    }

    private String redirect(long applicationId, SectionResource section, UserResource user) {
        ProcessRoleResource role = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        return redirect(applicationId, section, role.getOrganisationId());
    }

    private String redirect(long applicationId, SectionResource section, long organisationId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        return applicationUrlHelper.getSectionUrl(section.getType(), section.getId(), applicationId, organisationId, application.getCompetition())
                .map(url -> "redirect:" + url)
                .orElseThrow(ObjectNotFoundException::new);
    }
}
