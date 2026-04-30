#!/bin/bash

cd ../..

# Build all services
cd configserver && ./mvnw spring-boot:build-image -DskipTests && cd ..
cd eureka && ./mvnw spring-boot:build-image -DskipTests && cd ..
cd gateway && ./mvnw spring-boot:build-image -DskipTests && cd ..
cd company && ./mvnw spring-boot:build-image -DskipTests && cd ..
cd job && ./mvnw spring-boot:build-image -DskipTests && cd ..
cd review && ./mvnw spring-boot:build-image -DskipTests && cd ..