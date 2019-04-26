*** Settings ***
Documentation   IFS-5700 - Create new project team page to manage roles in project setup
Resource            ../CommonResource.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

Foo Test Case
    [tags]              FooTag
    [Documentation]     Created by John Doe
    Do An Action        Argument
    Do Another Action   ${robotVar}
