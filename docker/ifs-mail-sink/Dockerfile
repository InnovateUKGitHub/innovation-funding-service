FROM httpd:2.2

ENV DEBIAN_FRONTEND noninterative

RUN echo "#!/bin/sh\nexit 0" > /usr/sbin/policy-rc.d

RUN apt-get update && \
    apt-get install -y --no-install-recommends postfix courier-base sqwebmail courier-imap sudo && \
	rm -fr /var/lib/apt/lists/*

RUN useradd -ms /bin/bash -p PcdO6g4gV662A smtp
RUN  echo  "smtp    ALL=NOPASSWD: ALL" | sudo tee -a /etc/sudoers

USER smtp
# enable cgi scripts
RUN sudo a2enmod cgi

# configure redirection on apache
ADD 000-default.conf /etc/apache2/sites-enabled/
RUN sudo mv /var/www/sqwebmail /var/www/html/

# Generate script to run at startup

# Expose the HTTP port
EXPOSE 8080
EXPOSE 8025
EXPOSE 8143

WORKDIR /home/smtp
RUN maildirmake Maildir
RUN echo "Listen 8080" | sudo tee /etc/apache2/ports.conf

ADD imap-start.sh /home/smtp/
ADD webmail-start.sh /home/smtp/
ADD start.sh /home/smtp/

USER root
RUN chmod +x /home/smtp/imap-start.sh
RUN chmod +x /home/smtp/webmail-start.sh
RUN chmod +x /home/smtp/start.sh
USER smtp

CMD ["sudo", "./start.sh"]
