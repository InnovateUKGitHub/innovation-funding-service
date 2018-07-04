package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

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

    protected List<QuestionResource> filterQuestions(final List<Long> ids, final List<QuestionResource> list) {
        return simpleFilter(list, question -> ids.contains(question.getId()));
    }

    protected List<FormInputResource> filterFormInputsByQuestion(final Long id, final List<FormInputResource> list) {
        return simpleFilter(list, input -> id.equals(input.getQuestion()) && !FormInputType.EMPTY.equals(input.getType()));
    }
}
