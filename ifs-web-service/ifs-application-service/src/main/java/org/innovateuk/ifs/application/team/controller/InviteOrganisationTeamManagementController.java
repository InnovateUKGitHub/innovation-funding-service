package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.team.service.InviteOrganisationTeamManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;

/**
 * For an {@InviteOrganisation} this controller exposes the functions of {@AbstractApplicationTeamManagementController} to a request mapping
 * and assigns a {@AbstractApplicationTeamManagementService} to be used for resolving service queries.
 */
@Controller
@RequestMapping("/application/{applicationId}/team/update/invited/{organisationId}")
public class InviteOrganisationTeamManagementController extends AbstractTeamManagementController<InviteOrganisationTeamManagementService> {
    protected static String MAPPING_FORMAT_STRING = "/application/%s/team/update/invited/%s";

    protected String getMappingFormatString(long applicationId, long organisationId) {
        return format(MAPPING_FORMAT_STRING, applicationId, organisationId);
    }
}