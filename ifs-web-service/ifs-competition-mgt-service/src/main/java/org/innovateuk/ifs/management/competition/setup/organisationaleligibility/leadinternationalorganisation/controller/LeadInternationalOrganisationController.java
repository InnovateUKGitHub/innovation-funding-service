package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.form.LeadInternationalOrganisationForm;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.populator.LeadInternationalOrganisationViewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

@Controller
@RequestMapping("/competition/setup/{competitionId}/organisational-eligibility/lead-international-organisation")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = LeadInternationalOrganisationController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class LeadInternationalOrganisationController {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Autowired
    private LeadInternationalOrganisationViewModelPopulator leadInternationalOrganisationViewModelPopulator;

    @GetMapping
    public String viewLeadInternationalOrganisationDetails(@PathVariable("competitionId") long competitionId,
                                                           @ModelAttribute LeadInternationalOrganisationForm leadInternationalOrganisationForm,
                                                           Model model) {
        RestResult<CompetitionOrganisationConfigResource> configResource = competitionOrganisationConfigRestService.findByCompetitionId(competitionId);
        model.addAttribute("model", leadInternationalOrganisationViewModelPopulator.populateModel(competitionId, configResource.getSuccess()));

        return "competition/setup/lead-international-organisation";
    }

    @PostMapping
    public String submitLeadInternationalOrganisationDetails(@PathVariable("competitionId") long competitionId,
                                                             @Valid @ModelAttribute LeadInternationalOrganisationForm leadInternationalOrganisationForm,
                                                             BindingResult bindingResult,
                                                             ValidationHandler validationHandler,
                                                             Model model) {

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionOrganisationConfigResource configResource = competitionOrganisationConfigRestService.findByCompetitionId(competitionId).getSuccess();

        Supplier<String> failureView = () -> {
            model.addAttribute("model", leadInternationalOrganisationViewModelPopulator.populateModel(competitionResource.getId(), configResource));
            return "competition/setup/lead-international-organisation";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = doSaveSection(competitionResource, leadInternationalOrganisationForm);
            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () ->
                            format("redirect:/competition/setup/%d/section/organisational-eligibility", competitionResource.getId()));
        });
    }

    private ServiceResult<Void> doSaveSection(CompetitionResource competition, LeadInternationalOrganisationForm leadInternationalOrganisationForm) {

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competition.getId()).getSuccess();
        competitionOrganisationConfigResource.setInternationalLeadOrganisationAllowed(leadInternationalOrganisationForm.getLeadInternationalOrganisationsApplicable());

        return competitionOrganisationConfigRestService.update(competition.getId(), competitionOrganisationConfigResource).toServiceResult().andOnSuccessReturnVoid();
    }
}