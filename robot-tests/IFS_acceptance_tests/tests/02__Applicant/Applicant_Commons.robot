*** Settings ***
Resource    ../resources/defaultResources.robot

*** Variables ***

*** Keywords ***
the applicant completes the application details
    the user clicks the button/link    link=Application details
    Clear Element Text    id=application_details-startdate_day
    The user enters text to a text field    id=application_details-startdate_day    18
    Clear Element Text    id=application_details-startdate_year
    The user enters text to a text field    id=application_details-startdate_year    2018
    Clear Element Text    id=application_details-startdate_month
    The user enters text to a text field    id=application_details-startdate_month    11
    The user enters text to a text field    id=application_details-duration    20
    the user selects the radio button    application.resubmission    false
    the user clicks the button/link    name=mark_as_complete


the applicant incompletes the application details
    the user clicks the button/link    link=Application details
    the user clicks the button/link    name=mark_as_incomplete
