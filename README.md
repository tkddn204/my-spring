# My Spring 🌱

**Spring Boot**를 배우면서 개발한 **Spring REST API 서버** 토이프로젝트입니다.

## 개발 환경

- IDE : Intellij
- Main Language : **Java 17**
- Development Framework : **Spring Boot 3.1.3**
- Database : **Mysql, Redis**
- Dependencies : **[build.gradle](./build.gradle.kts)**
- Git Branch : like git-flow strategy

## 구현 목록

아래의 내용들이 구현되어 있습니다.

- 멤버 회원가입, 로그인, 조회 구현
- 게시글(Post) CRUD 구현
- JWT를 이용한 인증 체계 구현 (Redis로 리프레시 토큰 관리)
- 각 API에 대한 통합 테스트 작성
- 각 서비스의 유닛 테스트 코드 작성
- [API 문서](https://tkddn204.github.io/my-spring/) 작성 (github pages 업로드)

## 개발 사전 작업

> 사전에 mysql과 redis가 설치되어 있어야 합니다.

1. 설정파일은 다음과 같이 3가지 파일이 필요합니다.

    - `src/resources/application.yml`
    - `src/resources/application-dev.yml`
    - `src/resources/application-test.yml`

2. 처음 저장소를 클론했다면, `application.yml` 파일만 있을 것입니다. 다음 코드를 복사하여 `application-dev.yml`와 `application-test.yml` 파일에 넣어주세요.

    ```yaml
    spring:
      datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        username: <your-mysql-username>
        url: jdbc:mysql://localhost:3306/myspring?characterEncoding=UTF-8&serverTimezone=UTC
        password: <your-mysql-password>
      jpa:
        hibernate:
          ddl-auto: create
        generate-ddl: true
    
    # 개발용으로 생성된 랜덤 secret입니다.
    jwt:
      secret: YnVmZmV0aW5ncy10b2lsZnVsbHktYXVzdHJhbGlhbml6ZS1taWNyb2dsaWFsLWNhcmFtZWxpc2VkLWxlZWNoZXItZGV0ZXJyYWJsZS11bmZhaXRoZnVsbHktZW5maWVsZC1icmFjaHljZXBoYWxpemF0aW9uLXN1Y2Nlc3NvcmFsLXVuZGVmZXJlbnRpYWwtdHlwb2xvZ2ljYWxseQ==
      expire:
        access: 900000
        refresh: 86400000
    ```

3. mysql에 데이터베이스를 만들어줍니다.

    ```mysql
    CREATE DATABASE myspring;
   ```

4. 사전 작업은 완료됐습니다! 자신의 IDE 환경에서 스프링 서버를 실행하면 됩니다.

## [API 문서](https://tkddn204.github.io/my-spring/)

- Member API
    - 멤버 회원가입
    - 멤버 로그인
    - 멤버 조회
- JWT API
    - 액세스 토큰 재생성
- POST API
    - 포스트 목록 조회
    - 포스트 조회
    - 포스트 작성
    - 포스트 수정
    - 포스트 삭제
