package org.innovateuk.ifs.management.application.view.service;

import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.AppendixResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.management.application.view.populator.ManageApplicationModelPopulator;
import org.innovateuk.ifs.management.application.view.viewmodel.ManageApplicationViewModel;
import org.innovateuk.ifs.management.navigation.NavigationOrigin;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildBackUrl;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;

/**
 * Implementation of {@link CompetitionManagementApplicationService}
 */
@Service
public class CompetitionManagementApplicationServiceImpl implements CompetitionManagementApplicationService {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private ManageApplicationModelPopulator manageApplicationModelPopulator;

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
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        String queryParam = buildOriginQueryString(NavigationOrigin.valueOf(origin), queryParams);
        queryParams.put("competitionId", asList(String.valueOf(competitionId)));
        queryParams.put("applicationId", asList(String.valueOf(application.getId())));
        String backUrl = buildBackUrl(NavigationOrigin.valueOf(origin), queryParams, "assessorId", "applicationId", "competitionId");

        ManageApplicationViewModel viewModel = manageApplicationModelPopulator.populate(application,
                        competition,
                        backUrl,
                        queryParam,
                        user,
                        getAppendices(application.getId(), responses, model),
                        form);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        return "competition-mgt-application-overview";
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

    private List<AppendixResource> getAppendices(Long applicationId, List<FormInputResponseResource> responses, Model model) {
        return responses.stream().filter(fir -> fir.getFileEntry() != null).
                map(fir -> {
                    FormInputResource formInputResource = formInputRestService.getById(fir.getFormInput()).getSuccess();
                    FileEntryResource fileEntryResource = fileEntryRestService.findOne(fir.getFileEntry()).getSuccess();
                    String title = formInputResource.getDescription() != null ? formInputResource.getDescription() : fileEntryResource.getName();
                    return new AppendixResource(applicationId, formInputResource.getId(), title, fileEntryResource);
                }).
                collect(Collectors.toList());
    }
}
