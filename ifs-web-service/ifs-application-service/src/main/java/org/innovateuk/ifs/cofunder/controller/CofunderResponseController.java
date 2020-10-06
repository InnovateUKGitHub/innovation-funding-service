package org.innovateuk.ifs.cofunder.controller;

import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.cofunder.form.CofunderResponseForm;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.service.CofunderAssignmentRestService;
import org.innovateuk.ifs.cofunder.viewmodel.CofunderResponseViewModel;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

@Controller
@RequestMapping("cofunder/application/{applicationId}/response")
public class CofunderResponseController {

    @Autowired
    private CofunderAssignmentRestService cofunderAssignmentRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @GetMapping
    public String editResponse(@PathVariable long applicationId,
                               Model model,
                               UserResource user) {
        CofunderAssignmentResource assignment = cofunderAssignmentRestService.getAssignment(user.getId(), applicationId).getSuccess();
        CofunderResponseForm form = new CofunderResponseForm(assignment);
        model.addAttribute("form", form);
        return editView(model, applicationId);
    }

    @GetMapping("/view")
    public String viewResponse(@PathVariable long applicationId,
                               Model model,
                               UserResource user) {
        CofunderAssignmentResource assignment = cofunderAssignmentRestService.getAssignment(user.getId(), applicationId).getSuccess();
        CofunderResponseForm form = new CofunderResponseForm(assignment);
        model.addAttribute("form", form);
        return readonlyView(model, applicationId);
    }

    @PostMapping
    public String saveResponse(@PathVariable long applicationId,
                           Model model,
                           UserResource user,
                           @Valid @ModelAttribute("form") CofunderResponseForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> success = () -> String.format("redirect:/cofunder/application/%d/response/view", applicationId);
        Supplier<String> failure = () -> editView(model, applicationId);

        return validationHandler.failNowOrSucceedWith(failure, () -> {
            CofunderDecisionResource decision = new CofunderDecisionResource();
            decision.setAccept(form.getDecision());
            decision.setComments(form.getComments());
            validationHandler.addAnyErrors(cofunderAssignmentRestService.decision(form.getAssignmentId(), decision));
            return validationHandler.failNowOrSucceedWith(failure, success);
        });
    }
    private String editView(Model model, long applicationId) {
        return view(model, applicationId, false);
    }

    private String readonlyView(Model model, long applicationId) {
        return view(model, applicationId, true);
    }

    private String view(Model model, long applicationId, boolean readonly) {
        model.addAttribute("model", new CofunderResponseViewModel(applicationRestService.getApplicationById(applicationId).getSuccess(), readonly));
        return "cofunder/response";
    }
}
