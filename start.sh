#!/bin/bash
set -e

echo "Buildando e subindo containers..."
docker compose up -d --build

echo "Containers ativos:"
docker compose ps
