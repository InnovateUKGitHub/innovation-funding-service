FROM davidcaste/alpine-tomcat:jdk8tomcat8
MAINTAINER David Soff <dsoff@worth.systems>

COPY setup-files/scripts/docker/jrebel.jar /jrebel.jar

ENV IVY_HOME /cache
ENV GRADLE_VERSION 2.8
ENV GRADLE_HOME /usr/local/gradle
ENV PATH ${PATH}:${GRADLE_HOME}/bin
ENV CATALINA_OPTS="-showversion -javaagent:/jrebel.jar -Drebel.remoting_plugin=true"

WORKDIR /usr/local
RUN wget  https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip && \
    unzip gradle-$GRADLE_VERSION-bin.zip && \
    rm -f gradle-$GRADLE_VERSION-bin.zip && \
    ln -s gradle-$GRADLE_VERSION gradle && \
    echo -ne "- with Gradle $GRADLE_VERSION\n" >> /root/.built

VOLUME /code

WORKDIR /code

EXPOSE 8080

ENTRYPOINT sh /opt/tomcat/bin/catalina.sh run

COPY setup-files/scripts/docker/server.xml /opt/tomcat/conf/server.xml