package org.innovateuk.ifs.project.monitoringofficer.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.validation.constraints.FieldComparison;
import org.innovateuk.ifs.commons.validation.predicate.BiPredicateProvider;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.io.Serializable;
import java.util.function.BiPredicate;

@FieldComparison(
        firstField = "keywordSearch",
        secondField = "keywordSearchMinLength",
        message = "{validation.modashboard.filterprojects.keywordsearch.min.length}",
        predicate = MODashboardForm.KeywordSearchMinPredicateProvider.class)
@FieldComparison(
        firstField = "keywordSearch",
        secondField = "keywordSearchMaxLength",
        message = "{validation.modashboard.filterprojects.keywordsearch.max.length}",
        predicate = MODashboardForm.KeywordSearchMaxPredicateProvider.class)
public class MODashboardForm extends BaseBindingResultTarget {

    private static final Integer KEYWORD_SEARCH_MIN_LENGTH = 3;
    private static final Integer KEYWORD_SEARCH_MAX_LENGTH = 100;

    private String keywordSearch;
    private boolean projectInSetup;
    private boolean previousProject;

    private boolean documentsComplete;
    private boolean documentsIncomplete;
    private boolean documentsAwaitingReview;

    private boolean spendProfileComplete;
    private boolean spendProfileIncomplete;
    private boolean spendProfileAwaitingReview;

    public MODashboardForm() {
    }

    public String getKeywordSearch() {
        return keywordSearch;
    }

    public void setKeywordSearch(String keywordSearch) {
        this.keywordSearch = keywordSearch;
    }

    public boolean isProjectInSetup() {
        return projectInSetup;
    }

    public void setProjectInSetup(boolean projectInSetup) {
        this.projectInSetup = projectInSetup;
    }

    public boolean isPreviousProject() {
        return previousProject;
    }

    public void setPreviousProject(boolean previousProject) {
        this.previousProject = previousProject;
    }

    public boolean isDocumentsComplete() {
        return documentsComplete;
    }

    public void setDocumentsComplete(boolean documentsComplete) {
        this.documentsComplete = documentsComplete;
    }

    public boolean isDocumentsIncomplete() {
        return documentsIncomplete;
    }

    public void setDocumentsIncomplete(boolean documentsIncomplete) {
        this.documentsIncomplete = documentsIncomplete;
    }

    public boolean isDocumentsAwaitingReview() {
        return documentsAwaitingReview;
    }

    public void setDocumentsAwaitingReview(boolean documentsAwaitingReview) {
        this.documentsAwaitingReview = documentsAwaitingReview;
    }

    public boolean isSpendProfileComplete() {
        return spendProfileComplete;
    }

    public void setSpendProfileComplete(boolean spendProfileComplete) {
        this.spendProfileComplete = spendProfileComplete;
    }

    public boolean isSpendProfileIncomplete() {
        return spendProfileIncomplete;
    }

    public void setSpendProfileIncomplete(boolean spendProfileIncomplete) {
        this.spendProfileIncomplete = spendProfileIncomplete;
    }

    public boolean isSpendProfileAwaitingReview() {
        return spendProfileAwaitingReview;
    }

    public void setSpendProfileAwaitingReview(boolean spendProfileAwaitingReview) {
        this.spendProfileAwaitingReview = spendProfileAwaitingReview;
    }

    @JsonIgnore
    public Integer getKeywordSearchMinLength() {
        return KEYWORD_SEARCH_MIN_LENGTH;
    }

    @JsonIgnore
    public Integer getKeywordSearchMaxLength() {
        return KEYWORD_SEARCH_MAX_LENGTH;
    }

    public static class KeywordSearchMinPredicateProvider implements BiPredicateProvider<String, Integer> {
        public BiPredicate<String, Integer> predicate() {
            return (keywordSearch, min) -> isKeywordSearchSatisfiesMin(keywordSearch, min);
        }

        private boolean isKeywordSearchSatisfiesMin(String keywordSearch, Integer min) {
            if(keywordSearch != null && !keywordSearch.isEmpty()) {
                return keywordSearch.length() >= min.intValue();
            }

            return true;
        }
    }

    public static class KeywordSearchMaxPredicateProvider implements BiPredicateProvider<String, Integer> {
        public BiPredicate<String, Integer> predicate() {
            return (keywordSearch, max) -> isKeywordSearchSatisfiesMax(keywordSearch, max);
        }

        private boolean isKeywordSearchSatisfiesMax(String keywordSearch, Integer max) {
            if(keywordSearch != null && !keywordSearch.isEmpty()) {
                return keywordSearch.length() <= max.intValue();
            }

            return true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MODashboardForm that = (MODashboardForm) o;

        return new EqualsBuilder()
                .append(keywordSearch, that.keywordSearch)
                .append(projectInSetup, that.projectInSetup)
                .append(previousProject, that.previousProject)
                .append(documentsComplete, that.documentsComplete)
                .append(documentsIncomplete, that.documentsIncomplete)
                .append(documentsAwaitingReview, that.documentsAwaitingReview)
                .append(spendProfileComplete, that.spendProfileComplete)
                .append(spendProfileIncomplete, that.spendProfileIncomplete)
                .append(spendProfileAwaitingReview, that.spendProfileAwaitingReview)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(keywordSearch)
                .append(projectInSetup)
                .append(previousProject)
                .append(documentsComplete)
                .append(documentsIncomplete)
                .append(documentsAwaitingReview)
                .append(spendProfileComplete)
                .append(spendProfileIncomplete)
                .append(spendProfileAwaitingReview)
                .toHashCode();
    }
}
