#!/bin/bash

docker-compose -f docker-compose.yml -f docker-compose.services.yml up
# docker run -it -p 15672:15672 -p 5672:5672 rabbitmq:3.7.8-management


