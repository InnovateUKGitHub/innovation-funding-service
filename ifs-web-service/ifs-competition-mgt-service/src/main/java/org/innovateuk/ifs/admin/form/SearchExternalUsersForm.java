package org.innovateuk.ifs.admin.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.user.resource.SearchCategory;

import javax.validation.constraints.Size;

public class SearchExternalUsersForm extends BaseBindingResultTarget {
    @Size.List ({
            @Size(min=3, message="{validation.standard.user.search.min}")
    })
    private String searchString;

    private SearchCategory searchCategory;

    public SearchExternalUsersForm() {
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public SearchCategory getSearchCategory() {
        return searchCategory;
    }

    public void setSearchCategory(SearchCategory searchCategory) {
        this.searchCategory = searchCategory;
    }
}
