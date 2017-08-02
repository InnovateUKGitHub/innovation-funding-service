#!/bin/sh
echo "testing endpoints.txt with concurrency 10 and 1000 requests"
 ./run-test.sh endpoints.txt 10 100 && ./plot.sh endpoints.txt
