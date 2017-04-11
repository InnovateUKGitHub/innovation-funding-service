-- Sector competition questions, appendix guidance

-- Approach and innovation
UPDATE form_input
SET guidance_answer='<p>You can submit up to 2 pages in PDF format no larger than 1MB. This can provide graphics, diagrams or an image to explain your innovation.</p>'
WHERE guidance_answer='<p>you may submit in pdf format up to 2 pages to provide graphics/diagrams/an image to explain the technology/product/service innovation.</p>';

-- Team and resources
UPDATE form_input
SET guidance_answer='<p>You can submit up to 4 pages in PDF format no larger than 1MB. This can describe the skills and experience of the main people working on the project.</p>'
WHERE guidance_answer='<p>you may submit in pdf format up to 4 pages to describe the skills and experience of the main people who will be working on the project.</p>';

-- Project management
UPDATE form_input
SET guidance_answer='<p>You can submit a project plan or Gantt chart of up to 2 pages in PDF format no larger than 1MB.</p>'
WHERE guidance_answer='<p>you can submit a project plan/Gantt chart up to 2 A4 pages in length in pdf format</p>';

-- Risks
UPDATE form_input
SET guidance_answer='<p>You can submit a risk register of up to 2 pages in PDF format no larger than 1MB.</p>'
WHERE guidance_answer='<p>you can submit a risk register of up to 2 A4 pages in length in pdf format</p>';
