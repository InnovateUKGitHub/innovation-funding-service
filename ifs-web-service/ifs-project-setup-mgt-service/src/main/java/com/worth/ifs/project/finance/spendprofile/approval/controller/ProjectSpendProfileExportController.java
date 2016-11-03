package com.worth.ifs.project.finance.spendprofile.approval.controller;

import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.resource.SpendProfileCSVResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller will handle all requests that are related to spend profile export/downloads.
 */
@Controller
@RequestMapping("/" + ProjectSpendProfileExportController.BASE_DIR + "/{projectId}/partner-organisation/{organisationId}/spend-profile-export")
public class ProjectSpendProfileExportController {

    public static final String BASE_DIR = "project";
    public static final String CONTENT_TYPE = "text/csv";
    public static final String HEADER_CONTENT_DISPOSITION = "Content-disposition";
    private static final String ATTACHMENT_HEADER = "attachment;filename=";

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_SPEND_PROFILE_SECTION')")
    @RequestMapping(value = "/csv", method = GET)
    public void exportProjectPartnerSpendProfileAsCSV(@PathVariable("projectId") final Long projectId,
                                                      @PathVariable("organisationId") final Long organisationId,
                                                      @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                      HttpServletResponse response) throws IOException {
        SpendProfileCSVResource spendProfileCSVResource = projectFinanceService.getSpendProfileCSV(projectId, organisationId);
        response.setContentType(CONTENT_TYPE);
        response.setHeader(HEADER_CONTENT_DISPOSITION, getCSVAttachmentHeader(spendProfileCSVResource.getFileName()));
        response.getOutputStream().print(spendProfileCSVResource.getCsvData());
        response.getOutputStream().flush();
    }

    private String getCSVAttachmentHeader(String fileName) {
        return new StringBuffer().append(ATTACHMENT_HEADER).append(fileName).toString();
    }
}
