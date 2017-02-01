package org.innovateuk.ifs.publiccontent.viewmodel;

import org.innovateuk.ifs.publiccontent.viewmodel.submodel.DateViewModel;

import java.util.List;

/**
 * View model for the dates section.
 */
public class DatesViewModel extends AbstractPublicContentViewModel {
    List<DateViewModel> publicContentDates;

    public List<DateViewModel> getPublicContentDates() {
        return publicContentDates;
    }

    public void setPublicContentDates(List<DateViewModel> publicContentDates) {
        this.publicContentDates = publicContentDates;
    }
}
