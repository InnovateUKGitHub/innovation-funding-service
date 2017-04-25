package org.innovateuk.ifs.application.viewmodel.overview;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;

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
    private List<SectionResource> financeSections;
    private Boolean hasFinanceSection;
    private Long financeSectionId;

    public ApplicationOverviewSectionViewModel(SortedMap<Long, SectionResource> sections, Map<Long, List<SectionResource>> subSections,
                                               Map<Long, List<QuestionResource>> sectionQuestions, List<SectionResource> financeSections,
                                               Boolean hasFinanceSection, Long financeSectionId) {
        this.sections = sections;
        this.subSections = subSections;
        this.sectionQuestions = sectionQuestions;
        this.financeSections = financeSections;
        this.hasFinanceSection = hasFinanceSection;
        this.financeSectionId = financeSectionId;
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
}
