@startuml
participant User
participant Client
participant Server
participant "Cache Store" as Cache
participant "Database" as DB
participant "Mail Server" as Mail

User -> Client: email, username, password 입력
Client -> Client: Public Key로 암호화
Client -> Server: [API : POST /signup] 회원가입 요청 (암호화된 데이터)

Server -> Server: Private Key로 복호화
Server -> Server: 유효성 검증 (이메일, 비밀번호, 이름)
alt 유효성 오류
Server -> Client: 400 Bad Request 응답
else 유효성 통과
Server -> DB: 이메일 중복 검사
DB -> Server: 결과 반환
alt 중복된 이메일 존재
Server -> Client: 409 Conflict 응답
else 이메일 중복 없음
Server -> Cache: (email, verification code, session id) 5분 저장
Server -> Mail: 인증 코드 이메일 발송
Server -> Client: session id를 쿠키에 담아 응답
end
end

User -> Client: 이메일 확인 후 인증 코드 입력
Client -> Server: [API : POST /signup] verification code 제출 (쿠키에 session id 포함)
Server -> Cache: 세션 검증 (session id, email, verification code)
alt 세션 일치하지 않음
Server -> Client: 400 Bad Request 응답
else 세션 만료됨
Server -> Client: 408 Request Timeout 응답
else 인증 성공
Server -> Server: 비밀번호 해시화
Server -> DB: 유저 저장
DB -> Server: 저장 완료
Server -> Cache: 캐시 무효화
Server -> Client: email, 생성일시 응답
end
@enduml
