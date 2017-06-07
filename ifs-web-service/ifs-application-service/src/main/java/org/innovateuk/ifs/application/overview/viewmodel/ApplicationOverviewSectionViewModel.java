package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * View model for the application overview - section
 */
public class ApplicationOverviewSectionViewModel {
    private SortedMap<Long, SectionResource> sections;
    private Map<Long, List<SectionResource>> subSections;
    private Map<Long, List<QuestionResource>> sectionQuestions;
    private Map<Long, AssignButtonsViewModel> assignButtonViewModels;
    private List<SectionResource> financeSections;
    private Boolean hasFinanceSection;
    private Long financeSectionId;

    public ApplicationOverviewSectionViewModel(SortedMap<Long, SectionResource> sections, Map<Long, List<SectionResource>> subSections,
                                               Map<Long, List<QuestionResource>> sectionQuestions, List<SectionResource> financeSections,
                                               Boolean hasFinanceSection, Long financeSectionId, Map<Long, AssignButtonsViewModel> assignButtonViewModels) {
        this.sections = sections;
        this.subSections = subSections;
        this.sectionQuestions = sectionQuestions;
        this.financeSections = financeSections;
        this.hasFinanceSection = hasFinanceSection;
        this.financeSectionId = financeSectionId;
        this.assignButtonViewModels = assignButtonViewModels;
    }

    public SortedMap<Long, SectionResource> getSections() {
        return sections;
    }

    public Map<Long, List<SectionResource>> getSubSections() {
        return subSections;
    }

    public Map<Long, List<QuestionResource>> getSectionQuestions() {
        return sectionQuestions;
    }

    public List<SectionResource> getFinanceSections() {
        return financeSections;
    }

    public Boolean getHasFinanceSection() {
        return hasFinanceSection;
    }

    public Long getFinanceSectionId() {
        return financeSectionId;
    }

    public Map<Long, AssignButtonsViewModel> getAssignButtonViewModels() {
        return assignButtonViewModels;
    }

    public Boolean hasSubSection(Long sectionId) {
        return subSections.get(sectionId).size() != 0;
    }
}
