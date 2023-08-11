# start-server.sh
./gradlew clean bootJar &&  # 1. build jar
docker compose -f docker-compose.yml down &&  # 2. 기존 컨테이너 종료 및 삭제
docker compose -f docker-compose.yml build --no-cache &&  # 3. 새로운 이미지 빌드
docker compose -f docker-compose.yml up -d  # 4. 컨테이너 실행