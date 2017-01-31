*** Settings ***

Documentation     INFUND-7358     Inflight competition dashboards: Ready to open dashboard
...
...               INFUND-7562      Inflight competition dashboards: Open dashboard
Suite Setup       Run Keywords    Log in as user    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***


