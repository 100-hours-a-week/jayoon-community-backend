# 목차

- [가이드](#가이드)
- [Users (회원)](#Users)
- [Posts (게시글)](#Posts)
- [Auth (인증)](#Auth)
- [Images (이미지)](#Images)

-----

# 가이드

### PUT과 PATCH의 차이

- **PUT**: 리소스 전체를 완전히 대체합니다. (모든 필드 수정)
- **PATCH**: 리소스의 일부 필드만 부분적으로 수정합니다.

-----

# Users

## 1\. 회원 가입

`POST` `/users`

### 요청 (Request)

#### Headers

없음

#### Path Variables

없음

#### Query Parameters

없음

#### Body

**Content-Type:** `application/json`

| 필드명               | 필수 |   타입   | 제약 조건    | 설명                               |
|:------------------|:--:|:------:|:---------|:---------------------------------|
| `email`           | O  | String | 320자 이내  | 소문자 영문, 숫자, `@`, `.`만 가능. 이메일 형식 |
| `password`        | O  | String | 8\~20자   | 대/소문자, 숫자, 특수문자 각 1개 이상 포함       |
| `nickname`        | O  | String | 2\~10자   | 띄어쓰기 불가능                         |
| `profileImageUrl` | X  | String | 2048자 이내 | 미입력 시 `null` 저장                  |

```json
{
  "email": "test@startupcode.kr",
  "password": "test1234",
  "nickname": "startup",
  "profileImageUrl": "https://image.kr/img.jpg"
}
```

### 응답 (Response)

#### 성공 (200 OK)

> **Note:** 사용자 경험을 위해 회원가입과 동시에 자동 로그인 처리됩니다.

**Headers**

- `Set-Cookie`: `ACCESS_TOKEN` (HttpOnly, Path=/)
- `Set-Cookie`: `REFRESH_TOKEN` (HttpOnly, Path=/api/auth/refresh)

**Body**

```json
{
  "success": true,
  "message": "회원가입이 성공적으로 완료 되었습니다.",
  "data": {
    "userId": 1,
    "email": "test@startupcode.kr",
    "nickname": "startup",
    "profileImageUrl": "https://image.kr/img.jpg"
  },
  "error": null
}
```

#### 실패

- **400 Bad Request**: 잘못된 형식
- **500 Internal Server Error**: 서버 일시적 오류

-----

## 2\. 유저 정보 변경

`PATCH` `/users/me`

프로필 사진, 닉네임, 비밀번호를 변경할 수 있습니다.

### 요청 (Request)

#### Headers

| 이름       | 필수 | 설명                              |
|:---------|:--:|:--------------------------------|
| `Cookie` | O  | `ACCESS_TOKEN`, `REFRESH_TOKEN` |

#### Path Variables

> **Dev Note:** `userId`를 Path Variable로 받지 않고 `/me`를 사용합니다. ID 예측을 통한 보안 위협을 방지하고, 토큰 기반으로 본인을 식별하는 패턴이 더 안전하고 깔끔합니다.

#### Body

수정하고 싶은 필드만 보냅니다. (선택적)

| 필드명               | 필수 |   타입   | 제약 조건    | 설명           |
|:------------------|:--:|:------:|:---------|:-------------|
| `profileImageUrl` | X  | String | 2048자 이내 |              |
| `nickname`        | X  | String | 2\~10자   | 띄어쓰기 불가능     |
| `currentPassword` | X  | String | 8\~20자   | 비밀번호 변경 시 필수 |
| `updatedPassword` | X  | String | 8\~20자   | 비밀번호 변경 시 필수 |

```json
{
  "profileImageUrl": "https://your-cdn.com/images/...",
  "nickname": "jayoon",
  "currentPassword": "current_password",
  "updatedPassword": "updated_password"
}
```

### 응답 (Response)

#### 성공 (200 OK)

**Case 1: 프로필 사진 변경**

```json
{
  "success": true,
  "message": "프로필 사진이 성공적으로 변경되었습니다.",
  "data": {
    "userId": 1,
    "profileImageUrl": "https://your-cdn.com/images/new.jpg"
  },
  "error": null
}
```

**Case 2: 닉네임 변경**

```json
{
  "success": true,
  "message": "닉네임이 성공적으로 변경되었습니다.",
  "data": {
    "userId": 1,
    "nickname": "jayoon"
  },
  "error": null
}
```

**Case 3: 비밀번호 변경**
*Header에 `REFRESH_TOKEN` 재발급 (HttpOnly)*

> **Note:** 비밀번호 변경 후 해당 기기만 인증 상태를 유지하고, 다른 기기에서는 로그아웃 처리됩니다.

```json
{
  "success": true,
  "message": "비밀번호 변경이 성공적으로 완료 되었습니다. 다른 기기에서 로그아웃 됩니다.",
  "data": null,
  "error": null
}
```

#### 실패

- **400**: 잘못된 형식 / 비밀번호 불일치
- **401**: 존재하지 않는 인증 정보 (토큰 없음 또는 만료)
- **403**: 권한 없음
- **404**: 토큰의 userId가 DB에 없음
- **500**: 서버 오류

-----

## 3\. 회원 탈퇴

`DELETE` `/users/me`

### 요청 (Request)

#### Headers

| 이름       | 필수 | 설명                              |
|:---------|:--:|:--------------------------------|
| `Cookie` | O  | `ACCESS_TOKEN`, `REFRESH_TOKEN` |

#### Body

> **Note:** RFC 7231에 따르면 DELETE 메서드에 Body를 포함하는 것은 정의된 의미가 없으나, 본인 확인을 위해 비밀번호를 받습니다.

```json
{
  "password": "current_password"
}
```

### 응답 (Response)

#### 성공 (200 OK)

```json
{
  "success": true,
  "message": "회원 탈퇴가 성공적으로 완료 되었습니다.",
  "data": null,
  "error": null
}
```

#### 실패

- **400**: 비밀번호 불일치 등
- **401**: 미인증
- **404**: 리소스 없음
- **500**: 서버 오류

-----

# Posts

## 1\. 게시글 생성

`POST` `/posts`

### 요청 (Request)

#### Headers

| 이름       | 필수 | 설명                              |
|:---------|:--:|:--------------------------------|
| `Cookie` | O  | `ACCESS_TOKEN`, `REFRESH_TOKEN` |

#### Body

| 필드명         | 필수 |   타입   | 설명                      |
|:------------|:--:|:------:|:------------------------|
| `title`     | O  | String | 제목                      |
| `body`      | O  | String | 본문 (마크다운 지원)            |
| `imageUrls` | X  | Array  | 이미지 URL 배열 (현재는 1개만 지원) |

```json
{
  "title": "제목1",
  "body": "본문 내용",
  "imageUrls": [
    "https://your-cdn.com/images/..."
  ]
}
```

### 응답 (Response)

#### 성공 (201 Created)

**Header** `Location`: `/api/v1/posts/123`

```json
{
  "success": true,
  "message": "게시글이 성공적으로 생성되었습니다.",
  "data": {
    "id": 123,
    "title": "제목",
    "body": "본문",
    "likeCount": 0,
    "commentCount": 0,
    "viewCount": 0,
    "imageUrls": [
      "..."
    ],
    "createdAt": "2025-10-13T07:45:43Z",
    "user": {
      "id": 1,
      "nickname": "jayoon"
    },
    "isAuthor": true,
    "isLiked": false
  },
  "error": null
}
```

-----

## 2\. 게시글 목록 조회 (Infinite Scroll)

`GET` `/posts`

### 요청 (Request)

#### Headers

없음 (공개 API)

#### Query Parameters

| 파라미터     | 필수 |   타입   | 설명                            |
|:---------|:--:|:------:|:------------------------------|
| `limit`  | O  | Number | 한 번에 불러올 개수 (기본 10)           |
| `cursor` | X  | Number | 마지막으로 불러온 게시글의 ID (첫 요청 시 생략) |

- **첫 번째 요청:** `GET /posts?limit=10`
- **두 번째 요청:** `GET /posts?limit=10&cursor={이전 응답의 nextCursor}`

### 응답 (Response)

#### 성공 (200 OK)

`nextCursor`가 `null`이면 더 이상 데이터가 없음을 의미합니다.

```json
{
  "success": true,
  "message": null,
  "data": {
    "posts": [
      {
        "id": 10,
        "title": "제목 10",
        "user": {
          ...
        }
      }
      // ...
    ],
    "nextCursor": 10
  },
  "error": null
}
```

-----

## 3\. 게시글 상세 조회

`GET` `/posts/:postId`

### 요청 (Request)

#### Headers

없음 (공개 API)

#### Path Variables

| 변수명      | 설명              |
|:---------|:----------------|
| `postId` | 게시글 ID (Number) |

### 응답 (Response)

#### 성공 (200 OK)

```json
{
  "success": true,
  "data": {
    "id": 123,
    "title": "제목",
    "body": "본문",
    "isAuthor": true
    // ... 상세 정보
  },
  "error": null
}
```

#### 실패

- **404**: 게시글 없음 또는 삭제됨

-----

## 4\. 게시글 삭제

`DELETE` `/posts/:postId`

### 요청 (Request)

**Headers**: `Cookie` (`ACCESS_TOKEN`, `REFRESH_TOKEN`)
**Path Variables**: `postId`

### 응답 (Response)

#### 성공 (200 OK)

```json
{
  "success": true,
  "message": "게시글이 성공적으로 삭제되었습니다.",
  "data": null,
  "error": null
}
```

#### 실패

- **403**: 권한 없음 (작성자가 아님) -\> 보안상 **404**로 응답할 수 있음.
- **404**: 게시글 없음

-----

## 5\. 게시글 수정

`PATCH` `/posts/:postId`

### 요청 (Request)

#### Headers

| 이름       | 필수 | 설명                              |
|:---------|:--:|:--------------------------------|
| `Cookie` | O  | `ACCESS_TOKEN`, `REFRESH_TOKEN` |

#### Body

수정하려는 필드만 포함합니다.

```json
{
  "title": "수정된 제목",
  "body": "수정된 본문"
}
```

### 응답 (Response)

#### 성공 (200 OK)

수정된 게시글 정보를 반환합니다.

-----

## 6\. 댓글 목록 조회

`GET` `/posts/:postId/comments`

게시물 상세 조회 시 함께 요청되어야 하는 API입니다.

### 요청 (Request)

#### Headers

없음 (공개 API)

#### Query Parameters

| 파라미터     | 필수 |   타입   | 설명               |
|:---------|:--:|:------:|:-----------------|
| `limit`  | O  | Number | 10               |
| `cursor` | X  | Number | 페이징 커서 (첫 요청 생략) |

### 응답 (Response)

#### 성공 (200 OK)

```json
{
  "success": true,
  "data": {
    "comments": [
      {
        "id": 1,
        "body": "댓글 내용",
        "user": {
          "id": 1,
          "nickname": "jayoon"
        },
        "isAuthor": true
      }
    ],
    "nextCursor": 10
  },
  "error": null
}
```

-----

## 7\. 댓글 생성

`POST` `/posts/:postId/comments`

### 요청 (Request)

#### Headers

| 이름       | 필수 | 설명                              |
|:---------|:--:|:--------------------------------|
| `Cookie` | O  | `ACCESS_TOKEN`, `REFRESH_TOKEN` |

**Body**

```json
{
  "body": "댓글 본문 내용"
}
```

### 응답 (Response)

#### 성공 (200 OK)

> **Note:** 유저 경험을 위해 별도의 message 없이 생성된 데이터를 반환합니다.

```json
{
  "success": true,
  "data": {
    "id": 1,
    "body": "댓글 본문 내용",
    "user": {
      ...
    },
    "isAuthor": true
  },
  "error": null
}
```

-----

## 8\. 댓글 수정 / 삭제

- **수정:** `PATCH` `/posts/:postId/comments/:commentId`
- **삭제:** `DELETE` `/posts/:postId/comments/:commentId`

**Headers**: `Cookie` (`ACCESS_TOKEN`, `REFRESH_TOKEN`)

(상세 명세는 게시글 수정/삭제와 유사하므로 생략, JSON 포맷 유지)

-----

# Auth

## 1\. 로그인

`POST` `/auth`

### 요청 (Request)

#### Headers

없음

#### Body

```json
{
  "email": "test@startupcode.kr",
  "password": "test1234"
}
```

### 응답 (Response)

#### 성공 (200 OK)

**Headers**

- `Set-Cookie`: `ACCESS_TOKEN` (HttpOnly, Path=/)
- `Set-Cookie`: `REFRESH_TOKEN` (HttpOnly, Path=/api/auth/refresh)

**Body** (유저 정보 반환)

```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "userId": 1,
    "email": "...",
    "nickname": "..."
  },
  "error": null
}
```

#### 실패

- **401**: 아이디 또는 비밀번호 불일치

-----

## 2\. 로그아웃

`DELETE` `/auth`

### 요청 (Request)

#### Headers

| 이름       | 필수 | 설명                              |
|:---------|:--:|:--------------------------------|
| `Cookie` | O  | `ACCESS_TOKEN`, `REFRESH_TOKEN` |

### 응답 (Response)

#### 성공 (200 OK)

**Headers**: `Set-Cookie` (Max-Age=0, 즉시 만료 처리)

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

-----

## 3\. Access Token 재발급

`POST` `/auth/refresh`

> **Dev Note:**
> `POST /auth/access-token` vs `POST /auth/refresh` 중 고민하였으나, 인증 정보를 독립된 도메인으로 보고자 `/auth` 리소스를 사용했습니다.
>
>   - **특이사항:** 인가(JWT) 불필요. 오직 `REFRESH_TOKEN` 쿠키만 확인합니다.

### 요청 (Request)

#### Headers

| 이름       | 필수 | 설명              |
|:---------|:--:|:----------------|
| `Cookie` | O  | `REFRESH_TOKEN` |

### 응답 (Response)

#### 성공 (200 OK)

새로운 `ACCESS_TOKEN`이 쿠키로 설정됩니다.

#### 실패 (401 Unauthorized)

Refresh Token 만료 또는 유효하지 않음.

> **Action:** 모든 토큰 쿠키를 삭제하고 로그인 페이지로 리다이렉트 해야 합니다.

-----

# Images

## 1\. 이미지 업로드 (서버 저장)

`POST` `/images`

API Gateway + Lambda를 통해 별도 저장소에 저장됩니다. (회원가입 등 인증 전 사용 가능하므로 인증 헤더 불필요)

### 요청 (Request)

**Headers**: 없음
**Query Parameters**: `path` (저장 경로)

### 응답 (Response)

#### 성공 (200 OK)

```json
{
  "success": true,
  "data": "저장된 이미지 URL 또는 Content",
  "error": null
}
```

## 2\. Pre-signed URL 조회 (Deprecated)

`GET` `/images/pre-signed-url`

> **Status:** 현재 변경되어 사용하지 않음 (참고용)

### 요청 (Request)

**Query Params**: `filename`, `content-type`

### 응답 (Response)

```json
{
  "success": true,
  "data": {
    "preSignedUrl": "https://s3-bucket-url...",
    "profileImageUrl": "https://cdn-url..."
  },
  "error": null
}
```