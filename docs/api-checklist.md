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
    - [x] 조회수 관련 로직을 상세 조회에서 진행합니다.
- [x] DELETE /posts/:postId 게시글 삭제
- [x] PATCH /posts/:postId 게시글 수정

- [x] POST /posts/:postId/comments/ 댓글 생성
- [x] (인증x)GET /posts/:postId/comments 댓글 목록 조회
    - 댓글을 단일 조회가 없습니다.
- [x] PUT /posts/:postId/comments/:commentId 댓글 수정
- [x] DELETE /posts/:postId/comments/:commentId 댓글 삭제

- [x] POST /posts/:postId/likes 좋아요 생성
- [x] DELETE /posts/:postId/likes 좋아요 삭제.

#### auth

users와 다르게 인증 정보를 새롭게 생성 및 삭제를 하므로 유저와 관심사가 다르다고 생각했습니다. 때문에 분리하였습니다.

- [x] (인증x)POST /auth 로그인
- [x] DELETE /auth 로그아웃

#### images

API Gateway와 Lambda를 통해 개발 되었습니다. ALB를 통해 /images에 대한 요청은 spring 서버로 들어오지 않습니다.

- [x] (인증x)POST /images 이미지 추가

### 인가

---

- [x] 세션, 쿠키
- [x] JWT
    - [ ] refresh token
