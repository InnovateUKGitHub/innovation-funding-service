package org.innovateuk.ifs.application.forms.service;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service will handle redirection actions that are related to the application.
 */
@Service
public class ApplicationRedirectionService {

    private static final Log LOG = LogFactory.getLog(ApplicationRedirectionService.class);

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationService applicationService;

    public String redirectToSection(SectionType type, Long applicationId) {
        ApplicationResource application = applicationService.getById(applicationId);
        List<SectionResource> sections = sectionService.getSectionsForCompetitionByType(application.getCompetition(), type);
        if (sections.size() == 1) {
            return "redirect:/application/" + applicationId + "/form/section/" + sections.get(0).getId();
        }
        return "redirect:/application/" + applicationId;
    }
}
