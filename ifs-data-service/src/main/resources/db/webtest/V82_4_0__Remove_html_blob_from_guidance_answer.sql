-- Update scope guidance titles
UPDATE form_input
    SET guidance_answer = 'Your answer should be based upon the following:'
    WHERE form_input_type_id =
        (SELECT id from form_input_type WHERE name = 'TEXTAREA')
    AND scope = 'ASSESSMENT'
    AND guidance_answer LIKE '%answer%';

-- Update other guidance titles
UPDATE form_input
    SET guidance_answer = 'Your score should be based upon the following:'
    WHERE form_input_type_id =
        (SELECT id from form_input_type WHERE name = 'TEXTAREA')
    AND scope = 'ASSESSMENT'
    AND guidance_answer LIKE '%score%';