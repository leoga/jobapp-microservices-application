#!/bin/bash

cd ../..

# Build all services
cd configserver && ./mvnw clean compile jib:build && cd ..
cd eureka && ./mvnw clean compile jib:build && cd ..
cd gateway && ./mvnw clean compile jib:build && cd ..
cd company && ./mvnw clean compile jib:build && cd ..
cd job && ./mvnw clean compile jib:build && cd ..
cd review && ./mvnw clean compile jib:build && cd ..