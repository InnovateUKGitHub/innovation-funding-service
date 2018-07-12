package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputRestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

public abstract class AbstractFinanceModelPopulator {

    private SectionService sectionService;
    private FormInputRestService formInputRestService;
    private QuestionService questionService;

    public AbstractFinanceModelPopulator(SectionService sectionService,
                                         FormInputRestService formInputRestService,
                                         QuestionService questionService) {
        this.sectionService = sectionService;
        this.formInputRestService = formInputRestService;
        this.questionService = questionService;
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

    protected Map<Long, List<FormInputResource>> getFinanceSectionChildrenQuestionFormInputs(Long competitionId,
                                                                                             Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap) {
        List<FormInputResource> formInputs = formInputRestService.getByCompetitionIdAndScope(
                competitionId,
                APPLICATION
        ).getSuccess();

        Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs = financeSectionChildrenQuestionsMap
                .values().stream().flatMap(a -> a.stream())
                .collect(toMap(q -> q.getId(), k -> filterFormInputsByQuestion(k.getId(), formInputs)));

        removeAllQuestionsNonEmpty(financeSectionChildrenQuestionFormInputs, financeSectionChildrenQuestionsMap);
        return financeSectionChildrenQuestionFormInputs;
    }

    protected Map<Long, List<QuestionResource>> getFinanceSectionChildrenQuestionsMap(List<SectionResource> financeSubSectionChildren,
                                                                                      Long competitionId) {
        List<QuestionResource> allQuestions = questionService.findByCompetition(competitionId);
        return financeSubSectionChildren.stream()
                .collect(toMap(
                        SectionResource::getId,
                        s -> filterQuestions(s.getQuestions(), allQuestions)
                ));
    }

    private void removeAllQuestionsNonEmpty(Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs,
                                            Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap) {
        Set<Long> questionsWithoutNonEmptyFormInput = financeSectionChildrenQuestionFormInputs.keySet().stream()
                .filter(key -> financeSectionChildrenQuestionFormInputs.get(key).isEmpty()).collect(Collectors.toSet());
        questionsWithoutNonEmptyFormInput.forEach(questionId -> {
            financeSectionChildrenQuestionFormInputs.remove(questionId);
            financeSectionChildrenQuestionsMap.keySet().forEach(key -> financeSectionChildrenQuestionsMap.get(key)
                    .removeIf(questionResource -> questionResource.getId().equals(questionId)));
        });
    }

    protected List<QuestionResource> filterQuestions(final List<Long> ids, final List<QuestionResource> list) {
        return simpleFilter(list, question -> ids.contains(question.getId()));
    }

    protected List<FormInputResource> filterFormInputsByQuestion(final Long id, final List<FormInputResource> list) {
        return simpleFilter(list, input -> id.equals(input.getQuestion()) && !FormInputType.EMPTY.equals(input.getType()));
    }
}
