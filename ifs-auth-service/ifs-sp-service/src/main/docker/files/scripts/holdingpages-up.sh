#!/bin/bash
for h in $(find /var/www/ -type d -name locking); do echo "touch $h/bros"; touch $h/bros; done
