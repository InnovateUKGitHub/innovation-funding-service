update question set
description='Please provide a brief description of your project. If your application is successful, we will publish this description. This question is mandatory but is not scored.',
short_name='Public description'
where short_name='Public description\n';

-- Your Project Costs
-- Labour
update form_input f
inner join question q on f.question_id=q.id
inner join section s on q.section_id=s.id
set
q.description='<p>You may claim the labour costs of all employees you have working on your project.</p>'
where s.name='Labour' and f.form_input_type_id=6;

update question set
description='<p>If your application is awarded funding, you will need to account for all your labour costs as they occur. For example, you should keep timesheets and payroll records. These should show the actual hours worked by individuals and paid by the organisation.</p>'
where name='Labour';

-- Overheads
update form_input f
inner join question q on f.question_id=q.id
inner join section s on q.section_id=s.id
set
q.description='<p>You may incur overhead costs associated with those directly working on the project as well as indirect (administration) overheads. To be eligible both overhead categories need to be directly attributable to the project. The indirect overheads need to be additional as well as directly attributable. Note that there are certain cost categories/activities which are not eligible. To find out which costs are ineligible/eligible refer to our <a href="https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance">project costs guidance</a>.</p>',
f.guidance_answer='<p>There are 2 options available for claiming overheads:</p><p>Option 1: 20% overhead option. This allows you to claim 20% of your labour costs as overhead. No further documentation is needed.</p><p>Option2: Calculate overheads. This allows you to calculate direct and indirect overheads using our overhead spreadsheet. The overhead value claimed under this method will be subject to review if your application is successful. This is in order to assess the appropriateness of the overhead value you are claiming.</p>',
f.guidance_title='Overheads costs guidance'
where s.name='Overhead costs' and f.form_input_type_id=6;

update question
set description=''
where name='Overheads';

-- Materials
update form_input f
inner join question q on f.question_id=q.id
inner join section s on q.section_id=s.id
set
q.description='<p>You can claim the costs of materials used on your project providing:</p><ul class="list-bullet"><li>they are not already purchased or included in the overheads</li><li>they are purchased from third parties</li><li>they won’t have a residual/resale value at the end of your project. If they do you can claim the costs minus this value</li></ul><p><a href="https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance" rel="external">Please refer to our guide to project costs for further information.</a></p>'
where s.name='Materials' and f.form_input_type_id=6;

-- Capital usage
update form_input f
inner join question q on f.question_id=q.id
inner join section s on q.section_id=s.id
set
q.description='<p>You can claim the usage costs of capital assets you will buy for or use on, your project.</p>'
where s.name='Capital usage' and f.form_input_type_id=6;

-- Subcontracting costs
update form_input f
inner join question q on f.question_id=q.id
inner join section s on q.section_id=s.id
set
q.description='<p>You may subcontract work if you don’t have the expertise in your project team. You can also subcontract if it is cheaper than developing your skills in-house.</p>',
f.guidance_answer='<p>Subcontracting costs relate to work carried out by third party organisations. These organisations are not part of your project team.</p><p>Subcontracting is eligible providing it’s justified as to why the work cannot be performed by a project partner.</p><p>Subcontracting associate companies should be charged at cost.</p><p>Where possible you should select a UK based contractor. You should name the subcontractor (where known) and describe what they will be doing. You should also state where the work will be undertaken. We will look at the size of this contribution when assessing your eligibility and level of support.</p>'
where s.name='Subcontracting costs' and f.form_input_type_id=6;

-- Travel and subsistence
update form_input f
inner join question q on f.question_id=q.id
inner join section s on q.section_id=s.id
set
q.description='<p>You should include travel and subsistence costs that relate only to this project. </p>'
where s.name='Travel and subsistence' and f.form_input_type_id=6;

-- Other costs
update form_input f
inner join question q on f.question_id=q.id
inner join section s on q.section_id=s.id
set
q.description='<p>Please provide details of any project costs which cannot be covered by the other cost categories.</p>',
f.guidance_answer='<p>Examples of other costs include:</p><p><strong>Training costs:</strong> these costs are eligible for support if they relate to your project. We may support management training for your project but will not support ongoing training.</p><p><strong>Workshop/laboratory usage charges:</strong> direct costs which relate to workshops or laboratories and which can be supported with actual usage data can be claimed. You should provide details of how the workshop/laboratory charge out rates per hour or day are calculated. This can include specific labour, rent, rates, maintenance, equipment, calibration costs, etc. These should form the overall costs together with the available operational hours to form the hourly or daily charge out rates.</p><p><strong>Preparation of technical reports:</strong> if, for example, the main aim of your project is standard support or technology transfer. You should show how this is more than you would produce through good project management.</p><p><strong>Market assessment:</strong> we may support market assessments studies. The study will need to help us understand how your project is a good match for your target market. It could also be eligible if it helps commercialise your product.</p><p><strong>Licensing in new technologies:</strong> if new technology makes up a large part of your project, we will expect you to develop that technology. For instance, if the value of the technology is more than &pound;100,000.</p><p><strong>Patent filing costs for New IP generated by your project:</strong> these are eligible for SMEs up to a limit of &pound;7,500 per partner. You should not include legal costs relating to the filing or trademark related expenditure.</p><p>Regulatory compliance costs are eligible if necessary to carry out your project.</p>'
where s.name='Other costs' and f.form_input_type_id=6;


