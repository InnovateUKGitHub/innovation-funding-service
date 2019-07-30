package org.innovateuk.ifs.management.publiccontent.viewmodel;

import org.innovateuk.ifs.management.publiccontent.viewmodel.submodel.DateViewModel;

import java.util.ArrayList;
import java.util.Comparator;
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
        List<DateViewModel> emptyDates = new ArrayList();
        List<DateViewModel> sortedList = new ArrayList();

        listToBeSorted.stream()
                .filter(dateViewModel -> null != dateViewModel.getDateTime())
                .sorted(Comparator.comparing(DateViewModel::getDateTime))
                .forEach(dateViewModel -> sortedList.add(dateViewModel));

        listToBeSorted.stream()
                .filter(dateViewModel -> null == dateViewModel.getDateTime())
                .forEach(dateViewModel -> emptyDates.add(dateViewModel));

        sortedList.addAll(emptyDates);

        return sortedList;
    }


}
