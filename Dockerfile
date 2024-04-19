# To build this stack:
# docker build -t quay.io/openshiftlabs/mad-coolstore-devspaces:1.0 . --platform linux/x86_64
# docker push quay.io/openshiftlabs/mad-coolstore-devspaces:1.0
# macOS M1: --platform linux/x86_64

FROM registry.redhat.io/devspaces/udi-rhel8:3.12

ENV MANDREL_VERSION=23.1.2.0-Final
ENV MVN_VERSION=3.9.6
ENV GRAALVM_HOME="/usr/local/mandrel-java21-${MANDREL_VERSION}"
ENV PATH="/usr/local/maven/apache-maven-${MVN_VERSION}/bin:${PATH}"
ENV JAVA_HOME="/usr/lib/jvm/java-21-openjdk" 

USER root

RUN wget -O /tmp/openjdk-21.0.2.tar.gz https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz && tar -xvzf /tmp/openjdk-21.0.2.tar.gz && rm -rf /tmp/openjdk-21.0.2.tar.gz && mv jdk-21.0.2 /tmp/java-21-openjdk && sudo mv /tmp/java-21-openjdk /usr/lib/jvm/ && sudo alternatives --install /usr/bin/java java /usr/lib/jvm/java-21-openjdk/bin/java 1

RUN wget -O /tmp/mvn.tar.gz https://archive.apache.org/dist/maven/maven-3/${MVN_VERSION}/binaries/apache-maven-${MVN_VERSION}-bin.tar.gz && sudo tar -xvzf /tmp/mvn.tar.gz && rm -rf /tmp/mvn.tar.gz && mkdir /usr/local/maven && mv apache-maven-${MVN_VERSION}/ /usr/local/maven/ && alternatives --install /usr/bin/mvn mvn /usr/local/maven/apache-maven-${MVN_VERSION}/bin/mvn 1

RUN sudo rpm -Uvh https://dl.fedoraproject.org/pub/epel/epel-release-latest-8.noarch.rpm && sudo microdnf install -y zlib-devel gcc siege gcc-c++ && sudo curl -Lo /usr/bin/jq https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64 && sudo chmod a+x /usr/bin/jq

RUN wget -O /tmp/mandrel.tar.gz https://github.com/graalvm/mandrel/releases/download/mandrel-${MANDREL_VERSION}/mandrel-java21-linux-amd64-${MANDREL_VERSION}.tar.gz && cd /usr/local && sudo tar -xvzf /tmp/mandrel.tar.gz && rm -rf /tmp/mandrel.tar.gz

RUN ln -f -s /usr/lib/jvm/java-21-openjdk/* ${HOME}/.java/current

RUN curl -Ls https://sh.jbang.dev | bash -s - trust add https://repo1.maven.org/maven2/io/quarkus/quarkus-cli/ \
curl -Ls https://sh.jbang.dev | bash -s - app install --fresh --force quarkus@quarkusio

USER user