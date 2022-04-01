package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HorizonEuropeGuaranteeTemplateTest {

    private HorizonEuropeGuaranteeTemplate horizonEuropeGuaranteeTemplate;

    private Competition competition;

    @Before
    public void setup() {
        horizonEuropeGuaranteeTemplate = new HorizonEuropeGuaranteeTemplate();
        competition = newCompetition().build();
    }

    @Test
    public void questions() {
        List<SectionBuilder> sections = horizonEuropeGuaranteeTemplate.sections();

        Optional<SectionBuilder> optionalApplicationQuestions = sections.stream()
                .filter(section -> section.getType() == SectionType.APPLICATION_QUESTIONS)
                .findFirst();

        assertTrue(optionalApplicationQuestions.isPresent());

        SectionBuilder applicationQuestions = optionalApplicationQuestions.get();

        assertEquals(10, applicationQuestions.getQuestions().size());

        QuestionBuilder organisation = applicationQuestions.getQuestions().get(0);
        assertEquals("Tell us where your organisation is based", organisation.getName());
        assertEquals("Tell us where your organisation is based", organisation.getShortName());
        assertEquals("You must be an organisation based in the UK or a British Overseas Territory to receive funding.", organisation.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, organisation.getQuestionSetupType());

        QuestionBuilder projectRegion = applicationQuestions.getQuestions().get(1);
        assertEquals("Participating Organisation project region", projectRegion.getName());
        assertEquals("Participating Organisation project region", projectRegion.getShortName());
        assertEquals("Please select from the drop down the region your project is being carried out in.", projectRegion.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, projectRegion.getQuestionSetupType());

        QuestionBuilder eic = applicationQuestions.getQuestions().get(2);
        assertEquals("What EIC call have you been successfully evaluated for?", eic.getName());
        assertEquals("What EIC call have you been successfully evaluated for?", eic.getShortName());
        assertEquals("Please select below the Horizon Europe competition you have been successful with.", eic.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, eic.getQuestionSetupType());

        QuestionBuilder applicationReferenceNumber = applicationQuestions.getQuestions().get(3);
        assertEquals("Application reference number", applicationReferenceNumber.getName());
        assertEquals("Application reference number", applicationReferenceNumber.getShortName());
        assertEquals("Enter the 8 digit reference number from your Horizon Europe application.", applicationReferenceNumber.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, applicationReferenceNumber.getQuestionSetupType());

        QuestionBuilder fundingContribution = applicationQuestions.getQuestions().get(4);
        assertEquals("UK Funding contribution applied for (GBP)", fundingContribution.getName());
        assertEquals("UK Funding contribution applied for (GBP)", fundingContribution.getShortName());
        assertEquals("Enter the UK budget total from your Horizon Europe application in GBP. A maximum exchange rate of £1:€1.160354 will be accepted.", fundingContribution.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, fundingContribution.getQuestionSetupType());

        QuestionBuilder taskAssignment = applicationQuestions.getQuestions().get(5);
        assertEquals("Have the tasks assigned to your institution changed significantly since the original application?", taskAssignment.getName());
        assertEquals("Have the tasks assigned to your institution changed significantly since the original application?", taskAssignment.getShortName());
        assertEquals("If you are part of a consortium and the tasks assigned to your institution in the final grant agreement are significantly different from those indicated in the original proposal submitted to the European Commission, select yes below. If the tasks are broadly the same, select no.", taskAssignment.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, taskAssignment.getQuestionSetupType());

        QuestionBuilder amountChange = applicationQuestions.getQuestions().get(6);
        assertEquals("If this amount has changed please tell us how?", amountChange.getName());
        assertEquals("If this amount has changed please tell us how?", amountChange.getShortName());
        assertEquals("If the answer to Q6 was yes please state the new amount in GBP and attach your latest budget table to reflect the change. A maximum exchange rate of £1:€1.160354 will be accepted. If the answer to Q6 was no, please type 'No' below in the provided textbox.", amountChange.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, amountChange.getQuestionSetupType());

        QuestionBuilder phdStudents = applicationQuestions.getQuestions().get(7);
        assertEquals("Will you, as a UK institution, be employing PhD students as part of this project?", phdStudents.getName());
        assertEquals("Will you, as a UK institution, be employing PhD students as part of this project?", phdStudents.getShortName());
        assertEquals("Please select an answer from the multiple choice options.", phdStudents.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, phdStudents.getQuestionSetupType());

        QuestionBuilder projectPhdStudents = applicationQuestions.getQuestions().get(8);
        assertEquals("If so, how many PhD students will be employed at your institution on this project?", projectPhdStudents.getName());
        assertEquals("If so, how many PhD students will be employed at your institution on this project?", projectPhdStudents.getShortName());
        assertEquals("If the answer to Q8 was yes please state how many PhD students will be employed at your institution on this project. If the answer to Q8 was no, please type 'No' in the provided textbox below.", projectPhdStudents.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, projectPhdStudents.getQuestionSetupType());

        QuestionBuilder phdStudentsBudget = applicationQuestions.getQuestions().get(9);
        assertEquals("How much budget is allocated for PhD students employed at your institution on this project?", phdStudentsBudget.getName());
        assertEquals("How much budget is allocated for PhD students employed at your institution on this project?", phdStudentsBudget.getShortName());
        assertEquals("If the answer to Q8 was yes, please state the total budget allocated for PhD students employed at your institution on this project. If the answer to Q8 was no, please type 'No' in the provided textbox below.", phdStudentsBudget.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, phdStudentsBudget.getQuestionSetupType());
    }
}
