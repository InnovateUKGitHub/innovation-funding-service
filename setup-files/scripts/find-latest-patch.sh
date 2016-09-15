cd ../../ifs-data-service

find src/main/resources/db -name "*.sql" | sed 's/.*\(\/V.*\)/\1/g' | sort --version-sort | tail -1
