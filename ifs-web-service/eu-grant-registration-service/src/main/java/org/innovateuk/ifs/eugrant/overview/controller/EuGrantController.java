package org.innovateuk.ifs.eugrant.overview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuGrantRestService;
import org.innovateuk.ifs.eugrant.overview.form.EuGrantSubmitForm;
import org.innovateuk.ifs.eugrant.overview.populator.EuGrantOverviewViewModelPopulator;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

/**
 * A controller for the Horizon 2020 grant registration.
 */
@Controller
@RequestMapping("/")
public class EuGrantController {

    @Autowired
    private EuGrantOverviewViewModelPopulator euGrantOverviewViewModelPopulator;

    @Autowired
    private EuGrantRestService euGrantRestService;

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    @GetMapping("/overview")
    public String overview(@ModelAttribute(value = "form", binding = false) EuGrantSubmitForm form,
                           BindingResult result,
                           ValidationHandler validationHandler,
                           Model model) {
        model.addAttribute("model", euGrantOverviewViewModelPopulator.populate());
        return "eugrant/overview";
    }

    @PostMapping(value = "/overview", params = "without-dialog")
    public String noJsDialogView(@Valid @ModelAttribute("form") EuGrantSubmitForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model) {

        Supplier<String> failureView = () -> overview(form, bindingResult, validationHandler, model);
        return validationHandler.failNowOrSucceedWith(failureView, () -> "eugrant/confirm-submit");
    }

    @PostMapping("/overview")
    public String submit(@Valid @ModelAttribute("form") EuGrantSubmitForm form,
                         BindingResult bindingResult,
                         ValidationHandler validationHandler,
                         Model model) {
        Supplier<String> failureView = () -> overview(form, bindingResult, validationHandler, model);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            EuGrantResource euGrantResource = euGrantCookieService.get();
            if (euGrantResource.getId() == null) {
                return failureView.get();
            }
            RestResult<EuGrantResource> result = euGrantRestService.submit(euGrantResource.getId());
            return validationHandler.addAnyErrors(result)
                    .failNowOrSucceedWith(failureView,
                            () -> {
                                euGrantCookieService.clear();
                                euGrantCookieService.setPreviouslySubmitted(result.getSuccess());
                                return "redirect:/submitted";
                            });
        });
    }

    @GetMapping("/submitted")
    public String submitted(Model model) {
        return euGrantCookieService.getPreviouslySubmitted()
                .map(euGrant -> {
                    model.addAttribute("model", euGrant);
                    return "eugrant/submitted";
                })
                .orElse("redirect:/overview");
    }

}
