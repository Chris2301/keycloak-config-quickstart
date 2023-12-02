# Base has no package manager, so we add ubi to install wget for health checks
FROM registry.access.redhat.com/ubi9-minimal as base

# Intermediate builder stage
FROM quay.io/keycloak/keycloak:23.0.1 as builder

WORKDIR /opt/keycloak

# for demonstration purposes only, please make sure to use proper certificates in production instead
# RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 -dname "CN=server" -alias server -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -keystore conf/server.keystore

USER 1000

RUN /opt/keycloak/bin/kc.sh build --db=postgres

# Final stage
FROM registry.access.redhat.com/ubi9-minimal

RUN microdnf update -y && \
    microdnf reinstall -y tzdata && \
    microdnf install -y java-17-openjdk-headless && \
    microdnf clean all && rm -rf /var/cache/yum/* && \
    echo "keycloak:x:0:root" >> /etc/group && \
    echo "keycloak:x:10001:0:keycloak user:/opt/keycloak:/sbin/nologin" >> /etc/passwd && \
    ln -sf /usr/share/zoneinfo/Europe/Amsterdam /etc/localtime # set timezone

COPY --from=builder /opt/keycloak/ /opt/keycloak/

COPY --from=builder --chown=1000:0 /opt/keycloak /opt/keycloak

USER 1000
WORKDIR /opt/keycloak-config

EXPOSE 8080
EXPOSE 8443

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]