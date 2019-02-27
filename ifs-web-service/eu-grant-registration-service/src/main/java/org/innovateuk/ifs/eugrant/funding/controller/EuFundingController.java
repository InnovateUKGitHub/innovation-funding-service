package org.innovateuk.ifs.eugrant.funding.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.eu.grant.EuActionTypeRestService;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.funding.form.EuFundingForm;
import org.innovateuk.ifs.eugrant.funding.populator.EuFundingFormPopulator;
import org.innovateuk.ifs.eugrant.funding.saver.EuFundingSaver;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.time.LocalDate;
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

    public EuFundingController() {
    }

    @Autowired
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
    public String fundingDetails(@ModelAttribute(value = "form", binding = false) EuFundingForm form,
                                 Model model) {

        EuGrantResource grantResource = euGrantCookieService.get();

        if (grantResource.getFunding() == null) {
            return "redirect:/funding-details/edit";
        }

        form = euFundingFormPopulator.populate(form);
        model.addAttribute("actionType", euActionTypeRestService.getById(form.getActionType()).getSuccess());

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

    private boolean validateDateOrdering(EuFundingForm form) {
        LocalDate startDate = form.getStartDate();
        LocalDate endDate = form.getEndDate();
        // if either date is not provided / invalid we will not raise this error as there will be more specific ones.
        return startDate == null || endDate == null || startDate.isBefore(endDate);
    }

    @PostMapping("/funding-details/edit")
    public String submitFundingDetails(@ModelAttribute("form") @Valid EuFundingForm form,
                                       BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       Model model) {

        Supplier<String> failureView = () -> fundingDetailsEdit(form, bindingResult, model);
        Supplier<String> successView = () -> "redirect:/funding-details";

        // custom validation for start date being before end date
        if(!validateDateOrdering(form)) {
            bindingResult.addError(new FieldError("form", "endDateMonth", "End date must be after start date."));
        }

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
