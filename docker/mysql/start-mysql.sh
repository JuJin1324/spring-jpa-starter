docker build --tag starter/jpa-mysql:1.0 .
docker run -d \
-p 3311:3306 \
--name jpa-starter-mysql \
starter/jpa-mysql:1.0
