#!/bin/bash
/usr/lib/courier/courier-authlib/authdaemond &
/usr/sbin/couriertcpd -address=0 -maxprocs=40 -maxperip=20 -nodnslookup -noidentlookup 8143 /usr/lib/courier/courier/imaplogin /usr/bin/imapd Maildir
