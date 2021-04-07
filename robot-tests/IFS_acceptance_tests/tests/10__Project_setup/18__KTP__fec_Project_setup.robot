*** Settings ***
Documentation     IFS-9305  KTP fEC/Non-fEC: display correct finance table if fEC option changes
...
Suite Setup       Custom Suite Setup
Resource          ../../../../resources/defaultResources.robot
Resource          ../../../../resources/common/Applicant_Commons.robot
Resource          ../../../../resources/common/Competition_Commons.robot
Resource          ../../../../resources/common/PS_Common.robot

*** Variables ***
${KTPapplication}       FEC application duplicate
${KTPapplicationId}     ${application_ids["${KTPapplication}"]}
${KTPcompetiton}        FEC KTP competition duplicate
${KTPcompetitonId}      ${competition_ids["${KTPcompetiton}"]}
&{KTPLead}              email=joseph.vijay@master.64    password=${short_password}





*** Test Cases ***







*** Keywords ***
Custom suite setup
    the user logs-in in new browser       &{KTPLead}
    the user clicks the button/link       link = ${KTPapplication}
    the user clicks the button/link       link = Your project finances
    the user clicks the button/link       link = Your fEC model