*** Settings ***
Documentation     Importing all the Resources so that we can call only one file.
Resource          GLOBAL_LIBRARIES.robot
Resource          keywords/EMAIL_KEYWORDS.robot
Resource          keywords/Login_actions.robot
Resource          keywords/MYSQL_AND_DATE_KEYWORDS.robot
Resource          keywords/SUITE_SET_UP_ACTIONS.robot
Resource          keywords/User_actions.robot
Resource          variables/EMAIL_VARIABLES.robot
Resource          variables/EMAIL_VARIABLES_SENSITIVE.robot
Resource          variables/GLOBAL_VARIABLES.robot
Resource          variables/PASSWORD_VARIABLES.robot
Resource          variables/User_credentials.robot
Resource          keywords/01__Edit_Form_Actions.robot
Resource          keywords/02__Validation_error_keywords.robot
Resource          keywords/03__Navigation_keywords.robot
Resource          keywords/04__Upload_Download_keywords.robot
Resource          ../tests/10__Project_setup/PS_Variables.robot