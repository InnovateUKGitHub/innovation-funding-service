ps -ef | grep apache-tomcat-web | grep 'Bootstrap start' | awk '{print $2}' | xargs -i kill -9 {}
