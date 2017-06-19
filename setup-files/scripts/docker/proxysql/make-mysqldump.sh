#!/usr/bin/env bash

mysqldump -uroot -ppassword -h127.0.0.1 -P6033 ifs --skip-extended-insert > /tmp/dump.sql