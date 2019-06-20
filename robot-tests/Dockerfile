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
RUN pip install -r /tmp/robotPythonLibs-requirements.txt
COPY . /tmp/
COPY set-umask0002.sh /robot-tests/set-umask0002.sh

WORKDIR robot-tests
COPY ./ ./
RUN chown -R gluster:gluster . && \
  find . \( -type d -o -type f \) -exec chmod 775 {} + && \
  chown -R gluster:gluster /tmp && \
  find /tmp \( -type d -o -type f \) -exec chmod 775 {} +

USER gluster

ENTRYPOINT ["./set-umask0002.sh"]
CMD ["sh", "-c", "./os_run_tests.sh -q"]

