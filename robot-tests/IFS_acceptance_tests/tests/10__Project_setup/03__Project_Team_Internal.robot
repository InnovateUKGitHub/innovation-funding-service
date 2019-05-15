*** Settings ***

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
