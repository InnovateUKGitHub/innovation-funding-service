package org.innovateuk.ifs.eugrant.funding.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.eugrant.EuActionTypeRestService;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.funding.form.EuFundingForm;
import org.innovateuk.ifs.eugrant.funding.populator.EuFundingFormPopulator;
import org.innovateuk.ifs.eugrant.funding.saver.EuFundingSaver;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

/**
 * This Controller handles the funding details activity
 */
@Controller
@RequestMapping("/")
public class EuFundingController {

    private EuGrantCookieService euGrantCookieService;

    private EuFundingFormPopulator euFundingFormPopulator;

    private EuFundingSaver euFundingSaver;

    private EuActionTypeRestService euActionTypeRestService;

    public EuFundingController(EuGrantCookieService euGrantCookieService,
                               EuFundingFormPopulator euFundingFormPopulator,
                               EuFundingSaver euFundingSaver,
                               EuActionTypeRestService euActionTypeRestService) {
        this.euGrantCookieService = euGrantCookieService;
        this.euFundingFormPopulator = euFundingFormPopulator;
        this.euFundingSaver = euFundingSaver;
        this.euActionTypeRestService = euActionTypeRestService;
    }

    @GetMapping("/funding-details")
    public String fundingDetails(@ModelAttribute(value = "form", binding = false) EuFundingForm form) {

        EuGrantResource grantResource = euGrantCookieService.get();

        if (grantResource.getFunding() == null) {
            return "redirect:/funding-details/edit";
        }

        form = euFundingFormPopulator.populate(form);

        return "funding/funding-details";
    }

    @GetMapping("/funding-details/edit")
    public String fundingDetailsEdit(@ModelAttribute(value = "form", binding = false) EuFundingForm form,
                                     BindingResult bindingResult,
                                     Model model) {

        form = euFundingFormPopulator.populate(form);
        model.addAttribute("actionTypes", euActionTypeRestService.findAll().getSuccess());

        return "funding/funding-details-edit";
    }

    @PostMapping("/funding-details/edit")
    public String submitFundingDetails(@ModelAttribute("form") @Valid EuFundingForm form,
                                       BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       Model model) {

        Supplier<String> failureView = () -> fundingDetailsEdit(form, bindingResult, model);
        Supplier<String> successView = () -> "redirect:/funding-details";

        if (bindingResult.hasErrors()) {
            return failureView.get();
        }

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            RestResult<Void> sendResult = euFundingSaver.save(form);

            return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors()))).
                    failNowOrSucceedWith(failureView, successView);
        });

    }
}
