#!/bin/bash
./webmail-start.sh &
./imap-start.sh &
smtp-sink -c -d /home/smtp/Maildir/new/%M. -u smtp 0.0.0.0:8025 10