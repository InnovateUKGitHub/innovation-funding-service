FROM ubuntu:16.04

RUN apt-get update && \
    apt-get install -y mysql-client && \
    apt-get install -y curl && \
    apt-get install -y python-pip && \
    apt-get install -y xvfb && \
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
	pip install pymysql

WORKDIR robot-tests
COPY ./ ./

CMD ["./micro_run_tests.sh", "-qhp"]
