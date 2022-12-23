# jpa-starter

## Database
### MySQL 8.0.31 - Docker
> 프로젝트 디렉터리의 docker/mysql 이동 후 start-mysql.sh 실행  

---

## Set vs List
### Set
> Java 의 Collection 중 Set 은 중복 항목을 허용하지 않는다. 이미 가지고 있는 항목을 Set 에 add 하면 그 항목은 add 되지 않는다.    
> JPA Entity 의 Set 에서 동일 항목인지 비교는 동일성(PK 비교)가 아닌 동등성(EqualsAndHashCode) 로 비교한다.    
> 동등성 비교를 위해서는 `@EqualsAndHashCode(of = {})` 에서 @Id 항목을 제외하고 동등해야하는 항목을 넣는다.  

---

## Database lock
### Optimistic Lock
> 

### Pessimistic Lock
> `SELECT FOR UPDATE`: 동시성 제어를 위해 특정 row 에 배타적 LOCK 을 거는 행위, 해당 row 는 commit 이 되기 전까지 다른 트랜잭션에서 조회가 지연된다.  

### 참조사이트
> https://isntyet.github.io/jpa/JPA-%EB%B9%84%EA%B4%80%EC%A0%81-%EC%9E%A0%EA%B8%88(Pessimistic-Lock)/
> https://reiphiel.tistory.com/entry/understanding-jpa-lock
> https://sabarada.tistory.com/187

---

## application.yml
### spring.jpa.properties.hibernate.enable_lazy_load_no_trans
> 트랜잭션 범위가 끝나면 영속성 컨텍스트가 함께 종료되는 것이 JPA의 기본 전략이다.  
> enable_lazy_load_no_trans 옵션은 OSIV와 비슷하기는 한데, 영속성 컨텍스트가 종료되어도, 새로운 데이터베이스 커넥션을 획득해서 지연로딩을 
> 가능하게 해준다. 이 방법은 여러번 지연로딩이 있으면 그때마다 각각 새로운 데이터베이스 커넥션을 획득하기 때문에 성능상 매우 좋지 않다.
> 해당 기능은 사용하지 않는다.

---

## EqualsAndHashCode
### Entity
> Entity 에 @EqualsAndHashcode 적용 시 기존 Hibernate 의 구현에 따라 @Id 를 비교하도록 되어있음.  
> Schedule 에서 Member 정보를 받아 Schedule 의 종속 테이블인 ScheduleMember 에 추가 시 중복을 없애기 위해서  
> Set + @EqualsAndHashcode 를 고려했었음.  
> 하지만 추가 로직에서는 ScheduleMember 의 생성자 호출 방식으로 추가하기 때문에 @Id 가 없어서 중복 제거가 안됨.  
> 즉 중복 제거를 위해서 Set + @EqualsAndHashcode 를 사용할 수 없음.  

### 복합키
> @Id 는 동일성 / 동등성 비교를 위해서 @EqualsAndHashcode 구현이 필요함.

---

## Repository
### JpaRepository vs CommonRepository
> JpaRepository 의 경우 기본적인 CRUD 관련 메서드가 제공되며, JpaRepository 를 상속받는 Repository 인터페이스는 앞의 이유로 기본적인 CRUD 관련 메서드가 
> 개발자의 뜻과 상관없이 모두 노출(제공)된다.  
> Repository 에 선언한 메서드만 외부로 노출하고 싶은 경우 CommonRepository 를 상속하여 사용한다.  

---
