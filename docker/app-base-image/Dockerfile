FROM innovateuk/openjdk


RUN mkdir -p /opt/app
RUN chgrp -R 0 /opt/app && chmod -R g=u /opt/app

# - Logs for Tomcat. These are required for CoScale.
RUN mkdir -p /var/log/tomcat
RUN chgrp -R 0 /var/log/tomcat && chmod -R g=u /var/log/tomcat


WORKDIR /opt/app
