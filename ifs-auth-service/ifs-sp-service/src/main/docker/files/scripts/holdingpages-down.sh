#!/bin/bash
for h in $(find /var/www/ -type d -name locking); do echo "rm $h/bros"; rm $h/bros; done
