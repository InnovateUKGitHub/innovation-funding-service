package org.innovateuk.ifs.competition.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * Competition public content search view model.
 */
public class CompetitionSearchViewModel {
    public final static Integer PAGE_SIZE = 10;

    private List<InnovationAreaResource> innovationAreas;
    private List<PublicContentItemViewModel> publicContentItems;
    private Long totalResults;

    private Long selectedInnovationAreaId;
    private String searchKeywords;

    private Integer pageNumber;
    private String nextPageLink;
    private String previousPageLink;


    public List<PublicContentItemViewModel> getPublicContentItems() {
        return publicContentItems;
    }

    public void setPublicContentItems(List<PublicContentItemViewModel> publicContentItems) {
        this.publicContentItems = publicContentItems;
    }

    public List<InnovationAreaResource> getInnovationAreas() {
        return innovationAreas;
    }

    public void setInnovationAreas(List<InnovationAreaResource> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }

    public Long getSelectedInnovationAreaId() {
        return selectedInnovationAreaId;
    }

    public void setSelectedInnovationAreaId(Long selectedInnovationAreaId) {
        this.selectedInnovationAreaId = selectedInnovationAreaId;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getNextPageLink() {
        return nextPageLink;
    }

    public void setNextPageLink(String nextPage) {
        this.nextPageLink = nextPage;
    }

    public String getPreviousPageLink() {
        return previousPageLink;
    }

    public void setPreviousPageLink(String previousPage) {
        this.previousPageLink = previousPage;
    }

    public boolean hasNextPage() {
        int lastResultOnPage = (this.pageNumber + 1) * PAGE_SIZE;
        return lastResultOnPage < this.totalResults;
    }

    public boolean hasPreviousPage() {
        return pageNumber > 0;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Long totalResults) {
        this.totalResults = totalResults;
    }

    private int getCurrentPageStart() {
        return this.pageNumber * PAGE_SIZE + 1;
    }

    public int getNextPageStart() {
        return getCurrentPageStart() + PAGE_SIZE;
    }

    public int getNextPageEnd() {
        int nextPageEnd = getNextPageStart() + PAGE_SIZE - 1;

        if (nextPageEnd > totalResults){
            return totalResults.intValue();
        }
        return nextPageEnd;
    }

    public int getPreviousPageStart() {
        return getCurrentPageStart() - PAGE_SIZE;
    }

    public int getPreviousPageEnd() {
        return getCurrentPageStart() - 1;
    }

    public String getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }
}
