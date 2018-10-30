package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.OpenSectionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.section.FinanceOverviewSectionViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Locale;
import java.util.Optional;

/**
 * Finance overview section view models.
 */
@Component
public class FinanceOverviewSectionPopulator extends AbstractSectionPopulator<FinanceOverviewSectionViewModel> {

    private OpenSectionModelPopulator openSectionModelPopulator;
    private FormInputViewModelGenerator formInputViewModelGenerator;
    private MessageSource messageSource;

    public FinanceOverviewSectionPopulator(final ApplicationNavigationPopulator navigationPopulator,
                                           final OpenSectionModelPopulator openSectionModelPopulator,
                                           final FormInputViewModelGenerator formInputViewModelGenerator,
                                           final MessageSource messageSource) {
        super(navigationPopulator);
        this.openSectionModelPopulator = openSectionModelPopulator;
        this.formInputViewModelGenerator = formInputViewModelGenerator;
        this.messageSource = messageSource;
    }

    @Override
    protected void populateNoReturn(ApplicantSectionResource section,
                                    ApplicationForm form,
                                    FinanceOverviewSectionViewModel viewModel,
                                    Model model,
                                    BindingResult bindingResult,
                                    Boolean readOnly,
                                    Optional<Long> applicantOrganisationId) {

        updateFinancesOverviewSectionDescription(section.getApplication(), section.getSection());

        viewModel.setOpenSectionViewModel((OpenSectionViewModel) openSectionModelPopulator.populateModel(form, model,
                bindingResult, section));
    }

    private SectionResource updateFinancesOverviewSectionDescription(ApplicationResource application,
                                                                     SectionResource section) {
        String description = application.isCollaborativeProject() ?
                messageSource.getMessage("ifs.section.financesOverview.collaborativeProject.description", null,
                        Locale.ENGLISH) :
                messageSource.getMessage("ifs.section.financesOverview.description", null, Locale.ENGLISH);

        section.setDescription(description);
        return section;
    }

    @Override
    protected FinanceOverviewSectionViewModel createNew(ApplicantSectionResource section,
                                                        ApplicationForm form,
                                                        Boolean readOnly,
                                                        Optional<Long> applicantOrganisationId,
                                                        Boolean readOnlyAllApplicantApplicationFinances) {
        return new FinanceOverviewSectionViewModel(section, formInputViewModelGenerator.fromSection(section, section,
                form, readOnly), getNavigationViewModel(section), readOnly, applicantOrganisationId,
                readOnlyAllApplicantApplicationFinances);
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.OVERVIEW_FINANCES;
    }

}

