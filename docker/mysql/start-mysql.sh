docker build --tag starter/jpa-mysql:1.0 .
docker run -d \
-p 3306:3306 \
--name jpa-starter-mysql \
starter/security-jpa:1.0
