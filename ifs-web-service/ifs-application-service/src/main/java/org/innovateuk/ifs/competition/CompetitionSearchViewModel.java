package org.innovateuk.ifs.competition;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;

import java.util.List;

/**
 * Competition public content search view model.
 */
public class CompetitionSearchViewModel {
    public final static Long PAGE_SIZE = 10L;

    private List<InnovationAreaResource> innovationAreas;
    private List<PublicContentItemResource> publicContentItems;
    private Long totalResults;

    private Long selectedInnovationAreaId;
    private String searchKeywords;

    private Long pageNumber;
    private String nextPageLink;
    private String previousPageLink;


    public List<PublicContentItemResource> getPublicContentItems() {
        return publicContentItems;
    }

    public void setPublicContentItems(List<PublicContentItemResource> publicContentItems) {
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

    public Long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Long pageNumber) {
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
        Long lastResultOnPage = (this.pageNumber + 1) * PAGE_SIZE;
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

    private Long getCurrentPageStart() {
        return this.pageNumber * PAGE_SIZE + 1;
    }

    public Long getNextPageStart() {
        return getCurrentPageStart() + PAGE_SIZE;
    }

    public Long getNextPageEnd() {
        return getNextPageStart() + PAGE_SIZE - 1;
    }

    public Long getPreviousPageStart() {
        return getCurrentPageStart() - PAGE_SIZE;
    }

    public Long getPreviousPageEnd() {
        return getCurrentPageStart() - 1;
    }

    public String getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }
}
