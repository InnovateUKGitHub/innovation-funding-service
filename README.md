# Innovation Funding Service Project

Grant management system.

# Quick Installation

Before starting make sure you have the following installed

* Linux or MacOS environment
* Docker - increase Docker memory allocation to at least 8GiB
* Java 8

Update your /etc/hosts file to include

```
127.0.0.1 ifs.local-dev
127.0.0.1 auth.local-dev
127.0.0.1 ifs-database
127.0.0.1 ifs-finance-database
127.0.0.1 ifs-survey-database
```

Create docker network

    docker network create ifs

Copy the properties from the starter.properties file into ~/.gradle/gradle.properties - your local gradle properties
file.

Build and deploy

    ./gradlew clean build deploy wait syncShib -Pinitialise=true -x test

Access https://ifs.local-dev with one of the following users (with password "Passw0rd")

* steve.smith@empire.com - lead applicant
* john.doe@innovateuk.test - competition admin
* lee.bowman@innovateuk.test - project finance

# Further configuration

You can enable various services in your ~/.gradle/gradle.properties and then re-build if you wish to run more of the
stack.  Note that you may need to allocate some more memory in Docker preferences for this.

# Gradle Tasks

Gradle is efficient - it wont re-run tasks if it doesn't need to (--rerun-tasks overrides this behaviour if you want).
If a jar has been built and no source files have changed it wont build the jar again.


| Task     | Description
|----------|--------------
| deploy   | Deploy docker service.
| stop     | Stops docker service.
| syncShib | Syncs user data from database to LDAP after database initialisation.
| clean    | Cleans the build directory of the project.

## Typical combinations

Deploy a branch for the first time with out full test suite

    ./gradlew clean build deploy wait syncShib -Pinitialise=true -x test

Redeploy my changed jar files (no database changes)

    ./gradlew clean build deploy wait

Redeploy a specific service

    ./gradlew ifs-web-service:ifs-application-service:deploy wait

I have made database changes only

    ./gradlew flywayClean flywayMigrate

I am a tester and want to deploy my system for testing

    ./robot_tests/micro_run_tests

I am a tester and I want to swap to a new branch for testing

    ./gradlew stop -Pinitialise=true && ./robot_tests/micro_run_tests

Running a gradle task for only web services (eg. test)

    ./gradlew test -p ifs-web-service

Create asciidoc

    ./gradlewe clean asciidoctorOnly


