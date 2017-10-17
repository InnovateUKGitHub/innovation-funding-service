FROM ubuntu:xenial AS JRE_DOWNLOADER

ENV JRE_VERSION=8-u144
ENV JRE_DOWNLOAD_URL=http://download.oracle.com/otn-pub/java/jdk/8u144-b01/090f390dda5b47b9b721c7dfaa008135/server-jre-8u144-linux-x64.tar.gz
ENV JRE_SHA256=8ba6f1c692518beb0c727c6e1fb8c30a5dfcc38f8ef9f4f7c7c114c01c747ebc

ENV JCE_POLICY_DOWNLOAD_URL=http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip
ENV JCE_POLICY_SHA256=f3020a3922efd6626c2fff45695d527f34a8020e938a49292561f18ad1320b59

##############################################
# Install all dependencies we need from Ubuntu
# - curl to download the JRE
# - unzip to unzip the JCE policy
# - corutils to have sha256sum, to verify the downloads
ENV JRE_BOOTSTRAP_PKGS="curl unzip coreutils"

RUN apt-get update
RUN apt-get install -yq $JRE_BOOTSTRAP_PKGS

####################
# Install Oracle JRE
# - Dowloads the server JRE to /tmp
# - Verify the cryptographic signature of the file, to prevent corruption
# - Extract download & clean up downloaded file
# - Mark the Oracle JDK as the one to be used by Ubuntu

# Common curl retry options, retry 10 times, continue where we left off,
# wait two seconds after each error to continue
# These settings are in place due to the InnovateUK networks.
ENV CURL_RETRY_OPTS="--retry 10 --continue-at - --retry-delay 2"

ENV TMP_DOWNLOAD_FILE=/tmp/server-jre-$JRE_VERSION.tar.gz
RUN curl $CURL_RETRY_OPTS --progress-bar -f --output $TMP_DOWNLOAD_FILE --show-error --location --header "Cookie: oraclelicense=accept-securebackup-cookie" $JRE_DOWNLOAD_URL && \
  ((echo "$JRE_SHA256 $TMP_DOWNLOAD_FILE" | sha256sum --strict --check --quiet) || (echo "SHASUM failed, expected $JRE_SHA256 but got:" >&2; sha256sum $TMP_DOWNLOAD_FILE >&2; exit 1))
RUN tar -xzf $TMP_DOWNLOAD_FILE -C /tmp
RUN rm $TMP_DOWNLOAD_FILE
RUN mkdir -p /user/local/java
RUN mv /tmp/jdk*/ /usr/local/java/
RUN update-alternatives --install "/usr/bin/java" "java" /usr/local/java/bin/java 1

# - Enable Unlimited JCE
ENV TMP_DOWNLOAD_FILE=/tmp/jce-policy-8.zip
RUN curl $CURL_RETRY_OPTS --progress-bar -f --output $TMP_DOWNLOAD_FILE --show-error --location --header "Cookie: oraclelicense=accept-securebackup-cookie" $JCE_POLICY_DOWNLOAD_URL && \
  (echo "$JCE_POLICY_SHA256 $TMP_DOWNLOAD_FILE" | sha256sum --strict --check --quiet) || (echo "SHASUM failed, expected $JCE_POLICY_SHA256 but got:" >&2; sha256sum $TMP_DOWNLOAD_FILE >&2; exit 1)
RUN unzip -o -j -x $TMP_DOWNLOAD_FILE 'UnlimitedJCEPolicyJDK8/*.jar' -d /usr/local/java/jre/lib/security
RUN rm $TMP_DOWNLOAD_FILE

# Now build the actual container, where we only copy over the /usr/local/java from the 1st stage container.
FROM ubuntu:xenial
COPY --from=JRE_DOWNLOADER /usr/local/java /usr/local/java
RUN update-alternatives --install "/usr/bin/java" "java" /usr/local/java/bin/java 1
ENV JAVA_HOME=/usr/local/java/

# Needed for docker-compose health checks
RUN apt-get update && apt-get install -yq curl
