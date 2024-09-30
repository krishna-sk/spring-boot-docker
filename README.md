1. [Commands to start the Project](#Commands-to-start-the-Project)
    1. [use docker run](#use-docker-run)
    2. [use docker compose](#use-docker-compose)
2. [Docker Commands](#Docker-Commands)
3. [Docker networking](#Docker-networking)
4. [Docker Compose](#Docker-compose)

# Commands to start the Project

### use docker compose

* you can directly use docker-compose from the project root directory if you want

``` 
docker-compose up  
docker-compose down
docker-compose start
docker-compose stop
```

* Use up to start everything from scratch.
* Use down to stop and remove everything.
* Use start to resume stopped containers.
* Use stop to pause running containers without deleting them.

### use docker run

* Create a docker image from the project root directory

```
docker build -t spring-boot-docker:1.0 .
```

* Create network to run both spring boot app and mysql_container on same network so that they can communicate.

```
 docker network create spring-boot-network
```

* run the mysql image on spring-boot-network which we created above, port 3306 and provide root password for user root(
  default root user) and create test_user
  and test_password and create db 'test' during container starting and mount the volume for persistence.

```
docker run --name mysql_container --network spring-boot-network -p3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=test
 -e MYSQL_USER=test_user -e MYSQL_PASSWORD=test_password -v /volume_docker/mysql:/var/lib/mysql -d mysql

```

* run spring boot image spring-boot-network which we created above, port 8080 and provide spring.profiles.active which
  will pick prod config and mysql username and password
  and use spring.datasource.url as well,as url from provide in application-prod.yml is causing issues, might be
  environmental issues.

```
docker run --name spring_app --network spring-boot-network -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod 
-e MYSQL_USERNAME=test_user -e MYSQL_PASSWORD=test_password
-e SPRING_DATASOURCE_URL="jdbc:mysql://mysql_container:3306/test" -d spring-boot-docker:1.0

// giving communication link failure error
docker run --name spring_app --network spring-boot-network -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod 
-e MYSQL_USERNAME=test_user -e MYSQL_PASSWORD=test_password
-e MYSQL_SERVER=mysql_container -d spring-boot-docker:1.0
```

## Docker Commands

Docker commands are essential for managing your containers and images in this project.

## Overview

Docker is a platform designed to help developers build, run, and manage applications in containers. Below are some key
commands you will use frequently.

### 1. `docker info`

Displays system-wide information about Docker, including the number of containers and images. Great for verifying your
Docker setup.

### 2. `docker --version`

Shows the currently installed Docker version. Useful for ensuring compatibility with Docker images.

### 3. `docker login`

Authenticates your Docker client to a registry. You'll need your username and password to pull/push images from/to
private repositories.

### 4. `docker ps -a`

Lists all containers, including stopped ones. Essential for tracking all instances of your containers.

### 5. `docker ps`

Displays only running containers. Helpful for monitoring active applications.

### 6. `docker images`

Shows all local images available for creating containers. Useful for managing your Docker images.

### 7. `docker rmi <imageName>`

Removes a specified image from your local repository. Be careful, this deletes the image permanently.

### 8. `docker rm <containerName>`

Removes a stopped container.

### 9. `docker start <containerName>`

Starts a specified stopped container. Use this to resume applications.

### 10. `docker stop <containerName>`

Stops a running container. This command is useful for gracefully shutting down services.

### 11. `docker build <imageName:tag> Dockerfile location`

Builds the docker image.

### 12. `docker network create <networkName>`

Creates a new Docker network for container communication.

### 13. `docker pull <imageName>`

Downloads a specified image from a Docker registry to your local machine. Essential for obtaining images to run.

### 14. `docker run <options> <imageName>`

Creates and starts a new container from a specified image. Options allow you to customize the container's behavior, such
as port mapping or naming.

### 15. `docker network inspect <networkName>`

This command retrieves detailed information about a specified Docker network. It provides information such as the
network's configuration, connected containers, IP address ranges, and more.

### 16. `docker inspect <imageName>`

This command retrieves detailed information about a specified Docker image. It includes metadata such as image layers,
environment variables, ports, and configurations.

### 17. `docker inspect <containerName>`

This command retrieves detailed information about a specified Docker container. It provides insights into the
container's configuration, runtime status, network settings, and more.

## Docker networking

* when you start docker containers without any network option from docker run command they are on default network
  provided by docker, and they can talk with host and each other on the same default network. inspect the container and
  get the ips and login to one pod and ping another pods ip, you
  are able to connect.

Dockerfile

```
# Use the OpenJDK 17 base image
FROM openjdk:8-jdk-slim

RUN apt-get update && \
    apt-get install -y curl iputils-ping && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Expose the application port
EXPOSE 8080

# Use tail to keep the container running
CMD ["tail", "-f", "/dev/null"]
```

steps to check the networking

```
docker build -t app:1.0 .

docker run --name app1 -p8080:8080 -d app:1.0

docker run --name app2 -p8081:8080 -d app:1.0

# get container ip => 172.17.0.2
docker inspect app1 

# get container ip => 172.17.0.3
docker inspect app2 

docker exec -it app1 bash
ping 172.17.0.3

docker exec -it app2 bash
ping 172.17.0.2

```

* you can create custom network add your containers to that network and so that they can communicate as well just like
  you started the project.

## Docker Compose

To start project, we have to create network and then run mysql and then build spring-boot app docker image and run it,
we have to run multiple commands for this, we can configure all this steps in a single file and run it start the
project, that is docker-compose file. docker compose creates a custom network by default, you can specify your custom
network name in docker compose file.

docker-compose.yml

``` 
version: "3.8"

services:
  mysql_db:
    image: mysql:latest
    container_name: mysql_container
    networks:
      - spring_boot_mysql_network
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: test
      MYSQL_USER: test_user
      MYSQL_PASSWORD: test_password
    volumes:
      - /volume_docker/mysql:/var/lib/mysql

  spring_app:
    image: spring-boot-docker:1.0
    build:          # builds image if not present with above image name
      context: .
    container_name: spring_boot_app
    networks:
      - spring_boot_mysql_network
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql_db:3306/test
      MYSQL_USERNAME: test_user
      MYSQL_PASSWORD: test_password
    depends_on:
      - mysql_db

networks:
  spring_boot_mysql_network:
    driver: bridge
    name: spring_boot_mysql_network        # directory name will be prefixed for docker network name, so to avoid provided name key  will be
```