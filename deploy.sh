#!/bin/bash

if [ ! -f .env ]; then
  read -p "Enter MongoDB username: " MONGO_USER
  read -sp "Enter MongoDB password: " MONGO_PASS
  echo

  echo "MONGO_USER=$MONGO_USER" > .env
  echo "MONGO_PASS=$MONGO_PASS" >> .env

  echo ".env file created."
else
  echo ".env file already exists. Skipping creation."
fi

docker-compose up -d