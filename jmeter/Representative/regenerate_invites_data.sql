-- Useful query to regenerate the data in invites_data.csv after a change in the database.

select concat_ws(',', u.email, 'Passw0rd', a.id, pr.organisation_id, application_details_question.id)
            from user u
            join process_role pr
              on pr.user_id = u.id
            join application a
              on a.id = pr.application_id
            join competition c
              on c.id = a.competition
            join question application_details_question
              on application_details_question.competition_id = c.id
             and application_details_question.short_name = 'Application details'
           where email in ('peter.styles@load.example.com',
                           'jessica.mason@load.example.com',
                           'malcom.jones@load.example.com', 
                           'bethany.timpson@load.example.com',
                           'derik.arnold@load.example.com',
                           'sarah.james@load.example.com',
                           'richard.williams@load.example.com',
                           'karen.smith@load.example.com',
                           'simon.lightfoot@load.example.com',
                           'mary.evans@load.example.com',
                           'mike.ericsson@load.example.com',
                           'helen.rhodes@load.example.com',
                           'david.wellington@load.example.com',
                           'felicity.jefferies@load.example.com')
            order by a.id
            INTO OUTFILE 'invites-csv.csv' FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n';
