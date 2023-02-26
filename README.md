# jpa-starter

## Database
### MySQL 8.0.31 - Docker
> 프로젝트 디렉터리의 docker/mysql 이동 후 start-mysql.sh 실행  

---

## Spring Data JPA
### 설명
> Spring 에서 JPA 를 사용 시 Spring Data JPA 를 통해서 사용.

### Dependency
> SpringBoot 기준 Spring Data JPA 디펜던시 추가 설정
> ```groovy
> ...
> dependencies {
>     implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
>     ...
> }
> ```

### JpaRepository
> Spring Data JPA 를 사용하면 기존에 DAO 클래스를 사용하지 않고 리포지토리 인터페이스를 생성 후 
> Spring Data JPA 에서 제공하는 JpaRepository 인터페이스를 상속하는 방식으로 데이터베이스에 접근하도록 한다.
> 
> JpaRepository 를 상속하게 되면 JpaRepository 에 선언된 메서드들을 Override 하여 구현하지 않아도 모두 사용 가능해진다.  
> 
> JpaRepository 는 제네릭 인터페이스로 첫번째 인자는 해당 리포지토리가 접근하는 엔티티 클래스, 두번째 인자는 해당 엔티티의 PK 자료형을 입력한다.  
> 예시: Order 엔티티의 데이터베이스 접근을 위한 Repository 인터페이스 생성
> ```java
> public interface OrderRepository extends JpaRepository<Order, Long> {
> }
> ```

### CommonRepository
> JpaRepository 를 상속받은 경우 해당 Repository 는 JpaRepository 에 선언된 모든 메서드를 외부에 제공하게 된다.  
> 하지만 JpaRepository 에 선언된 모든 메서드가 아닌 Repository 에 선언된 메서드만 외부에 제공하고 싶어지는 경우가 있다.  
> 그런 경우 JpaRepository 를 상속받지 말고 CommonRepository 인터페이스를 직접 생성 후 Repository 에 해당 인터페이스를 상속받는 형태로 만든다.  
> 
> CommonRepository.java
> ```java
> @NoRepositoryBean
> public interface CommonRepository<T, ID extends Serializable> extends Repository<T, ID> {
> }
> ```

---

## Transaction
### @Transactional
> @Transactional 은 RuntimeException 과 그 자식인 `언체크(Unchecked)` 예외만 롤백한다.  
> 만약 체크 예외가 발생해도 롤백하고 싶다면 @Transactional(rollbackFor = Exception.class) 처럼 롤백할 예외를 지정해야 한다.  

### JUnit 에서 사용
> JUnit 테스트 클래스에 @Transactional 애노테이션을 붙인 경우 각 테스트 메서드가 실행 후 테스트가 끝나면 트랜잭션을 강제로 롤백한다.   

---

## JPQL
### Collection fetch join  
> JPQL 을 통한 조회 시 둘 이상의 컬렉션을 fetch 할 수 없다.  
> 둘 이상의 컬렉션을 fetch join 한 JPQL 을 실행하면 다음의 예외가 발생한다: 
> `PersistenceException: org.hibernate.loader.MultipleBagFetchExcetpino: cannot simultaneously fetch multiple bags`  
> 
> 컬렉션을 fetch join 시 (OneToMany 객체 참조) 페이징 API 를 사용할 수 없다.  
> JPA 구현체로 Hibernate 를 사용하는 경우 경고 로그를 남기면서 메모리에서 페이징 처리가 되지만, 데이터가 많을 시 성능 이슈 및 메모리 초과 예외가 발생할 수 있다.  

### Collection size
> select 절에서 OneToMany 객체의 size 기능을 통해서 count 함수를 호출한 것과 동일한 기능을 수행할 수 있다.  
> ex) `select t.members.size from Team t`

### Collection is empty
> where 절에서 컬렉션(OneToMany) 객체가 empty 인지 아닌지 조건을 줄 수 있다.  
> ex1) `select m from Member m where m.orders is not empty` -> SQL 에서는 exists 사용   
> ex2) `select m from Member m where m.orders is empty` -> SQL 에서는 not exists 사용  
> 컬렉션 객체에 `is null` 은 사용할 수 없다.

### is null
> JPQL 사용 시 `is null` 혹은 `is not null` 을 사용해야하며 그 외에 null = null 혹은 m.age = null 과 같이 사용하지 않아야한다.   

### FlushModeType.AUTO 에 대한 고찰
> 사실 1. JPQL 실행하면(select 쿼리라고 가정) 쿼리 결과 값이 영속성 컨텍스트에 존재하는지 여부와 관계없이 DB 에 쿼리를 전송한다.  
> 사실 2. JPQL 이 DB 에 날린 쿼리로 인하여 가져온 데이터가 영속성 컨텍스트에 이미 있는 경우 REPEATABLE READ 를 지키기 위해서 DB 에서 가져온 데이터를 
> 영속성 컨텍스트에 있는 데이터로 교체한다.  
> 
> JPQL 을 실행하기 전에 엔티티를 수정한 경우 엔티티 수정이 JPQL 의 결과에 영향을 미치는 경우가 존재한다.  
> 예를 들어서 DB 에는 Member 테이블의 데이터가 1개 밖에 없고 해당 Member 엔티티의 Name 속성을 'name' -> 'name 수정' 으로 수정한 후에   
> JPQL 에 `select m from Member m where m.name = 'name 수정'` 을 실행했을 때 문제가 발생할 수 있다.  
> 기본적으로 JPQL 의 결과는 DB 에서 조회한 데이터를 기반으로 만들어지기 때문에 만약 JPQL 실행 이전에 영속성 컨테스트를 Flush 하지 않으면 
> DB 에는 member 의 name 속성이 'name 수정' 인 데이터가 없음으로 JPQL 쿼리의 실행 결과는 없다.  
> JPQL 은 DB Table 이 아닌 엔티티를 조회하는 개념으로 봐야하기 때문에 엔티티를 조회하는 영속성 컨텍스트에는 name 이 'name 수정' 인 Member 엔티티가 존재하는데 
> JPQL 의 결과 값에는 존재하지 않는 것으로 나타나게 되기 때문에 이는 비정상 동작으로 볼 수 있다.  
> 해당 불일치를 없애기 위해서 JPQL 실행 전에는 자동으로 영속성 컨텍스트를 Flush 하도록 설정되어 있다.   
> (영속성 컨텍스트의 Flush 는 컨텍스트를 비우는 것이 아닌 컨텍스트와 DB를 동기화하는 것을 뜻한다.)  
> 
> FlushModeType.COMMIT 을 지정하게 되면 JPQL 실행 전에 영속성 컨텍스트의 Flush 가 일어나지 않으며, 트랜잭션 커밋 시점에만 Flush 가 일어나게 된다.  
> 보통은 해당 옵션을 사용하지 않으며 위의 DB 와 영속성 컨텍스트와의 불일치가 발생하지 않는 선에서 성능 최적화가 필요한 경우에만 사용한다. 
> 평소에는 FlushModeType.AUTO 로 사용한다.    
> 
> FlushModeType.AUTO 와 FlushModeType.COMMIT 에 대한 오해가 있었다.  
> FlushModeType.AUTO 로 설정한 경우 JPQL 실행 이전에 엔티티를 수정했을 때 수정한 엔티티가 DB 에 반영이 완료되었기 때문에 트랜잭션이 롤백되도 해당 DB 의 내용이
> 롤백이 되지 않는 것으로 오해를 한 것이다. 하지만 이것은 오해이며 영속성 컨텍스트의 내용을 DB 에 반영하는 것은 트랜잭션 내에서 하는 것이기 때문에 
> 트랜잭션이 롤백되면 영속성 컨텍스트에서 DB 에 반영한 내용은 롤백이 된다. 

---

## 오류 대처
### antlr.SemanticException: with-clause not allowed on fetched associations; use filters
> 발생 원인: JPQL 에서 fetch join 사용 시에 on 조건을 걸면 발생하는 오류
> 
> 원인 내용: 예를 들어서 Member 엔티티와 Team 엔티티가 있다고 가정한다.  
> 발생 원인이 된 쿼리는 다음과 같다고 가정한다: `select m from Member m inner join fetch m.team t on t.name = :teamName`  
> fetch join 의 경우에는 Member 엔티티와 Team 모두 영속성 컨텍스트에 등록되기 때문에 join 필터인 on 조건을 걸게 되면 실제 데이터베이스에 저장된 
> Member 테이블과 영속성 컨텍스트에 존재하는 Member 엔티티와 불일치가 일어나게 된다.    
> 왜냐면 데이터베이스의 Member 테이블 TeamId 칼럼 값이 존재하는데 fetch join + on 조건으로 인해서 영속성 컨텍스트로 가져온 Member 엔티티의 멤버 변수인 Team 엔티티가
> null 로 가져오게 되면 차이가 발생되기 때문이다.  
> 
> 대처: fetch join + on 조건은 사용하지 않는다.

---

## Entity 지연 로딩
### 지연 로딩 예제
> Member 객체와 Team 객체가 연관 관계를 가지고 있고 Member 다 대 Team 일의 연관관계인 다대일 연관관계를 가진다.  
> Member 테이블이 Team 의 PK(식별자)를 가지며 Member 객체가 Team 객체를 참조한다.  
> Member 객체에서 참조하는 Team 객체에 지연로딩을 설정하였다면 해당 객체는 프록시 객체 상태로 되어있으며 Member 객체가 참조하는 Team 객체의 멤버 변수(property) 를
> 사용하는 경우에 Team 프록시가 가지고 있는 Team 객체가 초기화된다.  

### ID(식별자)만 사용하는 경우
> Member 객체가 참조하는 Team 객체의 멤버 변수를 자주 사용하는 경우 JPQL 에서 join 을 이용해서 처음부터 Team 객체를 초기화한 상태로 가져오는 경우가 있다.  
> 하지만 Team 객체의 멤버 변수 중 ID(식별자)만 사용하는 경우 Team 객체가 초기화되어 있지 안더라도 사용할 수 있음으로 JPQL 에서 join 을 이용해서 가져올 필요가 없다.  

---

## Entity 지연 쓰기
### 기본 전략
> JPA 의 Entity 는 기본 전략으로 지연 쓰기를 사용한다.   
> 지연 쓰기란 Entity 를 save 할 시에 transaction commit 후 flush 시점에 Entity 를 데이터베이스에 insert 쿼리를 모아서 한꺼번에 insert 시키는 전략이다.

### Auto Increment
> 먼저 알아야할 점은 Entity 가 영속성 컨텍스트에 존재하기 위해서는 해당 Entity 객체가 식별자(PK)를 가지고 있어야한다는 점이다.      
> MySQL, Postgres 등의 DB 에서 사용하는 Auto Increment 를 사용하기 위해서 Entity 에 `@GeneratedValue(strategy = GenerationType.IDENTITY)` 를 사용하는 경우,  
> 해당 Entity 를 영속성 컨텍스트에 저장하기 위해서 save 를 하게 되면 지연 쓰기가 되지 않고 곧바로 insert 쿼리가 실행된다.   
> 위에서 말했듯이 영속성 컨텍스트에 저장하기 위해서는 해당 Entity 가 식별자(PK)를 가지고 있어야하는데 Auto increment 를 사용하는 DB 에서는 식별자를 가지기 위해서는
> insert 쿼리가 실행되어야하기 때문이다.  
> 
> 지연 쓰기를 사용하기 위해서는 Entity 식별자(PK)를 UUID 를 사용하여 Auto increment 가 아닌 애플리케이션 내에서 UUID 를 생성하여 식별자를 직접 지정하여 사용하거나,
> Oracle 과 같은 시퀀스를 사용하는 DB 의 경우에는 시퀀스를 사용한다.(하지만 save 를 통해서 영속성 컨텍스트에 엔티티를 저장하기 위해서는 시퀀스 넘버를 얻어오는 쿼리가 실행된다.)  

---

## Set vs List
### Set
> Java 의 Collection 중 Set 은 중복 항목을 허용하지 않는다. 이미 가지고 있는 항목을 Set 에 add 하면 그 항목은 add 되지 않는다.    
> JPA Entity 의 Set 에서 동일 항목인지 비교는 동일성(PK 비교)가 아닌 동등성(EqualsAndHashCode) 로 비교한다.    

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

## EqualsAndHashCode - Entity
> Entity 에 EqualsAndHashCode 구현시 다음의 3가지 방식이 존재한다.  
> 1.PK 로만 equals() 구현하기  
> 2.PK 를 제외하고 equals() 구현하기  
> 3.비지니스 키를 사용한 동등성 구현하기  

### 1.PK 로만 equals() 구현하기
> 비교하려는 두 객체가 모두 영속성 컨텍스트에 존재해야한다. 영속 객체와 준영속 객체를 비교 시 항상 false 처리되기 때문에 
> 영속 객체를 담은 Set 객체에 준영속 객체를 추가해야하는 상황에서는 적합하지 않다.

### 2.PK 를 제외하고 equals() 구현하기
> 엔티티에서 PK 를 제외한 property 들만 equals() 메서드 구현 시 만약 참조 객체(join 하는 객체)가 있다면 해당 객체는 제외해서 구현해야한다.   
> 준영속 객체와 비교하려는 영속 객체의 property 일부가 변경된 경우가 존재하여 비지니스 적으로는 같아야하는 객체인데 시점에 의해서 다른 객체로 인식할 수 있는 상황이 발생할 수 있음.    

### 3.비지니스 키를 사용한 동등성 구현하기
> 영속 객체를 Set 객체에 준영속 객체를 추가해야하는 상황에서 가장 적합해보임.  
> PK 를 제외하고 엔티티에서 가장 변경이 적으며 대체 식별자로 사용 가능한 Property 만 equals 메서드로 비교.  
> 대체 식별자로 사용이 가능하려면 1.UNIQUE 제약 조건을 가져야하며, 2.변경 횟수가 다소 적은 편에 속하여야한다.   
> 위의 PK 를 제외하고 equals() 구현에서 맹점이 되었던 영속 객체의 property 일부가 변경된 경우에도 동일 객체로 인식할 수 있음.  

### 참조사이트
> [JPA Entity의 equals와 hashCode](https://velog.io/@park2348190/JPA-Entity%EC%9D%98-equals%EC%99%80-hashCode)
> [Jpa Entity 의 Equals, 객체 동일성과 동등성, Lombok 을 써도 될까?](https://blog.yevgnenll.me/posts/jpa-entity-eqauls-and-hashcode-equality)

---

## Repository
### JpaRepository vs CommonRepository
> JpaRepository 의 경우 기본적인 CRUD 관련 메서드가 제공되며, JpaRepository 를 상속받는 Repository 인터페이스는 앞의 이유로 기본적인 CRUD 관련 메서드가 
> 개발자의 뜻과 상관없이 모두 노출(제공)된다.  
> Repository 에 선언한 메서드만 외부로 노출하고 싶은 경우 CommonRepository 를 상속하여 사용한다.  
