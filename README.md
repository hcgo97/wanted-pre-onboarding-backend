# 원티드 프리온보딩 백엔드 인턴쉽 선발과제

## 1. 지원자 성명
- 김효정(BE)
- gywjd5251@gmail.com
  <br></br>

## 2. 서버 구성
## 2-1. 개발 환경
- **OS**: Alpine Linux 3.16
- **Language**: openjdk 17
- **Framework**: SpringBoot 3.1.2
- **build & lib management**: Gradle 8.1.2
- **DB**: MySQL 8.0.25
- **ORM**: Spring Boot JPA
- **Etc**: lombok, jjwt, flyway

## 2-2. Endpoint
- 로컬: http://127.0.0.1:8080
- 상용: https://api-wanted-internship.hyoj.me
  <br></br>

## 3. 애플리케이션 실행 방법
- **실행 전 `openjdk-17`, `docker`, `docker-compose` 가 설치되어 있어야 합니다.**

## 3-1. 프로젝트 루트 경로에 `.env` 생성
```env
# 활성화 할 프로파일
SPRING_PROFILE: local

# DB 유저 정보
DB_PASSWORD: yourpassword1234

# JWT 설정
JWT_SECRET: yoursecretkey1234
JWT_EXPIRE: 600
```

## 3-2. 프로젝트 루트 경로에서 build & run 진행
### 1. start-server.sh 사용하여 build, run 한꺼번에 실행하기
```bash
./start-server.sh
```
### 2. start-server.sh 사용하지 않고 실행하기
1. build jar
```bash
./gradlew clean bootJar
```
2. docker compose up
```bash
docker compose -f docker-compose.yml up -d
```

## 3-3. Endpoint 호출 방법
### Health Check URL 을 통해 서버가 정상적으로 실행되었는지 확인
- 로컬
```bash
curl -X GET http://127.0.0.1:8080/actuator/health
```
- 상용
```bash
curl -X GET https://api-wanted-internship.hyoj.me/actuator/health
```
<br></br>

## 4. [데이터베이스 테이블 구조](https://dbdiagram.io/d/64d521bd02bd1c4a5e95db88)
![wanted-internship-db](https://github.com/hcgo97/wanted-pre-onboarding-backend/assets/72455719/6be3c46d-5f68-4564-9520-f0d05ed3fd52)
```sql
# 유저 정보 테이블
CREATE TABLE users
(
    id         bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email      varchar(50) NOT NULL UNIQUE COMMENT '이메일(로그인 ID)',
    password   varchar(50) NOT NULL COMMENT '비밀번호(bcrypt)',
    created_at datetime COMMENT '생성일자',
    updated_at datetime COMMENT '수정일자',
    is_deleted tinyint(1) DEFAULT false COMMENT '삭제여부 true/false',
    deleted_at datetime   DEFAULT NULL COMMENT '삭제일자'
) COMMENT = '유저 정보';

# 게시글 정보 테이블
CREATE TABLE posts
(
    id         bigint      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id    bigint      NOT NULL COMMENT 'users PK',
    title      varchar(20) NOT NULL COMMENT '게시글 제목',
    contents   text        NOT NULL COMMENT '게시글 내용',
    created_at datetime COMMENT '생성일자',
    updated_at datetime COMMENT '수정일자',
    is_deleted tinyint(1) DEFAULT false COMMENT '삭제여부 true/false',
    deleted_at datetime   DEFAULT NULL COMMENT '삭제일자',
    FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT = '게시글 정보';
```
<br></br>

## 5. API 동작 데모 영상
- [YouTube Link](https://youtu.be/YpvDwI46trw)
- [Google Drive Link](https://drive.google.com/file/d/1FUrDcgyWQBQZnJqR9z6flGi67YVCM_nv/view?usp=drive_link)
<br></br>

## 6. 구현 방법
## 6-1. 프로젝트 아키텍처
### 1. 통일된 API 응답 구조 활용
- 각 API 마다 일관된 응답을 제공하기 위해 기본 응답 구조를 정의하였습니다.
- 각 요청마다 트랜잭션 ID를 할당하여 추후 로그 추적을 용이하게 하였습니다.
### 2. DB 마이그레이션을 위한 flyway 라이브러리 사용
- Spring Boot 환경에서 유연한 DB 변경 이력 관리를 위해 flyway 라이브러리를 도입하였습니다.
- `src/main/resources/db/migration/V1__init_db.sql` 파일을 사용하여 테이블을 자동으로 마이그레이션함으로써 개발자에게 편리하도록 하였습니다.
### 3. API 응답 에러 시 에러코드 포함
- 각 에러에 고유한 에러코드를 부여하여 디버깅을 용이하게 하였습니다.
### 4. Health Check URL 구현
- Spring Actuator를 활용하여 `/actuator/health` 엔드포인트를 통해 서버 Health Check 기능을 구현하였습니다.
- 프로파일에 따라 Actuator 설정을 다르게 하여 외부에 노출할 URL과 제한할 URL을 구분하였습니다.

## 6-2. 사용자(Users)
### 1. JWT를 활용한 사용자 인증
- 세션 없이 간편하게 사용자 인증을 수행하기 위해 JWT 를 활용하였습니다.
- JWT 만료 시간을 10분으로 짧게 설정하여 토큰 탈취 시의 위험성을 최소화하였습니다.
- Spring Boot 필터 단에서 JWT 검증을 수행하여 Request 최상단에서 사용자 인증이 되도록 하였습니다.
- 민감한 정보인 `password`는 JWT claims에 포함시키지 않고, 사용자 구분을 위한 정보만을 담았습니다(`id`, `email`).
### 2. Bcrypt 알고리즘을 사용한 사용자 비밀번호 암호화
- 비밀번호는 복호화되서는 안되는 민감한 정보이므로 단방향 해시 알고리즘인 Bcrypt를 활용하여 암호화하고, 암호화된 비밀번호를 DB에 저장하도록 하였습니다.
- 로그인 시 입력된 `password` 파라미터를 Bcrypt 암호화하여 DB에 저장된 `password`와 대조합니다.
### 3. 파라미터 유효성 검사
- Spring Boot Validation 을 활용하여 파라미터의 유효성을 검사합니다.
- 유효성 검사 실패 시 Exception Handler 에서 `MethodArgumentNotValidException` 을 catch 하여 적절한 에러 메시지를 응답에 포함시킵니다.
### 4. 회원가입 시 중복 이메일 검사 기능 추가
- 회원가입 시 이미 가입된 이메일을 입력할 경우, '`E4090, 이미 가입된 이메일입니다.`' 응답을 반환합니다.

## 6-3. 게시글(Posts)
### 1. `Users`와 `Posts` Entity 간의 One-to-Many 관계 설정
- 게시글은 한 사용자에 의해 작성되며, 사용자는 여러 개의 게시글을 작성할 수 있기에 `Users`와 `Posts` 사이에 다대일(One-to-Many) 관계를 설정하였습니다.
- Lazy 로딩을 활용하여 `Posts` 테이블 조회 시 `Users` 테이블 정보는 필요 시에만 가져오도록 구성하였습니다.
### 2. 사용자 로그인 상태 확인
- Request Header의 액세스 토큰 유무를 검사하고, 액세스 토큰이 없는 경우 '`E4010, 로그인 정보를 찾을 수 없습니다.`' 응답을 반환합니다.
### 3. 게시글 목록 조회 시 Pagination 기능 적용
- Pagination 기능을 적용하여 요청 당 출력되는 데이터 양을 관리하고 적절한 개수의 데이터만을 조회할 수 있도록 하였습니다.
### 4. 게시글 삭제 시 Soft Delete 처리
- DB 상의 데이터를 영구적으로 삭제하는 Hard Delete 대신 Soft Delete 를 적용하여 추후 데이터 복구가 가능하도록 설계하였습니다.
- 게시글 삭제 시 `Posts` 테이블의 `is_deleted` 컬럼을 `true`로 변경하고, `deleted_at` 컬럼을 삭제 시각으로 업데이트하여 삭제 여부를 구분할 수 있도록 하였습니다.
### 5. 게시글 수정 및 삭제 시 권한 확인
- 액세스 토큰에 포함된 사용자 정보를 활용하여 작업하려는 게시글이 현재 사용자의 것인지 확인합니다.
- 현재 사용자의 게시글이 아닌 경우, '`E4031, 해당 게시물에 대한 권한이 없습니다.`' 응답을 반환합니다.
### 6. 게시글 상세 조회, 수정, 삭제 시 해당 게시글의 존재 여부 확인
- Path Variables에 포함된 `postId` 가 존재하지 않는 게시글 id 이거나 이미 삭제된 게시글 id 인 경우, '`E4042, 존재하지 않는 게시글입니다.`' 응답을 반환합니다.
### 7. 게시글 삭제 API 성공 시의 HTTP 상태 코드를 `204` 가 아닌 `200` 으로 처리
- `204 NO_CONTENT` 상태 코드는 응답 본문이 없어 출력되지 않기 때문에, 모든 API의 일관성 있는 응답 구조를 위해 `200 OK`로 처리하였습니다.
  <br></br>

## 7. API 명세
- [Postman Collection 다운로드](https://drive.google.com/file/d/1CvQqLTsTyZfUx_cS1Loe7IF9dOIuITK2/view?usp=drive_link)
## 7-1. 회원가입 API
### URL
- `/api/v1/users/join`

### Method
- `POST`

### Request Header
- `Content-Type`: `application/json`

### Request Body
- `email`: 이메일, string
- `password`: 패스워드, string

### Response Body
- `txid`: 요청 트랜잭션 id, string
- `status`: http 상태 코드, integer
- `message`: 응답 메시지, string
- `data`: 응답 데이터 객체, object
    - `id`: 생성된 유저 PK, long
    - `email`: 생성된 유저 이메일, string

### Example
- **Request Body**
```json
{
    "email": "test@abc.com",
    "password": "test1234"
}
```
- **Response Body**
```json
{
    "txid": "558ca027-1248-4a63-935f-b3523153812a",
    "status": 201,
    "message": "정상적으로 처리되었습니다.",
    "data": {
        "id": 1,
        "email": "test@abc.com"
    }
}
```
- **curl**
```bash
curl --location 'https://api-wanted-internship.hyoj.me/api/v1/users/join' \
--header 'Content-Type: application/json' \
--data '{
    "email": "test@abc.com",
    "password": "test1234"
}'
```
<br></br>

## 7-2. 로그인 API
### URL
- `/api/v1/users/login`

### Method
- `POST`

### Request Header
- `Content-Type`: `application/json`

### Request Body
- `email`: 이메일, string
- `password`: 패스워드, string

### Response Body
- `txid`: 요청 트랜잭션 id, string
- `status`: http 상태 코드, integer
- `message`: 응답 메시지, string
- `data`: 응답 데이터 객체, object
    - `accessToken`: 유저 액세스 토큰, string
    - `userInfo`: 유저 정보 객체, object
        - `id`: 생성된 유저 PK, long
        - `email`: 생성된 유저 이메일, string

### Example
- **Request Body**
```json
{
    "email": "test@abc.com",
    "password": "test1234"
}
```
- **Response Body**
```json
{
    "txid": "46fac5d6-7279-4bf2-9140-8d35dc160e24",
    "status": 200,
    "message": "정상적으로 처리되었습니다.",
    "data": {
        "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6MSwiZW1haWwiOiJ0ZXN0QGFiYy5jb20iLCJpYXQiOjE2OTE5Mjg5MDQsImV4cCI6MTY5MTkyOTUwNH0.gKkxmJkx_ExqzsYYhrCEm7be36W9ZeDqWPIoKTrGVwJTBcHZ60KoshGe5HaY6InuuMivUMPg5KkbdjVGeUTruw",
        "userInfo": {
            "id": 1,
            "email": "test@abc.com"
        }
    }
}
```
- **curl**
```bash
curl --location 'https://api-wanted-internship.hyoj.me/api/v1/users/login' \
--header 'Content-Type: application/json' \
--data '{
    "email": "test@abc.com",
    "password": "test1234"
}'
```
<br></br>

## 7-3. 게시글 작성 API
### URL
- `/api/v1/posts`

### Method
- `POST`

### Request Header
- `Content-Type`: `application/json`
- `Authorization`: Bearer Token, string

### Request Body
- `title`: 게시글 제목, string
- `contents`: 게시글 내용, string

### Response Body
- `txid`: 요청 트랜잭션 id, string
- `status`: http 상태 코드, integer
- `message`: 응답 메시지, string
- `data`: 응답 데이터 객체, object
    - `id`: 생성된 게시글 PK, long
    - `author`: 게시글 작성자, string
    - `createdAt`: 생성 시각(yyyy-MM-dd HH-mm-ss), string
    - `updatedAt`: 수정 시각(yyyy-MM-dd HH-mm-ss), string
    - `isUpdated`: 수정 여부, boolean
    - `title`: 생성된 게시글 제목, string
    - `contents`: 생성된 게시글 내용, string

### Example
- **Request Body**
```json
{
    "title": "test title",
    "contents": "test contents~~~"
}
```
- **Response Body**
```json
{
    "txid": "bd8ba5a6-bb13-4329-9edf-f45a15da1554",
    "status": 201,
    "message": "정상적으로 처리되었습니다.",
    "data": {
        "id": 72,
        "author": "test@abc.com",
        "createdAt": "2023-08-14 17:22:48",
        "updatedAt": "2023-08-14 17:22:48",
        "isUpdated": false,
        "title": "test title",
        "contents": "test contents~~~"
    }
}
```
- **curl**
```bash
curl --location 'https://api-wanted-internship.hyoj.me/api/v1/posts' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer xxxx' \
--data '{
    "title": "test title",
    "contents": "test contents~~~"
}'
```
<br></br>

## 7-4. 게시글 목록 조회 API
### URL
- `/api/v1/posts`

### Method
- `GET`

### Request Header
- `Content-Type`: `application/json`
- `Authorization`: Bearer Token, string

### Request Parameters
- `page`: 페이지 번호 (1부터 시작), integer
- `size`: 페이지 당 표시할 게시글 수, integer

### Response Body
- `txid`: 요청 트랜잭션 id, string
- `status`: http 상태 코드, integer
- `message`: 응답 메시지, string
- `data`: 응답 데이터 객체, object
    - `content`: 조회된 게시글 목록, array
        - `id`: 게시글 PK, long
        - `author`: 게시글 작성자, string
        - `createdAt`: 생성 시각(yyyy-MM-dd HH-mm-ss), string
        - `isUpdated`: 수정 여부, boolean
        - `title`: 게시글 제목, string
    - `pageable`: 페이징 객체, object

### Example
- **Response Body**
```json
{
    "txid": "dbf43fd4-1b36-4f2c-ac5a-e1f19546d318",
    "status": 200,
    "message": "정상적으로 처리되었습니다.",
    "data": {
        "content": [
            {
                "id": 72,
                "author": "test@abc.com",
                "createdAt": "2023-08-14 17:22:48",
                "isUpdated": false,
                "title": "test title"
            }
        ],
        "pageable": {
            "sort": {
                "empty": true,
                "sorted": false,
                "unsorted": true
            },
            "offset": 0,
            "pageNumber": 0,
            "pageSize": 1,
            "paged": true,
            "unpaged": false
        },
        "last": false,
        "totalPages": 10,
        "totalElements": 10,
        "size": 1,
        "number": 0,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "first": true,
        "numberOfElements": 1,
        "empty": false
    }
}
```
- **curl**
```bash
curl --location 'https://api-wanted-internship.hyoj.me/api/v1/posts?page=1&size=1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer xxxx'
```
<br></br>

## 7-5. 게시글 상세 조회 API
### URL
- `/api/v1/posts/{postId}`

### Method
- `GET`

### Path Variables
- `postId`: 조회할 게시글 PK, long

### Request Header
- `Content-Type`: `application/json`
- `Authorization`: Bearer Token, string

### Response Body
- `txid`: 요청 트랜잭션 id, string
- `status`: http 상태 코드, integer
- `message`: 응답 메시지, string
- `data`: 응답 데이터 객체, object
    - `id`: 게시글 PK, long
    - `author`: 작성자, string
    - `createdAt`: 생성 시각(yyyy-MM-dd HH-mm-ss), string
    - `updatedAt`: 수정 시각(yyyy-MM-dd HH-mm-ss), string
    - `isUpdated`: 수정 여부, boolean
    - `title`: 게시글 제목, string
    - `contents`: 게시글 내용, string

### Example
- **Response Body**
```json
{
    "txid": "269c279e-1822-4733-ba68-5e26c2150ba9",
    "status": 200,
    "message": "정상적으로 처리되었습니다.",
    "data": {
        "id": 72,
        "author": "test@abc.com",
        "createdAt": "2023-08-14 17:22:48",
        "updatedAt": "2023-08-14 17:22:48",
        "isUpdated": false,
        "title": "test title",
        "contents": "test contents~~~"
    }
}
```
- **curl**
```bash
curl --location 'https://api-wanted-internship.hyoj.me/api/v1/posts/72' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer xxxx'
```
<br></br>

## 7-6. 게시글 수정 API
### URL
- `/api/v1/posts/{postId}`

### Method
- `PUT`

### Path Variables
- `postId`: 수정할 게시글 PK, long

### Request Header
- `Content-Type`: `application/json`
- `Authorization`: Bearer Token, string

### Request Body
- `title`: 게시글 제목, string
- `contents`: 게시글 내용, string

### Response Body
- `txid`: 요청 트랜잭션 id, string
- `status`: http 상태 코드, integer
- `message`: 응답 메시지, string
- `data`: 응답 데이터 객체, object
    - `id`: 수정된 게시글 PK, long
    - `author`: 작성자, string
    - `createdAt`: 생성 시각(yyyy-MM-dd HH-mm-ss), string
    - `updatedAt`: 수정 시각(yyyy-MM-dd HH-mm-ss), string
    - `isUpdated`: 수정 여부, boolean
    - `title`: 수정된 게시글 제목, string
    - `contents`: 수정된 게시글 내용, string

### Example
- **Request Body**
```json
{
    "title": "edited title",
    "contents": "edited contents~~~"
}
```
- **Response Body**
```json
{
    "txid": "bd8ba5a6-bb13-4329-9edf-f45a15da1554",
    "status": 201,
    "message": "정상적으로 처리되었습니다.",
    "data": {
        "id": 72,
        "author": "test@abc.com",
        "createdAt": "2023-08-14 17:22:48",
        "updatedAt": "2023-08-15 02:28:51",
        "isUpdated": true,
        "title": "edited title",
        "contents": "edited contents~~~"
    }
}
```
- **curl**
```bash
curl --location --request PUT 'https://api-wanted-internship.hyoj.me/api/v1/posts/72' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer xxxx' \
--data '{
    "title": "edited title",
    "contents": "edited contents~~~"
}'
```
<br></br>

## 7-7. 게시글 삭제 API
### URL
- `/api/v1/posts/{postId}`

### Method
- `DELETE`

### Path Variables
- `postId`: 삭제할 게시글 PK, long

### Request Header
- `Content-Type`: `application/json`
- `Authorization`: Bearer Token, string

### Response Body
- `txid`: 요청 트랜잭션 id, string
- `status`: http 상태 코드, integer
- `message`: 응답 메시지, string
- `data`: 응답 데이터 객체, object
    - `id`: 삭제된 게시글 PK, long
    - `author`: 작성자, string
    - `createdAt`: 생성 시각(yyyy-MM-dd HH-mm-ss), string
    - `updatedAt`: 수정 시각(yyyy-MM-dd HH-mm-ss), string
    - `isUpdated`: 수정 여부, boolean
    - `title`: 삭제된 게시글 제목, string
    - `contents`: 삭제된 게시글 내용, string

### Example
- **Response Body**
```json
{
    "txid": "bd8ba5a6-bb13-4329-9edf-f45a15da1554",
    "status": 201,
    "message": "정상적으로 처리되었습니다.",
    "data": {
        "id": 72,
        "author": "test@abc.com",
        "createdAt": "2023-08-14 17:22:48",
        "updatedAt": "2023-08-15 02:28:51",
        "isUpdated": true,
        "title": "edited title",
        "contents": "edited contents~~~"
    }
}
```
- **curl**
```bash
curl --location --request DELETE 'https://api-wanted-internship.hyoj.me/api/v1/posts/72' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer xxxx'
```
<br></br>

## 7-8. 에러 정의
|에러 코드|에러 이름|상태 코드|에러 설명|
|----|----|----|----|
|E4000|CLIENT_ERROR|400|잘못된 요청 파라미터|
|**E4010**|**UNAUTHORIZED**|**401**|**로그인 정보가 없음**|
|E4011|TOKEN_NOT_FOUND|401|토큰을 찾을 수 없음|
|E4012|TOKEN_INVALID|401|유효하지 않은 토큰|
|E4013|TOKEN_DATE_EXPIRED|401|토큰이 만료됨|
|E4030|FORBIDDEN|403|권한 없음|
|**E4031**|**NOT_MY_POST**|**403**|**해당 게시글에 대한 작업 권한 없음**|
|E4040|NOT_FOUND|404|존재하지 않는 데이터 또는 경로|
|**E4041**|**USER_NOT_FOUND**|**404**|**존재하지 않는 유저**|
|**E4042**|**POST_NOT_FOUND**|**404**|**존재하지 않는 게시글**|
|**E4090**|**ALREADY_SIGNED_UP**|**409**|**이미 가입된 이메일**|
|**E4220**|**INVALID_FORMAT**|**422**|**파라미터 유효성 검사 실패**|
|E9000|INTERNAL_SERVER_ERROR|500|서버 에러|
|E9100|DATABASE_ACCESS_ERROR|500|DB 에러|
|E9200|TOKEN_CREATED_FAILED|500|토큰 생성 중 에러|

### Response Body
- `txid`: 요청 트랜잭션 id, string
- `status`: http 상태 코드, integer
- `message`: 응답 메시지, string
- `error`: 에러 데이터 객체, object
  - `code`: 에러 코드, string
  - `description`: 에러 이름(설명), string

### Example
- **Response Body**
```json
{
    "txid": "054871bd-32a1-4b4a-916e-fac786077d8a",
    "status": 401,
    "message": "로그인 정보가 일치하지 않습니다.",
    "error": {
        "code": "E4014",
        "description": "LOGIN_FAILED"
    }
}
```
<br></br>

## 8. 클라우드 환경 구조
### Endpoint: https://api-wanted-internship.hyoj.me
![김효정-AWS구조](https://github.com/hcgo97/wanted-pre-onboarding-backend/assets/72455719/8c9aabcc-714f-4f56-8b70-957c8e602303)
- EC2 인스턴스 상에서 Docker Compose를 활용하여 API 서버와 MySQL 컨테이너를 실행시켰습니다.
- EC2 인스턴스가 종료 후 재시작하여도 서버 IP가 변경되지 않도록 하기 위해 Elastic IP를 활용하여 서버 IP를 고정하였습니다.
- Cloudflare DNS 를 사용하여 개인 도메인 `hyoj.me` 에 EC2 인스턴스 주소를 연결하였습니다.
- Certbot 을 활용하여 SSL 인증서를 발급받고, 이를 Nginx 에 적용하여 https 요청이 가능하도록 하였습니다.
- 보안을 강화하기 위해 EC2 인스턴스의 인바운드 포트는 `80` 과 `443` 만 허용하였으며, Nginx 를 사용하여 API 서버 포트 `8080` 에 연결되도록 하였습니다.
- 만약 `80` 포트로 들어오는 http 요청이 발생하면, 이를 `443` 포트의 https 요청으로 리다이렉트시켜 서버 접속이 https 프로토콜로만 이루어지도록 설정하였습니다.
