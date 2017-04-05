UPDATE form_input f
INNER JOIN question q ON f.question_id=q.id
INNER JOIN section s ON q.section_id=s.id
SET f.guidance_answer = '<p>You will need to calculate a ‘usage’ value for each item. You can do this by deducting its expected value from its original price at the end of your project. If you owned the equipment before the project started then you should use its net present value.</p><p>This value is then multiplied by the amount, in percentages, that is used during the project. This final value represents the eligible cost to your project.</p>'
WHERE f.guidance_title = 'Capital usage guidance' AND f.form_input_type_id=6 AND s.name='Capital usage';