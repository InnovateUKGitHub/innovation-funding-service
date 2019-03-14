*** Settings ***
Documentation    IFS-5208 EU registrants email list
...
...              IFS-5265 EU registrants DB - automated email send to systematic applicants
...
...              IFS-5266 EU registrants email list - add additional columns and filter out research
...
Suite Setup  Custom Setup
Suite Teardown  Custom Teardown
Resource          ../../resources/defaultResources.robot


*** Variables ***
${notifyPortalRegistrantsPage}   ${server}/management/eu-invite-non-notified
${underwriteGuaranteePage}       ${server}/eu-grant/overview

*** Test Cases ***
Business organisation registers for underwrite guarantee
    [Documentation]  IFS-5266
    Given the user navigates to the page                   ${underwriteGuaranteePage}
    Then the user registers for the underwrite guarantee   Business   INNOVATE   Business Name

RTO organisation registers for underwrite guarantee
    [Documentation]  IFS-5266
    Given the user navigates to the page                   ${underwriteGuaranteePage}
    Then the user registers for the underwrite guarantee   Research and technology organisation (RTO)   INNOVATE   ResearchTech Name

Public sector or charity organisation registers for underwrite guarantee
    [Documentation]  IFS-5266
    Given the user navigates to the page                   ${underwriteGuaranteePage}
    Then the user registers for the underwrite guarantee   Public sector, charity or non Je-S registered research organisation   INNOVATE    Jes Name

Research organisation registers for underwrite guarantee
    [Documentation]  IFS-5266  IFS-5266
    Given the user navigates to the page                   ${underwriteGuaranteePage}
    Then the user registers for the underwrite guarantee   Research   RECOFTC    Research Name
    [Teardown]  The user closes the browser

Registration details of all organisation types except for Research appear on the Notify Registrants Portal page
    [Documentation]  IFS-5208
    [Setup]  the user logs-in in new browser            &{Comp_admin1_credentials}
    Given the user navigates to the page                ${notifyPortalRegistrantsPage}
    Then the user should see the element                jQuery = tr:contains("Business Name")
    And the user should see the element                 jQuery = tr:contains("ResearchTech Name")
    And the user should see the element                 jQuery = tr:contains("Jes Name")
    And the user should not see the element             jQuery = tr:contains("Research Name")

Send an email invite to a Business
    [Documentation]  IFS-5266
    Given the user clicks the button/link                  jQuery = tr:contains("Business Name") :checkbox ~ label
    When the user clicks the button/link                   jQuery = button:contains("Send email to selected")
    Then the user reads his email                          test@test.com  	Invite to register Horizon 2020 grant transfer of (IA) Innovation action   You have been contacted as a result of your registration on the Horizon 2020 registration portal

*** Keywords ***
Custom Setup
    The guest user opens the browser

Custom Teardown
    The user closes the browser

The user registers for the underwrite guarantee
    [Arguments]  ${orgType}  ${orgName}  ${contactName}
    The user completes your organisation section       ${orgType}  ${orgName}
    The user completes the contact details section     ${contactName}
    The user completes funding details section
    The user clicks the button/link                    id = submit-grant
    The user clicks the button/link                    css = .registration-modal button[type="submit"]
    The user should see the element                    jQuery = h1:contains("Registration complete")

The user completes your organisation section
    [Arguments]  ${orgType}  ${orgName}
    The user clicks the button/link           link = Your organisation
    The user clicks the button/link           jQuery = span:contains("${orgType}")
    The user clicks the button/link           jQuery = button:contains("Save and continue")
    The user enters text to a text field      id = organisationSearchName    ${orgName}
    The user clicks the button/link           id = org-search
    The user clicks the button/link           jQuery = button:contains("${orgName}")
    The user clicks the button/link           link = Save and return

The user completes the contact details section
    [Arguments]  ${contactName}
    The user clicks the button/link           link = Contact details
    The user enters text to a text field      id = name        ${contactName}
    The user enters text to a text field      id = jobTitle    Job title
    The user enters text to a text field      id = email       test@test.com
    The user enters text to a text field      id = telephone   012345678901
    The user clicks the button/link           jQuery = button:contains("Continue")
    The user should see the element           jQuery = dl:contains("Name")
    The user should see the element           jQuery = dl:contains("Job title")
    The user should see the element           jQuery = dl:contains("test@test.com")
    The user should see the element           jQuery = dl:contains("012345678901")
    The user clicks the button/link           link = Save and return

The user completes funding details section
    The user clicks the button/link                        link = Funding details
    The user enters text to a text field                   id = grantAgreementNumber            123456
    The user enters text to a text field                   id = participantId                   123456789
    The user selects the index from the drop-down menu     12   id=actionType  #(IA) Innovation action
    The user enters text to a text field                   id = projectName                     (IA) Innovation action
    The user enters text to a text field                   id = startDateMonth                  10
    The user enters text to a text field                   id = startDateYear                   2010
    The user enters text to a text field                   id = endDateMonth                    10
    The user enters text to a text field                   id = endDateYear                     2020
    The user enters text to a text field                   id = fundingContribution             123456
    The user clicks the button/link                        jQuery = label:contains("No")
    The user clicks the button/link                        jQuery = label:contains("No")
    The user clicks the button/link                        jQuery = button:contains("Continue")
    The user clicks the button/link                        jQuery = a:contains("Save and return")

