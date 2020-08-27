package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.resource.FileTypeCategory;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.GuidanceRowBuilder.aGuidanceRow;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.QuestionBuilder.aDefaultAssessedQuestion;

public class EoiTemplate {

    public static Competition copyTemplatePropertiesToCompetition(Competition template, Competition competition) {
        //todo remove dependency on template comp.
        competition.setGrantClaimMaximums(new ArrayList<>(template.getGrantClaimMaximums()));
        competition.setTermsAndConditions(template.getTermsAndConditions());
        competition.setAcademicGrantPercentage(template.getAcademicGrantPercentage());
        competition.setMinProjectDuration(template.getMinProjectDuration());
        competition.setMaxProjectDuration(template.getMaxProjectDuration());
        competition.setApplicationFinanceType(template.getApplicationFinanceType());
        return competition;
    }
    
    public static List<SectionBuilder> sections() {
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
                finances(),
                termsAndConditions()
        );

    }

    private static List<QuestionBuilder> eoiDefaultQuestions() {
        return newArrayList(
                needOrChallenge(),
                approachAndInnovation(),
                teamAndResources(),
                marketAwareness(),
                outcomes(),
                widerImpacts(),
                projectManagement(),
                risks(),
                additionality(),
                costsAndValueForMoney()
        );
    }

    private static QuestionBuilder businessOpportunity() {
        return aDefaultAssessedQuestion()
                .withShortName("Business opportunity and potential market")
                .withName("What is the business opportunity and potential market for your project?")
                .withAssessorMaximumScore(10)
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(applicationTextAreaBuilder ->
                                        applicationTextAreaBuilder
                                                .withGuidanceTitle("What should I include in the business opportunity and potential market section?")
                                                .withGuidanceAnswer("<p>Describe:</p><ul class=\\\"list-bullet\\\"><li>the main motivation for your project: the business need or market opportunity</li><li>the domestic and international markets you will target, and the other markets you are considering targeting</li></ul>")
                                                .withWordCount(400),
                                assessorTextAreaBuilder ->
                                        assessorTextAreaBuilder
                                                .withGuidanceTitle("Guidance for assessing market opportunity")
                                                .withGuidanceAnswer("Your score should be based upon the following:")
                                                .withWordCount(100)
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


    /*
'Innovation',
'What is innovative about your project?',
'Explain how your project is innovative in both a commercial and technical sense.',
'10',
'What should I include in the project innovation section?',
'<p>Describe:</p><ul class=\"list-bullet\">         <li>what the innovation will focus on</li><li>whether your project will apply existing technologies to new areas, develop new technologies for existing areas or use a totally disruptive approach</li><li>the freedom you have to operate</li></ul></ul>',
'400',
'0',
NULL,NULL,'Guidance for assessing innovation',
'Your score should be based upon the following:',
'100',
'9,10',
'The project is significantly innovative either commercially or technically and will make a substantial contribution to the field. The technology is well understood and there is high confidence that there is freedom to operate.',
'7,8',
'The project will be innovative and relevant to the market. There is some understanding of the technology and confidence that there is freedom to operate.',
'5,6',
'The project is innovative and the technology relevant, but there is not enough confidence in the freedom to operate.',
'3,4',
'The project lacks sufficient innovation both technically and commercially.',
'1,2',
'The project is either not innovative or there is no exploitable route due to previous IP.'

'Project team',
'Who is in the project team and what are their roles?',
'Describe your ability to develop and exploit this technology. Include details of your team\'s track record in managing research and development projects.',
'10',
'What should I include in the project team section?',
'<p>Describe or give:</p><ul class=\"list-bullet\">         <li>the roles, skills and relevant experience of all members of the project team</li><li>the resources, equipment and facilities needed for your project, and how you will access them</li><li>details of any external parties, including sub-contractors, you will need</li></ul>',
'400',
'0',
NULL,NULL,'Guidance for assessing team skills',
'Your score should be based upon the following:',
'100',
'9,10',
'The consortium is ideally placed to carry out the project and exploit the results. The skills are well balanced and the partners likely to form a strong consortium with good knowledge transfer.',
'7,8',
'The consortium is strong and contains all the required skills and experience. The consortium is likely to work well.',
'5,6',
'The consortium has most of the required skills and experience but there are a few gaps. The consortium will need to work hard to maintain a good working relationship.',
'3,4',
'There are significant gaps in the consortium or the formation objectives are unclear. There could be some irrelevant members or there is a poor balance between the work needed and the commitment shown.',
'1,2',
'The consortium is not capable of either carrying out the project or exploiting the results.'

'Funding and adding value',
'How much will your project cost, and how does it represent value for money for your team and the taxpayer?',
'Estimate the total costs of the project and tell us how much funding you need from Innovate UK and why.',
'10',
'What should I include in the funding and adding value section?',
'<p>Tell us:</p><ul class=\"list-bullet\"><li>the estimated total cost of your project</li><li>how your project’s goals justify the total project cost and the grant you are requesting</li><li>how your project represents value for money for you, and for the taxpayer</li><li>what you would spend your money on otherwise</li><li>whether your project could go ahead in any form without public funding, and if so, what difference the funding would make, such as speeding up the route to market, attracting more partners or reducing risk</li></ul>',
'400',
'0',
NULL,NULL,'Guidance for assessing funding and adding value',
'Your score should be based upon the following:',
'100',
'9,10',
'The project costs are appropriate. Any mix of research and development types (such as industrial research with some work packages of experimental development) is justified and costed correctly. The project will significantly increase the industrial partners\' R&D spend during the project and afterwards. The public funding will make a significant difference. The arguments for added value are very strong and believable.',
'7,8',
'The project costs should be sufficient to successfully complete the project. Any mix of research and development types is justified and costed correctly. The project will increase the industrial partners\' commitment to R&D. The public funding will make a difference. The arguments for added value are good and justified.',
'5,6',
'The project costs and justifications are broadly correct but there are some concerns. Any mix of research and development types is just about acceptable. The project will improve the industrial partners\' commitment to R&D. The public funding will help. The arguments for added value are just about acceptable.',
'3,4',
'The costs are either too high or too low to carry out the project. Poor justification is provided for any research and development type mix. There is not likely to be any improvement to the industrial partner\'s commitment to R&D. The public funding won’t make much difference. The arguments for added value are poor or not sufficiently justified.',
'1,2',
'The costs are not appropriate or justified. Any mix of research and development type is not justified. The work should be funded internally and does not deserve state funding.'



     */



}
