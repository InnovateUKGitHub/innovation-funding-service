FROM innovateuk/oracle-jre

# - Add service user for the application
RUN useradd --home /opt/app --system app
RUN mkdir -p /opt/app

# - Logs for Tomcat. These are required for CoScale.
RUN mkdir -p /var/log/tomcat
RUN chown app /var/log/tomcat

WORKDIR /opt/app
USER app
