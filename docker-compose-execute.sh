# queue 실행
docker compose up -d

# kafka 토픽 목록
kafka-topics --bootstrap-server localhost:9092 --list

# 토픽내 메시지 전체 삭제
kafka-topics --bootstrap-server localhost:9092 --delete --topic {{topic_name}}

# 메시지 오프셋 확인
kafka-run-class kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic {{topic_name}} --time -1
