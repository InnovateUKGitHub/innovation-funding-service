*** Settings ***
Documentation    
...

#Suite Setup       Custom suite setup
#Suite Teardown    the user closes the browser
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${supporter01_email}                  mister.branches@money.com
${supporter02_email}                  horrace.horse@anarchy.com
&{Supporter01_credentials}            email=${supporter01_email}  password=${short_password}
&{Supporter02_credentials}            email=${supporter01_email}  password=${short_password}
#&{KTP_Application_Link}               
${KTP_Application_URL}                ${SERVER}/assessment/cofunder/application/247/response

*** Test Cases ***

The user sees the validation when responding to the Cofunder/Supprter review
    [Documentation]   IFS-8409
    #Given the user logs-in in new browser               &{Supporter01_credentials}
    Then the user navigates to the page                 ${KTP_Application_URL} 
    Logging in and Error Checking                       ${supporter01_email}  ${short_password}
    the user clicks the button/link                     jQuery = button:contains("Save review and return to applications")
    #the user should see a field and summary error       Please select an option.  #To do TBC 
    the user should see a field and summary error       Please enter some text.
    #To do - Should be more erros here!!! More enter text and select an option!!!

    the user selects the radio button     decision  decision-no
    the user clicks the button/link       jQuery = button:contains("Save review and return to applications")
    #the user should not see a field and summary error       Please select an option.  #To do TBC 
    the user should see a field and summary error       Please enter some text.

    the user selects the radio button     decision  decision-yes
    the user clicks the button/link       jQuery = button:contains("Save review and return to applications")
    #the user should not see a field and summary error       Please select an option.  #To do TBC 
    the user should see a field and summary error       Please enter some text.

The user responds to the Cofunder/Supprter review No
    [Documentation]   IFS-8409
    the user selects the radio button            decision  decision-no
    
    The user enters text to a docusign field     comments  This is the comments from the supporter
    the user clicks the button/link              jQuery = button:contains("Save review and return to applications")

The user responds to the Cofunder/Supprter review No
    [Documentation]   IFS-8409
    Then the user navigates to the page                 ${KTP_Application_URL} 
    the user selects the radio button            decision  decision-yes
    
    The user enters text to a docusign field     comments  This is the comments from the supporter
    the user clicks the button/link              jQuery = button:contains("Save review and return to applications")


*** Keywords ***
Custom suite setup
    The user logs-in in new browser     &{ifs_admin_user_credentials}  #May need to change this