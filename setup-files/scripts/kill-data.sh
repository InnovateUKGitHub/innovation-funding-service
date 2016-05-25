ps -ef | grep apache-tomcat-data | grep 'Bootstrap start' | awk '{print $2}' | xargs -i kill -9 {}
