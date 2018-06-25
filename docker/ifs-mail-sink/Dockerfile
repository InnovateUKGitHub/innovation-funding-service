FROM httpd:2.2

ENV DEBIAN_FRONTEND noninterative
ENV FQDN localhost
ENV USER_NAME smtp
ENV USER_ID 10001
ENV USER_PASSWORD PcdO6g4gV662A

RUN echo "#!/bin/sh\nexit 0" > /usr/sbin/policy-rc.d

RUN apt-get update && \
    apt-get install -y --no-install-recommends postfix courier-base sqwebmail courier-imap && \
    apt-get autoremove -y && \
    rm -fr /var/lib/apt/lists/*

RUN useradd -ms /bin/bash -p $USER_PASSWORD -u $USER_ID -g 0 $USER_NAME

# enable cgi scripts
RUN a2enmod cgi && \
    a2enmod ssl

# configure redirection on apache
ADD 000-default.conf /etc/apache2/sites-enabled/
RUN mv /var/www/sqwebmail /var/www/html/sqwebmail
# Generate script to run at startup

# Expose the ports
EXPOSE 8080
EXPOSE 8025
EXPOSE 8143
EXPOSE 4443

WORKDIR /home/smtp
RUN maildirmake Maildir && \
    echo "Listen 8080" | tee /etc/apache2/ports.conf && \
    echo "Listen 4443" | tee -a /etc/apache2/ports.conf

ADD generate-certs.sh /home/smtp/
ADD imap-start.sh /home/smtp/
ADD webmail-start.sh /home/smtp/
ADD start.sh /home/smtp/

RUN chown -R 0 / 2>/dev/null || true
RUN chmod -R 777 / 2>/dev/null || true
USER $USER_ID

CMD ["./start.sh"]
