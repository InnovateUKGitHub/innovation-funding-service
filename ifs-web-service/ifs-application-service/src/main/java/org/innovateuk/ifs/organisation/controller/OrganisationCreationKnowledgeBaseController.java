package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.registration.form.KnowledgeBaseForm;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.KnowledgeBaseRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

/**
 * Provides methods for both:
 * - Finding your company or research type organisation through Companies House or JES search.
 * - Verifying or amending the address attached to the organisation.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/knowledge-base")
@SecuredBySpring(value = "Controller", description = "Applicants, support staff, innovation leads, stakeholders, comp admins and project finance users have permission to select knowledge base organisations.", securedType = OrganisationCreationKnowledgeBaseController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationKnowledgeBaseController extends AbstractOrganisationCreationController {

    @Autowired
    private KnowledgeBaseRestService knowledgeBaseRestService;

    @GetMapping
    public String selectKnowledgeBase(@ModelAttribute(name = "form", binding = false) KnowledgeBaseForm organisationForm,
                                      Model model,
                                      HttpServletRequest request,
                                      UserResource user) {
        addPageSubtitleToModel(request, user, model);
        model.addAttribute("knowledgeBases", knowledgeBaseRestService.getKnowledgeBases().getSuccess());
        return "registration/organisation/knowledge-base";
    }

    @PostMapping
    public String selectedKnowledgeBase(@Valid @ModelAttribute("form") KnowledgeBaseForm organisationForm,
                                     BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     UserResource user) {
        Supplier<String> failureView = () -> selectKnowledgeBase(organisationForm, model, request, user);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            OrganisationCreationForm organisationCreationForm = registrationCookieService.getOrganisationCreationCookieValue(request).get();
            organisationCreationForm.setOrganisationName(organisationForm.getKnowledgeBase());
            registrationCookieService.saveToOrganisationCreationCookie(organisationCreationForm, response);
            return "redirect:" + AbstractOrganisationCreationController.BASE_URL + "/" + CONFIRM_ORGANISATION;
        });
    }
}
