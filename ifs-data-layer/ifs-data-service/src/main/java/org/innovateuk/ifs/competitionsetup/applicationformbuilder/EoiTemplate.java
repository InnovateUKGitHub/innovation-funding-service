package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.GuidanceRowBuilder.aGuidanceRow;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.QuestionBuilder.aDefaultAssessedQuestion;

@Component
public class EoiTemplate implements CompetitionTemplate {

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        //todo remove dependency on template comp.
//        competition.setGrantClaimMaximums(new ArrayList<>(template.getGrantClaimMaximums()));
//        competition.setTermsAndConditions(template.getTermsAndConditions());
//        competition.setAcademicGrantPercentage(template.getAcademicGrantPercentage());
//        competition.setMinProjectDuration(template.getMinProjectDuration());
//        competition.setMaxProjectDuration(template.getMaxProjectDuration());
//        competition.setApplicationFinanceType(template.getApplicationFinanceType());
        return competition;
    }

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.EXPRESSION_OF_INTEREST;
    }

    @Override
    public List<SectionBuilder> sections() {
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationTeam(),
                                applicationDetails(),
                                researchCategory(),
                                equalityDiversityAndInclusion(),
                                projectSummary(),
                                scope()
                        )),
                applicationQuestions()
                        .withQuestions(eoiDefaultQuestions()),
                eoiTermsAndConditions()
        );

    }

    private SectionBuilder eoiTermsAndConditions() {
        SectionBuilder termsSection = termsAndConditions();
        termsSection.getQuestions().get(0)
                .withMultipleStatuses(false);
        return termsSection;
    }

    public static List<QuestionBuilder> eoiDefaultQuestions() {
        return newArrayList(
                businessOpportunity(),
                innovation(),
                projectTeam(),
                funding()
        );
    }

    private static QuestionBuilder businessOpportunity() {
        return aDefaultAssessedQuestion()
                .withShortName("Business opportunity and potential market")
                .withName("What is the business opportunity and potential market for your project?")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the business opportunity and potential market section?")
                                                .withGuidanceAnswer("<p>Describe:</p><ul class=\"list-bullet\"><li>the main motivation for your project: the business need or market opportunity</li><li>the domestic and international markets you will target, and the other markets you are considering targeting</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing market opportunity")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The applicants understand the business opportunity. The market is well understood."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The applicants have a good idea of the potential business opportunity and market."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The business opportunity is plausible and there is some understanding of the market."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The business opportunity is unrealistic or poorly defined. The market size is not well understood."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("There is little or no business drive to the project. The market is not well defined or is wrong.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder innovation() {
        return aDefaultAssessedQuestion()
                .withShortName("Innovation")
                .withName("What is innovative about your project?")
                .withDescription("Explain how your project is innovative in both a commercial and technical sense.")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the project innovation section?")
                                                .withGuidanceAnswer("<p>Describe:</p><ul class=\"list-bullet\">         <li>what the innovation will focus on</li><li>whether your project will apply existing technologies to new areas, develop new technologies for existing areas or use a totally disruptive approach</li><li>the freedom you have to operate</li></ul></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing innovation")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The project is significantly innovative either commercially or technically and will make a substantial contribution to the field. The technology is well understood and there is high confidence that there is freedom to operate."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The project will be innovative and relevant to the market. There is some understanding of the technology and confidence that there is freedom to operate."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The project is innovative and the technology relevant, but there is not enough confidence in the freedom to operate."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The project lacks sufficient innovation both technically and commercially."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The project is either not innovative or there is no exploitable route due to previous IP.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder projectTeam() {
        return aDefaultAssessedQuestion()
                .withShortName("Project team")
                .withName("Who is in the project team and what are their roles?")
                .withDescription("Describe your ability to develop and exploit this technology. Include details of your team's track record in managing research and development projects.")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the project team section?")
                                                .withGuidanceAnswer("<p>Describe or give:</p><ul class=\"list-bullet\">         <li>the roles, skills and relevant experience of all members of the project team</li><li>the resources, equipment and facilities needed for your project, and how you will access them</li><li>details of any external parties, including sub-contractors, you will need</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing team skills")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The consortium is ideally placed to carry out the project and exploit the results. The skills are well balanced and the partners likely to form a strong consortium with good knowledge transfer."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The consortium is strong and contains all the required skills and experience. The consortium is likely to work well."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The consortium has most of the required skills and experience but there are a few gaps. The consortium will need to work hard to maintain a good working relationship."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("There are significant gaps in the consortium or the formation objectives are unclear. There could be some irrelevant members or there is a poor balance between the work needed and the commitment shown."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The consortium is not capable of either carrying out the project or exploiting the results.")
                                                ))
                        )
                );
    }

    private static QuestionBuilder funding() {
        return aDefaultAssessedQuestion()
                .withShortName("Funding and adding value")
                .withName("How much will your project cost, and how does it represent value for money for your team and the taxpayer?")
                .withDescription("Estimate the total costs of the project and tell us how much funding you need from Innovate UK and why.")
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the funding and adding value section?")
                                                .withGuidanceAnswer("<p>Tell us:</p><ul class=\"list-bullet\"><li>the estimated total cost of your project</li><li>how your project’s goals justify the total project cost and the grant you are requesting</li><li>how your project represents value for money for you, and for the taxpayer</li><li>what you would spend your money on otherwise</li><li>whether your project could go ahead in any form without public funding, and if so, what difference the funding would make, such as speeding up the route to market, attracting more partners or reducing risk</li></ul>")
                                , assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing funding and adding value")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withGuidanceRows(newArrayList(
                                                        aGuidanceRow()
                                                                .withSubject("9,10")
                                                                .withJustification("The project costs are appropriate. Any mix of research and development types (such as industrial research with some work packages of experimental development) is justified and costed correctly. The project will significantly increase the industrial partners' R&D spend during the project and afterwards. The public funding will make a significant difference. The arguments for added value are very strong and believable."),
                                                        aGuidanceRow()
                                                                .withSubject("7,8")
                                                                .withJustification("The project costs should be sufficient to successfully complete the project. Any mix of research and development types is justified and costed correctly. The project will increase the industrial partners' commitment to R&D. The public funding will make a difference. The arguments for added value are good and justified."),
                                                        aGuidanceRow()
                                                                .withSubject("5,6")
                                                                .withJustification("The project costs and justifications are broadly correct but there are some concerns. Any mix of research and development types is just about acceptable. The project will improve the industrial partners' commitment to R&D. The public funding will help. The arguments for added value are just about acceptable."),
                                                        aGuidanceRow()
                                                                .withSubject("3,4")
                                                                .withJustification("The costs are either too high or too low to carry out the project. Poor justification is provided for any research and development type mix. There is not likely to be any improvement to the industrial partner's commitment to R&D. The public funding won’t make much difference. The arguments for added value are poor or not sufficiently justified."),
                                                        aGuidanceRow()
                                                                .withSubject("1,2")
                                                                .withJustification("The costs are not appropriate or justified. Any mix of research and development type is not justified. The work should be funded internally and does not deserve state funding.")
                                                ))
                        )
                );
    }


}
