-- Overheads
update form_input f
inner join question q on f.question_id=q.id
inner join section s on q.section_id=s.id
set
f.guidance_answer='<p>If you wish to claim overheads, there are 2 options available to you:</p><p>Option 1: 20% overhead option. This allows you to claim 20% of your labour costs as overhead. No further documentation is needed.</p><p>Option2: Calculate overheads. This allows you to calculate direct and indirect overheads using our overhead spreadsheet. The overhead value claimed under this method will be subject to review if your application is successful. This is in order to assess the appropriateness of the overhead value you are claiming.</p>'
where s.name='Overhead costs' and f.form_input_type_id=6;


