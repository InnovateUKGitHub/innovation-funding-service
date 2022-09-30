package org.innovateuk.ifs.management.application.view.controller;

import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * Handles the Competition Management eoi overview page (and associated actions).
 */
@Controller
public class CompetitionManagementEoiController {

    @Autowired
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @SecuredBySpring(value = "DOWNLOAD_EOI_EVIDENCE", description = "internal user can download eoi evidence")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'support', 'innovation_lead', 'stakeholder', 'super_admin_user', 'auditor', 'ifs_administrator', 'project_finance')")
    @GetMapping("/application/{applicationId}/view-eoi-evidence")
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadEOIEvidenceFile(
            @PathVariable("applicationId") final Long applicationId) {

        final ByteArrayResource resource = applicationEoiEvidenceResponseRestService.getEvidenceByApplication(applicationId).getSuccess();
        final FileEntryResource fileDetails = applicationEoiEvidenceResponseRestService.getEvidenceDetailsByApplication(applicationId).getSuccess();

        return getFileResponseEntity(resource, fileDetails);
    }
}
