package org.innovateuk.ifs.management.form;

import java.util.Optional;

import static java.util.Optional.empty;

/**
 * Form for handling filtering on the ineligible applications page.
 */
public class IneligibleApplicationsForm {
    private String filterSearch = "";
    private Optional<Boolean> filterInform = empty();

    public String getFilterSearch() {
        return filterSearch;
    }

    public void setFilterSearch(String filterSearch) {
        this.filterSearch = filterSearch;
    }

    public Optional<Boolean> getFilterInform() {
        return filterInform;
    }

    public void setFilterInform(Optional<Boolean> filterInform) {
        this.filterInform = filterInform;
    }
}
