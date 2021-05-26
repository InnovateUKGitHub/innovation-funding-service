*** Variables ***
&{lead_applicant_credentials}                     email=steve.smith@empire.com                           password=${short_password}
&{lead_applicant2_credentials}                    email=jesse.edwards@example.com                        password=${short_password}
&{collaborator1_credentials}                      email=jessica.doe@ludlow.co.uk                         password=${short_password}
&{collaborator2_credentials}                      email=pete.tom@egg.com                                 password=${short_password}
&{worth_test_credentials}                         email=${test_mailbox_one}+submit@gmail.com             password=${short_password}
&{Comp_admin1_credentials}                        email=john.doe@innovateuk.test                         password=${short_password}
&{successful_applicant_credentials}               email=${test_mailbox_one}+fundsuccess@gmail.com        password=${short_password}
&{successful_released_credentials}                email=${test_mailbox_two}+releasefeedback@gmail.com    password=${short_password}
&{unsuccessful_applicant_credentials}             email=${test_mailbox_two}+fundfailure@gmail.com        password=${short_password}
&{unsuccessful_released_credentials}              email=james.lewis@example.com                          password=${short_password}
&{assessor_credentials}                           email=paul.plum@gmail.com                              password=${short_password}
&{assessor2_credentials}                          email=felix.wilson@gmail.com                           password=${short_password}
&{assessor_bob_credentials}                       email=bob.malone@gmail.com                             password=${short_password}
&{existing_assessor1_credentials}                 email=${test_mailbox_one}+jeremy.alufson@gmail.com     password=${short_password}
&{nonregistered_assessor2_credentials}            email=${test_mailbox_one}+david.peters@gmail.com       password=${short_password}
&{nonregistered_assessor3_credentials}            email=${test_mailbox_one}+thomas.fister@gmail.com      password=Passw0rd1357123
&{internal_finance_credentials}                   email=lee.bowman@innovateuk.test                       password=${short_password}
&{innovation_lead_one}                            email=ian.cooper@innovateuk.test                       password=${short_password}
&{innovation_lead_two}                            email=peter.freeman@innovateuk.test                    password=${short_password}
&{stakeholder_user}                               email=Rayon.Kevin@gmail.com                            password=${short_password}
&{Multiple_user_credentials}                      email=jo.peters@ntag.example.com                       password=${short_password}
&{Ineligible_user}                                email=nancy.peterson@gmail.com                         password=${short_password}
&{support_user_credentials}                       email=support@innovateuk.test                          password=${short_password}
&{ifs_admin_user_credentials}                     email=arden.pimenta@innovateuk.test                    password=${short_password}
&{Assessor_e2e}                                   email=${test_mailbox_one}+AJE2E@gmail.com              password=Passw0rd1357123
&{lead_applicant_alternative_user_credentials}    email=${test_mailbox_one}+mario@gmail.com              password=${short_password}
&{collaborator1_alternative_user_credentials}     email=kevin.summers@ludlow.co.uk                       password=${short_password}
&{collaborator2_alternative_user_credentials}     email=casey.evans@egg.com                              password=${short_password}
&{RTO_lead_applicant_credentials}                 email=dave.adams@gmail.com                             password=${short_password}
&{Research_lead_applicant_credentials}            email=heather.ross@example.com                         password=${short_password}
&{PublicSector_lead_applicant_credentials}        email=becky.mason@gmail.com                            password=${short_password}
&{monitoring_officer_one_credentials}             email=orville.gibbs@gmail.com                          password=${short_password}
&{monitoring_officer_two_credentials}             email=nilesh.patti@gmail.com                           password=${short_password}
&{assessor_stakeholder_credentials}               email=blake.wood@gmail.com                             password=${short_password}
&{applicant_stakeholder_credentials}              email=gene.bowman@jetpulse.example.com                 password=${short_password}
&{triple_user_credentials}                        email=carolyn.reed@example.com                         password=${short_password}
&{PS_EF_Application_Partner_Email_credentials}    email= ${test_mailbox_one}+karen@gmail.com             password=${short_password}
&{troy_ward_crendentials}                         email=troy.ward@gmail.com                              password=${short_password}
&{becky_mason_credentials}                        email=becky.mason@gmail.com                            password=${short_password}
&{abby_gallagher_credentials}                     email=abby.gallagher@example.com	                     password=${short_password}
&{scLeadApplicantCredentials}                     email=janet.howard@example.com                         password=${short_password}
&{superAdminCredentials}                          email=bucky.barnes@innovateuk.test                     password=${short_password}
##### DO NOT USE THIS USER FOR ANYTHING ELSE ######
&{terms_and_conditions_login_credentials}         email=laura.jackson@example.com                        password=Passw0rd1357
###################################################