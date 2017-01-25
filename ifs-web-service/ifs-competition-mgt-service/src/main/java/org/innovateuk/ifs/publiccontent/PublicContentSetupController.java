package org.innovateuk.ifs.publiccontent;

import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.publiccontent.form.PublishForm;
import org.innovateuk.ifs.publiccontent.populator.PublicContentMenuPopulator;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.function.Supplier;

import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;


@Controller
@RequestMapping("/competition/setup/public-content")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class PublicContentSetupController {

    private static final String TEMPLATE_FOLDER = "competition/";

    @Autowired
    private PublicContentMenuPopulator publicContentMenuPopulator;

    @Autowired
    private PublicContentService publicContentService;

    @RequestMapping(value = "/{competitionId}", method = RequestMethod.GET)
    public String publicContentMenu(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId) {
        return menuPage(competitionId, model, new PublishForm());
    }

    @RequestMapping(value = "/{competitionId}", method = RequestMethod.POST)
    public String publish(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                          PublishForm publishForm, BindingResult bindingResult, ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> menuPage(competitionId, model, publishForm);
        Supplier<String> successView = () -> "redirect:/competition/setup/public-content/" + competitionId;

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> publicContentService.publishByCompetitionId(competitionId));
    }

    private String menuPage(Long competitionId, Model model, PublishForm publishForm) {
        return publicContentMenuPopulator.populate(competitionId).andOnSuccessReturn(viewModel -> {
            model.addAttribute("model", viewModel);
            model.addAttribute("form", publishForm);
            return TEMPLATE_FOLDER + "public-content-menu";
        }).getSuccessObjectOrThrowException();
    }



}
