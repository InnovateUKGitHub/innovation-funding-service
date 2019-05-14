FROM ubuntu:18.04

RUN apt-get update && \
    apt-get install -y mysql-client && \
    apt-get install -y curl && \
    apt-get install -y python-pip && \
    apt-get install -y xvfb && \
    apt-get install -y rsync && \
    apt-get install -y telnet && \
    rm -rf /var/lib/apt/lists/*

ARG BAMBOO_CREDS_ARG
ARG BAMBOO_URL_ARG
ARG BAMBOO_PLAN_PROJ_ARG
ARG BAMBOO_BUILD_NO_ARG

ENV BAMBOO_CREDS=$BAMBOO_CREDS_ARG
ENV BAMBOO_URL=$BAMBOO_URL_ARG
ENV BAMBOO_PLAN_PROJ=$BAMBOO_PLAN_PROJ_ARG
ENV BAMBOO_BUILD_NO=$BAMBOO_BUILD_NO_ARG

COPY robotPythonLibs-requirements.txt /tmp/
RUN pip install -r /tmp/robotPythonLibs-requirements.txt
COPY . /tmp/

WORKDIR robot-tests
COPY ./ ./

CMD ["./os_run_tests.sh", "-q"]
