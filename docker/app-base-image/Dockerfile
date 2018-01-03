FROM innovateuk/oracle-jre

ENV APP_ROOT=/opt/app
ENV TOMCAT_LOGS=/var/log/tomcat

RUN mkdir -p ${APP_ROOT}
RUN chmod -R u+x ${APP_ROOT} && \
    chgrp -R 0 ${APP_ROOT}


# - Logs for Tomcat. These are required for CoScale.
RUN mkdir -p ${TOMCAT_LOGS}
RUN chmod -R u+x ${TOMCAT_LOGS} && \
    chgrp -R 0 ${TOMCAT_LOGS}

WORKDIR ${APP_ROOT}
