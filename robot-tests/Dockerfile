FROM ubuntu:18.04

RUN apt-get update && \
    apt-get install -y mysql-client && \
    apt-get install -y curl && \
    apt-get install -y python-pip && \
    apt-get install -y xvfb && \
    apt-get install -y rsync && \
    apt-get install -y telnet && \
    rm -rf /var/lib/apt/lists/*

ARG GID=993
ARG UID=996
ARG PW=default

ENV GID=993
ENV UID=996
ENV PW=default

RUN groupadd -g ${GID} gluster
RUN useradd -p ${PW} -u ${UID} -g ${GID} gluster

COPY robotPythonLibs-requirements.txt /tmp/
RUN pip install -r /tmp/robotPythonLibs-requirements.txt
COPY . /tmp/
COPY ../setup-files/scripts/docker/set-umask0002.sh /robot-tests/set-umask0002.sh

WORKDIR robot-tests
COPY ./ ./
RUN find . -type d -exec chmod 777 {} +
USER gluster

ENTRYPOINT ["./set-umask0002.sh"]
CMD ["sh", "-c", "./os_run_tests.sh -q"]
