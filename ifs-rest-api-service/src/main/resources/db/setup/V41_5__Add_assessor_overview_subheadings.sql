UPDATE `section` SET `assessor_guidance_description`='Each of these questions should be scored out of 10 and written feedback provided.' WHERE `name`='Application questions';
UPDATE `section` SET `assessor_guidance_description`='These sections of the application should be read for background information about the project - but do not require scoring.' WHERE `name`='Project details';
UPDATE `section` SET `assessor_guidance_description`='Each partner is required to submit their own project finances and funding rates. The overall project costs for all partners can be seen in the Finances overview section' WHERE `name`='Finances';

