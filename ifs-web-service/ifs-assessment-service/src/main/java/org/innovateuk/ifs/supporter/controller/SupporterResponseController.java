package org.innovateuk.ifs.supporter.controller;

import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.supporter.form.SupporterResponseForm;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterDecisionResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.supporter.viewmodel.SupporterResponseViewModel;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newArrayList;

@Controller
@RequestMapping("supporter/application/{applicationId}/response")
@SecuredBySpring(value = "Controller", description = "Only supporters can edit their responses", securedType = SupporterResponseController.class)
@PreAuthorize("hasAnyAuthority('supporter')")
public class SupporterResponseController {

    @Autowired
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @GetMapping
    public String editResponse(@PathVariable long applicationId,
                               @ModelAttribute("previousResponse") SupporterAssignmentResource previousAssignment,
                               Model model,
                               UserResource user) {
        SupporterAssignmentResource assignment;
        if (previousAssignment.getState() != null) {
            assignment = previousAssignment;
        } else {
            assignment = supporterAssignmentRestService.getAssignment(user.getId(), applicationId).getSuccess();
            if (newArrayList(SupporterState.ACCEPTED, SupporterState.REJECTED).contains(assignment.getState())) {
                return String.format("redirect:/supporter/application/%d/response/view", applicationId);
            }
        }
        SupporterResponseForm form = new SupporterResponseForm(assignment);
        model.addAttribute("form", form);
        return editView(model, applicationId);
    }

    @GetMapping("/view")
    public String viewResponse(@PathVariable long applicationId,
                               Model model,
                               UserResource user) {
        SupporterAssignmentResource assignment = supporterAssignmentRestService.getAssignment(user.getId(), applicationId).getSuccess();
        if (assignment.getState() == SupporterState.CREATED) {
            return String.format("redirect:/supporter/application/%d/response", applicationId);
        }
        SupporterResponseForm form = new SupporterResponseForm(assignment);
        model.addAttribute("form", form);
        return readonlyView(model, applicationId);
    }

    @PostMapping("/view")
    public String changeResponse(@PathVariable long applicationId,
                                 Model model,
                                 UserResource user,
                                 RedirectAttributes redirectAttributes) {
        SupporterAssignmentResource assignment = supporterAssignmentRestService.getAssignment(user.getId(), applicationId).getSuccess();
        supporterAssignmentRestService.edit(assignment.getAssignmentId()).getSuccess();
        redirectAttributes.addFlashAttribute("previousResponse", assignment);
        return String.format("redirect:/supporter/application/%d/response", applicationId);
    }
    @PostMapping
    public String saveResponse(@PathVariable long applicationId,
                           Model model,
                           UserResource user,
                           @Valid @ModelAttribute("form") SupporterResponseForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        Supplier<String> success = () -> String.format("redirect:/supporter/application/%d/response/view", applicationId);
        Supplier<String> failure = () -> editView(model, applicationId);

        return validationHandler.failNowOrSucceedWith(failure, () -> {
            SupporterDecisionResource decision = new SupporterDecisionResource();
            decision.setAccept(form.getDecision());
            decision.setComments(form.getComments());
            validationHandler.addAnyErrors(supporterAssignmentRestService.decision(form.getAssignmentId(), decision));
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
        model.addAttribute("model", new SupporterResponseViewModel(applicationRestService.getApplicationById(applicationId).getSuccess(), readonly));
        return "supporter/response";
    }
}
