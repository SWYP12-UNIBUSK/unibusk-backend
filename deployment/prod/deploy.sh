#!/bin/bash
set -Eeuo pipefail

BASE_DIR="$HOME/deployment/prod"
NGINX_DIR="$BASE_DIR/nginx"
COMPOSE="$BASE_DIR/docker/docker-compose.yml"

MAX_RETRY=15
INTERVAL=3
APP_ENV_FILE="/opt/unibusk/env/prod.env"

log() {
  echo "[$(date +"%T")] $1"
}

rollback() {
  log "ROLLBACK triggered"

  sudo cp "$NGINX_DIR/unibusk-$CURRENT.conf" /etc/nginx/conf.d/default.conf
  sudo nginx -t
  sudo nginx -s reload

  docker compose -f "$COMPOSE" up -d $CURRENT

  exit 1
}

trap rollback ERR

cd "$BASE_DIR"

if [ ! -f "$APP_ENV_FILE" ]; then
  echo "❌ $APP_ENV_FILE not found"
  exit 1
fi

if docker ps --filter "name=unibusk-blue" --filter "status=running" | grep unibusk-blue >/dev/null; then
  CURRENT="blue"
  NEXT="green"
  PORT=8082
else
  CURRENT="green"
  NEXT="blue"
  PORT=8081
fi

log "Deploy: $CURRENT → $NEXT"

export IMAGE_TAG=${IMAGE_TAG:-latest}

docker compose -f "$COMPOSE" pull $NEXT
docker compose -f "$COMPOSE" up -d $NEXT

sleep 10

COUNT=0
until curl -sf http://127.0.0.1:$PORT/actuator/health >/dev/null; do
  COUNT=$((COUNT+1))
  [ $COUNT -ge $MAX_RETRY ] && exit 1
  sleep $INTERVAL
done

log "$NEXT healthy"

sudo cp "$NGINX_DIR/unibusk-$NEXT.conf" /etc/nginx/conf.d/default.conf
sudo nginx -t
sudo nginx -s reload

docker compose -f "$COMPOSE" stop $CURRENT || true
docker compose -f "$COMPOSE" rm -f $CURRENT || true

log "Deploy success: $NEXT live"
