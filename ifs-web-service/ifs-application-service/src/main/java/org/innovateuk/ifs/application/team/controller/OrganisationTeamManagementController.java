package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.team.service.OrganisationTeamManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;

/**
 * For an {@Organisation} this controller exposes the functions of {@AbstractApplicationTeamManagementController} to a request mapping
 * and assigns a {@AbstractApplicationTeamManagementService} to be used for resolving service queries.
 */
@Controller
@RequestMapping("/application/{applicationId}/team/update/existing/{organisationId}")
public class OrganisationTeamManagementController extends AbstractTeamManagementController<OrganisationTeamManagementService> {
    protected static String MAPPING_FORMAT_STRING = "/application/%s/team/update/existing/%s";

    protected String getMappingFormatString(long applicationId, long organisationId) {
        return format(MAPPING_FORMAT_STRING, applicationId, organisationId);
    }
}
