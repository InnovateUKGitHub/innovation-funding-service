FROM docker-ifs.devops.innovateuk.org/releases/ubuntu:20.04

USER root

ARG DEBIAN_FRONTEND=noninteractive
# ARG WEBSWING_TOKEN=""

RUN apt-get update
RUN apt-get install -y mysql-client
RUN apt-get install -y rsync
RUN apt-get install -y telnet
RUN apt-get install fastjar

RUN apt-get install -q -y --fix-missing \
	make \
	automake \
	autoconf \
	gcc g++ \
	openjdk-11-jdk \
	wget \
	curl \
	xmlstarlet \
	unzip \
	git \
	openbox \
	xterm \
	net-tools \
	python3-pip \
	python-is-python3 \
	firefox \
	xvfb \
	x11vnc && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ARG BAMBOO_CREDS_ARG
ARG BAMBOO_URL_ARG
ARG BAMBOO_PLAN_PROJ_ARG
ARG BAMBOO_BUILD_NO_ARG
ARG GID=993
ARG UID=996
ARG PW=default

ENV BAMBOO_CREDS=$BAMBOO_CREDS_ARG
ENV BAMBOO_URL=$BAMBOO_URL_ARG
ENV BAMBOO_PLAN_PROJ=$BAMBOO_PLAN_PROJ_ARG
ENV BAMBOO_BUILD_NO=$BAMBOO_BUILD_NO_ARG
ENV GID=993
ENV UID=996
ENV PW=default

COPY robotPythonLibs-requirements.txt /tmp/
RUN pip3 install -r /tmp/robotPythonLibs-requirements.txt
COPY . /tmp/
COPY set-umask0002.sh /robot-tests/set-umask0002.sh



WORKDIR robot-tests
RUN chmod -R 777 ./

COPY ./ ./

ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
# Default port for use with zapcli
ENV IS_CONTAINERIZED true
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

ARG KUBECTL_VERSION=v1.22.4
RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/${KUBECTL_VERSION}/bin/linux/amd64/kubectl && \
  mv kubectl /usr/bin/kubectl && \
  chmod +x /usr/bin/kubectl

COPY IFS_acceptance_tests/resources/variables/EMAIL_VARIABLES_TEMPLATE.robot /robot-tests/IFS_acceptance_tests/resources/variables/EMAIL_VARIABLES_SENSITIVE.robot

ENTRYPOINT ["./set-umask0002.sh"]
CMD ["sh", "-c", "./os_run_tests.sh -q"]