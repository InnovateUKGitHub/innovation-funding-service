--  Update justification content for Sector competition template and existing competition

SET @template_q5_form_input_id = (SELECT id FROM form_input WHERE guidance_title LIKE 'Guidance for assessing outcomes and route to market' AND competition_id = 3);
SET @comp17_q5_form_input_id = (SELECT id FROM form_input WHERE guidance_title LIKE 'Guidance for assessing outcomes and route to market' AND competition_id = 17);

UPDATE guidance_row
SET justification = 'The applicant provides little or no information about the target customers.'
WHERE subject = '1,2'
AND form_input_id in (@template_q5_form_input_id, @comp17_q5_form_input_id);

UPDATE guidance_row
SET justification = 'There is some information about the target customer types but there is little about the value proposition or how profit, productivity or growth will be affected.'
WHERE subject = '3,4'
AND form_input_id in (@template_q5_form_input_id, @comp17_q5_form_input_id);

UPDATE guidance_row
SET justification = 'Target customer types are described but the value proposition to them is less clear. There is some information about how profit, productivity or growth increases may be achieved at some point.'
WHERE subject = '5,6'
AND form_input_id in (@template_q5_form_input_id, @comp17_q5_form_input_id);

UPDATE guidance_row
SET justification = 'Target customers are identified along with the value proposition to them. The routes to market and how profit, productivity and growth will increase is outlined with some evidence. The exploitation and/or dissemination of the main project outputs is outlined.'
WHERE subject = '7,8'
AND form_input_id in (@template_q5_form_input_id, @comp17_q5_form_input_id);

UPDATE guidance_row
SET justification = 'Target customers are identified along with the value proposition to them. The routes to market and how profit, productivity and growth will increase is identified and evidenced. The exploitation and/or dissemination of the main project outputs is outlined.'
WHERE subject = '9,10'
AND form_input_id in (@template_q5_form_input_id, @comp17_q5_form_input_id);
