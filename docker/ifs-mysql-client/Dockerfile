FROM debian:jessie-slim
RUN apt-get update
RUN apt-get install -y mysql-client
RUN mkdir -p //.gnupg
RUN chgrp -R 0 //.gnupg && chmod -R g+rwX //.gnupg
CMD ["tail", "-f", "/dev/null"]
