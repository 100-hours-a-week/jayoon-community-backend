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

### 공통 에러 처리 원칙

- **401 Unauthorized**: 인증 토큰이 없거나 만료됨.
- **403 Forbidden**: 권한이 없음. (단, 리소스 존재 여부 노출을 막기 위해 **404**로 응답할 수 있음)
- **500 Internal Server Error**: 서버 내부 오류.

-----

# Users

## 1\. 회원 가입

`POST` `/users`

### 요청 (Request)

#### Headers

없음

#### Body

**Content-Type:** `application/json`

| 필드명               | 필수 |   타입   | 제약 조건    | 설명                       |
|:------------------|:--:|:------:|:---------|:-------------------------|
| `email`           | O  | String | 320자 이내  | 소문자 영문, 숫자, `@`, `.`만 가능 |
| `password`        | O  | String | 8\~20자   | 대/소문자, 숫자, 특수문자 각 1개 이상  |
| `nickname`        | O  | String | 2\~10자   | 띄어쓰기 불가능                 |
| `profileImageUrl` | X  | String | 2048자 이내 | 미입력 시 `null`             |

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

> **Note:** 회원가입 성공 시 자동으로 로그인 처리됩니다.

**Headers**: `Set-Cookie` (ACCESS\_TOKEN, REFRESH\_TOKEN)

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

**400 Bad Request** (유효성 검사 실패)

```json
{
  "success": false,
  "message": "잘못된 이메일 형식입니다.",
  "data": null,
  "error": {
    "statusCode": "400"
  }
}
```

**409 Conflict** (이메일 중복)

```json
{
  "success": false,
  "message": "이미 존재하는 이메일입니다.",
  "data": null,
  "error": {
    "statusCode": "409"
  }
}
```

**500 Internal Server Error**

```json
{
  "success": false,
  "message": "서비스가 일시적으로 불안정합니다. 관리자에게 문의해주세요.",
  "data": null,
  "error": {
    "statusCode": "500"
  }
}
```

-----

## 2\. 유저 정보 변경

`PATCH` `/users/me`

### 요청 (Request)

#### Headers

| 이름       | 필수 | 설명                              |
|:---------|:--:|:--------------------------------|
| `Cookie` | O  | `ACCESS_TOKEN`, `REFRESH_TOKEN` |

#### Body

(변경할 필드만 전송)

```json
{
  "profileImageUrl": "http://image.kr/img.jpg",
  "nickname": "jayoon",
  "currentPassword": "current_password",
  "updatedPassword": "updated_password"
}
```

### 응답 (Response)

#### 성공 (200 OK)

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

```json
{
  "success": true,
  "message": "프로필 사진이 성공적으로 변경되었습니다.",
  "data": {
    "userId": 1,
    "profileImageUrl": "https://your-cdn.com/images/profile/new-image.jpg"
  },
  "error": null
}
```

##### header

set-cookie "refreshToken": "df...", httpOnly

- 비밀번호를 변경한 후 해당 기기만 인증 상태를 유지합니다.

```json
{
  "success": true,
  "message": "비밀번호 변경이 성공적으로 완료 되었습니다. 다른 기기에서 로그아웃 됩니다.",
  "data": null,
  "error": null
}
```

#### 실패

**400 Bad Request** (비밀번호 불일치)

```json
{
  "success": false,
  "message": "현재 비밀번호가 일치하지 않습니다.",
  "data": null,
  "error": {
    "statusCode": "400"
  }
}
```

**401 Unauthorized** (인증 정보 없음)

```json
{
  "success": false,
  "message": "존재하지 않는 인증 정보입니다.",
  "data": null,
  "error": {
    "statusCode": "401"
  }
}
```

-----

## 3\. 회원 탈퇴

`DELETE` `/users/me`

### 요청 (Request)

**Headers**: `Cookie` (Tokens)
**Body**: `{"password": "current_password"}`

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

**400 Bad Request** (비밀번호 불일치)

```json
{
  "success": false,
  "message": "비밀번호가 잘못 되었습니다.",
  "data": null,
  "error": {
    "statusCode": "400"
  }
}
```

**401 Unauthorized**

```json
{
  "success": false,
  "message": "존재하지 않는 인증 정보입니다.",
  "data": null,
  "error": {
    "statusCode": "401"
  }
}
```

-----

# Posts

## 1\. 게시글 생성

`POST` `/posts`

### 요청 (Request)

**Headers**: `Cookie` (Tokens)
**Body**: `title`, `body` 필수

| 필드명         | 필수 |    타입    | 제약 조건 | 설명              |
|:------------|:--:|:--------:|:------|:----------------|
| `title`     | O  |  String  |       | 게시글 제목          |
| `body`      | O  |  String  |       | 게시글 본문          |
| `imageUrls` | X  | String[] |       | 이미지 URL 배열 (선택) |

```json
{
  "title": "게시글 제목입니다",
  "body": "게시글 본문 내용입니다. \n 안녕하세요!",
  "imageUrls": [
    "https://cdn.your-domain.com/path/image1.jpg"
  ]
}
```

### 응답 (Response)

#### 성공 (201 Created)

```json
{
  "success": true,
  "message": "게시글이 성공적으로 생성되었습니다.",
  "data": {
    "id": 123,
    "title": "...",
    "createdAt": "..."
  },
  "error": null
}
```

#### 실패

**400 Bad Request** (입력값 누락)

```json
{
  "success": false,
  "message": "제목은 필수 입력 항목입니다.",
  "data": null,
  "error": {
    "statusCode": "400"
  }
}
```

**401 Unauthorized**

```json
{
  "success": false,
  "message": "로그인이 필요합니다.",
  "data": null,
  "error": {
    "statusCode": "401"
  }
}
```

-----

## 2\. 게시글 목록 조회

`GET` `/posts`

### 요청 (Request)

**Headers**: 없음 (공개)
**Query**: `limit=10`, `cursor=10`

### 응답 (Response)

#### 성공 (200 OK)

```json
{
  "success": true,
  "data": {
    "posts": [
      ...
    ],
    "nextCursor": 10
  },
  "error": null
}
```

#### 실패

**400 Bad Request** (파라미터 오류)

```json
{
  "success": false,
  "message": "limit 값은 1 이상이어야 합니다.",
  "data": null,
  "error": {
    "statusCode": "400"
  }
}
```

-----

## 3\. 게시글 상세 조회

`GET` `/posts/:postId`

### 요청 (Request)

**Headers**: 없음 (공개)

### 응답 (Response)

#### 성공 (200 OK)

```json
{
  "success": true,
  "data": {
    "id": 123,
    "title": "...",
    "body": "..."
  },
  "error": null
}
```

#### 실패

**400 Bad Request** (ID 형식 오류)

```json
{
  "success": false,
  "message": "잘못된 형식의 ID입니다.",
  "data": null,
  "error": {
    "statusCode": "400"
  }
}
```

**404 Not Found**

```json
{
  "success": false,
  "message": "요청한 리소스가 존재하지 않습니다.",
  "data": null,
  "error": {
    "statusCode": "404"
  }
}
```

-----

## 4\. 게시글 삭제

`DELETE` `/posts/:postId`

### 요청 (Request)

**Headers**: `Cookie` (Tokens)

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

**401 Unauthorized**

```json
{
  "success": false,
  "message": "존재하지 않는 인증 정보입니다.",
  "data": null,
  "error": {
    "statusCode": "401"
  }
}
```

**404 Not Found**

> **Security Note:** 게시글이 없거나, 게시글은 있지만 삭제 권한이 없는 경우(403) 보안을 위해 모두 404로 응답합니다.

```json
{
  "success": false,
  "message": "요청한 리소스가 존재하지 않습니다.",
  "data": null,
  "error": {
    "statusCode": "404"
  }
}
```

-----

## 5\. 게시글 수정

`PATCH` `/posts/:postId`

### 요청 (Request)

**Headers**: `Cookie` (Tokens)
**Body**: `{"title": "수정"}`

### 응답 (Response)

#### 성공 (200 OK)

(수정된 데이터 반환)

#### 실패

**400 Bad Request**

```json
{
  "success": false,
  "message": "수정할 내용이 없습니다.",
  "data": null,
  "error": {
    "statusCode": "400"
  }
}
```

**404 Not Found** (삭제와 동일하게 권한 없음 포함)

```json
{
  "success": false,
  "message": "요청한 리소스가 존재하지 않습니다.",
  "data": null,
  "error": {
    "statusCode": "404"
  }
}
```

-----

## 6\. 댓글 목록 조회

`GET` `/posts/:postId/comments`

### 요청 (Request)

**Headers**: 없음 (공개)

### 응답 (Response)

#### 성공 (200 OK)

(댓글 리스트 반환)

#### 실패

**404 Not Found** (게시글이 없을 때)

```json
{
  "success": false,
  "message": "해당 게시글을 찾을 수 없습니다.",
  "data": null,
  "error": {
    "statusCode": "404"
  }
}
```

-----

## 7\. 댓글 생성

`POST` `/posts/:postId/comments`

### 요청 (Request)

**Headers**: `Cookie` (Tokens)
**Body**: `{"body": "내용"}`

### 응답 (Response)

#### 성공 (200 OK)

(생성된 댓글 반환)

#### 실패

**400 Bad Request**

```json
{
  "success": false,
  "message": "댓글 내용을 입력해주세요.",
  "data": null,
  "error": {
    "statusCode": "400"
  }
}
```

**401 Unauthorized**

```json
{
  "success": false,
  "message": "로그인이 필요합니다.",
  "data": null,
  "error": {
    "statusCode": "401"
  }
}
```

**404 Not Found** (게시글 없음)

```json
{
  "success": false,
  "message": "요청한 리소스가 존재하지 않습니다.",
  "data": null,
  "error": {
    "statusCode": "404"
  }
}
```

-----

# Auth

## 1\. 로그인

`POST` `/auth`

### 요청 (Request)

**Headers**: 없음
**Body**: `email`, `password`

### 응답 (Response)

#### 성공 (200 OK)

**Headers**: `Set-Cookie` (Tokens)

```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "userId": 1,
    ...
  },
  "error": null
}
```

#### 실패

**400 Bad Request**

```json
{
  "success": false,
  "message": "잘못된 형식입니다.",
  "data": null,
  "error": {
    "statusCode": "400"
  }
}
```

**401 Unauthorized**

```json
{
  "success": false,
  "message": "아이디 또는 비밀번호가 잘못 되었습니다.",
  "data": null,
  "error": {
    "statusCode": "401"
  }
}
```

-----

## 2\. 로그아웃

`DELETE` `/auth`

### 요청 (Request)

**Headers**: `Cookie` (Tokens)

### 응답 (Response)

#### 성공 (200 OK)

**Headers**: `Set-Cookie` (Max-Age=0)

#### 실패

**401 Unauthorized**

```json
{
  "success": false,
  "message": "이미 로그아웃 되었거나 잘못된 토큰입니다.",
  "data": null,
  "error": {
    "statusCode": "401"
  }
}
```

-----

## 3\. Access Token 재발급

`POST` `/auth/refresh`

### 요청 (Request)

**Headers**: `Cookie` (`REFRESH_TOKEN` 필수)

### 응답 (Response)

#### 성공 (200 OK)

**Headers**: New `ACCESS_TOKEN` Cookie

#### 실패

**401 Unauthorized** (리프레시 토큰 만료/조작)

> **Action:** 이 응답을 받으면 프론트엔드는 모든 쿠키를 삭제하고 로그인 페이지로 이동해야 합니다.

```json
{
  "success": false,
  "message": "인증 정보가 만료되었습니다. 다시 로그인해주세요.",
  "data": null,
  "error": {
    "statusCode": "401"
  }
}
```

-----

# Images

## 1\. 이미지 업로드

`POST` `/images`

### 요청 (Request)

**Headers**: 없음
**Query**: `path`

### 응답 (Response)

#### 성공 (200 OK)

```json
{
  "success": true,
  "data": "https://cdn.your-domain.com/path/image.jpg",
  "error": null
}
```

#### 실패

**400 Bad Request** (파일 없음/형식 오류)

```json
{
  "success": false,
  "message": "이미지 파일만 업로드 가능합니다.",
  "data": null,
  "error": {
    "statusCode": "400"
  }
}
```

**500 Internal Server Error** (업로드 실패)

```json
{
  "success": false,
  "message": "이미지 업로드 중 오류가 발생했습니다.",
  "data": null,
  "error": {
    "statusCode": "500"
  }
}
```