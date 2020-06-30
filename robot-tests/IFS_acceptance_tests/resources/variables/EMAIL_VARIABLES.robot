*** Variables ***
${valid_email}                  ewan@hiveit.co.uk
${valid_email2}                 agile.Robin@test.com
${invalid_email_plain}          notavalidemail
${invalid_email_symbols}        @%^%#$@#$@#.com
${invalid_email_no_username}    @example.com
${invalid_email_format}         Joe Smith <email@example.com>
${invalid_email_no_at}          email.example.com
${invalid_email_no_domain}      joesmith@example
${sender}                       noreply-innovateuk@example.com    # note that this variable is correct for docker, and is overridden as a pybot argument for other environments
${test_mailbox_one}             worth.email.test
${test_mailbox_two}             worth.email.test.two
${unique_email_number}          1
${submit_bus_email}             ${test_mailbox_one}+submitbus@gmail.com
${submit_rto_email}             ${test_mailbox_one}+submitrto@gmail.com
${local_imap}                   mail
${local_imap_port}              8143
${invite_email}                 ${test_mailbox_one}+inviteorg${unique_email_number}@gmail.com
${international_invite_email}   international@example.com
${lead_applicant}               steve.smith@empire.com
${team_member}                  member@test.com
${partner_org}                  partner@test.com
${uk_based_applicant}           russell.jenkins@load.example.com
${uk_based_applicant_new}       tony.blair@test.com
${lead_international_email}     stephan.marriek@empire.fr
${lead_intl_email_one}          daniel.tan@example.com
${partner_international_email}  tim.simpson@test.com
${lead_intl_email_two}          roselin.messy1@empire.fr
${zero_funding_partner_email}   json.smith@gmail.com