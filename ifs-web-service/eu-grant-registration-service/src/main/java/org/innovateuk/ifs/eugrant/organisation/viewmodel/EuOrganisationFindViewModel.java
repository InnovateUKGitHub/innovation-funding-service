package org.innovateuk.ifs.eugrant.organisation.viewmodel;

import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;

import java.util.List;

public class EuOrganisationFindViewModel {

    private final String searchLabel;
    private final String searchHint;
    private final EuOrganisationType type;
    private final List<OrganisationSearchResult> results;

    public EuOrganisationFindViewModel(String searchLabel, String searchHint, EuOrganisationType type, List<OrganisationSearchResult> results) {
        this.searchLabel = searchLabel;
        this.searchHint = searchHint;
        this.type = type;
        this.results = results;
    }

    public String getSearchLabel() {
        return searchLabel;
    }

    public String getSearchHint() {
        return searchHint;
    }

    public EuOrganisationType getType() {
        return type;
    }

    public List<OrganisationSearchResult> getResults() {
        return results;
    }
}
