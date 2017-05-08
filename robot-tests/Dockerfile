FROM ubuntu:16.04

RUN apt-get update && \
    apt-get install -y mysql-client && \
    apt-get install -y curl && \
    apt-get install -y python-pip && \
    apt-get install -y xvfb && \
    apt-get install -y rsync && \
    apt-get install -y telnet && \
    rm -rf /var/lib/apt/lists/*

RUN pip install robotframework && \
    pip install robotframework-selenium2library && \
	pip install robotframework-imaplibrary && \
	pip install robotframework-xvfb && \
	pip install robotframework-excellibrary && \
	pip install robotframework-ftplibrary && \
	pip install openpyxl && \
	pip install -U selenium && \
	pip install simplejson requests && \
	pip install robotframework-databaselibrary && \
	pip install pymysql && \
	pip install pdfminer

WORKDIR robot-tests
COPY ./ ./

CMD ["./os_run_tests.sh", "-q"]
#CMD ["sleep", "10000000000000"]

