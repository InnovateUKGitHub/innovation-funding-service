#!/bin/bash

if ! whoami &> /dev/null; then
  if [ -w /etc/passwd ]; then
    sed -i "s/:x:$USER_ID:/:x:$(id -u):/" /etc/passwd
  fi
fi

./webmail-start.sh &
./imap-start.sh &
smtp-sink -c -d /home/smtp/Maildir/new/%M. 0.0.0.0:8025 10
