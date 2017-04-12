#!/usr/bin/env bash

for i in "$@"
do
    case $i in

        -c)
            # Configuration mode: return the custom metrics data should be defined

            echo -n '{';
            # First we define how long the script is allowed to run
            echo -n '"maxruntime":1000,'

            # Second we set the period the script will run, in this case it will run every 60 seconds
            echo -n '"period": 60,'

            # Next we define the metrics we want the CoScale agent to fetch every minute
            echo -n '"metrics":['
            echo -n '    {'
            echo -n '        "id": 1,'
            echo -n '        "datatype": "DOUBLE",'
            echo -n '        "name": "AJP Connection Count",'
            echo -n '        "description": "Number of 8009 (AJP) connections",'
            echo -n '        "groups": "Network",'
            echo -n '        "unit": "",'
            echo -n '        "tags": "",'
            echo -n '        "calctype": "Instant"'
            echo -n '    }'
            echo    ']}'
            ;;

        -d)
            # Data retrieval mode: return the data for the custom metrics

            # Get the number of connections to port 8009
            echo "M1 $(netstat -t -n | awk '{print $4}' | grep ':8009' | wc -l)"
            ;;
    esac
done
