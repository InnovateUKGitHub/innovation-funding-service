FROM alpine:3.5
RUN apk add --no-cache mysql-client
RUN apk add --no-cache gnupg
RUN apk add --no-cache rng-tools
RUN mkdir -p //.gnupg
RUN chgrp -R 0 //.gnupg && chmod -R g+rwX //.gnupg
CMD ["tail", "-f", "/dev/null"]
