package org.innovateuk.ifs.management.competition.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.management.competition.setup.core.form.TermsAndConditionsForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.TermsAndConditionsFormPopulator;
import org.innovateuk.ifs.management.competition.setup.core.populator.TermsAndConditionsModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.TERMS_AND_CONDITIONS;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;

@Controller
@RequestMapping("/competition/setup")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionSetupTermsAndConditionsController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionSetupTermsAndConditionsController {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupTermsAndConditionsController.class);
    public static final String COMPETITION_ID_KEY = "competitionId";
    public static final String COMPETITION_SETUP_FORM_KEY = "competitionSetupForm";
    private static final String MODEL = "model";

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

    @GetMapping("/{competitionId}/section/terms-and-conditions")
    public String editTermsAndConditions(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                         Model model,
                                         UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        model.addAttribute(MODEL, termsAndConditionsModelPopulator.populateModel(competition, loggedInUser, false));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, termsAndConditionsFormPopulator.populateForm(competition));

        return "competition/setup";
    }

    @GetMapping("/{competitionId}/section/subsidy-control-terms-and-conditions")
    public String editSubsidyControlTermsAndConditions(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                       Model model,
                                                       UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (FundingRules.SUBSIDY_CONTROL != competition.getFundingRules()) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        model.addAttribute(MODEL, termsAndConditionsModelPopulator.populateModel(competition, loggedInUser, true));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, termsAndConditionsFormPopulator.populateFormForSubsidyControl(competition));

        return "competition/setup";
    }

    @PostMapping("/{competitionId}/section/terms-and-conditions")
    public String submitTermsAndConditionsSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm competitionSetupForm,
                                                         @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                         ValidationHandler validationHandler,
                                                         @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                         UserResource loggedInUser,
                                                         Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (isProcurement(competitionSetupForm.getTermsAndConditionsId())) {
            if (competition.getCompetitionTerms() == null) {
                bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "termsAndConditionsDoc", "Upload a terms and conditions document."));
            }
        } else {
            competitionSetupRestService.deleteCompetitionTerms(competitionId);
        }

        return termsAndConditionsSection(competitionSetupForm, validationHandler, competition, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/section/subsidy-control-terms-and-conditions")
    public String submitSubsidyControlTermsAndConditionsSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm competitionSetupForm,
                                                         @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                         ValidationHandler validationHandler,
                                                         @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                         UserResource loggedInUser,
                                                         Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (FundingRules.SUBSIDY_CONTROL != competition.getFundingRules()) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        Supplier<String> successView = () -> format("redirect:/competition/setup/%d/section/terms-and-conditions", competition.getId(), TERMS_AND_CONDITIONS.getPostMarkCompletePath());

        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, termsAndConditionsModelPopulator.populateModel(competition, loggedInUser, false));
            return "competition/setup";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = competitionRestService.updateSubsidyControlTermsAndConditionsForCompetition(
                    competition.getId(),
                    competitionSetupForm.getTermsAndConditionsId()
            ).toServiceResult().andOnSuccess(() -> {
                if (competitionSetupForm.isMarkAsCompleteAction()) {
                    return competitionSetupRestService.markSectionComplete(competition.getId(), TERMS_AND_CONDITIONS).toServiceResult();
                }
                return serviceSuccess();
            });

        return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, successView);
        });

    }

    private boolean isProcurement(long termsAndConditionsId) {
        return termsAndConditionsRestService.getById(termsAndConditionsId).getSuccess().isProcurement();
    }

    @PostMapping(path="/{competitionId}/section/terms-and-conditions", params = "uploadTermsAndConditionsDoc")
    public String uploadTermsAndConditions(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm termsAndConditionsForm,
                                           @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                           UserResource loggedInUser,
                                           Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        Supplier<String> success = () -> format("redirect:/competition/setup/%d/section/terms-and-conditions", + competition.getId());
        Supplier<String> failure = () -> termsAndConditionsSection(termsAndConditionsForm, validationHandler, competition, loggedInUser, model);

        MultipartFile file = termsAndConditionsForm.getTermsAndConditionsDoc();
        RestResult<FileEntryResource> uploadResult = competitionSetupRestService.uploadCompetitionTerms(competitionId, file.getContentType(), file.getSize(),
                file.getOriginalFilename(), getMultipartFileBytes(file));

        termsAndConditionsForm.setMarkAsCompleteAction(false);
        competitionSetupService.saveCompetitionSetupSection(termsAndConditionsForm, competition, CompetitionSetupSection.TERMS_AND_CONDITIONS);

        return validationHandler.addAnyErrors(error(uploadResult.getErrors()), fileUploadField("termsAndConditionsDoc"), defaultConverters())
                .failNowOrSucceedWith(failure, success);
    }

    @PostMapping(path="/{competitionId}/section/terms-and-conditions", params = "deleteTermsAndConditionsDoc")
    public String deleteTermsAndConditions(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm termsAndConditionsForm,
                                           @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                           Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        Supplier<String> failureAndSuccessView = () -> format("redirect:/competition/setup/%d/section/terms-and-conditions", + competition.getId());

        RestResult<Void> deleteResult = competitionSetupRestService.deleteCompetitionTerms(competitionId);
        return validationHandler.addAnyErrors(error(deleteResult.getErrors()), fileUploadField("termsAndConditionsDoc"), defaultConverters())
                .failNowOrSucceedWith(failureAndSuccessView, failureAndSuccessView);
    }

    private String termsAndConditionsSection(TermsAndConditionsForm competitionSetupForm,
                                             ValidationHandler validationHandler,
                                             CompetitionResource competition,
                                             UserResource loggedInUser,
                                             Model model) {
        if (competition.isNonIfs()) {
            return format("redirect:/non-ifs-competition/setup/%d", competition.getId());
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId())) {
            return format("redirect:/competition/setup/%d", competition.getId());
        }

        Supplier<String> successView;
        if (FundingRules.SUBSIDY_CONTROL == competition.getFundingRules()) {
            successView = () -> format("redirect:/competition/setup/%d/section/subsidy-control-terms-and-conditions", competition.getId(), TERMS_AND_CONDITIONS.getPostMarkCompletePath());
        } else {
            successView = () -> format("redirect:/competition/setup/%d/section/%s", competition.getId(), TERMS_AND_CONDITIONS.getPostMarkCompletePath());
        }

        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, termsAndConditionsModelPopulator.populateModel(competition, loggedInUser, false));
            return "competition/setup";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = competitionRestService.updateTermsAndConditionsForCompetition(
                    competition.getId(),
                    competitionSetupForm.getTermsAndConditionsId()
            ).toServiceResult().andOnSuccess(() -> {
                if (FundingRules.SUBSIDY_CONTROL != competition.getFundingRules() && competitionSetupForm.isMarkAsCompleteAction()) {
                    return competitionSetupRestService.markSectionComplete(competition.getId(), TERMS_AND_CONDITIONS).toServiceResult();
                }
                return serviceSuccess();
            });
            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, successView);
        });
    }
}
