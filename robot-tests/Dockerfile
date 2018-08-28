FROM ubuntu:16.04

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
# RUN pip install -U selenium==3.3.1 && \
#     pip install robotframework \
#                 robotframework-selenium2library \
#                 robotframework-imaplibrary \
#                 robotframework-xvfb \
#                 robotframework-excellibrary \
#                 robotframework-ftplibrary \
#                 openpyxl \
#                 simplejson \
#                 requests \
#                 robotframework-databaselibrary \

WORKDIR robot-tests
COPY ./ ./

CMD ["./os_run_tests.sh", "-q"]
#CMD ["sleep", "10000000000000"]
