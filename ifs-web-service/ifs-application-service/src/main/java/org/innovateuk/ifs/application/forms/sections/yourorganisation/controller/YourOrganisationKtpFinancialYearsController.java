package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import org.innovateuk.ifs.application.forms.questions.grantagreement.form.GrantAgreementForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormSaver;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.innovateuk.ifs.finance.service.ApplicationYourOrganisationRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Supplier;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MODEL_ATTRIBUTE_FORM;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * The Controller for the "Your organisation" page in the Application Form process when its a ktp competition.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-organisation/competition/{competitionId}/organisation/{organisationId}/section/{sectionId}/ktp-financial-years")
public class YourOrganisationKtpFinancialYearsController extends AbstractYourOrganisationFormController<YourOrganisationKtpFinancialYearsForm> {

    @Autowired
    private ApplicationYourOrganisationRestService yourOrganisationRestService;

    @Autowired
    private YourOrganisationKtpFinancialYearsFormPopulator formPopulator;

    @Autowired
    private YourOrganisationKtpFinancialYearsFormSaver saver;

    @Autowired
    private FormInputRestService formInputRestService;

    @Override
    protected YourOrganisationKtpFinancialYearsForm populateForm(long applicationId, long organisationId) {
        OrganisationFinancesKtpYearsResource finances = yourOrganisationRestService.getOrganisationKtpYears(applicationId, organisationId).getSuccess();
        return formPopulator.populate(finances);
    }

    @Override
    protected String formFragment() {
        return "ktp-financial-years";
    }

    @Override
    protected void update(long applicationId, long organisationId, long userId, YourOrganisationKtpFinancialYearsForm form) {
         saver.save(applicationId, organisationId, userId, form, yourOrganisationRestService);
    }

    @Override
    protected String redirectToViewPage(long applicationId, long competitionId, long organisationId, long sectionId) {
        return "redirect:" + APPLICATION_BASE_URL +
                String.format("%d/form/your-organisation/competition/%d/organisation/%d/section/%d/ktp-financial-years",
                        applicationId,
                        competitionId,
                        organisationId,
                        sectionId);
    }

    /*
    @PostMapping(params = "uploadAdditionalInfo")
    public String uploadAdditionalInfo(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) GrantAgreementForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       Model model,
                                       @PathVariable long applicationId,
                                       @PathVariable long questionId,
                                       UserResource user) {

        Supplier<String> failureAndSuccessView = () -> redirectToViewPage(form, bindingResult, model, applicationId, questionId, user);
        MultipartFile file = form.getGrantAgreement();
        return validationHandler.performFileUpload("grantAgreement", failureAndSuccessView, () -> euGrantTransferRestService
                .uploadGrantAgreement(applicationId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file)));
    }

    @PostMapping(params = "removeAdditionalInfo")
    public String removeAdditionalInfo(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) GrantAgreementForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       Model model,
                                       @PathVariable long applicationId,
                                       @PathVariable long questionId,
                                       UserResource user) {

        RestResult<Void> sendResult = euGrantTransferRestService
                .deleteGrantAgreement(applicationId);

        Supplier<String> failureAndSuccesView = () -> redirectToViewPage(form, bindingResult, model, applicationId, questionId, user);

        return validationHandler.addAnyErrors(sendResult.getErrors())
                .failNowOrSucceedWith(failureAndSuccesView, failureAndSuccesView);
    }

    @GetMapping("/download")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAdditionalInfo(@PathVariable long applicationId) {
        CompetitionSetupQuestionResource question = questionSetupCompetitionRestService.getByQuestionId(questionId).getSuccess();
        return getFileResponseEntity(formInputRestService.downloadFile(question.getTemplateFormInput()).getSuccess(),
                formInputRestService.findFile(question.getTemplateFormInput()).getSuccess());
    }
*/

}
