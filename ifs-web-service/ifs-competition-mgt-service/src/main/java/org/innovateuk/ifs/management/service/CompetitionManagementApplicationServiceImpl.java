package org.innovateuk.ifs.management.service;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.AppendixResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.management.model.ApplicationOverviewIneligibilityModelPopulator;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
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
import java.util.stream.IntStream;

import static org.innovateuk.ifs.util.MapFunctions.asMap;

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
                                             ApplicationResource application) {
        form.setAdminMode(true);

        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(application.getId()).getSuccessObjectOrThrowException();

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
        Map<Long, BaseFinanceResource> organisationFinances = (Map<Long, BaseFinanceResource> ) model.asMap().get("organisationFinances");
        Map<Long, Boolean> detailedFinanceLink = organisations.stream().collect(Collectors.toMap(o -> o.getId(),
                o -> user.hasRole(UserRoleType.SUPPORT) &&
                ((organisationFinances.containsKey(o.getId()) && organisationFinances.get(o.getId()).getOrganisationSize() != null) ||
                  isAcademicOrganisation.get(o.getId()))
            ? Boolean.TRUE : Boolean.FALSE));
        model.addAttribute("showDetailedFinanceLink", detailedFinanceLink);

        model.addAttribute("isSupportUser", user.hasRole(UserRoleType.SUPPORT));
        model.addAttribute("form", form);
        model.addAttribute("applicationReadyForSubmit", false);
        model.addAttribute("isCompManagementDownload", true);
        model.addAttribute("ineligibility", applicationOverviewIneligibilityModelPopulator.populateModel(application));
        model.addAttribute("showApplicationTeamLink", applicationService.showApplicationTeam(application.getId(), user.getId()));

        model.addAttribute("backUrl", buildBackUrl(origin, application.getId(), competitionId, queryParams));
        String params = UriComponentsBuilder.newInstance()
                .queryParam("origin", origin)
                .queryParams(queryParams)
                .build()
                .encode()
                .toUriString();
        model.addAttribute("queryParams", params);

        return "competition-mgt-application-overview";
    }

    @Override
    public String markApplicationAsIneligible(long applicationId,
                                              long competitionId,
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
                    applicationService.getById(applicationId));
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

    private String buildBackUrl(String origin, Long applicationId, Long competitionId, MultiValueMap<String, String> queryParams) {
        String baseUrl = ApplicationOverviewOrigin.valueOf(origin).getBaseOriginUrl();

        queryParams.remove("origin");

        return UriComponentsBuilder.fromPath(baseUrl)
                .queryParams(queryParams)
                .buildAndExpand(asMap(
                        "competitionId", competitionId,
                        "applicationId", applicationId
                ))
                .encode()
                .toUriString();
    }

    private void addAppendices(Long applicationId, List<FormInputResponseResource> responses, Model model) {
        final List<AppendixResource> appendices = responses.stream().filter(fir -> fir.getFileEntry() != null).
                map(fir -> {
                    FormInputResource formInputResource = formInputRestService.getById(fir.getFormInput()).getSuccessObjectOrThrowException();
                    FileEntryResource fileEntryResource = fileEntryRestService.findOne(fir.getFileEntry()).getSuccessObject();
                    String title = formInputResource.getDescription() != null ? formInputResource.getDescription() : fileEntryResource.getName();
                    return new AppendixResource(applicationId, formInputResource.getId(), title, fileEntryResource);
                }).
                collect(Collectors.toList());
        model.addAttribute("appendices", appendices);
    }

    public enum ApplicationOverviewOrigin {
        ALL_APPLICATIONS("/competition/{competitionId}/applications/all"),
        SUBMITTED_APPLICATIONS("/competition/{competitionId}/applications/submitted"),
        INELIGIBLE_APPLICATIONS("/competition/{competitionId}/applications/ineligible"),
        MANAGE_APPLICATIONS("/assessment/competition/{competitionId}/applications"),
        FUNDING_APPLICATIONS("/competition/{competitionId}/funding"),
        APPLICATION_PROGRESS("/competition/{competitionId}/application/{applicationId}/assessors"),
        MANAGE_ASSESSMENTS("/assessment/competition/{competitionId}");

        private String baseOriginUrl;

        ApplicationOverviewOrigin(String baseOriginUrl) {
            this.baseOriginUrl = baseOriginUrl;
        }

        public String getBaseOriginUrl() {
            return baseOriginUrl;
        }
    }
}
