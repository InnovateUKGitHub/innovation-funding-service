package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.form.resource.SectionResource;

import java.util.ArrayList;
import java.util.List;

public class AbstractFinanceModelPopulator {

    private SectionService sectionService;

    public AbstractFinanceModelPopulator(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    protected List<SectionResource> getFinanceSubSectionChildren(Long competitionId, SectionResource section) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competitionId);
        List<SectionResource> financeSectionChildren = sectionService.findResourceByIdInList(section.getChildSections(), allSections);
        List<SectionResource> financeSubSectionChildren = new ArrayList<>();
        financeSectionChildren.forEach(sectionResource -> {
                    if (!sectionResource.getChildSections().isEmpty()) {
                        financeSubSectionChildren.addAll(
                                sectionService.findResourceByIdInList(sectionResource.getChildSections(), allSections)
                        );
                    }
                }
        );
        return financeSubSectionChildren;
    }
}
