FROM owasp/zap2docker-stable

USER root
WORKDIR /root/

# Set environment variables
ENV LC_ALL C.UTF-8
ENV LANG C.UTF-8
ENV HOME /root/
ENV OLDPWD /root/.ZAP
ENV PWD /root

RUN apt-get update -y
# Add Google Chrome Repo
RUN curl -sS -o - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add
RUN echo "deb https://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list

# Install Dependencies
RUN apt-get install -y apt-transport-https
RUN apt-get update -y
RUN apt-get install -y python3-pip && apt-get install -y unzip openjdk-8-jre-headless xvfb libxi6 libgconf-2-4

# Install Google Chrome
RUN apt-get install -y google-chrome-stable

# Add ZAP Python Scripts
RUN pip install --upgrade pip
RUN pip3 install --upgrade pip
RUN pip3 install zapcli

WORKDIR /root/
# Do not forget to set this to ZAP
ENV http_proxy http://127.0.0.1:80
# Reuse webdrivers
ENV PATH $PATH:/root/.ZAP/webdriver/linux/64/