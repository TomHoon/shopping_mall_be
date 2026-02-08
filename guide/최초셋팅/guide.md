# 도커 셋팅
1. 실행
```dbn-psql
cd docker
docker-compose up -d
```

2. 끌 경우. 볼륨 초기화 옵션 넣어서 내려야함
```dbn-psql
docker-compose down -v
```