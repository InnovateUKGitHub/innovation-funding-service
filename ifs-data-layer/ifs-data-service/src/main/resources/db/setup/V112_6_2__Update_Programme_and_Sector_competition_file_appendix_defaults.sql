-- This query is part of IFS-2564 and IFS-2565. We're changing the default appendix guidance as well
-- as the existing guidance on existing competitions to be compatible with the application
-- question appendix guidance on application questions.

-- Change Programme competition template application questions file appendix default guidance
-- Technical approach
UPDATE form_input
SET guidance_answer='<p>You can include an appendix of additional information to support the technical approach the project will undertake.</p><p>This can include for example, a Gantt chart or project management structure.</p><p>The appendix should:</p><ul><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li></ul>'
WHERE form_input.guidance_answer = '<p>You may include an appendix of additional information to support the technical approach the project will undertake.</p><p>You may include, for example, a Gantt chart or project management structure.</p><p>The appendix should:</p><ul class="list-bullet"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><li>be less than 1mb in size</li></ul>';

-- Innovation
UPDATE form_input
SET guidance_answer='<p>You can include an appendix of additional information to support your answer. This appendix can include graphics describing the innovation or the nature of the problem. You can include evidence of freedom to operate, patent searches or competitor analysis as supporting information.</p><p>The appendix should:</p><ul><li>contain your application number and project title at the top</li><li>not be any longer than 5 sides of A4. Longer appendices will only have the first 5 pages assessed</li></ul>'
WHERE form_input.guidance_answer = '<p>You may include an appendix of additional information to support your response to this question. This appendix may include graphics describing the innovation or the nature of the problem. You may want to include evidence of freedom to operate, patent searches or competitor analysis as supporting information.</p><p>The appendix should:</p><ul class="list-bullet"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 5 sides of A4. Longer appendices will only have the first 5 pages assessed</li><li>be less than 1mb in size</li></ul>';

-- Project team
UPDATE form_input
SET guidance_answer='<p>You can include an appendix of additional information to detail the specific expertise and track record of each project partner and subcontractor. Academic collaborators can refer to their research standing.</p><p>The appendix should:</p><ul><li>contain your application number and project title at the top</li><li>include up to half an A4 page per partner describing the skills and experience of the main people who will be working on the project</li></ul>'
WHERE form_input.guidance_answer = '<p>You may include an appendix of additional information to provide details of the specific expertise and track record of each project partner and each subcontractor. Academic collaborators may refer to their research standing.</p><p>The appendix should:</p><ul class="list-bullet"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>include up to half an A4 page per partner describing the skills and experience of the main people who will be working on the project</li><li>be less than 1mb in size</li></ul>';


-- Change Sector competition template application questions file appendix default guidance
-- Approach and innovation
UPDATE form_input
SET guidance_answer='<p>You can submit up to 2 pages to provide graphics, diagrams or an image to explain your innovation.</p>'
WHERE form_input.guidance_answer = '<p>You can submit up to 2 pages in PDF format no larger than 1MB. This can provide graphics, diagrams or an image to explain your innovation.</p>';

-- Team and resources
UPDATE form_input
SET guidance_answer='<p>You can submit up to 4 pages to describe the skills and experience of the main people working on the project.</p>'
WHERE form_input.guidance_answer = '<p>You can submit up to 4 pages in PDF format no larger than 1MB. This can describe the skills and experience of the main people working on the project.</p>';

-- Project management
UPDATE form_input
SET guidance_answer='<p>You can submit a project plan or Gantt chart of up to 2 pages.</p>'
WHERE form_input.guidance_answer = '<p>You can submit a project plan or Gantt chart of up to 2 pages in PDF format no larger than 1MB.</p>';

-- Risks
UPDATE form_input
SET guidance_answer='<p>You can submit a risk register of up to 2 pages.</p>'
WHERE form_input.guidance_answer = '<p>You can submit a risk register of up to 2 pages in PDF format no larger than 1MB.</p>'