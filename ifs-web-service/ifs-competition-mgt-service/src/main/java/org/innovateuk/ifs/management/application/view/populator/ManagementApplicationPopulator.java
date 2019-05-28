package org.innovateuk.ifs.management.application.view.populator;

import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.management.application.view.viewmodel.AppendixViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.management.application.view.viewmodel.ApplicationOverviewIneligibilityViewModel;
import org.innovateuk.ifs.management.application.view.viewmodel.ManagementApplicationViewModel;
import org.innovateuk.ifs.management.navigation.ManagementApplicationOrigin;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildBackUrl;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;

@Component
public class ManagementApplicationPopulator {

    @Autowired
    private ApplicationOverviewIneligibilityModelPopulator applicationOverviewIneligibilityModelPopulator;

    @Autowired
    private ApplicationReadOnlyViewModelPopulator applicationSummaryViewModelPopulator;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    public ManagementApplicationViewModel populate(long applicationId,
                                                   UserResource user,
                                                   String origin,
                                                   MultiValueMap<String, String> queryParams) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        ApplicationReadOnlyViewModel applicationReadOnlyViewModel = applicationSummaryViewModelPopulator.populate(application, competition, user, defaultSettings());
        ApplicationOverviewIneligibilityViewModel ineligibilityViewModel = applicationOverviewIneligibilityModelPopulator.populateModel(application, competition);

        String originQuery = buildOriginQueryString(ManagementApplicationOrigin.valueOf(origin), queryParams);
        queryParams.put("competitionId", asList(String.valueOf(application.getCompetition())));
        queryParams.put("applicationId", asList(String.valueOf(application.getId())));
        String backUrl = buildBackUrl(ManagementApplicationOrigin.valueOf(origin), queryParams, "assessorId", "applicationId", "competitionId");

        return new ManagementApplicationViewModel(
                application,
                competition,
                backUrl,
                originQuery,
                ineligibilityViewModel,
                applicationReadOnlyViewModel,
                getAppendices(applicationId),
                canMarkAsIneligible(application, user),
                user.hasAnyRoles(Role.PROJECT_FINANCE, Role.COMP_ADMIN)
        );

    }

    private List<AppendixViewModel> getAppendices(Long applicationId) {
        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(applicationId).getSuccess();
        return responses.stream().filter(fir -> fir.getFileEntry() != null).
                map(fir -> {
                    FormInputResource formInputResource = formInputRestService.getById(fir.getFormInput()).getSuccess();
                    FileEntryResource fileEntryResource = fileEntryRestService.findOne(fir.getFileEntry()).getSuccess();
                    String title = formInputResource.getDescription() != null ? formInputResource.getDescription() : fileEntryResource.getName();
                    return new AppendixViewModel(applicationId, formInputResource.getId(), title, fileEntryResource);
                }).
                collect(Collectors.toList());
    }

    private boolean canMarkAsIneligible(ApplicationResource application, UserResource user) {
        return application.getApplicationState() == SUBMITTED
                && user.hasAnyRoles(Role.PROJECT_FINANCE, Role.COMP_ADMIN, Role.INNOVATION_LEAD);
    }
}
