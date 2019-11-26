package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * View model to support "Your organisation" pages
 */
public class YourOrganisationViewModel {

    private boolean showStateAidAgreement;
    private boolean fundingSectionComplete;
    private boolean h2020;

    public YourOrganisationViewModel(boolean showStateAidAgreement, boolean fundingSectionComplete, boolean h2020) {
        this.showStateAidAgreement = showStateAidAgreement;
        this.fundingSectionComplete = fundingSectionComplete;
        this.h2020 = h2020;
    }

    public List<FormOption> getOrganisationSizeOptions() {
        return simpleMap(OrganisationSize.values(), size -> new FormOption(size.getDescription(), size.name()));
    }

    public boolean isShowStateAidAgreement() {
        return showStateAidAgreement;
    }

    public boolean isShowOrganisationSizeAlert() {
        return fundingSectionComplete;
    }

    public boolean isH2020() {
        return h2020;
    }
}
