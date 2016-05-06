UPDATE form_input
JOIN question_form_input ON form_input.id=question_form_input.form_input_id
JOIN question ON question.id=question_form_input.question_id
SET form_input.guidance_question=question.guidance_question,
    form_input.guidance_answer=question.guidance_answer;