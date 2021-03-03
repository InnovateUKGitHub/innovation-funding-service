package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.util.CollectionFunctions;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public abstract class AbstractApplicationModelPopulator {

    private SectionService sectionService;
    private QuestionRestService questionRestService;

    protected AbstractApplicationModelPopulator(SectionService sectionService,
                                                QuestionRestService questionRestService) {
        this.sectionService = sectionService;
        this.questionRestService = questionRestService;
    }

    protected Map<Long, List<QuestionResource>> getSectionQuestions(Long competitionId) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competitionId);
        List<SectionResource> parentSections = sectionService.filterParentSections(allSections);

        List<QuestionResource> questions = questionRestService.findByCompetition(competitionId).getSuccess();

        return parentSections.stream()
                .collect(toMap(
                        SectionResource::getId,
                        s -> getQuestionsBySection(s.getQuestions(), questions)
                ));
    }

    protected Map<Long, SectionResource> getSections(Long competitionId) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competitionId);
        List<SectionResource> parentSections = sectionService.filterParentSections(allSections);

        return parentSections.stream().collect(CollectionFunctions.toLinkedMap(SectionResource::getId,
                Function.identity()));
    }

    private Optional<Long> getSectionIdByType(long competitionId, SectionType sectionType) {
        return sectionService.getSectionsForCompetitionByType(competitionId, sectionType)
                .stream()
                .findFirst()
                .map(SectionResource::getId);
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource>
            questions) {
        return questions.stream().filter(q -> questionIds.contains(q.getId()))
                .sorted()
                .collect(toList());
    }
}
