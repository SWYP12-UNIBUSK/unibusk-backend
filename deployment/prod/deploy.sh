#!/bin/bash
set -Eeuo pipefail

BASE_DIR="$HOME/deployment/prod"
NGINX_DIR="$BASE_DIR/nginx"
COMPOSE="$BASE_DIR/docker/docker-compose.yml"

MAX_RETRY=30
INTERVAL=5
APP_ENV_FILE="$BASE_DIR/.env"

log() { echo "[$(date +"%T")] $1"; }

rollback() {
  log "ROLLBACK triggered"

  sudo cp "$NGINX_DIR/unibusk-$CURRENT.conf" /etc/nginx/conf.d/default.conf
  sudo nginx -t
  sudo nginx -s reload

  docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" up -d $CURRENT

  # 기존 NEXT 컨테이너 제거
  docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" rm -f $NEXT || true

  exit 1
}

trap rollback ERR

cd "$BASE_DIR"

[ ! -f "$APP_ENV_FILE" ] && echo "❌ $APP_ENV_FILE not found" && exit 1

set -a; source "$APP_ENV_FILE"; set +a

CURRENT="green"
NEXT="blue"
PORT=8081

if docker ps --filter "name=unibusk-blue" --filter "status=running" | grep unibusk-blue >/dev/null; then
  CURRENT="blue"
  NEXT="green"
  PORT=8082
fi

log "Deploy: $CURRENT → $NEXT"

export IMAGE_TAG=${IMAGE_TAG:-latest}

# 배포 전 기존 NEXT 컨테이너 삭제
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" rm -f $NEXT || true

# 최신 이미지 pull 및 컨테이너 실행
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" pull $NEXT
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" up -d $NEXT

# 헬스체크
COUNT=0
until curl -sf http://127.0.0.1:$PORT/api/actuator/health >/dev/null; do
  COUNT=$((COUNT+1))
  [ $COUNT -ge $MAX_RETRY ] && { log "$NEXT unhealthy"; exit 1; }
  log "Waiting for $NEXT to be healthy... ($COUNT/$MAX_RETRY)"
  sleep $INTERVAL
done

log "$NEXT healthy"

# Nginx 교체
sudo cp "$NGINX_DIR/unibusk-$NEXT.conf" /etc/nginx/conf.d/default.conf
sudo nginx -t
sudo nginx -s reload

# 기존 컨테이너 정리
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" stop $CURRENT
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" rm -f $CURRENT

log "Deploy success: $NEXT live"

