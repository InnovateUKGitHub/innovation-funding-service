-- Useful query to regenerate the data in assessor_data.csv after a change in the database.

SELECT concat_ws(',', u.email, 'Passw0rd', c.id, p.id, application_details_question.id, project_summary_question.id,
                 public_description_question.id, scope.id, business_opportunity.id, potential_market.id,
                 project_exploitation.id, economic_benefit.id, technical_approach.id, innovation.id, risks.id,
                 project_team.id, funding.id, adding_value.id
)
FROM user u
  JOIN process_role pr ON u.id = pr.user_id
  JOIN process p ON pr.id = p.participant_id
  JOIN application a ON p.target_id = a.id
  JOIN competition c ON a.competition = c.id
  JOIN question application_details_question
    ON application_details_question.competition_id = c.id
       AND application_details_question.short_name = 'Application details'
  JOIN question project_summary_question
    ON project_summary_question.competition_id = c.id
       AND project_summary_question.short_name = 'Project summary'
  JOIN question public_description_question
    ON public_description_question.competition_id = c.id
       AND public_description_question.short_name = 'Public description'
  JOIN question scope
    ON scope.competition_id = c.id
       AND scope.short_name = 'Scope'
  JOIN question business_opportunity
    ON business_opportunity.competition_id = c.id
       AND business_opportunity.short_name = 'Business opportunity'
  JOIN question potential_market
    ON potential_market.competition_id = c.id
       AND potential_market.short_name = 'Potential market'
  JOIN question project_exploitation
    ON project_exploitation.competition_id = c.id
       AND project_exploitation.short_name = 'Project exploitation'
  JOIN question economic_benefit
    ON economic_benefit.competition_id = c.id
       AND economic_benefit.short_name = 'Economic benefit'
  JOIN question technical_approach
    ON technical_approach.competition_id = c.id
       AND technical_approach.short_name = 'Technical approach'
  JOIN question innovation
    ON innovation.competition_id = c.id
       AND innovation.short_name = 'Innovation'
  JOIN question risks
    ON risks.competition_id = c.id
       AND risks.short_name = 'Risks'
  JOIN question project_team
    ON project_team.competition_id = c.id
       AND project_team.short_name = 'Project team'
  JOIN question funding
    ON funding.competition_id = c.id
       AND funding.short_name = 'Funding'
  JOIN question adding_value
    ON adding_value.competition_id = c.id
       AND adding_value.short_name = 'Adding value'
WHERE u.email IN (  'load-assessor1@load.test',
                    'load-assessor2@load.test',
                    'load-assessor3@load.test',
                    'load-assessor4@load.test',
                    'load-assessor5@load.test')
      AND p.event = 'accept'
ORDER BY u.id

