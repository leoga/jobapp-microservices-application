#!/bin/bash

cd ../..

# Build all services
cd configserver && ./mvnw clean package -DskipTests && cd ..
cd eureka && ./mvnw clean package -DskipTests && cd ..
cd gateway && ./mvnw clean package -DskipTests && cd ..
cd company && ./mvnw clean package -DskipTests && cd ..
cd job && ./mvnw clean package -DskipTests && cd ..
cd review && ./mvnw clean package -DskipTests && cd ..