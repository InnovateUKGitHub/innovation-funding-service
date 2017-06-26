package org.innovateuk.ifs.application.forms.service;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.innovateuk.ifs.application.finance.view.jes.JESFinanceFormHandler;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;

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

    public String getRedirectUrl(HttpServletRequest request, Long applicationId, Optional<SectionType> sectionType) {
        if (request.getParameter("submit-section") == null
                && (request.getParameter(ASSIGN_QUESTION_PARAM) != null ||
                request.getParameter(MARK_AS_INCOMPLETE) != null ||
                request.getParameter(MARK_SECTION_AS_INCOMPLETE) != null ||
                request.getParameter(ADD_COST) != null ||
                request.getParameter(REMOVE_COST) != null ||
                request.getParameter(MARK_AS_COMPLETE) != null ||
                request.getParameter(REMOVE_UPLOADED_FILE) != null ||
                request.getParameter(UPLOAD_FILE) != null ||
                request.getParameter(JESFinanceFormHandler.REMOVE_FINANCE_DOCUMENT) != null ||
                request.getParameter(JESFinanceFormHandler.UPLOAD_FINANCE_DOCUMENT) != null ||
                request.getParameter(EDIT_QUESTION) != null ||
                request.getParameter(REQUESTING_FUNDING) != null ||
                request.getParameter(NOT_REQUESTING_FUNDING) != null ||
                request.getParameter(ACADEMIC_FINANCE_REMOVE) != null)) {
            // user did a action, just display the same page.
            LOG.debug("redirect: " + request.getRequestURI());
            return "redirect:" + request.getRequestURI();
        } else if (request.getParameter("submit-section-redirect") != null) {
            return "redirect:" + APPLICATION_BASE_URL + applicationId + request.getParameter("submit-section-redirect");
        } else {
            if (sectionType.isPresent() && sectionType.get().getParent().isPresent()) {
                return redirectToSection(sectionType.get().getParent().get(), applicationId);
            }
            // add redirect, to make sure the user cannot resubmit the form by refreshing the page.
            LOG.debug("default redirect: ");
            return "redirect:" + APPLICATION_BASE_URL + applicationId;
        }
    }
}
