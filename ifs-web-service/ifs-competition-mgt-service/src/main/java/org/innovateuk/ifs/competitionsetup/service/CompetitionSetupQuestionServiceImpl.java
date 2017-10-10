package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupQuestionRestService;
import org.innovateuk.ifs.competitionsetup.form.LandingPageForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.application.ApplicationDetailsFormPopulator;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.application.ApplicationProjectFormPopulator;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.application.ApplicationQuestionFormPopulator;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.application.ApplicationFinanceFormPopulator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

@Service
public class CompetitionSetupQuestionServiceImpl implements CompetitionSetupQuestionService {

	private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionServiceImpl.class);

	@Autowired
	private CompetitionSetupQuestionRestService competitionSetupQuestionRestService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @Autowired
    private ApplicationQuestionFormPopulator applicationQuestionFormPopulator;
    @Autowired
    private ApplicationProjectFormPopulator applicationProjectFormPopulator;
    @Autowired
    private ApplicationDetailsFormPopulator applicationDetailsFormPopulator;
    @Autowired
    private ApplicationFinanceFormPopulator applicationFinanceFormPopulator;

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> createDefaultQuestion(Long competitionId) {
        return competitionSetupQuestionRestService.addDefaultToCompetition(competitionId).toServiceResult();
    }

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getQuestion(final Long questionId) {
        return competitionSetupQuestionRestService.getByQuestionId(questionId).toServiceResult();
    }

    @Override
	public ServiceResult<Void> updateQuestion(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return competitionSetupQuestionRestService.save(competitionSetupQuestionResource).toServiceResult();
    }

    @Override
    public ServiceResult<Void> deleteQuestion(Long questionId) {
        return competitionSetupQuestionRestService.deleteById(questionId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> validateApplicationQuestions(CompetitionResource competitionResource, LandingPageForm form, BindingResult bindingResult) {
        List<QuestionResource> questionResources = questionService.findByCompetition(competitionResource.getId());
        List<SectionResource> sections = sectionService.getAllByCompetitionId(competitionResource.getId());
        Set<SectionResource> parentSections = sections.stream()
                .filter(sectionResource -> sectionResource.getParentSection() == null)
                .collect(Collectors.toSet());

        Set<Long> projectDetailsSections = parentSections.stream()
                .filter(sectionResource -> sectionResource.getName().equals("Project details"))
                .map(SectionResource::getId)
                .collect(Collectors.toSet());

        Set<Long> applicationSections = parentSections.stream()
                .filter(sectionResource -> sectionResource.getName().equals("Application questions"))
                .map(SectionResource::getId)
                .collect(Collectors.toSet());

        form.setQuestions(questionResources.stream()
                .filter(question -> projectDetailsSections.contains(question.getSection()))
                //Application details question has its own form.
                .filter(questionResource -> !CompetitionSetupQuestionType.APPLICATION_DETAILS.getShortName().equals(questionResource.getShortName()))
                .map(questionResource -> applicationProjectFormPopulator.populateForm(competitionResource, Optional.of(questionResource.getId())))
                .collect(Collectors.toList()));

        form.getQuestions().addAll(questionResources.stream()
                .filter(question -> applicationSections.contains(question.getSection()))
                .map(questionResource -> applicationQuestionFormPopulator.populateForm(competitionResource, Optional.of(questionResource.getId())))
                .collect(Collectors.toList()));

        form.setDetailsForm((ApplicationDetailsForm) applicationDetailsFormPopulator.populateForm(competitionResource, Optional.empty()));
        form.setFinanceForm((ApplicationFinanceForm) applicationFinanceFormPopulator.populateForm(competitionResource, Optional.empty()));

        validator.validate(form, bindingResult);

        if(bindingResult.hasErrors()) {
            return serviceFailure(Collections.emptyList());
        } else {
            return competitionService.setSetupSectionMarkedAsComplete(competitionResource.getId(), CompetitionSetupSection.APPLICATION_FORM);
        }


    }

}
