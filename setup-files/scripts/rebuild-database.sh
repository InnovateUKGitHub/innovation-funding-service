cd ../../ifs-data-service
./gradlew flywayClean flywayMigrate -x test -x compileJava -x compileTestJava -x testClasses
