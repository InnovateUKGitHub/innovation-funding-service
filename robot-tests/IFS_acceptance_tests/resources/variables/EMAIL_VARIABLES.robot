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
