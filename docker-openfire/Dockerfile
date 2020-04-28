FROM openjdk:8u242-jre-slim

ENV OPENFIRE_VERSION=4.5.1 \
    OPENFIRE_USER=openfire

RUN apt-get -qq update \
 && DEBIAN_FRONTEND=noninteractive apt-get -qq -o=Dpkg::Use-Pty=0 install curl \
 && echo "Downloading openfire_${OPENFIRE_VERSION}_all.deb ..." \
 && curl -fsSL "http://download.igniterealtime.org/openfire/openfire_${OPENFIRE_VERSION}_all.deb" -o /tmp/openfire_${OPENFIRE_VERSION}_all.deb \
 && dpkg -i --force-depends /tmp/openfire_${OPENFIRE_VERSION}_all.deb \
 && rm -rf /tmp/openfire_${OPENFIRE_VERSION}_all.deb

RUN rm -rf /var/lib/apt/lists/*

HEALTHCHECK --interval=1s --retries=20 --timeout=5s CMD curl -fsSL http://localhost:9090

COPY entrypoint.sh /sbin/entrypoint.sh
RUN chmod 755 /sbin/entrypoint.sh

USER $OPENFIRE_USER

EXPOSE 3478/tcp 3479/tcp 5222/tcp 5223/tcp 5229/tcp 5275/tcp 5276/tcp 5262/tcp 5263/tcp 7070/tcp 7443/tcp 7777/tcp 9090/tcp 9091/tcp
ENTRYPOINT ["/sbin/entrypoint.sh"]
