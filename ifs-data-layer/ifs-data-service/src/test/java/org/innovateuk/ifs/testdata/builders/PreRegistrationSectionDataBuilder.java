package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.testdata.builders.data.PreRegistrationSectionLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generates data for Pre Registration Sections and attaches it to a competition
 */
public class PreRegistrationSectionDataBuilder extends BaseDataBuilder<Void, PreRegistrationSectionDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(PreRegistrationSectionDataBuilder.class);

    public PreRegistrationSectionDataBuilder withPreRegistrationSections(PreRegistrationSectionLine sectionLine) {
        return with(data -> {
            testService.doWithinTransaction(() -> {
                Competition competition = retrieveCompetitionByName(sectionLine.getCompetitionName());
                List<SectionResource> competitionSections = sectionService.getByCompetitionId(competition.getId()).getSuccess();

                competitionSections.stream()
                        .forEach(sectionResource -> {
                            if (sectionResource.getName().equals(sectionLine.getSectionName())) {
                                if (sectionLine.getSubSectionName() == null && sectionLine.getQuestionName() == null) {
                                    markSectionForPreRegistration(sectionResource, sectionLine.getSubSectionName(), sectionLine.getQuestionName());
                                } else if (sectionLine.getQuestionName() == null) {
                                    markSubsectionForPreRegistration(sectionResource, sectionLine.getSubSectionName(), sectionLine.getQuestionName());
                                } else {
                                    markQuestionForPreRegistration(sectionResource, sectionLine.getQuestionName());
                                }
                            }
                        });
            });
        });
    }

    private void  markSectionForPreRegistration(SectionResource section, String subSectionName, String questionName) {
        section.setEnabledForPreRegistration(false);
        section = saveSection(section);

        markQuestionForPreRegistration(section, questionName);

        markSubsectionForPreRegistration(section, subSectionName, questionName);
    }

    private SectionResource saveSection(SectionResource section) {
        return sectionMapper.mapToResource(sectionRepository.save(sectionMapper.mapToDomain(section)));
    }

    private void markSubsectionForPreRegistration(SectionResource section, String subSectionName, String questionName) {
        sectionRepository.findById(section.getId()).get().getChildSections().stream()
                .filter(subSection -> subSectionName == null ? true : subSection.getName().equals(subSectionName))
                .forEach(subSection -> markSectionForPreRegistration(sectionMapper.mapToResource(subSection), subSectionName, questionName));
    }

    private void markQuestionForPreRegistration(SectionResource section, String questionName) {
        section.getQuestions().stream()
                .map(questionId -> questionRepository.findById(questionId).get())
                .filter(question -> questionName == null ? true : question.getName().equals(questionName))
                .forEach(question -> {
                    question.setEnabledForPreRegistration(false);
                    questionService.save(questionMapper.mapToResource(question));
                });
    }

    public static PreRegistrationSectionDataBuilder newCompetitionPreRegistrationSections(ServiceLocator serviceLocator) {
        return new PreRegistrationSectionDataBuilder(Collections.emptyList(), serviceLocator);
    }

    private PreRegistrationSectionDataBuilder(List<BiConsumer<Integer, Void>> multiActions, ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected PreRegistrationSectionDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new PreRegistrationSectionDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }

    @Override
    protected void postProcess(int index, Void instance) {
        super.postProcess(index, instance);
        LOG.info("Enabled Competition Pre Registration Sections");
    }
}
