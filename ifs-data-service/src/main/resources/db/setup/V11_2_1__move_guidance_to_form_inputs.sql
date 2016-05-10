UPDATE form_input
JOIN question_form_input ON form_input.id=question_form_input.form_input_id
JOIN question ON question.id=question_form_input.question_id
SET form_input.guidance_question=question.guidance_question,
    form_input.guidance_answer=question.guidance_answer;

UPDATE form_input
SET guidance_question="What should I include in the appendix?",
    guidance_answer='<p>You should include only supporting information in the appendix. You shouldn’t use it to provide responses to the question.</p><p>The appendix must:</p><ul class="list-bullet"><li>be in a Portable Document Format (.pdf)</li><li>be legible at 100% magnification</li><li>display the application number and project title at the top of the document.</li><li>be no more than 6 sides of A4 – if your appendix is longer than this, we will assess only the first 6 pages</li><li>be less than 1mb in size</li></ul>'
WHERE form_input_type_id = 4