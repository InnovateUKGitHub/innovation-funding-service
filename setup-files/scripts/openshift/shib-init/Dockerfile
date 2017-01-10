FROM ubuntu:16.04

RUN apt-get update && \
    apt-get install -y mysql-client && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR script
COPY syncShibServices.sh syncShibServices.sh
RUN chmod +x syncShibServices.sh

CMD ["./syncShibServices.sh"]
