package org.innovateuk.ifs.management.competition.setup;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.CompetitionThirdPartyConfigRestService;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.management.competition.setup.core.form.TermsAndConditionsForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.TermsAndConditionsFormPopulator;
import org.innovateuk.ifs.management.competition.setup.core.populator.TermsAndConditionsModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.TERMS_AND_CONDITIONS;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;

@Controller
@RequestMapping("/competition/setup")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionSetupTermsAndConditionsController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionSetupTermsAndConditionsController {
    public static final String COMPETITION_ID_KEY = "competitionId";
    public static final String COMPETITION_SETUP_FORM_KEY = "competitionSetupForm";
    private static final String MODEL = "model";
    private static final String PROJECT_COST_GUIDANCE_HTTP = "http://";

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private TermsAndConditionsFormPopulator termsAndConditionsFormPopulator;

    @Autowired
    private TermsAndConditionsModelPopulator termsAndConditionsModelPopulator;

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionThirdPartyConfigRestService competitionThirdPartyConfigRestService;

    @Value("${ifs.subsidy.control.northern.ireland.enabled:false}")
    private boolean subsidyControlNorthernIrelandEnabled;

    @GetMapping("/{competitionId}/section/terms-and-conditions")
    public String editTermsAndConditions(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                         Model model,
                                         UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return ifsCompetitionSetup(competitionId);
        }

        if (competition.isNonIfs()) {
            return nonIfsCompetitionSetup(competitionId);
        }

        model.addAttribute(MODEL, termsAndConditionsModelPopulator.populateModel(competition, loggedInUser, false));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, termsAndConditionsFormPopulator.populateForm(competition));

        return "competition/setup";
    }

    @GetMapping("/{competitionId}/section/state-aid-terms-and-conditions")
    public String editStateAidTermsAndConditions(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                 Model model,
                                                 UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return ifsCompetitionSetup(competitionId);
        }

        if (competition.isNonIfs()) {
            return nonIfsCompetitionSetup(competitionId);
        }

        if (!shouldHaveSeparateTerms(competition)) {
            return ifsCompetitionSetup(competitionId);
        }

        model.addAttribute(MODEL, termsAndConditionsModelPopulator.populateModel(competition, loggedInUser, true));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, termsAndConditionsFormPopulator.populateFormForStateAid(competition));

        return "competition/setup";
    }

    @PostMapping("/{competitionId}/section/terms-and-conditions")
    public String submitTermsAndConditionsSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm termsAndConditionsForm,
                                                         @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                         ValidationHandler validationHandler,
                                                         @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                         UserResource loggedInUser,
                                                         Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (validationHandler.hasErrors()) {
            model.addAttribute(MODEL, termsAndConditionsModelPopulator.populateModel(competition, loggedInUser, false));
            return "competition/setup";
        }

        boolean isProcurementThirdParty = isProcurementThirdParty(termsAndConditionsForm.getTermsAndConditionsId());
        boolean isProcurement = isProcurement(termsAndConditionsForm.getTermsAndConditionsId());

        if (isProcurementThirdParty) {
            termsAndConditionsFormPopulator.populateThirdPartyConfigData(termsAndConditionsForm, competition);
            validateThirdPartyConfigFields(competition, bindingResult);
        }

        validateUploadFragment(isProcurementThirdParty, isProcurement, competition, bindingResult);

        if (validationHandler.hasErrors()) {
            model.addAttribute(MODEL, termsAndConditionsModelPopulator.populateModel(competition, loggedInUser, false));
            return "competition/setup";
        }

        if ( !(isProcurement || isProcurementThirdParty)) {
            competitionSetupRestService.deleteCompetitionTerms(competitionId);
        }

        Supplier<ServiceResult<Void>> saveAction = () -> nonStateAidSaveAction(competition, termsAndConditionsForm);
        Supplier<String> postSaveRedirect = () -> postSaveRedirectForSingleTermsAndConditions(competition);

        if (isProcurementThirdParty(termsAndConditionsForm.getTermsAndConditionsId())) {
            saveThirdPartyTermsAndConditionsConfigData(competition);
        }
        return termsAndConditionsSection(validationHandler, competition, loggedInUser, model, saveAction, postSaveRedirect, false);
    }

    @PostMapping("/{competitionId}/section/state-aid-terms-and-conditions")
    public String submitStateAidTermsAndConditionsSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm termsAndConditionsForm,
                                                                 @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                                 ValidationHandler validationHandler,
                                                                 @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                                 UserResource loggedInUser,
                                                                 Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!shouldHaveSeparateTerms(competition)) {
            return ifsCompetitionSetup(competition.getId());
        }

        Supplier<ServiceResult<Void>> saveAction = () -> stateAidSaveAction(competition, termsAndConditionsForm);
        Supplier<String> postSaveRedirect = () -> format("redirect:/competition/setup/%d/section/terms-and-conditions", competition.getId());
        return termsAndConditionsSection(validationHandler, competition, loggedInUser, model, saveAction, postSaveRedirect, true);
    }

    @PostMapping(path = "/{competitionId}/section/terms-and-conditions", params = "uploadTermsAndConditionsDoc")
    public String uploadTermsAndConditions(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm termsAndConditionsForm,
                                           @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                           UserResource loggedInUser,
                                           Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        Supplier<ServiceResult<Void>> saveAction = () -> nonStateAidSaveAction(competition, termsAndConditionsForm);
        Supplier<String> postSaveRedirect = () -> postSaveRedirectForSingleTermsAndConditions(competition);
        Supplier<String> failure = () -> termsAndConditionsSection(validationHandler, competition, loggedInUser, model, saveAction, postSaveRedirect, false);

        MultipartFile file = getTermsAndConditionsFile(termsAndConditionsForm);

        RestResult<FileEntryResource> uploadResult = competitionSetupRestService.uploadCompetitionTerms(competitionId, file.getContentType(), file.getSize(),
                file.getOriginalFilename(), getMultipartFileBytes(file));

        termsAndConditionsForm.setMarkAsCompleteAction(false);
        saveTermsAndConditions(competition, termsAndConditionsForm);

        Supplier<String> success = () -> format("redirect:/competition/setup/%d/section/terms-and-conditions", +competition.getId());
        return validationHandler.addAnyErrors(error(uploadResult.getErrors()), fileUploadField(getFileUploadedString(competition)), defaultConverters())
                .failNowOrSucceedWith(failure, success);
    }


    @PostMapping(path = "/{competitionId}/section/terms-and-conditions", params = "deleteTermsAndConditionsDoc")
    public String deleteTermsAndConditions(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm termsAndConditionsForm,
                                           @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                           Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        Supplier<String> failureAndSuccessView = () -> format("redirect:/competition/setup/%d/section/terms-and-conditions", +competition.getId());

        RestResult<Void> deleteResult = competitionSetupRestService.deleteCompetitionTerms(competitionId);
        return validationHandler.addAnyErrors(error(deleteResult.getErrors()), fileUploadField(getFileUploadedString(competition)), defaultConverters())
                .failNowOrSucceedWith(failureAndSuccessView, failureAndSuccessView);
    }

    private String termsAndConditionsSection(ValidationHandler validationHandler,
                                             CompetitionResource competition,
                                             UserResource loggedInUser,
                                             Model model,
                                             Supplier<ServiceResult<Void>> saveAction,
                                             Supplier<String> postSaveRedirect,
                                             boolean stateAidPage) {
        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId())) {
            return ifsCompetitionSetup(competition.getId());
        }

        if (competition.isNonIfs()) {
            return nonIfsCompetitionSetup(competition.getId());
        }

        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, termsAndConditionsModelPopulator.populateModel(competition, loggedInUser, stateAidPage));
            return "competition/setup";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = saveAction.get();
            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, postSaveRedirect);
        });
    }

    private ServiceResult<Void> stateAidSaveAction(CompetitionResource competition, TermsAndConditionsForm termsAndConditionsForm) {
        return saveOtherFundingRulesTermsAndConditions(competition, termsAndConditionsForm)
                .andOnSuccess(() -> optionallyMarkTermsSectionComplete(competition, termsAndConditionsForm));
    }

    private ServiceResult<Void> nonStateAidSaveAction(CompetitionResource competition, TermsAndConditionsForm termsAndConditionsForm) {
        if (shouldHaveSeparateTerms(competition)) {
            return saveTermsAndConditions(competition, termsAndConditionsForm);
        }
        return saveTermsAndConditions(competition, termsAndConditionsForm)
                .andOnSuccess(() -> optionallyMarkTermsSectionComplete(competition, termsAndConditionsForm));
    }

    private ServiceResult<Void> saveTermsAndConditions(CompetitionResource competition, TermsAndConditionsForm termsAndConditionsForm) {
        return competitionRestService.updateTermsAndConditionsForCompetition(
                competition.getId(),
                termsAndConditionsForm.getTermsAndConditionsId()
        ).toServiceResult();
    }

    private ServiceResult<Void> saveOtherFundingRulesTermsAndConditions(CompetitionResource competition, TermsAndConditionsForm termsAndConditionsForm) {
        return competitionRestService.updateOtherFundingRulesTermsAndConditionsForCompetition(
                competition.getId(),
                termsAndConditionsForm.getTermsAndConditionsId()
        ).toServiceResult();
    }

    private ServiceResult<Void> optionallyMarkTermsSectionComplete(CompetitionResource competition, TermsAndConditionsForm termsAndConditionsForm) {
        if (termsAndConditionsForm.isMarkAsCompleteAction()) {
            return competitionSetupRestService.markSectionComplete(competition.getId(), TERMS_AND_CONDITIONS).toServiceResult();
        }
        return serviceSuccess();
    }

    private boolean isProcurement(long termsAndConditionsId) {
        return termsAndConditionsRestService.getById(termsAndConditionsId).getSuccess().isProcurement();
    }

    private boolean shouldHaveSeparateTerms(CompetitionResource competition) {
        return subsidyControlNorthernIrelandEnabled
                && FundingRules.SUBSIDY_CONTROL == competition.getFundingRules()
                && !competition.isExpressionOfInterest();
    }

    private String ifsCompetitionSetup(long competitionId) {
        return "redirect:/competition/setup/" + competitionId;
    }

    private String nonIfsCompetitionSetup(long competitionId) {
        return "redirect:/non-ifs-competition/setup/" + competitionId;
    }

    private String postSaveRedirectForSingleTermsAndConditions(CompetitionResource competition) {
        if (shouldHaveSeparateTerms(competition)) {
            return format("redirect:/competition/setup/%d/section/state-aid-terms-and-conditions", competition.getId());
        }
        return format("redirect:/competition/setup/%d/section/%s", competition.getId(), TERMS_AND_CONDITIONS.getPostMarkCompletePath());
    }

    private boolean isProcurementThirdParty(long termsAndConditionsId) {
        return termsAndConditionsRestService.getById(termsAndConditionsId).getSuccess().isProcurementThirdParty();
    }

    private ServiceResult<Void> saveThirdPartyTermsAndConditionsConfigData(CompetitionResource competition) {
        return competitionThirdPartyConfigRestService.update(competition.getId(), competition.getCompetitionThirdPartyConfigResource()).toServiceResult();
    }

    private void validateThirdPartyConfigFields(CompetitionResource competition, BindingResult bindingResult) {
        if (competition.getCompetitionThirdPartyConfigResource() == null) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "thirdPartyTermsAndConditionsLabel", "Please enter a label to replace terms and conditions."));
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "thirdPartyTermsAndConditionsText", "Please enter a description text for terms and conditions page."));
        } else {
            if (competition.getCompetitionThirdPartyConfigResource().getTermsAndConditionsLabel().isEmpty()) {
                bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "thirdPartyTermsAndConditionsLabel", "Please enter a label to replace terms and conditions."));
            }
            if (competition.getCompetitionThirdPartyConfigResource().getTermsAndConditionsGuidance().isEmpty()) {
                bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "thirdPartyTermsAndConditionsText", "Please enter a description text for terms and conditions page."));
            }
        }
        validateProjectCostGuidanceLink(competition, bindingResult);
    }

    private void validateUploadFragment(boolean isProcurementThirdParty, boolean isProcurement, CompetitionResource competition, BindingResult bindingResult) {
        competition = deleteDataInTermsSetupswitch(isProcurementThirdParty, isProcurement, competition, bindingResult);

        if (competition.getCompetitionTerms() == null) {
            if (isProcurement) {
                bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "termsAndConditionsDoc", "Upload a terms and conditions document."));
            }
        }
    }

    private CompetitionResource deleteDataInTermsSetupswitch(boolean isProcurementThirdParty, boolean isProcurement, CompetitionResource competition, BindingResult bindingResult) {
        if (competition.getCompetitionTerms() != null)  {
            boolean isProcurementSaved = false;
            boolean isProcurementThirdPartySaved = false;
            if (competition.getTermsAndConditions() != null) {
               isProcurementSaved = competition.getTermsAndConditions().isProcurement();
                isProcurementThirdPartySaved = competition.getTermsAndConditions().isProcurementThirdParty();
            }
            if (!bindingResult.hasErrors() && isProcurementThirdParty && isProcurementSaved) {
                competitionSetupRestService.deleteCompetitionTerms(competition.getId());
            }
            if (isProcurement && isProcurementThirdPartySaved) {
                competitionSetupRestService.deleteCompetitionThirdPartyConfigData(competition.getId());
                competitionSetupRestService.deleteCompetitionTerms(competition.getId());
            }
            competition = competitionRestService.getCompetitionById(competition.getId()).getSuccess();
        }
        return competition;
    }

    private MultipartFile getTermsAndConditionsFile(TermsAndConditionsForm termsAndConditionsForm) {
        MultipartFile file = null;
        if (isProcurement(termsAndConditionsForm.getTermsAndConditionsId())) {
            file = termsAndConditionsForm.getTermsAndConditionsDoc();
        }
        if (isProcurementThirdParty(termsAndConditionsForm.getTermsAndConditionsId())) {
            file = termsAndConditionsForm.getThirdPartyTermsAndConditionsDoc();
        }
        return file;
    }

    private String getFileUploadedString(CompetitionResource competitionResource) {
        return competitionResource.getTermsAndConditions().isProcurement() ? "termsAndConditionsDoc"
                : "thirdPartyTermsAndConditionsDoc";
    }

    private void validateProjectCostGuidanceLink(CompetitionResource competition, BindingResult bindingResult) {
        if (competition.getCompetitionThirdPartyConfigResource() == null ||
                competition.getCompetitionThirdPartyConfigResource().getProjectCostGuidanceUrl().isEmpty() ||
                competition.getCompetitionThirdPartyConfigResource().getProjectCostGuidanceUrl().equals(PROJECT_COST_GUIDANCE_HTTP)) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "projectCostGuidanceLink", "Please enter a project costs guidance link."));
        }
    }
}
