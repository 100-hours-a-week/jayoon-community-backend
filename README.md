# 가이드윤의 바른 생활 Community service

## 사이트: [가이드윤의 바른생활](https://guidey.site)

## API 명세서

- [x] [API 명세서](https://github.com/100-hours-a-week/jayoon-til/blob/main/community-docs/api-specification.md)

## ERD

- [x] [ERDCloud](https://www.erdcloud.com/d/GRWCfnyCjSsbtM4AD)

## 화면설계

- [x] [Figma - 아무말 대잔치](https://www.figma.com/design/uzVLRNRe4ocdIjr7xegIuf/%EA%B5%90%EC%9E%AC%EC%9A%A9-%EC%BB%A4%EB%AE%A4%EB%8B%88%ED%8B%B0-%EC%9B%B9?node-id=0-1&t=BznhwcNMxzALRCZr-1)
    - 외부 비공개 자료입니다.
    - 권한이 있는 사람만 접근 가능합니다.

## Convention

- [x] [개인 Notion](https://jayoon.notion.site/Convention-2a32ff1a1acc80a6bca6c0d0d70e57b8?source=copy_link)

## 진행 상황

### API 개발(리소스 별 분류)

---

#### users

- [x] (인증x)POST /users 회원가입
- [x] PATCH /users/me 회원정보 수정
    - 프로필 이미지, 닉네임, 비밀번호를 수정할 수 있습니다.
- [x] DELETE /users/me 회원 탈퇴

#### posts

- [x] POST /posts
- [x] (인증x)GET /posts 게시글 목록 조회
- [x] (인증x)GET /posts/:postId 게시글 상세 조회
    - 조회수 관련 로직을 상세 조회에서 진행합니다.
- [x] DELETE /posts/:postId 게시글 삭제
- [ ] PATCH /posts/:postId 게시글 수정

- [ ] (인증x)GET /posts/:postId/comments 댓글 목록 조회
    - 댓글을 단일 조회가 없습니다.
- [ ] POST /posts/:postId/comments/ 댓글 생성
- [ ] PUT /posts/:postId/comments/:commentId 댓글 수정
- [ ] DELETE /posts/:postId/comments/:commentId 댓글 삭제

- [ ] PUT /posts/:postId/likes 좋아요 생성 또는 삭제.
    - 좋아요를 누를 때는 클라이언트의 상태를 믿지 않으므로 POST 또는 DELETE를 보내는 것이 올바르지 않다고 생각합니다.

#### auth

users와 다르게 인증 정보를 새롭게 생성 및 삭제를 하므로 유저와 관심사가 다르다고 생각했습니다. 때문에 분리하였습니다.

- [x] (인증x)POST /auth 로그인
- [x] DELETE /auth 로그아웃

#### images

API Gateway와 Lambda를 통해 개발 되었습니다. ALB를 통해 /images에 대한 요청은 spring 서버로 들어오지 않습니다.

- [x] (인증x)POST /images 이미지 추가

### 인가

---

- [ ] 세션, 쿠키
- [ ] JWT