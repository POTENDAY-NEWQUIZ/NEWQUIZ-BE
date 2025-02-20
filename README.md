## NEWQUIZ-BE

**🔀 Branch Rule**

- Github Projects를 이용하여 Issue를 관리합니다.
- 각자의 feature branch에서 작업한 후, main branch로 merge합니다.
- {commit명}/{이슈 번호} 순으로 작명합니다.
- `ex) feat/#3`

**💬 Commit Convention**

| commit 명 | commit 규칙 |
| --- | --- |
| `feat` | 새로운 기능 추가 / 일부 코드 추가 / 일부 코드 수정 (리팩토링과 구분) / 디자인 요소 수정 |
| `fix` | 버그 수정 |
| `refactor` | 코드 리팩토링 |
| `style` | 코드 의미에 영향을 주지 않는 변경사항 (코드 포맷팅, 오타 수정, 변수명 변경, 에셋 추가) |
| `chore` | 빌드 부분 혹은 패키지 매니저 수정 사항 |
| `docs` | 문서 추가 및 수정 |
| `rename` | 패키지 혹은 폴더명, 클래스명 수정 (단독으로 시행하였을 시) |
| `remove` | 패키지 혹은 폴더, 클래스를 삭제하였을 때 (단독으로 시행하였을 시) |
- `ex) [feat] : 로그인 기능 구현`

**Issue**

- Issue Title : **`[conventionType] : 작업할 내용`**
- 모든 작업은 `Issue`를 만든 후, 해당 이슈 번호에 대한 branch를 통해 수행
- 수행할 작업에 대한 설명과 할 일을 작성

**Pull Request**

- Pull Request Title : **`[ContentionType/#이슈번호] 작업한 내용`**
- 수행한 작업에 대한 설명을 작성하고 관련 스크린샷을 첨부
- Assigner, Label, Project, Milestone, 관련 이슈를 태그
