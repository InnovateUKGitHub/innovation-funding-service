package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionType;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.service.CompetitionSetupQuestionRestService;
import com.worth.ifs.competitionsetup.form.LandingPageForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import com.worth.ifs.competitionsetup.service.formpopulator.application.ApplicationDetailsFormPopulator;
import com.worth.ifs.competitionsetup.service.formpopulator.application.ApplicationQuestionFormPopulator;
import com.worth.ifs.competitionsetup.service.formpopulator.application.FinancesFormPopulator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;

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
    private Validator validator;

    @Autowired
    private ApplicationQuestionFormPopulator applicationQuestionFormPopulator;
    @Autowired
    private ApplicationDetailsFormPopulator applicationDetailsFormPopulator;
    @Autowired
    private FinancesFormPopulator financesFormPopulator;

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getQuestion(final Long questionId) {
        return competitionSetupQuestionRestService.getByQuestionId(questionId).toServiceResult();
    }

    @Override
	public ServiceResult<Void> updateQuestion(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return competitionSetupQuestionRestService.save(competitionSetupQuestionResource).toServiceResult();
    }

    @Override
    public ServiceResult<Void> validateApplicationQuestions(CompetitionResource competitionResource, LandingPageForm form, BindingResult bindingResult) {
        List<QuestionResource> questionResources = questionService.findByCompetition(competitionResource.getId());
        List<SectionResource> sections = sectionService.getAllByCompetitionId(competitionResource.getId());
        Set<Long> validateableSections = sections.stream().filter(sectionResource ->
                sectionResource.getParentSection() == null &&
                        (sectionResource.getName().equals("Project details")
                                || sectionResource.getName().equals("Application questions")))
                .map(SectionResource::getId)
                .collect(Collectors.toSet());

        form.setQuestion(questionResources.stream()
                .filter(question -> validateableSections.contains(question.getSection()))
                //Application details question has its own form.
                .filter(questionResource -> !CompetitionSetupQuestionType.APPLICATION_DETAILS.getShortName().equals(questionResource.getShortName()))
                .map(questionResource -> (ApplicationQuestionForm) applicationQuestionFormPopulator.populateForm(competitionResource, Optional.of(questionResource.getId())))
                .collect(Collectors.toList()));

        form.setDetailsForm((ApplicationDetailsForm) applicationDetailsFormPopulator.populateForm(competitionResource, Optional.empty()));
        form.setFinanceForm((ApplicationFinanceForm) financesFormPopulator.populateForm(competitionResource, Optional.empty()));

        validator.validate(form, bindingResult);

        if(bindingResult.hasErrors()) {
            translateErrors(bindingResult, form);
            return serviceFailure(Collections.emptyList());
        } else {
            return competitionService.setSetupSectionMarkedAsComplete(competitionResource.getId(), CompetitionSetupSection.APPLICATION_FORM);
        }


    }

    private void translateErrors(BindingResult bindingResult, LandingPageForm form) {


    }
}
