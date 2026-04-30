#!/bin/bash

cd ../..

# Build all services
cd configserver && ./mvnw clean compile jib:dockerBuild && cd ..
cd eureka && ./mvnw clean compile jib:dockerBuild && cd ..
cd gateway && ./mvnw clean compile jib:dockerBuild && cd ..
cd company && ./mvnw clean compile jib:dockerBuild && cd ..
cd job && ./mvnw clean compile jib:dockerBuild && cd ..
cd review && ./mvnw clean compile jib:dockerBuild && cd ..