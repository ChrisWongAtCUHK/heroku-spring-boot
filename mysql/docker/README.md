# Docker MySQL Server
## Build
```
docker build \
  --build-arg AIVEN_DATABASE=${AIVEN_DATABASE} \
  --build-arg AIVEN_UID=${AIVEN_UID} \
  --build-arg AIVEN_PWD=${AIVEN_PWD} \
  -t mysql-server .
```
## Run with volume
```
docker run \
  -v mysql_data:/var/lib/mysql \
  -d -p 18211:3306 mysql-server
```