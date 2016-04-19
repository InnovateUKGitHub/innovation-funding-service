package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.file.service.FileEntryRestService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

@Component
public class FinanceOverviewModelManager {
    ApplicationFinanceRestService applicationFinanceRestService;
    SectionService sectionService;
    QuestionService questionService;
    FinanceService financeService;
    FileEntryRestService fileEntryRestService;

    @Autowired
    public FinanceOverviewModelManager(ApplicationFinanceRestService applicationFinanceRestService, SectionService sectionService,
                                       FinanceService financeService, QuestionService questionService, FileEntryRestService fileEntryRestService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.sectionService = sectionService;
        this.financeService = financeService;
        this.questionService = questionService;
        this.fileEntryRestService = fileEntryRestService;
    }

    // TODO DW - INFUND-1555 - handle rest results
    public void addFinanceDetails(Model model, Long competitionId, Long applicationId) {
        addFinanceSections(competitionId, model);
        OrganisationFinanceOverview organisationFinanceOverview = new OrganisationFinanceOverview(financeService, fileEntryRestService, applicationId);
        model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
        model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType());
        Map<Long, ApplicationFinanceResource> organisationFinances = organisationFinanceOverview.getApplicationFinancesByOrganisation();
        model.addAttribute("organisationFinances", organisationFinances);
        model.addAttribute("academicFileEntries", organisationFinanceOverview.getAcademicOrganisationFileEntries());
        model.addAttribute("totalFundingSought", organisationFinanceOverview.getTotalFundingSought());
        model.addAttribute("totalContribution", organisationFinanceOverview.getTotalContribution());
        model.addAttribute("totalOtherFunding", organisationFinanceOverview.getTotalOtherFunding());
        model.addAttribute("researchParticipationPercentage", applicationFinanceRestService.getResearchParticipationPercentage(applicationId).getSuccessObjectOrThrowException());

    }

    private void addFinanceSections(Long competitionId, Model model) {
    	Long sectionId = sectionService.getFinanceSectionForCompetition(competitionId);
    	
    	if(sectionId == null) {
    		return;
    	}
    	
        SectionResource section = sectionService.getById(sectionId);
        sectionService.removeSectionsQuestionsWithType(section, "empty");

        model.addAttribute("financeSection", section);
        List<SectionResource> financeSectionChildren = simpleMap(section.getChildSections(), sectionService::getById);
        model.addAttribute("financeSectionChildren", financeSectionChildren);

        Map<Long, List<Question>> financeSectionChildrenQuestionsMap = financeSectionChildren.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> simpleMap(s.getQuestions(), questionService::getById)
                ));
        model.addAttribute("financeSectionChildrenQuestionsMap", financeSectionChildrenQuestionsMap);
    }
}
