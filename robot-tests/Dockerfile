FROM ubuntu:18.04

RUN apt-get update && \
    apt-get install -y mysql-client && \
    apt-get install -y curl && \
    apt-get install -y python-pip && \
    apt-get install -y xvfb && \
    apt-get install -y rsync && \
    apt-get install -y telnet && \
    rm -rf /var/lib/apt/lists/*

COPY robotPythonLibs-requirements.txt /tmp/
RUN pip install -r /tmp/robotPythonLibs-requirements.txt
COPY . /tmp/

WORKDIR robot-tests
COPY ./ ./

CMD ["./os_run_tests.sh", "-q"]
#CMD ["sleep", "10000000000000"]
