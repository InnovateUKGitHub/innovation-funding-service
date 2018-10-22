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

Access https://ifs.local-dev with username lee.bowman@innovateuk.test and password Passw0rd