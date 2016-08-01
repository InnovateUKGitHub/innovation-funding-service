package com.worth.ifs.competitionsetup.service.modelpopulator;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.service.CompetitionsRestService;
import com.worth.ifs.competitionsetup.model.Question;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputTypeResource;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * populates the model for the initial details competition setup section.
 */
@Service
public class ApplicationFormModelPopulator implements CompetitionSetupSectionModelPopulator {

	@Autowired
	private CompetitionService competitionService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputService formInputService;


    @Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.APPLICATION_FORM;
	}

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource) {
		List<SectionResource> sections = sectionService.getAllByCompetitionId(competitionResource.getId());
		List<QuestionResource> questionResources = questionService.findByCompetition(competitionResource.getId());
        List<Question> questions = new ArrayList();

        List<SectionResource> generalSections = sections.stream().filter(sectionResource -> sectionResource.getType() == SectionType.GENERAL).collect(Collectors.toList());
        List<SectionResource> parentSections = generalSections.stream().filter(sectionResource -> sectionResource.getParentSection() == null).collect(Collectors.toList());

        List<Long> questionIds = parentSections.stream().filter(sectionResource -> sectionResource.getName().equals("Application questions")).findFirst().get().getQuestions();

        questionResources = questionResources.stream().filter(questionResource -> questionIds.contains(questionResource.getId())).collect(Collectors.toList());
        questionResources.forEach(questionResource -> {
            List<FormInputResource> formInputs = formInputService.findApplicationInputsByQuestion(questionResource.getId());

            Boolean appendix = formInputs
                    .stream()
                    //4L is fileupload
                    .anyMatch(formInputResource -> formInputResource.getFormInputType().equals(4L));

            formInputs
                    .stream()
                    //4L is fileupload
                    .filter(formInputResource -> !formInputResource.getFormInputType().equals(4L))
                    .forEach(formInputResource -> questions.add(createQuestionObjectFromQuestionResource(questionResource, formInputResource, appendix)));
        });

        model.addAttribute("generalSections", generalSections);
        model.addAttribute("generalParentSections", parentSections);
        model.addAttribute("questions", questions);

	}

	private Question createQuestionObjectFromQuestionResource(QuestionResource questionResource, FormInputResource formInputResource, Boolean appendix){
        Question question = new Question();
        question.setId(question.getId());
        question.setNumber(question.getNumber());
        question.setTitle(questionResource.getName());
        question.setSubTitle(questionResource.getDescription());

        question.setGuidanceTitle(formInputResource.getGuidanceQuestion());
        question.setGuidance(formInputResource.getGuidanceAnswer());
        question.setMaxWords(formInputResource.getWordCount());

        question.setAppendix(appendix);

	    return question;
    }

}
