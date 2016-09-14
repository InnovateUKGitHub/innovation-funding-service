package com.worth.ifs.project.grantofferletter.controller;

import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.grantofferletter.viewmodel.ProjectGrantOfferLetterViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Module: innovation-funding-service-dev
 * Controller for the grant offer letter
 **/
@Controller
@RequestMapping("/project/{projectId}/offer")
public class ProjectGrantOfferLetterController {

    private static final String FORM_ATTR = "form";
    public static final String BASE_DIR = "project";
    public static final String TEMPLATE_NAME = "grant-offer-letter";

    @Autowired
    private ProjectService projectService;

    @RequestMapping(method = GET)
    public String viewGrantOfferLetterPage(@PathVariable(BASE_DIR + "Id") Long projectId, Model model,
                                           @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        return createGrantOfferLetterPage(projectId, model, loggedInUser);
    }

    private String createGrantOfferLetterPage(Long projectId, Model model, UserResource loggedInUser) {
        ProjectGrantOfferLetterViewModel viewModel = populateGrantOfferLetterViewModel(projectId, loggedInUser);
        model.addAttribute("model", viewModel);
        return BASE_DIR + "/" + TEMPLATE_NAME;
    }

    private ProjectGrantOfferLetterViewModel populateGrantOfferLetterViewModel(Long projectId, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        boolean leadPartner = projectService.isUserLeadPartner(projectId, loggedInUser.getId());

        //TODO: get grant offer letter from project service
        //TODO: get extra contract file from project service
        Optional<FileEntryResource> grantOfferLetter = Optional.of(new FileEntryResource(1L, "grantOfferLetter", "application/pdf", 10000));
        Optional<FileEntryResource> additionalContractFile = Optional.of(new FileEntryResource(1L, "additionalContractFile", "application/pdf", 10000));

        LocalDateTime submittedDate = null;
        boolean offerSigned = false;
        boolean offerAccepted = false;
        boolean offerRejected = false;


        return new ProjectGrantOfferLetterViewModel(projectId, project.getName(),
                leadPartner, grantOfferLetter.map(FileDetailsViewModel::new).orElse(null),
                additionalContractFile.map(FileDetailsViewModel::new).orElse(null),
                offerSigned, submittedDate, offerAccepted, offerRejected);
    }


}
