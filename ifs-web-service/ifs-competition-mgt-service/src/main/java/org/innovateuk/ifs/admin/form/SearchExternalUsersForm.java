package org.innovateuk.ifs.admin.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SearchExternalUsersForm that = (SearchExternalUsersForm) o;

        return new EqualsBuilder()
                .append(searchString, that.searchString)
                .append(searchCategory, that.searchCategory)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(searchString)
                .append(searchCategory)
                .toHashCode();
    }
}
