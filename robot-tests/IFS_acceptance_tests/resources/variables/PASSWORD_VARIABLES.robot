*** Variables ***
${correct_password}    Passw0rd123
${incorrect_password}    wrongpassword
${long_password}    passwordpasswordpasswordpasswordpasswordpasswordpassword
${short_password}    pass
${blacklisted_password}    Password123
${blacklisted_password_message}    Password is too weak
${lower_case_password}    thisisallinlowercase1
${lower_case_message}    Password must contain at least one lower case letter
${upper_case_password}    THISISALLINUPPERCASE2
${upper_case_message}    Password must contain at least one upper case letter
${no_numbers_password}    thishasnonumbers
${no_numbers_message}    Password must contain at least one number
${personal_info_password}    Smith123
${personal_info_message}    ${EMPTY}
