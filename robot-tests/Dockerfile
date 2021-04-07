# This dockerfile builds the zap stable release
FROM ubuntu:18.04

ARG DEBIAN_FRONTEND=noninteractive
ARG WEBSWING_TOKEN=""

RUN apt-get update && apt-get install -q -y --fix-missing \
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
	x11vnc  \
	mysql-client  \
    rsync  \
    telnet  \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/*

RUN pip3 install --upgrade pip zapcli python-owasp-zap-v2.4

RUN useradd -d /home/zap -m -s /bin/bash zap
RUN echo zap:zap | chpasswd
RUN mkdir /zap && chown zap:zap /zap

WORKDIR /zap

#Change to the zap user so things get done as the right person (apart from copy)
USER zap

RUN mkdir /home/zap/.vnc

# Download and expand the latest stable release
RUN curl -s https://raw.githubusercontent.com/zaproxy/zap-admin/master/ZapVersions.xml | xmlstarlet sel -t -v //url |grep -i Linux | wget -nv --content-disposition -i - -O - | tar zxv && \
	cp -R ZAP*/* . &&  \
	rm -R ZAP* && \
	# Setup Webswing
	if [ -z "$WEBSWING_TOKEN" ] ; \
	then curl -s -L  "https://storage.googleapis.com/builds.webswing.org/releases/webswing-20.2.1.zip" > webswing.zip; \
	else curl -s -L  "https://dev.webswing.org/${WEBSWING_TOKEN}/nexus/repository/Webswing-Public/webswing-agpl-dist/webswing-20.2.1-distribution.zip" > webswing.zip; fi && \
	unzip webswing.zip && \
	rm webswing.zip && \
	mv webswing-* webswing && \
	# Remove Webswing bundled examples
	rm -Rf webswing/apps/

ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
ENV PATH $JAVA_HOME/bin:/zap/:$PATH
ENV ZAP_PATH /zap/zap.sh

# Default port for use with zapcli
ENV ZAP_PORT 8080
ENV IS_CONTAINERIZED true
ENV HOME /home/zap/
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

COPY zap* CHANGELOG.md /zap/
COPY webswing.config /zap/webswing/
COPY webswing.properties /zap/webswing/
COPY policies /home/zap/.ZAP/policies/
COPY policies /root/.ZAP/policies/
# The scan script loads the scripts from dev home dir.
COPY scripts /home/zap/.ZAP_D/scripts/

#Copy doesn't respect USER directives so we need to chown and to do that we need to be root
USER root

RUN chown zap:zap /zap/* && \
	chown zap:zap /zap/webswing/webswing.config && \
	chown zap:zap /zap/webswing/webswing.properties && \
	chown zap:zap -R /home/zap/.ZAP/

#Change back to zap at the end
USER zap

HEALTHCHECK --retries=5 --interval=5s CMD zap-cli status




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

RUN groupadd -g ${GID} gluster && \
  useradd -p ${PW} -u ${UID} -g ${GID} gluster

VOLUME /mnt/ifs_storage

COPY robotPythonLibs-requirements.txt /tmp/
RUN pip3 install -r /tmp/robotPythonLibs-requirements.txt
COPY . /tmp/
COPY set-umask0002.sh /robot-tests/set-umask0002.sh

WORKDIR robot-tests
COPY ./ ./
RUN chown -R gluster:gluster . && \
  find . \( -type d -o -type f \) -exec chmod 775 {} + && \
  chown -R gluster:gluster /tmp && \
  find /tmp \( -type d -o -type f \) -exec chmod 775 {} +

RUN pip3 install RoboZap

USER gluster

ENTRYPOINT ["./set-umask0002.sh"]
CMD ["sh", "-c", "./os_run_tests.sh -q"]





