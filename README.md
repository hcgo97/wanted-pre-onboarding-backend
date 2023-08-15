# 원티드 프리온보딩 백엔드 인턴쉽 선발과제

## 1. 지원자 성명
- 김효정(BE)
- gywjd5251@gmail.com
<br></br>

## 2. 서버 구성
**2-1. 개발 환경**
- **OS**: Alpine Linux 3.16
- **Language**: openjdk 17
- **Framework**: SpringBoot 3.1.2
- **build & lib management**: Gradle 8.1.2
- **DB**: MySQL 8.0.25
- **ORM**: Spring Boot JPA
- **Etc**: lombok, jjwt, flyway

**2-2. Endpoint**
- 로컬: http://127.0.0.1:8080
- 상용: https://api-wanted-internship.hyoj.me
<br></br>

## 3. 애플리케이션 실행 방법
- 실행 전 `openjdk-17`, `docker`, `docker-compose` 패키지가 설치되어 있어야 함

**3-1. 프로젝트 루트 경로에 `.env` 생성**
```env
SPRING_PROFILE: '프로파일(local/dev/prod)'

DB_USERNAME: 'Database 유저명'
DB_PASSWORD: 'Database 비밀번호'

JWT_SECRET: 'JWT Secret Key'
JWT_EXPIRE: 'JWT 만료 시간(초 단위)'
```

**3-2. 프로젝트 루트 경로의 `start-server.sh` 쉘 스크립트 실행**
```bash
# build jar, docker compose up 진행
./start-server.sh
```

**3-3. Endpoint 호출 방법**
- health check API 를 통해 서버가 정상적으로 실행되었는지 확인
```bash
# 로컬
curl -X GET http://localhost:8080/actuator/health
```
```bash
# 상용
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
    deleted_at datetime   DEFAULT NULL COMMENT '삭제일자'
) COMMENT = '게시글 정보';
```
<br></br>

## 5. API 동작 데모 영상
- https://drive.google.com/file/d/1FUrDcgyWQBQZnJqR9z6flGi67YVCM_nv/view?usp=drive_link
<br></br>

## 6. 구현 방법
## 6-1. 프로젝트 구조
### 1. API 응답 기본구조 사용
- 기본 응답 구조를 정해서 각각의 API 마다 일관성 있는 응답을 내려주도록 하였음
- 요청, 응답마다 트랜잭션 ID 를 발급하여 추후 로그 추적 시 용이하도록 하였음
### 2. flyway 라이브러리를 사용한 DB 마이그레이션
- Spring Boot 환경에서 수월하게 DB 변경 이력 관리하기 위해 사용함
- 테이블을 직접 만들지 않고 `src/main/resources/db/migration/V1__init_db.sql` 파일을 사용해 테이블이 자동으로 마이그레이션되므로 사용자에게 매우 편리
### 3. API 응답 에러 시 에러코드 출력
- 각각의 에러마다 에러코드를 따로 부여하여 디버깅 시 용이하도록 함
### 4. Health Check URL
- Spring Actuator 사용하여 health check 기능 구현 (`/actuator/health`)
- Profile 별로 Actuator 설정을 다르게 하여, 외부에 노출할 URL 과 노출하면 안되는 URL 을 구분하였음

## 6-2. 사용자(Users)
### 1. 사용자 인증에 JWT 사용
- 액세스 토큰만으로 세션없이 간단하게 사용자 인증 가능
- 간단한 만큼 토큰이 탈취되면 누구나 해당 사용자 정보로 인증 가능하기 때문에 JWT 만료 시간을 10분으로 짧게 설정하여 만일 탈취되었을 경우의 위험성을 줄임
- Spring Boot 필터단에서 JWT 를 검증하도록 하여 Request 최상단에서 사용자 인증 처리가 됨
- JWT claims 에는 민감한 정보(`password`)를 담지 않고 서버에서 사용자 구분을 위한 정보만을 담음(`id`, `email`)
### 2. 사용자 비밀번호 암호화에 Bcrypt 알고리즘 사용
- 비밀번호는 복호화되서는 안되는 민감한 정보이므로 단방향 해시 알고리즘인 Bcrypt 사용하여 암호화하여 DB 에 저장함
- 사용자 로그인 시 `password` 파라미터를 Bcrypt 암호화 후, DB에 암호화하여 저장된 `password` 와 대조함
### 3. 사용하여 파라미터 유효성 검사
- Spring Boot Validation 기능 사용하여 파라미터 유효성 검사 진행
- 유효성 검사 실패 시 Exception Handler 에서 `MethodArgumentNotValidException` 을 catch 하여 상황에 맞는 에러 메시지를 응답에 내려주도록 함
### 4. 회원가입 시 가입된 이메일인지 여부 검사
- `email` 파라미터에 이미 가입된 이메일을 입력하여 요청할 경우, 'E4090, 이미 가입된 이메일입니다.' 응답을 내려주도록 함

## 6-3. 게시글(Posts)
### 1. 사용자 로그인 여부 확인
- Request Header 의 엑세스 토큰 여부를 확인하고, 액세스 토큰이 존재하지 않다면 '`E4010, 로그인 정보를 찾을 수 없습니다`' 응답을 내려주도록 함
### 2. 게시글 목록 조회 시 Pagination 기능 적용
- Pagination 기능을 적용하여 한꺼번에 많은 데이터를 불러오지 않고, 적절한 개수의 데이터만을 불러오도록 하였음
### 3. 게시글 삭제 시 Soft Delete 처리
- DB 상에서 Hard Delete 가 아닌 Soft Delete 처리하여, 추후 삭제된 데이터를 복구해야하는 상황에서 대처 가능하도록 함
- 게시글 삭제 시 `Posts` 테이블의 `is_delete` 컬럼이 `true`, `delete_at` 컬럼은 `삭제된 시각`으로 변경되도록 하여 게시글의 삭제 여부를 구분할 수 있도록 함
### 4. 게시글 수정, 삭제 시 현재 사용자의 게시글인지 확인
- 액세스 토큰에 담긴 사용자 정보로 작업하려는 게시글이 현재 사용자의 게시글인지 확인함
- 현재 사용자의 게시글이 아닌 경우 '`E4031, 해당 게시물에 대한 권한이 없습니다.`' 응답을 내려주도록 함
### 5. 게시글 상세 조회, 수정, 삭제 시 존재하지 않는 게시글 여부 확인
- Path Variables 에 담긴 `postId` 가 존재하지 않는 게시글 id 이거나 삭제된 게시글 id 인 경우, '`E4042, 존재하지 않는 게시글입니다.`' 응답을 내려주도록 함
<br></br>

## 7. API 명세
- [Postman Collection 다운로드](https://drive.google.com/file/d/1CvQqLTsTyZfUx_cS1Loe7IF9dOIuITK2/view?usp=drive_link)
## 7-1. 회원가입 API
**URL**
  - `/api/v1/users/join`

**Method**
  - `POST`

**Request Body**
  - `email`: 이메일, string
  - `password`: 패스워드, string

**Response Body**
  - `txid`: 요청 트랜잭션 id, string
  - `status`: http 상태 코드, integer
  - `message`: 응답 메시지, string
  - `data`: 응답 데이터 객체, object
    - `id`: 생성된 유저 PK, long
    - `email`: 생성된 유저 이메일, string

**Example**
```json
// Request Body
{
    "email": "test@abc.com",
    "password": "test1234"
}

// Response Body
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
**curl**
```bash
curl --location 'https://api-wanted-internship.hyoj.me/api/v1/users/join' \
--data-raw '{
    "email": "test@abc.com",
    "password": "test1234"
}'
```
<br></br>

## 7-2. 로그인 API
**URL**
  - `/api/v1/users/login`

**Method**
  - `POST`

**Request Body**
  - `email`: 이메일, string
  - `password`: 패스워드, string

**Response Body**
  - `txid`: 요청 트랜잭션 id, string
  - `status`: http 상태 코드, integer
  - `message`: 응답 메시지, string
  - `data`: 응답 데이터 객체, object
    - `accessToken`: 유저 액세스 토큰, string
    - `userInfo`: 유저 정보 객체, object
      - `id`: 생성된 유저 PK, long
      - `email`: 생성된 유저 이메일, string

**Example**
```json
// Request Body
{
    "email": "test@abc.com",
    "password": "test1234"
}

// Response Body
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
**curl**
```bash
curl --location 'https://api-wanted-internship.hyoj.me/api/v1/users/login' \
--data-raw '{
    "email": "test@abc.com",
    "password": "test1234"
}'
```
<br></br>

## 7-3. 게시글 작성 API
**URL**
  - `/api/v1/posts`

**Method**
  - `POST`

**Request Header**
  - `Authorization`: Bearer Token, string

**Request Body**
  - `title`: 게시글 제목, string
  - `contents`: 게시글 내용, string

**Response Body**
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

**Example**
```json
// Request Body
{
    "title": "test title",
    "contents": "test contents~~~"
}

// Response Body
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
**curl**
```bash
curl --location 'https://api-wanted-internship.hyoj.me/api/v1/posts' \
--header 'Authorization: Bearer xxxx' \
--data '{
    "title": "test title",
    "contents": "test contents~~~"
}'
```
<br></br>

## 7-4. 게시글 목록 조회 API
**URL**
  - `/api/v1/posts`

**Method**
  - `GET`

**Request Header**
  - `Authorization`: Bearer Token, string

**Request Parameters**
  - `page`: 페이지 번호 (1부터 시작), integer
  - `size`: 페이지 당 표시할 게시글 수, integer

**Response Body**
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
   
**Example**
```json
// Response Body
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
**curl**
```bash
curl --location 'https://api-wanted-internship.hyoj.me/api/v1/posts?page=1&size=1' \
--header 'Authorization: Bearer xxxx'
```
<br></br>

## 7-5. 게시글 상세 조회 API
**URL**
  - `/api/v1/posts/{postId}`

**Method**
  - `GET`

**Path Variables**
  - `postId`: 조회할 게시글 PK, long

**Request Header**
  - `Authorization`: Bearer Token, string

**Response Body**
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
   
**Example**
```json
// Response Body
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
**curl**
```bash
curl --location 'https://api-wanted-internship.hyoj.me/api/v1/posts/72' \
--header 'Authorization: Bearer xxxx'
```
<br></br>

## 7-6. 게시글 수정 API
**URL**
  - `/api/v1/posts/{postId}`

**Method**
  - `PUT`

**Path Variables**
  - `postId`: 수정할 게시글 PK, long

**Request Header**
  - `Authorization`: Bearer Token, string

**Request Body**
  - `title`: 게시글 제목, string
  - `contents`: 게시글 내용, string

**Response Body**
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
   
**Example**
```json
// Request Body
{
    "title": "edited title",
    "contents": "edited contents~~~"
}

// Response Body
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
**curl**
```bash
curl --location --request PUT 'https://api-wanted-internship.hyoj.me/api/v1/posts/72' \
--header 'Authorization: Bearer xxxx' \
--data '{
    "title": "edited title",
    "contents": "edited contents~~~"
}'
```
<br></br>

## 7-7. 게시글 삭제 API
**URL**
  - `/api/v1/posts/{postId}`

**Method**
  - `DELETE`

**Path Variables**
  - `postId`: 삭제할 게시글 PK, long

**Request Header**
  - `Authorization`: Bearer Token, string

**Response Body**
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
   
**Example**
```json
// Response Body
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
**curl**
```bash
curl --location --request DELETE 'https://api-wanted-internship.hyoj.me/api/v1/posts/72' \
--header 'Authorization: Bearer xxxx'
```

<br></br>

## 8. 클라우드 환경 구조
- Endpoint: https://api-wanted-internship.hyoj.me
![김효정-AWS구조](https://github.com/hcgo97/wanted-pre-onboarding-backend/assets/72455719/f663c13e-6fda-4fce-8202-39bdc124e986)

