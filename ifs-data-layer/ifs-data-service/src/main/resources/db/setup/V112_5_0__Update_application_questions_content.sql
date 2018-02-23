-- IFS-1669 - Resolves various content related bugs in 'Application questions'.

--
-- Programme competition types
--

-- Economic benefit

UPDATE form_input
SET guidance_answer = '<p>Describe all the benefits you expect your project to deliver, including:</p><p><strong>Economic</strong> : this is the real impact the project will have on its economic environment. This is not traditional corporate accounting profit and can include cost avoidance. You should identify and quantify any expected benefits to:</p><ul class="list-bullet"><li>users (intermediaries and end users)</li><li>suppliers</li><li>broader industrial markets</li><li>the UK economy</li></ul><p><strong>Social</strong> : quantify any expected social impacts either positive or negative on, for example:</p><ul class="list-bullet"><li>quality of life</li><li>social inclusion or exclusion</li><li>education</li><li>public empowerment</li><li>health and safety</li><li>regulation</li><li>diversity</li><li>government priorities</li></ul><p><strong>Environmental</strong> : show how your project will benefit or have a low impact on the environment. For example, this could include:<p><ul class="list-bullet"><li>careful management of energy consumption</li><li>reductions in carbon emissions</li><li>reducing manufacturing and materials waste</li><li>rendering waste less toxic before disposing of it in a safe and legal manner</li><li>re-manufacturing (cradle to cradle)</li></ul>'
WHERE guidance_answer = '<p>Describe all the benefits you expect your project to deliver, including:</p><p><strong>Economic</strong> : this is the real impact the project will have on its economic environment. This is not traditional corporate accounting profit and can include cost avoidance. You should identify and quantify any expected benefits to:</p><ul class="list-bullet"><li>users (intermediaries and end users)</li><li>suppliers</li><li>broader industrial markets</li><li>the UK economy</li></ul><p><strong>Social</strong> : quantify any expected social impacts either positive or negative on, for example:</p><ul class="list-bullet"><li>quality of life</li><li>social inclusion/exclusion</li><li>education</li><li>public empowerment</li><li>health and safety</li><li>regulation</li><li>diversity</li><li>government priorities</li></ul><p><strong>Environmental</strong> : show how your project will benefit or have a low impact on the environment. For example, this could include:<p><ul class="list-bullet"><li>careful management of energy consumption</li><li>reductions in carbon emissions</li><li>reducing manufacturing and materials waste</li><li>rendering waste less toxic before disposing of it in a safe and legal manner</li><li>re-manufacturing (cradle to cradle)</li></ul>';

UPDATE guidance_row
SET justification = 'The consortium is ideally placed to carry out the project and exploit the results. The skills are well balanced and the partners likely to form a strong consortium with good knowledge transfer.'
WHERE justification = 'The consortium is ideally placed to carry out the project AND exploit the results. The skills are well balanced and the partners likely to form a strong consortium with good knowledge transfer.';

--
-- Sector competition types
--

-- Need or challenge

UPDATE form_input
SET guidance_answer = '<p class="p1">You should describe or explain:</p><ul><li class="li2">the main motivation for the project; the business need, technological challenge or market opportunity</li><li class="li2">the nearest current state-of-the-art (including those near-market/in development) and its limitations</li><li class="li2">any work you have already done to respond to this need, for example is the project focused on developing an existing capability or building a new one?</li><li class="li2">the wider economic, social, environmental, cultural and/or political challenges which are influential in creating the opportunity (for example, incoming regulations). Our <a href="http://www.slideshare.net/WebadminTSB/innovate-uk-horizons-sustainable-economy-framework">Horizons tool</a> can help.</li></ul>'
WHERE guidance_answer = '<p class="p1">You should describe or explain:</p><ul><li class="li2">the main motivation for the project; the business need, technological challenge or market opportunity</li><li class="li2">the nearest current state-of-the-art (including those near-market/in development) and its limitations</li><li class="li2">any work you have already done to respond to this need, for example is the project focused on developing an existing capability or building a new one?</li><li class="li2">the wider economic, social, environmental, cultural and/or political challenges which are influential in creating the opportunity (for example, incoming regulations). Our Horizons tool can help here: http://www.slideshare.net/WebadminTSB/innovate-uk-horizons-sustainable-economy-framework</li></ul>';

UPDATE guidance_row
SET justification = 'There is a compelling business motivation for the project. There is a clear understanding of the nearest state-of-the-art available.  The applicant has shown, if applicable, how the project will build on previous relevant work. Any wider factors influencing this opportunity are identified.'
WHERE justification = 'There is a compelling business motivation  for the project . There  is a clear understanding of the nearest state-of-the-art available.  The applicant has shown, if applicable, how the project will build on previous relevant work .   Any wider factors influencing this opportunity are identified. ';

UPDATE guidance_row
SET justification = 'There is a good motivation for the project. There is a good awareness of the nearest state-of-the-art and wider factors influencing the opportunity.'
WHERE justification = 'There is a good motivation for the project. There is a good awarenessof the nearest state-of-the-art and wider factors influencing the opportunity.  ';

UPDATE guidance_row
SET justification = 'The project motivation is good but there is a lack of understanding of the nearest state-of-the-art or wider factors influencing this opportunity.'
WHERE justification = 'The project motivation is good but a lack of understanding of the nearest state-of-the-art or wider factors influencing this opportunity.  ';

-- Approach and innovation

UPDATE form_input
SET guidance_answer = '<p class="p1">You should describe or explain:</p><ul><li class="li2">how you will respond to the need, challenge or opportunity identified</li><li class="li2">how you will improve on the nearest current state-of-the-art identified</li><li class="li2">where the focus of the innovation will be in the project (application of existing technologies in new areas, development of new technologies for existing areas or a totally disruptive approach) and the freedom you have to operate</li><li class="li2">how this project fits with your current product or service lines or offerings</li><li class="li2">how it will make you more competitive</li><li class="li2">the nature of the outputs you expect from the project (for example, report, demonstrator, know-how, new process, product or service design) and how these will help you to target the need, challenge or opportunity identified</li></ul>'
WHERE guidance_answer = '<p class="p1">You should describe or explain:</p><ul><li class="li2">how you will respond to the need, challenge or opportunity identified</li><li class="li2">how you will improve on the nearest current state-of-the-art identified</li><li class="li2">where the focus of the innovation will be in the project (application of existing technologies in new areas, development of new technologies for existing areas or a totally disruptive approach) and the freedom you have to operate</li><li class="li2">how this project fits with your current product/service lines/offerings</li><li class="li2">how it will make you more competitive</li><li class="li2">the nature of the outputs you expect from the project (for example, report, demonstrator, know-how, new process, product or service design) and how these will help you to target the need, challenge or opportunity identified</li></ul>';

UPDATE guidance_row
SET justification = 'The project may address the need or challenge identified in Q1 and the innovations are highlighted. The level of innovation or freedom to operate is not strongly backed up with evidence. The main risks are not fully identified. Innovation focus is plausible and shows a link to improvements in competitiveness and/or productivity.'
WHERE justification = 'The project may address the need or challenge identified in Q1 and the innovations are highlighted. The level of innovation or freedom to operate is not strongly backed up with evidence.  The main risks are not fully identified . Innovation focus is plausible and shows a link to improvements in competitiveness and/or productivity.';

UPDATE guidance_row
SET justification = 'The approach is poorly defined with an unconvincing link to the need or challenge identified in Q1. Improvement in competiveness and/or productivity is not very convincing.'
WHERE justification = 'The approach is poorly defined with an unconvincing link to the need or challenge identified in Q1. Improvement in competiveness and/or productivity is is not very convincing.';

-- Market awareness

UPDATE question
SET short_name = 'Market awareness'
WHERE short_name = ' Market awareness';

UPDATE form_input
SET guidance_answer = '<p class="p1">You should describe or explain:</p><ul><li class="li2">the markets (domestic and/or international) you will be targeting in the project and any other potential markets</li><li class="li2">for the target markets, describe:</li><ul><li class="li2">the size of the target markets for the project outcomes, backed up by references where available</li><li class="li2">the structure and dynamics of the market (such as customer segmentation), together with predicted growth rates within clear timeframes</li><li class="li2">the main supply or value chains and business models in operation (and any barriers to entry)</li><li class="li2">the current UK position in targeting this market</li></ul><li class="li2">for highly innovative projects, where the market may be unexplored, explain:</li><ul><li class="li2">what the route to market could or might be</li><li class="li2">what its size might be</li><li class="li2">how the project will look to explore the market potential</li></ul><li class="li2">briefly describe the size and main features of any other markets not already listed</li></ul>'
WHERE guidance_answer = '<p class="p1">You should describe or explain:</p><ul><li class="li2">the markets (domestic and/or international) you will be targeting in the project and any other potential markets</li><li class="li2">for the target markets, describe:</li><ul><li class="li2">the size of the target markets for the project outcomes, backed up by references where available</li><li class="li2">the structure and dynamics of the market (such as customer segmentation), together with predicted growth rates within clear timeframes</li><li class="li2">the main supply/value chains and business models in operation (and any barriers to entry)</li><li class="li2">the current UK position in targeting this market</li></ul><li class="li2">for highly innovative projects, where the market may be unexplored, explain:</li><ul><li class="li2">what the route to market could or might be</li><li class="li2">what its size might be</li><li class="li2">how the project will look to explore the market potential</li></ul><li class="li2">briefly describe the size and main features of any other markets not already listed</li></ul>';

UPDATE guidance_row
SET justification = 'There is a good awareness of the target market’s drivers and dynamics. The market size is quantified with some evidence. For a new market, a good attempt is made at describing the possible routes to market and estimating the market size. Relevant secondary markets are described showing good awareness.'
WHERE justification = 'There is a T good awareness of the target market’s drivers and dynamics. The market size is quantified with some evidence. For a new market, a good attempt is made at describing the possible routes to market and estimating the market size.  Relevant secondary markets are described showing good awareness.  ';

-- Outcomes and route to market

UPDATE form_input
SET guidance_answer = '<p class="p1">You should describe or explain:</p><ul><li class="li2">your current position in the markets and supply or value chains outlined (will you be extending or establishing your market position?)</li><li class="li2">your target customers and/or end users, and the value to them (why would they use or buy it?)</li><li class="li2">your route to market</li><li class="li2">how you are going to profit from the innovation (increased revenues or cost reduction)</li><li class="li2">how the innovation will impact your productivity and growth (in the short and long-term)</li><li class="li2">how you will protect and exploit the outputs of the project, for example through know-how, patenting, designs, changes to business model</li><li class="li2">your strategy for targeting the other markets identified during or after the project</li><li class="li2">for any research organisation activity in the project, describe your plans to spread project research outputs over a reasonable timescale</li><li class="li2">if you expect to use the results generated from the project in further research activities, describe how</li></ul>'
WHERE guidance_answer = '<p class="p1">You should describe or explain:</p><ul><li class="li2">your current position in the markets and supply/value chains outlined (will you be extending or establishing your market position?)</li><li class="li2">your target customers and/or end users, and the value to them (why would they use/buy it?)</li><li class="li2">your route to market</li><li class="li2">how you are going to profit from the innovation (increased revenues or cost reduction)</li><li class="li2">how the innovation will impact your productivity and growth (in the short and long-term)</li><li class="li2">how you will protect and exploit the outputs of the project, for example through know-how, patenting, designs, changes to business model</li><li class="li2">your strategy for targeting the other markets identified during or after the project</li><li class="li2">for any research organisation activity in the project, describe your plans to spread project research outputs over a reasonable timescale</li><li class="li2">if you expect to use the results generated from the project in further research activities, describe how</li></ul>';

-- Wider impacts

UPDATE form_input
SET guidance_answer = '<p class="p1">You should describe and where possible measure:</p><ul><li class="li2">the economic benefits from the project to external parties (customers, others in the supply chain, broader industry and the UK economy) such as productivity increases and import substitution</li><li class="li2">any expected social impacts, either positive or negative on, for example:</li><ul><li class="li2">quality of life</li><li class="li2">social inclusion or exclusion</li><li class="li2">jobs (safeguarded, created, changed, displaced)</li><li class="li2">education</li><li class="li2">public empowerment</li><li class="li2">health and safety</li><li class="li2">regulations</li><li class="li2">diversity</li></ul><li class="li2">any expected impact on government priorities</li><li class="li2">any expected environmental impacts, either positive or negative</li><li class="li2">identify any expected regional impacts of the project</li></ul>'
WHERE guidance_answer = '<p class="p1">You should describe and where possible measure:</p><ul><li class="li2">the economic benefits from the project to external parties (customers, others in the supply chain, broader industry and the UK economy) such as productivity increases and import substitution</li><li class="li2">any expected social impacts, either positive or negative on, for example:</li><ul><li class="li2">quality of life</li><li class="li2">social inclusion/exclusion</li><li class="li2">jobs (safeguarded, created, changed, displaced)</li><li class="li2">education</li><li class="li2">public empowerment</li><li class="li2">health and safety</li><li class="li2">regulations</li><li class="li2">diversity</li></ul><li class="li2">any expected impact on government priorities</li><li class="li2">any expected environmental impacts, either positive or negative</li><li class="li2">identify any expected regional impacts of the project</li></ul>';

UPDATE guidance_row
SET justification = 'The positive impact on others outside of the team is understood (such as supply chain partners, customers, broader industry). Social, economic and/or environmental impacts are considered. Expected regional impacts are described with compelling evidence to justify claims. Any possible negative impacts are fully mitigated where appropriate.'
WHERE justification = 'The positive impact on others outside of the team is understood (supply chain partners, customers, broader industry, etc) Social, economic and/or environmental impacts are considered.  Expected regional impacts are described with compelling evidence to justify claims.  Any possible negative impacts are fully mitigated where appropriate. ';

UPDATE guidance_row
SET justification = 'There is good awareness of how the project may impact others outside of the team. Expected regional impacts are described. Any possible negative impacts are partially mitigated where appropriate.'
WHERE justification = 'There is good awareness of how the project may impact others outside of the team. .  Expected regional impacts are described.  Any possible negative impacts are partially mitigated where appropriate.';

-- Project management

UPDATE form_input
SET guidance_answer = '<p>You should describe or explain:</p>
<ul>
 <li class="li2">the main work packages of the project, indicating the relevant research category, the lead partner assigned to each and the total cost of each one</li>
 <li class="li2">your approach to project management, identifying any major tools and mechanisms that will be used for a successful and innovative project outcome</li>
 <li class="li2">the management reporting lines</li>
 <li class="li2">your project plan in enough detail to identify any links or dependencies between work packages or milestones</li>
</ul>'
WHERE guidance_answer = '<p>You should describe or explain:</p>
<ul>
 <li class="li2">the main work packages of the project, indicating the relevant research category, the lead partner assigned to each and the total cost of each one</li>
 <li class="li2">your approach to project management, identifying any major tools and mechanisms that will be used for a successful and innovative project outcome.</li>
 <li class="li2">the management reporting lines</li>
 <li class="li2">your project plan in enough detail to identify any links or dependencies between work packages or milestones</li>
</ul>';

-- Costs and value for money

UPDATE guidance_row
SET justification = 'The project costs seem ok but the justifications are not clear. The balance of costs and grants between partners is acceptable.  Little information is offered about alternative approaches and the value for money this project offers.'
WHERE justification = 'The project costs seem ok but the justifications are not clear. The balance of costs and grants between partners is acceptable .  Little information is offered about alternative approaches and the value for money this project offers.';

--
-- EOI competition types
--

-- Project team

UPDATE form_input
SET guidance_answer = '<p>Describe or give:</p><ul class="list-bullet">         <li>the roles, skills and relevant experience of all members of the project team</li><li>the resources, equipment and facilities needed for your project, and how you will access them</li><li>details of any external parties, including sub-contractors, you will need</li></ul>'
WHERE guidance_answer = '<p>Describe or give:</p><ul class="list-bullet">         <li>the roles, skills and relevant experience of all members of the project team</li><li>the resources, equipment and facilities required for your project, and how you will access them</li><li>details of any external parties, including sub-contractors, you will need</li></ul>';

UPDATE guidance_row
SET justification = 'There are significant gaps in the consortium or the formation objectives are unclear. There could be some irrelevant members or there is a poor balance between the work needed and the commitment shown.'
WHERE justification = 'There are significant gaps in the consortium or the formation objectives are unclear. There could be some passengers or there is a poor balance between the work needed and the commitment shown.';

-- Funding and adding value

UPDATE form_input
SET guidance_answer = '<p>Tell us:</p><ul class="list-bullet"><li>the estimated total cost of your project</li><li>how your project’s goals justify the total project cost and the grant you are requesting</li><li>how your project represents value for money for you, and for the taxpayer</li><li>what you would spend your money on otherwise</li><li>whether your project could go ahead in any form without public funding, and if so, what difference the funding would make, such as speeding up the route to market, attracting more partners or reducing risk</li></ul>'
WHERE guidance_answer = '<p>Tell us:</p><ul class="list-bullet"><li>what you estimate your project will cost in total</li><li>how your project’s goals justify the total project cost and the grant you are requesting</li><li>how your project represents value for money for you, and for the taxpayer</li><li>what you would spend your money on otherwise</li><li>whether your project could go ahead in any form without public funding, and if so, what difference the funding would make, such as speeding up the route to market, attracting more partners or reducing risk</li></ul>';

UPDATE guidance_row
SET justification = 'The costs are either too high or too low to carry out the project. Poor justification is provided for any research and development type mix. There is not likely to be any improvement to the industrial partner''s commitment to R&D. The public funding won’t make much difference. The arguments for added value are poor or not sufficiently justified.'
WHERE justification = 'The costs are either too high or too low to carry out the project. Poor justification is provided for any research and development type mix. There is not likely to be any improvement to the industrial partner''s commitment to R&amp;D. The public funding won’t make much difference. The arguments for added value are poor or not sufficiently justified.';

UPDATE guidance_row
SET justification = 'The project costs and justifications are broadly correct but there are some concerns. Any mix of research and development types is just about acceptable. The project will improve the industrial partners'' commitment to R&D. The public funding will help. The arguments for added value are just about acceptable.'
WHERE justification = 'The project costs and justifications are broadly correct but there are some concerns. Any mix of research and development types is just about acceptable. The project will improve the industrial partners'' commitment to R&amp;D. The public funding will help. The arguments for added value are just about acceptable.';

UPDATE guidance_row
SET justification = 'The project costs should be sufficient to successfully complete the project. Any mix of research and development types is justified and costed correctly. The project will increase the industrial partners'' commitment to R&D. The public funding will make a difference. The arguments for added value are good and justified.'
WHERE justification = 'The project costs should be sufficient to successfully complete the project. Any mix of research and development types is justified and costed correctly. The project will increase the industrial partners'' commitment to R&amp;D. The public funding will make a difference. The arguments for added value are good and justified.';

UPDATE guidance_row
SET justification = 'The project costs are appropriate. Any mix of research and development types (such as industrial research with some work packages of experimental development) is justified and costed correctly. The project will significantly increase the industrial partners'' R&D spend during the project and afterwards. The public funding will make a significant difference. The arguments for added value are very strong and believable.'
WHERE justification = 'The project costs are appropriate. Any mix of research and development types (such as industrial research with some work packages of experimental development) is justified and costed correctly. The project will significantly increase the industrial partners'' R&amp;D spend during the project and afterwards. The public funding will make a significant difference. The arguments for added value are very strong and believable.';
