package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.publiccontent.form.PublishForm;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentMenuPopulator;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;

/**
 * Controller for setup of public content.
 */
@Controller
@RequestMapping("/competition/setup/public-content")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class PublicContentMenuController {

    private static final String TEMPLATE_FOLDER = "competition/";
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private PublicContentMenuPopulator publicContentMenuPopulator;

    @Autowired
    private PublicContentService publicContentService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @GetMapping("/{competitionId}")
    public String publicContentMenu(Model model,
                                    @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                    HttpServletRequest request) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId)
                .getSuccessObjectOrThrowException();

        if (!competition.isNonIfs() && !competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        return menuPage(competitionId, model, new PublishForm(), getBaseUrlFromRequest(request));
    }

    @PostMapping("/{competitionId}")
    public String publish(Model model,
                          @PathVariable(COMPETITION_ID_KEY) long competitionId,
                          @Valid @ModelAttribute(FORM_ATTR_NAME) PublishForm publishForm,
                          BindingResult bindingResult,
                          ValidationHandler validationHandler,
                          HttpServletRequest request) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId)
                .getSuccessObjectOrThrowException();

        if (!competition.isNonIfs() && !competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        Supplier<String> failureView = () -> menuPage(competitionId, model, publishForm, getBaseUrlFromRequest(request));
        Supplier<String> successView = () -> "redirect:/competition/setup/public-content/" + competitionId;

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> publicContentService.publishByCompetitionId(competitionId));
    }

    private String menuPage(Long competitionId, Model model, PublishForm publishForm, String webBaseUrl) {
        model.addAttribute("model", publicContentMenuPopulator.populate(competitionId, webBaseUrl));
        model.addAttribute(FORM_ATTR_NAME, publishForm);
        return TEMPLATE_FOLDER + "public-content-menu";
    }

    private String getBaseUrlFromRequest(HttpServletRequest request) {

        return request.getScheme() + "://" + request.getServerName();
    }
}
