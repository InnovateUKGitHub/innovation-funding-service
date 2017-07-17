package org.innovateuk.ifs.competition.viewmodel.publiccontent.section;

import org.innovateuk.ifs.competition.viewmodel.publiccontent.AbstractPublicSectionContentViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.submodel.DateViewModel;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 * View model for the public content dates section.
 */
public class DatesViewModel extends AbstractPublicSectionContentViewModel {

    private List<DateViewModel> publicContentDates;

    public List<DateViewModel> getPublicContentDates() {
        return publicContentDates;
    }

    public void setPublicContentDates(List<DateViewModel> publicContentDates) {
        this.publicContentDates = publicContentDates;
    }

    public List<DateViewModel> getSortedEvents() {
        List<DateViewModel> sortedList = getPublicContentDates().stream()
                .sorted(Comparator.comparing(DateViewModel::getDateTime))
                .collect(toList());

        return sortedList;
    }
}
