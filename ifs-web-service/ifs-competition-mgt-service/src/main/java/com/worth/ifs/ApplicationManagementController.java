package com.worth.ifs;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationSummaryRestService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileEntryRestService;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/competition/{competitionId}/application")
public class ApplicationManagementController extends AbstractApplicationController {
    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(ApplicationManagementController.class);
    @Autowired
    CompetitionService competitionService;

    @Autowired
    ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    FormInputResponseService formInputResponseService;

    @Autowired
    FileEntryRestService fileEntryRestService;

    @RequestMapping(value= "/{applicationId}", method = RequestMethod.GET)
    public String displayCompetitionInfo(@PathVariable("competitionId") final String competitionId,
                                               @PathVariable("applicationId") final String applicationIdString,
                                               @ModelAttribute("form") ApplicationForm form,
                                               Model model,
                                               HttpServletRequest request
    ){
        UserResource user = getLoggedUser(request);
        Long applicationId = Long.valueOf(applicationIdString);
        form.setAdminMode(true);

        List<FormInputResponseResource> responses = formInputResponseService.getByApplication(applicationId);
//        model.addAttribute("incompletedSections", sectionService.getInCompleted(applicationId));
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));

        ApplicationResource application = applicationService.getById(applicationId);
        // so the mode is viewonly
        application.enableViewMode();

        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSections(application, competition, user.getId(), Optional.empty(), Optional.empty(), model, form);
        addOrganisationAndUserFinanceDetails(applicationId, user, model, form);
        addAppendices(responses, model);

        model.addAttribute("applicationReadyForSubmit", false);
        return "competition-mgt-application-overview";
    }

    private void addAppendices(List<FormInputResponseResource> responses, Model model) {
        final List<FileEntryResource> appendices = responses.stream().map(fir -> fileEntryRestService.findOne(fir.getFileEntry()).getSuccessObject()).collect(Collectors.toList());
        model.addAttribute("appendices", appendices);
    }
}
