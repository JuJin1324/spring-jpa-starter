FROM mysql:8.0.31
LABEL maintainer="jujin1324@daum.net"
LABEL dockerhub="https://hub.docker.com/_/mysql"

EXPOSE 3306

ADD jpa-starter.cnf /etc/mysql/conf.d/

ENV MYSQL_ROOT_PASSWORD root
ENV MYSQL_DATABASE jpa_starter
ENV MYSQL_USER jpa
ENV MYSQL_PASSWORD starter
