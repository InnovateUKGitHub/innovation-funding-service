package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * View model to support "Your organisation" page
 */
public class YourOrganisationViewModel {

    private boolean showStateAidAgreement;
    private boolean showGrowthTable;

    public YourOrganisationViewModel(boolean showStateAidAgreement, boolean showGrowthTable) {
        this.showStateAidAgreement = showStateAidAgreement;
        this.showGrowthTable = showGrowthTable;
    }

    public List<FormOption> getOrganisationSizeOptions() {
        return simpleMap(OrganisationSize.values(), size -> new FormOption(size.getDescription(), size.name()));
    }

    public boolean isShowStateAidAgreement() {
        return showStateAidAgreement;
    }

    public boolean isShowGrowthTable() {
        return showGrowthTable;
    }
}
