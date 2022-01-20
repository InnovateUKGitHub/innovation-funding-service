ifs-starter-logging
===================

Use
---
> implementation "org.innovateuk.ifs.libraries:ifs-starter-logging"

    In logback-spring.xml per application...
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <include resource="logback-ifs.xml" />
    </configuration>

Outcome
---

Enables logging actuator endpoints

Per profile logback configuration

Dev Profile
---

Standard spring config coloured console with -:
1. ROOT as INFO
2. ifs code as DEBUG

Prod Profile
---

LogstashEncoder encoded json output
1. ROOT as ERROR
2. ifs code as WARN
   
Default
---

LogstashEncoder encoded json output
1. ROOT as WARN
2. ifs code as INFO