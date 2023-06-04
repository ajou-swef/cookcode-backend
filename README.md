# cookcode backend
## API 명세
[팀 API notion 바로가기](https://dolomite-mascara-65c.notion.site/API-5f5784b670a549978dc8d2442f40aca1)

## 협업 방식
1. issue를 열고 개발할 todo를 작성하고, 브랜치를 생성
    - [issue 바로가기](https://github.com/ajou-swef/cookcode-backend/issues)
2. 브랜치에서 작업 후, main에 Pull Request
    1. PR은 한 명의 코드 리뷰를 필수적으로 받아야 merge 가능함
    - [코드리뷰 바로가기](https://github.com/ajou-swef/cookcode-backend/pull/48)
3. 팀원이 코드 리뷰를 해주고, 이에 대한 논의 및 피드백을 반영함
4. main에 merge되면, github action이 실행되어서 빌드 파이프라인과 배포가 진행됨.

## ERD
<img width="1059" alt="Untitled (5)" src="https://github.com/ajou-swef/cookcode-backend/assets/52846807/51988e84-7665-4a8b-acd9-07b460592606">

## 시스템 아키텍처
<img width="1162" alt="image" src="https://github.com/ajou-swef/cookcode-backend/assets/52846807/c472bff5-7598-496e-9039-2f0f0dec9efa">

## 기술 스택
### 개발

- 언어: `Java (JDK 17)`
- 빌드도구: `Gradle (7.6.1)`
- 프레임워크: `Spring Boot (3.0)`
- ORM: `JPA`
- DB 관련 라이브러리: `Spring Data JPA`, `QueryDsl (5.0.0)`
- 데이터베이스:  `H2`, `rds (mysql 8.0.32)` , `elasticach (redis 7.0.7)`

### 협업

- API 명세서: `Notion`

### 배포, 운영

- CI/CD: `Github Actions` , `S3`, `CodeDeploy`
- 인프라:  `AWS vpc`, `EC2 AutoScalingGroup`, `Elastic LoadBalancer`, `rds`, `Elasticache`, `S3`, `cloudwatch`

