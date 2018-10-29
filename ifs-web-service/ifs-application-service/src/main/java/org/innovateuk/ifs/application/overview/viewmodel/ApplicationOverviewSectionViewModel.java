package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * View model for the application overview - section
 */
public class ApplicationOverviewSectionViewModel {
    private List<SectionResource> sections;
    private Map<Long, List<SectionResource>> subSections;
    private Map<Long, List<QuestionResource>> sectionQuestions;
    private Map<Long, AssignButtonsViewModel> assignButtonViewModels;
    private boolean hasFinanceSection;
    private Long financeSectionId;

    public ApplicationOverviewSectionViewModel(List<SectionResource> sections,
                                               Map<Long, List<SectionResource>> subSections,
                                               Map<Long, List<QuestionResource>> sectionQuestions,
                                               boolean hasFinanceSection,
                                               Long financeSectionId,
                                               Map<Long, AssignButtonsViewModel> assignButtonViewModels) {
        this.sections = sections;
        this.subSections = subSections;
        this.sectionQuestions = sectionQuestions;
        this.hasFinanceSection = hasFinanceSection;
        this.financeSectionId = financeSectionId;
        this.assignButtonViewModels = assignButtonViewModels;
    }

    public List<SectionResource> getSections() {
        return sections;
    }

    public Map<Long, List<SectionResource>> getSubSections() {
        return subSections;
    }

    public Map<Long, List<QuestionResource>> getSectionQuestions() {
        return sectionQuestions;
    }

    public boolean isHasFinanceSection() {
        return hasFinanceSection;
    }

    public Long getFinanceSectionId() {
        return financeSectionId;
    }

    public Map<Long, AssignButtonsViewModel> getAssignButtonViewModels() {
        return assignButtonViewModels;
    }

    public boolean hasSubSection(long sectionId) {
        return subSections.containsKey(sectionId) && !subSections.get(sectionId).isEmpty();
    }
}
