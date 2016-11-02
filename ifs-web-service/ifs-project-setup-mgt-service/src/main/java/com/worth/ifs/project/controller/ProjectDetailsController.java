package com.worth.ifs.project.controller;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.viewmodel.ProjectDetailsViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This controller will handle all requests that are related to project details.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project")
public class ProjectDetailsController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationService organisationService;

    @RequestMapping(value = "/{projectId}/details", method = RequestMethod.GET)
    public String viewProjectDetails(@PathVariable("competitionId") final Long competitionId,
                                     @PathVariable("projectId") final Long projectId, Model model,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectResource projectResource = projectService.getById(projectId);
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectResource.getId());
        OrganisationResource leadOrganisationResource = projectService.getLeadOrganisation(projectId);

        List<OrganisationResource> partnerOrganisations = getPartnerOrganisations(projectUsers);

        model.addAttribute("model", new ProjectDetailsViewModel(projectResource,
                competitionId,
                leadOrganisationResource.getName(),
                getProjectManager(projectUsers).orElse(null),
                getFinanceContactForPartnerOrganisation(projectUsers, partnerOrganisations)));

        return "project/detail";
    }

    private List<OrganisationResource> getPartnerOrganisations(final List<ProjectUserResource> projectRoles) {

        final Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);

        final Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        SortedSet<OrganisationResource> organisationSet = projectRoles.stream()
                .filter(uar -> uar.getRoleName().equals(PARTNER.getName()))
                .map(uar -> organisationService.getOrganisationById(uar.getOrganisation()))
                .collect(Collectors.toCollection(supplier));

        return new ArrayList<>(organisationSet);
    }

    private Optional<ProjectUserResource> getProjectManager(List<ProjectUserResource> projectUsers) {
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getName().equals(pu.getRoleName()));
    }

    private Map<OrganisationResource, ProjectUserResource> getFinanceContactForPartnerOrganisation(List<ProjectUserResource> projectUsers, List<OrganisationResource> partnerOrganisations) {
        List<ProjectUserResource> financeRoles = simpleFilter(projectUsers, ProjectUserResource::isFinanceContact);

        Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap = new LinkedHashMap<>();

        partnerOrganisations.stream().forEach(organisation ->
                organisationFinanceContactMap.put(organisation,
                        simpleFindFirst(financeRoles, financeUserResource -> financeUserResource.getOrganisation().equals(organisation.getId())).orElse(null))
        );

        return organisationFinanceContactMap;
    }

}
