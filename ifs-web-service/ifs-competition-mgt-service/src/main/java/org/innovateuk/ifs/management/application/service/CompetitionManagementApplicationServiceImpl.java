package org.innovateuk.ifs.management.application.service;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.management.application.populator.ApplicationOverviewIneligibilityModelPopulator;
import org.innovateuk.ifs.management.navigation.NavigationOrigin;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildBackUrl;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;
import static org.innovateuk.ifs.user.resource.Role.*;

/**
 * Implementation of {@link CompetitionManagementApplicationService}
 */
@Service
public class CompetitionManagementApplicationServiceImpl implements CompetitionManagementApplicationService {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Autowired
    private ApplicationOverviewIneligibilityModelPopulator applicationOverviewIneligibilityModelPopulator;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Override
    public String displayApplicationOverview(UserResource user,
                                             long competitionId,
                                             ApplicationForm form,
                                             String origin,
                                             MultiValueMap<String, String> queryParams,
                                             Model model,
                                             ApplicationResource application,
                                             Optional<Long> assessorId) {
        form.setAdminMode(true);

        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(application.getId()).getSuccess();

        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        applicationModelPopulator.addApplicationAndSections(application, competition, user, Optional.empty(), Optional.empty(), model, form, userApplicationRoles);
        organisationDetailsModelPopulator.populateModel(model, application.getId(), userApplicationRoles);

        // Having to pass getImpersonateOrganisationId here because look at the horrible code inside addOrganisationAndUserFinanceDetails with impersonation org id :(
        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competition.getId(), application.getId(), user, model, form, form.getImpersonateOrganisationId());
        addAppendices(application.getId(), responses, model);

        // organisationFinances populated by ApplicationFinanceOverviewModelManager, applicantOrganisationIsAcademic & applicationOrganisations populated by OrganisationDetailsModelPopulator, both above
        Map<Long, Boolean> isAcademicOrganisation = (Map<Long, Boolean>) model.asMap().get("applicantOrganisationIsAcademic");
        List<OrganisationResource> organisations = (List<OrganisationResource>) model.asMap().get("applicationOrganisations");
        Map<Long, BaseFinanceResource> organisationFinances = (Map<Long, BaseFinanceResource>) model.asMap().get("organisationFinances");

        Map<Long, Boolean> showDetailedFinanceLink = organisations.stream().collect(Collectors.toMap(OrganisationResource::getId,
                organisation -> {

                    boolean orgFinancesExist = ofNullable(organisationFinances)
                            .map(finances -> organisationFinances.get(organisation.getId()))
                            .map(BaseFinanceResource::getOrganisationSize)
                            .isPresent();
                    boolean academicFinancesExist = isAcademicOrganisation.get(organisation.getId());
                    boolean financesExist = orgFinancesExist || academicFinancesExist;

                    return isApplicationVisibleToUser(application, user) && financesExist;
                })
        );

        model.addAttribute("showDetailedFinanceLink", showDetailedFinanceLink);
        model.addAttribute("readOnly", user.hasRole(SUPPORT));
        model.addAttribute("canReinstate", !(user.hasRole(SUPPORT) || user.hasRole(INNOVATION_LEAD)));
        model.addAttribute("form", form);
        model.addAttribute("applicationReadyForSubmit", false);
        model.addAttribute("isCompManagementDownload", true);
        model.addAttribute("ineligibility", applicationOverviewIneligibilityModelPopulator.populateModel(application, competition));
        model.addAttribute("showApplicationTeamLink", applicationService.showApplicationTeam(application.getId(), user.getId()));

        queryParams.put("competitionId", asList(String.valueOf(competitionId)));
        queryParams.put("applicationId", asList(String.valueOf(application.getId())));
        model.addAttribute("backUrl", buildBackUrl(NavigationOrigin.valueOf(origin), queryParams, "assessorId", "applicationId", "competitionId"));
        UriComponentsBuilder builder =  UriComponentsBuilder.newInstance()
                .queryParam("origin", origin)
                .queryParams(queryParams);

        assessorId.ifPresent(id -> builder.queryParam("assessorId", id));

        model.addAttribute("queryParams", builder
                .build()
                .encode()
                .toUriString());

        model.addAttribute("fromApplicationService", false);

        return "competition-mgt-application-overview";
    }

    private boolean isApplicationVisibleToUser(ApplicationResource application, UserResource user) {
        boolean canSeeUnsubmitted = user.hasRole(IFS_ADMINISTRATOR) || user.hasRole(SUPPORT);
        boolean canSeeSubmitted = user.hasRole(PROJECT_FINANCE) || user.hasRole(COMP_ADMIN) || user.hasRole(INNOVATION_LEAD);
        boolean isSubmitted = application.getApplicationState() != ApplicationState.OPEN &&  application.getApplicationState() != ApplicationState.CREATED;

        return canSeeUnsubmitted || (canSeeSubmitted && isSubmitted);
    }

    @Override
    public String markApplicationAsIneligible(long applicationId,
                                              long competitionId,
                                              Optional<Long> assessorId,
                                              String origin,
                                              MultiValueMap<String, String> queryParams,
                                              ApplicationForm applicationForm,
                                              UserResource user,
                                              Model model) {
        IneligibleOutcomeResource ineligibleOutcomeResource =
                new IneligibleOutcomeResource(applicationForm.getIneligibleReason());

        ServiceResult<Void> result = applicationService.markAsIneligible(applicationId, ineligibleOutcomeResource);

        if (result != null && result.isSuccess()) {
            return "redirect:/competition/" + competitionId + "/applications/ineligible";
        } else {
            return displayApplicationOverview(user,
                    competitionId,
                    applicationForm,
                    origin,
                    queryParams,
                    model,
                    applicationService.getById(applicationId),
                    assessorId
            );
        }
    }

    @Override
    public String validateApplicationAndCompetitionIds(Long applicationId, Long competitionId, Function<ApplicationResource, String> success) {
        ApplicationResource application = applicationService.getById(applicationId);
        if (application.getCompetition().equals(competitionId)) {
            return success.apply(application);
        } else {
            throw new ObjectNotFoundException();
        }
    }

    private void addAppendices(Long applicationId, List<FormInputResponseResource> responses, Model model) {
        final List<AppendixResource> appendices = responses.stream().filter(fir -> fir.getFileEntry() != null).
                map(fir -> {
                    FormInputResource formInputResource = formInputRestService.getById(fir.getFormInput()).getSuccess();
                    FileEntryResource fileEntryResource = fileEntryRestService.findOne(fir.getFileEntry()).getSuccess();
                    String title = formInputResource.getDescription() != null ? formInputResource.getDescription() : fileEntryResource.getName();
                    return new AppendixResource(applicationId, formInputResource.getId(), title, fileEntryResource);
                }).
                collect(Collectors.toList());
        model.addAttribute("appendices", appendices);
    }
}
