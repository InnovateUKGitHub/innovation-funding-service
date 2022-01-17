ifs-starter-audit
=

    Add audit capabilities

Usage
-
Add ifs-starter-audit dependency 'starter:ifs-starter-logging'

AuditMessageBuilder for building messages

Audit.audit(AuditMessageBuilder.build()) - to log or send over AMQP

SPRING_PROFILES_ACTIVE: AMQP/Other

AuditType - enum disambiguation

Profile: AMQP
-
Allows audit via AMQP - this requires a configured and running RabbitMQ in docker/k8s

Profile: (not AMQP)
-
Logs audit calls for collection and collation in e.g. logstash/elastic

api:audit-api
-
    This is the api for this starter
