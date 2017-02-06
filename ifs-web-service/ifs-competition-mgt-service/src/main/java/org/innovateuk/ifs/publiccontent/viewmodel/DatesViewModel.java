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

    public List<DateViewModel> getSortedEvents() {
        List<DateViewModel> listToBeSorted = getPublicContentDates();
        listToBeSorted.sort((o1, o2) -> compareDateIfNotNull(o1, o2));

        return listToBeSorted;
    }

    private Integer compareDateIfNotNull(DateViewModel dateViewModelOne, DateViewModel dateViewModelTwo) {
        if(null == dateViewModelOne.getDateTime() && null == dateViewModelTwo.getDateTime()) {
            return 0;
        } else if(null == dateViewModelOne.getDateTime()) {
            return 1;
        } else if(null == dateViewModelTwo.getDateTime()) {
            return -1;
        }

        return dateViewModelOne.getDateTime().compareTo(dateViewModelTwo.getDateTime());
    }
}
