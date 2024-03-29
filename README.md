# jpa-starter

## Database
### MySQL 8.0.31 - Docker
> 프로젝트 디렉터리의 docker/mysql 이동 후 start-mysql.sh 실행  

---

## 영속성 컨텍스트(Persistent context)
### flush
> flush 는 영속성 컨텍스트의 내용을 데이터베이스에 반영하기 위해서 SQL 쿼리를 날리는 작업이다.(commit 제외)

### 생명 주기
> 영속성 컨텍스트의 생명 주기는 기본적으로 트랜잭션의 시작 시 생성되며 커밋 혹은 롤백 시 삭제된다.
>
> OSIV 설정을 통해서 영속성 컨텍스트의 생명 주기를 Filter/Interceptor 를 시작으로 Controller -> Service -> Repository 를 거쳐서 다시
> Filter/Interceptor 로 돌아오고나서 영속성 컨텐스트를 삭제하는 것도 가능하다.  
> 하지만 OSIV 는 스프링에서 뷰를 제공할 때 고려해볼 수 있는 옵션이며 REST API 와 같이 API 를 제공할 시에는 DTO 객체로 완충지대를 제공하는 것이 나아보인다.

### Batch 처리 시 영속성 컨텍스트 메모리 사용량 최적화
> 일정 수량의 업데이트(예: 20개)를 처리한 후 flush와 clear를 호출하면 다음과 같은 이점이 있다.
> * 메모리 사용량 관리:
> flush 호출로 영속성 컨텍스트의 변경사항을 데이터베이스에 반영한 후, clear를 통해 영속성 컨텍스트를 초기화하면, 더 이상 관리하지 않아도 되는 
> 엔티티에 대한 참조를 제거하여 메모리 사용량을 줄일 수 있다. 이는 특히 대량의 데이터를 처리하는 경우 유용하다.
> 
> * 데이터베이스 부하 관리:
> 각 업데이트마다 데이터베이스에 즉시 반영되지 않고, flush가 호출될 때까지 변경사항이 영속성 컨텍스트에 쌓인다. 
> 따라서, flush를 사용하면 데이터베이스 부하를 효율적으로 관리할 수 있다. 각 업데이트마다 개별적으로 SQL 문을 실행하는 것보다, 
> 묶음으로 처리함으로써 네트워크 및 데이터베이스 리소스 사용을 최적화할 수 있다.
> 
> **영속성 컨텍스트와 메모리 부하**
> * forEach 반복 처리
> 각 업데이트마다 영속성 컨텍스트에 변경사항이 쌓이고, 이러한 변경사항이 많아질수록 영속성 컨텍스트의 메모리 사용량도 증가한다. 
> 데이터가 많을 경우, 이는 메모리 부하로 이어질 수 있으며, 영속성 컨텍스트의 성능에도 영향을 줄 수 있다.
> 
> * 일정 수량마다 flush와 clear 처리
> 반면에 일정 수량(예: 20개)의 업데이트마다 flush를 호출하여 변경사항을 데이터베이스에 반영하고, clear를 통해 영속성 컨텍스트를 초기화하면, 
> 영속성 컨텍스트가 관리해야 하는 엔티티 수를 줄일 수 있다. 이는 메모리 사용량을 줄이고 영속성 컨텍스트의 성능을 향상시킬 수 있다.
> 
> **데이터 양에 따른 영향**  
> 
> * 데이터가 적은 경우
> 데이터 양이 적을 때는 두 방식 사이의 차이가 크게 눈에 띄지 않을 수 있다. 적은 양의 데이터 처리는 영속성 컨텍스트의 메모리 부하에 큰 영향을 주지 않기 때문이다.
> 
> * 데이터가 많은 경우
> 대량의 데이터를 처리해야 할 경우, 일정 수량마다 flush와 clear를 사용하는 방식이 메모리 관리 측면에서 유리하다. 
> 이 방식은 영속성 컨텍스트 내에서 한 번에 관리해야 하는 엔티티의 수를 줄여 메모리 부하를 감소시키고, 전체적인 성능을 향상시킬 수 있기 때문이다.
> 
> **결론**  
> 데이터 양이 많아질수록 영속성 컨텍스트의 메모리 부하와 성능 관리는 중요한 고려 사항이 된다.
> 일정 수량마다 flush와 clear를 사용하는 방식은 영속성 컨텍스트의 메모리 부하를 효과적으로 관리하고, 대량의 데이터 처리 성능을 최적화하는 데 도움이 될 수 있다.
> 따라서, 데이터 처리 요구 사항이 크고 성능 최적화가 필요한 경우, 이 방식을 고려하는 것이 좋다.

### 참조사이트
> [[Spring] Redis에서 RDB로 조회수 동기화하기](https://curiousjinan.tistory.com/entry/redis-to-rdb-data-sync)

---

## N+1 성능 최적화
### N+1: Pagination 혹은 List 조회 시 발생할 수 있는 문제
> JPQL 에서 객체를 조회했을 때 조회한 객체의 연관 관계를 가진 객체를 사용할 때 영속성 컨텍스트에 해당 객체가 없기 때문에 추가로 조회 SQL 쿼리가 데이터베이스로 요청된다.   
> 단건 조회 요청으로 인하여 객체를 1개만 조회한 후 연관 관계를 가진 객체를 사용할 때는 추가 쿼리가 1개가 더 나가게 되어 총 조회 쿼리의 갯수가 2개가 된다. 
> 이때 쿼리가 1개 나간 것과 2개 나간 것은 성능 상 차이가 크지 않다.  
> 하지만 페이지네이션이나 리스트 조회와 같이 한꺼번에 50개 정도의 객체를 조회했다고 가정해보면 해당 객체의 연관 관계를 가진 객체를 사용하기 위해서 50개의 조회 SQL 쿼리가
> 추가로 데이터베이스에 요청되어 총 조회 쿼리의 갯수가 100개가 되어버린다. 한번의 요청으로 인하여 조회 쿼리의 갯수가 100개가 발생한다면 이는 성능상 치명적이 되어버린다.  

### fetch join
> JPA 에서 조회한 객체 내의 연관 관계의 객체를 조회 시 추가로 발생하는 SQL 쿼리를 줄이기 위해서 JPQL 에서 fetch join 을 사용한다.  
> 예시)
> ```java
> @Query("select m from Member m inner join fetch m.orders")
> ...
> ```

### OneToMany fetch join 주의 사항
> fetch join 은 조회하려는 객체 내의 다대일(ManyToOne) 연관 관계 객체인 경우 연관 관계 객체가 몇 개든지 fetch join 을 함께 사용할 수 있다.   
> 예시)
> ```java
> @Query("select m from Member m " +
>        "inner join fetch m.profile " +
>        "inner join fetch m.company " +
>        "inner join fetch m.orders")
> ...
> ```
>
> 하지만 fetch join 중 일대다(OneToMany) 객체가 2개 이상 존재하는 경우(1개인 경우에는 문제가 되지 않는다.) 
> PersistenceBag 에러가 발생한다.  
> fetch join 하는 OneToMany 객체가 2개 이상인 경우에는 QueryDSL 을 사용하여 쿼리를 나눠서 조회하거나 application.yml 에
> `spring.jpa.properties.hibernate.default_batch_fetch_size` 옵션으로 1000 과 같은 값을 설정하여 사용하여 fetch join 없이 
> ManyToOne 객체를 조회하는 경우 조회 쿼리를 줄일 수 있다.

---

## Transaction
### @Transactional
> @Transactional 은 RuntimeException 과 그 자식인 `언체크(Unchecked)` 예외만 롤백한다.  
> 만약 체크 예외가 발생해도 롤백하고 싶다면 @Transactional(rollbackFor = Exception.class) 처럼 롤백할 예외를 지정해야 한다.

### JUnit 에서 사용
> JUnit 테스트 클래스에 @Transactional 애노테이션을 붙인 경우 각 테스트 메서드가 실행 후 테스트가 끝나면 트랜잭션을 강제로 롤백한다.   
> 강제로 롤백 시키기 때문에 영속성 컨텍스트를 flush 하지 않는다. 플러시를 하지 않으므로 플러시 시점에 어떤 SQL 이 실행되는지 콘솔 로그에 남지 않는다.
> 어떤 SQL 이 실행되는지 콘솔을 통해 보고 싶으면 테스트 마지막에 em.flush() 를 강제로 호출하면 된다.

### Query hint
> Repository 메서드에 사용한다. 예시)
> ```java
> @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
> ...
> ```
> Repository 의 메서드 중 조회만을 위해서 사용되는 메서드인 경우 위의 `org.hibernate.readOnly` 쿼리 힌트 애노테이션을 붙여서 성능을 향상 시킬 수 있다.  
> `org.hibernate.readOnly` 를 true 로 설정하면 영속성 컨텍스트에서 변경 감지를 위한 스냅샷 인스턴스를 보관하지 않아 애플리케이션의 메모리 사용이 줄어든다.  
> 다만 해당 쿼리 힌트의 애노테이션을 사용하여 조회한 엔티티는 변경 감지를 위한 스냅샷 인스턴스가 없음으로 변경하고 커밋을 하더라도 데이터베이스에 반영되지 않기 때문에 사용에 조심해야한다.  
> readOnly 를 사용한 메서드의 경우 메서드 명의 뒷쪽에 readOnly 를 붙여주어 이 메서드가 읽기 전용이라는 것을 나타내도록하자.  
> 그렇지 않으면 해당 메서드를 사용하고 엔티티를 수정을 하여 더티 체크가 발생할 것으로 기대할 수 있기 때문이다.  
> 이렇게 버그 발생 확률이 높기 때문에 성능상 이점이 큰지를 따져서 사용하는 것이 바람직하며 왠만하면 사용하지 않는다.(왠만해서는 큰 이점이 없는 듯 하다.)  

### @Transactional(readOnly = true)
> 엔티티를 읽기만 하고 수정이 없이 트랜잭션을 종료하는 Service 의 메서드에 `@Transactional(readOnly = true)` 애노테이션을 붙이면 읽기 전용 트랜잭션으로 동작한다.
> 트랜잭션을 커밋해도 영속성 컨텍스트를 플러시하지 않는다.

### @Transactional(propagation = Propagation.REQUIRED)
> 디폴트 값.  
> 기본적으로 해당 메서드를 호출한 곳에서 별도의 트랜잭션이 설정되어 있지 않았다면 트랜잭셕을 새로 시작한다.  
> 만약, 호출한 곳에서 이미 트랜잭션이 설정되어 있다면 기존의 트랜잭션 내에서 로직을 실행한다.  
> 예외가 발생하면 롤백이 되고 호출한 곳에도 롤백이 전파된다.  

### @Transactional(propagation = Propagation.REQUIRES_NEW)
> 매번 새로운 트랜잭션을 시작한다. (새로운 연결을 생성하고 실행한다.) 만약, 호출한 곳에서 이미 트랜잭션이 설정되어 있다면(기존의 연결이 존재한다면) 
> 기존의 트랜잭션은 메써드가 종료할 때까지 잠시 대기 상태로 두고 자신의 트랜잭션을 실행한다. 
> 새로운 트랜잭션 안에서 예외가 발생해도 호출한 곳에는 롤백이 전파되지 않는다. 즉, 2개의 트랜잭션은 완전히 독립적인 별개로 단위로 작동한다.  

### @Transactional 사용시 주의사항
> @Transactional 은 AOP 프록시를 통해서 동작함으로 같은 클래스의 메서드에서 @Transactional 애노테이션이 붙은 메서드를 호출하게 되면 애노테이션이
> 동작하지 않는다.  
> 
> 트랜잭션이 분리될 것으로 예상하지만, 실제로는 분리되지 않는 코드
>```java
> public class saveService(XxxEntity xxxEntity) {
>     //...
> 
>     @Transactional
>     public void totalSave(XxxEntity xxxEntity) {
>         // save 로직
>         // savePart1(xxxEntity);
>     }
>     
>     @Transactional(propagation = Propagation.REQUIRES_NEW)
>     public void savePart1(XxxEntity xxxEntity) {
>         // savePart1 로직 - xxxEntity 이용
>     }
> }
> ```

### 참조사이트
> [Spring Boot, @Transactional 전파 레벨 정리](https://jsonobject.tistory.com/467)  
> 

---

## EntityListener
### 개요
> EntityListener: @PrePersist, @PreUpdate 와 같은 애노테이션을 붙인 메서드.  
> 리스너 메서드를 구현한 클래스를 자식 클래스에 상속했을 때 자식 클래스에서도 정상 동작한다.  
> 상위 클래스의 리스너 메서드의 동작을 자식 클래스에서 막기 위해서는 자식 클래스에서 클래스 위에 `@ExcludeSuperclassListeners` 애노테이션을 선언한다.

### 종류
> 1.@PostLoad: 엔티티가 영속성 컨텍스트에 조회된 직후 호출  
> 2.@PrePersist: 엔티티가 영속성 컨텍스트에 관리하기 직전에 호출.  
> 3.@PreUpdate: flush 나 commit 을 호출해서 엔티티를 데이터베이스에 수정하기 직전에 호출.  
> 4.@PreRemove: remove() 메서드를 호출해서 엔티티를 영속성 컨텍스트에서 삭제하기 직전에 호출.  
> 5.@PostPersist: flush 나 commit 을 호출해서 엔티티를 데이터베이스에 저장한 직후에 호출.  
> 6.@PostUpdate: flush 나 commit 을 호출해서 엔티티를 데이터베이스에 수정한 직후에 호출.  
> 7.@PostRemove: flush 나 commit 을 호출해서 엔티티를 데이터베이스에 삭제한 직후에 호출.

### 리스너 등록
> 리스너 메서드들만 구현해서 Listener 클래스를 만들어서 엔티티에 해당 리스너를 사용하도록 할 수도 있다.  
> 예를 들면 엔티티의 생명주기에 따른 로깅 또는 생성 시 CreatedDate 에 값 넣기, 업데이트 시 UpdateDate 에 값 넣는 방식으로도 활용할 수 있다.
> ```java
> @Entity
> @EntityListeners(TimeListener.class)
> public class Member {
>   ...
> }
> 
> public class TimeListener {
>   
>     @PrePersist
>     private void prePersist() {
>         ...
>     }
> 
>     // 특정 타입이 확실하면 특정 타입을 받을 수 있다.
>     @PreUpdate
>     private void preUpdate(Object obj) {
>         ...
>     } 
> }
> ```

---

## Converter
### Global 설정
> Converter 클래스에 `@Converter(autoApply = true)` 를 선언하면 따로 @Convert 애노테이션을 지정하지 않아도 모두 적용된다.  
> 예시) 엔티티의 Boolean 타입을 데이터베이스에 'Y' 또는 'N' 으로 저장하기 위한 Converter 클래스를 정의하고 클래스 위에 `@Converter(autoApply = true)` 를
> 선언하면 엔티티의 Boolean 타입 위에 @Convert 애노테이션을 선언하지 않아도 Boolean 타입을 자동으로 'Y', 'N' 으로 저장해준다.
> ```java
> @Converter(autoApply = true)
> public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {
> ...
> }
> ```

---

## Collection
### PersistentBag
> 하이버네이트가 컬렉션을 효율적으로 관리하기 위해 엔티티를 영속 상태로 만들 때 원본 컬렉션을 감싸고 있는 내장 컬렉션을 생성하여 이 내장 컬렉션을 사용하도록 참조를 변경한다.
> 하이버네이트는 이런 특징 때문에 컬렉션을 사용할 때 다음처럼 즉시 초기화해서 사용하는 것을 권장한다.
> ```java
> ...
> @OneToMany
> private final List<Member> members = new ArrayList();
> ... 
> ```
> 하이버네이트에서 Set 의 경우 PersistentSet 을 래퍼클래스로 사용한다.

### Collection, List
> Collection, List 의 경우 중복을 허용해서 저장하기 때문에 .add 메서드 내부에서 어떤 비교도 하지 않고 항상 true 를 반환한다.  
> 같은 엔티티가 있는지 찾거나 삭제할 때는 equals() 메서드를 사용한다.
> ```java
> List<Comment> comments = new ArrayList<>();
> 
> // 단순히 추가만 한다. 결과는 항상 true 이다.
> boolean result = comments.add(data);
> 
> comments.contains(comment); // equals 비교
> comments.remove(comment); // equals 비교
> ```

### Set
> Set 은 중복을 허용하지 않는 컬렉션이다. 하이버네이트는 PersistentSet 을 컬렉션 래퍼로 사용한다. 초기화는 HashSet 으로 한다.
> ```java
> ...
> @OneToMany
> private final Set<Member> members = new HashSet<>();
> ... 
> ```
> HashSet 은 중뵥을 허용하지 않으므로 add() 메서드로 객체를 추가할 때 마다 equals() 메서드로 같은 객체가 있는지 비교한다.
> 같은 객체가 없으면 객체를 추가하고 true 를 반환하고, 같은 객체가 이미 있어서 추가에 실패하면 false 를 반환한다.
> 참고로 HashSet 은 해시 알고리즘을 사용하므로 hashcode() 도 함께 사용해서 비교한다.
> ```java
> Set<Comment> comments = new HashSet<>();
> 
> boolean result = comments.add(data); // hashcode + equals 비교
> comments.contains(comment);   // hashcode + equals() 비교
> comments.remove(comment);     // hashcode + equals() 비교
> ```
> 주의!: Set 은 엔티티를 add(추가)할 때마다 중복된 엔티티가 있는지 비교해야 한다. 따라서 엔티티를 추가할 때 지연 로딩된 컬렉션을 초기화한다.

### OneToMany Join 컬렉션 사용 시 주의 사항
> 엔티티에 조인 배열 사용시 재초기화하면 오류나기 때문에 final 변수로 초기화 후 removeAll 혹은 addAll 과 같은 메서드를 사용해서 변경해야한다.

---

## Entity 지연 로딩
### 지연 로딩 예제
> Member 객체와 Team 객체가 연관 관계를 가지고 있고 Member 다 대 Team 일의 연관관계인 다대일 연관관계를 가진다.  
> Member 테이블이 Team 의 PK(식별자)를 가지며 Member 객체가 Team 객체를 참조한다.  
> Member 객체에서 참조하는 Team 객체에 지연로딩을 설정하였다면 해당 객체는 프록시 객체 상태로 되어있으며 Member 객체가 참조하는 Team 객체의 멤버 변수(property) 를
> 사용하는 경우에 Team 프록시가 가지고 있는 Team 객체가 초기화된다.

### 연관 관계 객체의 ID(식별자)만 사용하는 경우
> Member 객체가 참조하는 Team 객체의 멤버 변수를 자주 사용하는 경우 JPQL 에서 fetch join 을 이용해서 처음부터 Team 객체를 초기화한 상태로 가져오는 경우가 있다.  
> 하지만 Team 객체의 멤버 변수 중 ID(식별자)만 사용하는 경우 Team 객체가 초기화되어 있지 않고 프록시 객체여도 ID 를 get 하여 사용할 수 있으므로 
> JPQL 에서 fetch join 을 이용해서 가져올 필요가 없다.

---

## Entity 지연 쓰기
### 기본 전략
> JPA 의 Entity 는 기본 전략으로 지연 쓰기를 사용한다.   
> 지연 쓰기란 하나의 트랜잭션에서 여러 Entity 를 save 할 시에 바로 insert 쿼리를 날리지 않고, transaction commit 을 하기 전까지 save 한 엔티티 정보를 모아두었다가
> commit 이 발생하여 flush 하는 시점에 모아 놓은 Entity 들의 insert 쿼리를 모아서 한꺼번에 데이터베이스에 insert 시키는 전략이다.

### Auto Increment
> 먼저 알아야할 점은 Entity 가 영속성 컨텍스트에 존재하기 위해서는 해당 Entity 객체가 식별자(PK)를 가지고 있어야한다는 점이다.      
> MySQL, Postgres 등의 DB 에서 사용하는 Auto Increment 를 사용하기 위해서 Entity 에 `@GeneratedValue(strategy = GenerationType.IDENTITY)` 를 사용하는 경우,  
> 해당 Entity 를 영속성 컨텍스트에 저장하기 위해서 save 를 하게 되면 지연 쓰기가 되지 않고 곧바로 insert 쿼리가 실행된다.   
> 위에서 말했듯이 영속성 컨텍스트에 저장하기 위해서는 해당 Entity 가 식별자(PK)를 가지고 있어야하는데 Auto increment 를 사용하는 DB 에서는 식별자를 가지기 위해서는
> insert 쿼리가 실행되어야하기 때문이다.
>
> 지연 쓰기를 사용하기 위해서는 Entity 식별자(PK)를 UUID 를 사용하여 Auto increment 가 아닌 애플리케이션 내에서 UUID 를 생성하여 식별자를 직접 지정하여 사용하거나,
> Oracle 과 같은 시퀀스를 사용하는 DB 의 경우에는 시퀀스를 사용한다.(하지만 save 를 통해서 영속성 컨텍스트에 엔티티를 저장하기 위해서는 시퀀스 넘버를 얻어오는 쿼리가 실행된다.)

### 쓰기 지연 성능 최적화 - spring.jpa.hibernate.jdbc.batch_size
> JPA 사용시 엔티티를 영속화하게 되면 영속성 컨텍스트를 flush 할 때 insert 문이 발생한다.
> 여러개의 엔티티를 영속화하게 되면 flush 하였을 때 여러개의 insert 문이 데이터베이스로 호출된다.  
> 데이터베이스 통신과 같이 네트워크 통신 1번이 내부에서 동작하는 메서드 호출보다 몇만배는 더 큰 비용이 든다.
> 그래서 여러번 나가는 insert 쿼리를 한번에 모아서 데이터베이스에 보내는 옵션이 `spring.jpa.hibernate.jdbc.batch_size` 이다.
>
> 값을 50으로 주면 최대 50개의 insert 문을 모아서 한꺼번에 데이터베이스에 보낸다. 하지만 SQL 배치는 같은 SQL 일 때만 유효하다.
> 중간에 다른 처리가 들어가면 SQL 배치를 다시 시작한다. 예를 들면 다음과 같다.
> ```java
> productRepository.save(new Product());    // 1
> productRepository.save(new Product());    // 2
> productRepository.save(new Product());    // 3
> productRepository.save(new Order());      // 4, 다른 연산
> productRepository.save(new Product());    // 5
> productRepository.save(new Product());    // 6
> ```
> 1,2,3 을 모아서 하나의 SQL 배치로 실행하고 4를 따로 한 번 실행하고 5,6을 모아서 실행하여 총 3번의 SQL 배치를 실행한다.
>
> 주의: `@GeneratedValue(strategy = GenerationType.IDENTITY)` 사용 시 AUTO_INCREMENT 를 사용하기 때문에 쓰기 지연이 발생하지 않고 즉시
> insert SQL 이 데이터베이스에 호출된다.
>
> 참고: 엔티티가 영속 상태가 되려면 식별자가 꼭 필요하다. 그런데 `@GeneratedValue(strategy = GenerationType.IDENTITY)` 를 사용하게 되면
> 데이터베이스의 AUTO_INCREMENT 를 사용하여 식별자를 생성하기 때문에 식별자의 정보를 JPA 에서 알기 위해서는 insert 문을 통해서 데이터베이스와 통신할 수 밖에 없다.

---

## application.yml
### spring.jpa.properties.hibernate.enable_lazy_load_no_trans
> 트랜잭션 범위가 끝나면 영속성 컨텍스트가 함께 종료되는 것이 JPA의 기본 전략이다.  
> enable_lazy_load_no_trans 옵션은 OSIV와 비슷하기는 한데, 영속성 컨텍스트가 종료되어도, 새로운 데이터베이스 커넥션을 획득해서 지연로딩을
> 가능하게 해준다. 이 방법은 여러번 지연로딩이 있으면 그때마다 각각 새로운 데이터베이스 커넥션을 획득하기 때문에 성능상 매우 좋지 않다.
> 해당 기능은 사용하지 않는다.

---

## EqualsAndHashCode - Entity
### Object.equals
> Object 객체의 equals 메서드는 기본적으로 동일 객체 즉 객체주소가 같아야 true 를 반환한다.

### Entity 에 EqualsAndHashCode 구현
> Entity 에 EqualsAndHashCode 구현시 다음의 3가지 방식이 존재한다.  
> 1.PK 로만 equals() 구현하기  
> 2.PK 를 제외하고 equals() 구현하기  
> 3.비즈니스 키를 사용한 동등성 구현하기

### 1.PK 로만 equals() 구현하기
> 비교하려는 두 객체가 모두 영속성 컨텍스트에 존재해야한다. 영속 객체와 준영속 객체를 비교 시 항상 false 처리되기 때문에
> 영속 객체를 담은 Set 객체에 준영속 객체를 추가해야하는 상황에서는 적합하지 않다.

### 2.PK 를 제외하고 equals() 구현하기
> 엔티티에서 PK 를 제외한 property 들만 equals() 메서드 구현 시 만약 참조 객체(join 하는 객체)가 있다면 해당 객체는 제외해서 구현해야한다.   
> 준영속 객체와 비교하려는 영속 객체의 property 일부가 변경된 경우가 존재하여 비즈니스 적으로는 같아야하는 객체인데 시점에 의해서 다른 객체로 인식할 수 있는 상황이 발생할 수 있음.

### 3.비즈니스 키를 사용한 동등성 구현하기
> 영속 객체들을 담은 Set 객체에 준영속 객체를 추가해야하는 상황에서 가장 적합해보임.  
> PK 를 제외하고 엔티티에서 가장 변경이 적으며 대체 식별자로 사용 가능한 Property 만 equals 메서드로 비교.  
> 대체 식별자로 사용이 가능하려면   
> 1.UNIQUE 제약 조건을 가져야하며   
> 2.변경 횟수가 다소 적은 편에 속하여야한다.     
> 위의 PK 를 제외하고 equals() 구현에서 맹점이 되었던 영속 객체의 property 일부가 변경된 경우에도 동일 객체로 인식할 수 있음.
>
> 비즈니스 키는 유일성만 보장되면 가끔 있는 변경 정도는 허용해도 좋다. 따라서 데이터베이스 기본 키 같이 너무 딱딱하게 정하지 않아도 된다.
> 예를 들어 회원 엔티티에 이름과 연락처가 같은 회원이 없다면 회원의 이름과 연락처 정도만 조합해서 사용해도 된다.

### 참조사이트
> [JPA Entity의 equals와 hashCode](https://velog.io/@park2348190/JPA-Entity%EC%9D%98-equals%EC%99%80-hashCode)
> [Jpa Entity 의 Equals, 객체 동일성과 동등성, Lombok 을 써도 될까?](https://blog.yevgnenll.me/posts/jpa-entity-eqauls-and-hashcode-equality)

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
> JpaRepository 는 제네릭 인터페이스로 첫번째 인자는 해당 리포지토리가 접근하는 엔티티 클래스, 두번째 인자는 해당 엔티티의 식별자(PK) 자료형을 입력한다.  
> 예시: Order 엔티티의 데이터베이스 접근을 위한 Repository 인터페이스 생성
> ```java
> public interface OrderRepository extends JpaRepository<Order, Long> {
> }
> ```

### CommonRepository
> JpaRepository 를 상속받은 경우 해당 Repository 는 JpaRepository 에 선언된 모든 메서드를 외부에 제공하게 된다.  
> 하지만 JpaRepository 에 선언된 모든 메서드가 아닌 Repository 에 선언된 메서드만 외부에 제공하고 싶어지는 경우가 있다.  
> 그런 경우 JpaRepository 를 상속받지 말고 Repository 인터페이스를 상속받는 형태로 만든다.  
> 
> CommonRepository.java
> ```java
> public interface OrderRepository extends Repository<Order, Long> {
> }
> ```

### JpaRepository vs Repository
> JpaRepository 의 경우 기본적인 CRUD 관련 메서드가 제공되며, JpaRepository 를 상속받는 Repository 인터페이스는 앞의 이유로 기본적인 CRUD 관련 메서드가
> 개발자의 뜻과 상관없이 모두 노출(제공)된다.  
> 상속받은 Repository 에서 선언한 메서드만 외부로 노출하고 싶은 경우 Repository 를 상속하여 사용한다.  

### 벌크성 쿼리 주의 사항
> JpaRepository 를 상속받는 리포지토리에서 벌크성 데이터 수정/삭제를 위한 쿼리에는 `@Modifying` 애노테이션을 붙이며,
> 해당 애노테이션의 옵션에는 `clearAutomatically = true/false` 및 `flushAutomatically = true/false` 가 있다.  
> 벌크성 쿼리는 JPA Entity LifeCycle 을 무시하고 쿼리가 실행되기 때문 해당 쿼리를 사용할 때는 영속성 컨텍스트 관리에 주의해야 한다.  

### @Modifying - clearAutomatically 
> 이 Attribute는 @Modifying 이 붙은 해당 쿼리 메서드 실행 직 후, 영속성 컨텍스트를 clear 할 것인지를 지정하는 Attribute 이다. default 값은 false.  
> 
> `clearAutomatically=false` 는 해당 벌크 쿼리가 실행된 후 영속성 컨테스트를 그대로 둔다.  
> 벌크 쿼리는 영속성 컨텍스트를 거치지 않고 데이터베이스에 쿼리를 실행하는 형태이기 때문에 벌크 쿼리를 통해서 영속성 컨텍스트가 수정되는 일은 없다.  
> 예를 들어서 Order 엔티티를 조회한 후에 Order 엔티티를 벌크 쿼리를 통해서 status 를 수정을 한 이후 Order 엔티티의 status 를 다시 살펴보자.
> Order 엔티티는 영속성 컨텍스트의 값을 바라보고 있기 때문에 벌크 쿼리를 통해서 업데이트된 status 수정이 반영되지 않는다.
> 
> `clearAutomatically=true` 는 해당 벌크 쿼리가 실행된 후 영속성 컨텍스트를 지워버린다.
> 만약 트랜잭션에서 해당 벌크 쿼리 이후에 벌크 쿼리와 관련된 엔티티와 관련된 연산이 존재하는 경우 해당 엔티티는 다시 조회가 발생하여 변경된 상태가 반영된다.
> 예를 들어서 Order 엔티티를 조회한 후에 Order 엔티티를 벌크 쿼리를 통해서 status 를 수정을 한 이후 Order 엔티티의 status 를 다시 살펴보자.
> Order 엔티티는 영속성 컨텍스트의 값을 바라보고 있는데 연속성 컨텍스트가 지워져버려 DB에서 재조회를 통해서 영속성 컨텍스트의 값을 갱신한다.   
> 그래서 벌크 쿼리를 통해서 업데이트된 status 수정이 반영된 상태이다.   
> 
> 주의할 점은 영속성 컨텍스트 자체를 전부 지워버리는 것이기 때문에 Order 엔티티 벌크 연산 이후 다른 엔티티와 관련된 연산이 존재할 경우에 다른 엔티티까지
> 재조회하도록 동작한다.

### @Modifying - flushAutomatically
> 이 Attribute는 @Query와 @Modifying을 통한 쿼리 메서드를 사용할 때, 해당 쿼리를 실행하기 전, 
> 영속성 컨텍스트의 변경 사항을 DB에 flush 할 것인지를 결정하는 Attribute 이다. Default 값은 clearAutomatically 와 마찬가지로 false.
>
> 예를 들어서 Order 엔티티를 조회한 후에 Order 엔티티를 벌크 쿼리를 통해서 status 를 수정을 한 이후 Order 엔티티의 status 를 다시 살펴보자.  
> 가정 1.Order 엔티티 벌크 쿼리를 수행 전에 Order 의 orderDate 를 변경함.  
> 가정 2.Order 엔티티 벌크 쿼리는 clearAutomatically=true, flushAutomatically=false 로 설정함.  
> Order 엔티티의 벌크 쿼리가 실행 이후 영속성 컨텍스트를 지워버리게 되면 벌크 쿼리를 실행하기 전에 수행했던 orderDate 의 변경분이 영속성 컨텍스트가 
> 지워짐으로써 같이 날라가게된다. 즉 벌크 연산 이전의 연산이 DB 에 반영되지 않고 날아가버릴 수 있게된다.  
> 
> 하지만 JPA 의 구현체인 Hibernate 의 FlushModeType 이 AUTO 로 설정된 경우 flushAutomatically 의 설정과 관계없이 무조건 벌크 연산 실행 전에
> 영속성 컨텍스트를 flush 하여 변경분이 DB 에 반영되도록 해준다.   
> 일반적으로 Hibernate 의 FlushModeType 를 AUTO 외에 COMMIT 으로는 잘 사용하지 않기 때문에 사실상 flushAutomatically 는 설정해주지 않아도 `true` 로
> 설정된 것과 같다.  
> 하지만 Hibernate 의 정책이 변경될 수 있기 때문에 명시적으로 설정해주고 싶으면 하도록한다.

### 참조사이트
> [Spring Data JPA @Modifying (1) - clearAutomatically](https://devhyogeon.tistory.com/4)  
> [Spring Data JPA @Modifying (2) - flushAutomatically](https://devhyogeon.tistory.com/5)

---

## 스프링 프레임워크의 JPA 예외 변환
### PersistenceExceptionTranslationPostProcessor
> JPA 예외를 스프링 변환 예외로 변경시켜서 발생시키기 위해서는 `PersistenceExceptionTranslationPostProcessor` 를 스프링 Bean 으로 등록해야한다.
> ```java
> @Bean
> public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
>     return new PersistenceExceptionTranslationPostProcessor();
> }
> ```
> @Repository 애노테이션을 붙인 클래스와 JpaRepository 를 상속받은 인터페이스의 메서드에서 예외 발생 시 스프링 변환 예외가 발생한다.

### SpringBoot 2.0 + Spring Data JPA
> SpringBoot 2.0 이상의 버전과 Spring Data JPA 를 사용하면 JPA 예외를 스프링 변환 예외로 변경시키기 위해서 위의 `PersistenceExceptionTranslationPostProcessor` 를
> 스프링 빈으로 등록할 필요없이 자동으로 등록되며, @Repository 애노테이션을 붙인 클래스, JpaRepository 를 상속받은 인터페이스 및 Repository 를 
> 상속받은 인터페이스까지 모두 스프링 변환 예외가 적용된다.

### 메서드 옆에 throws 붙이기
> 메서드 옆에 throws 로 JPA 예외를 던지도록 명시하면 스프링 예외로 변환하지 않고 throws 선언한 예외로 예외를 발생시킨다.    
> 주의점은 메서드 옆에 throws Exception 을 선언하게되는 경우인데 `java.lang.Exception` 을 throws 선언하면 스프링 예외로 변환하지 않고 본래 예외를 발생시킨다.

---

## JPQL
### Collection fetch join  
> JPQL 을 통한 조회 시 둘 이상의 컬렉션을 fetch join 할 수 없다.  
> 둘 이상의 컬렉션을 fetch join 한 JPQL 을 실행하면 다음의 예외가 발생한다: 
> `PersistenceException: org.hibernate.loader.MultipleBagFetchExcetpion: cannot simultaneously fetch multiple bags`  
> 
> 컬렉션을 fetch join 시 (OneToMany 객체 참조) 페이지네이션 API 를 사용할 수 없다.  
> JPA 구현체로 Hibernate 를 사용하는 경우 경고 로그를 남기면서 메모리에서 페이지네이션 처리가 되지만, 데이터가 많을 시 성능 이슈 및 메모리 초과 예외가 발생할 수 있다.  

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
> fetch join 의 경우에는 on 조건으로 인하여 join 문의 오른쪽 테이블에서 필터가 발생하면 안된다. 
> 이경우 on 조건이 아닌 where 조건을 사용하도록 한다.

---

## JPA lock
### JPA 의 특징
> 데이터베이스는 트랜잭션 도중에 조회 시에는 read lock 을 삽입/수정/삭제 시에는 write lock 을 row 에 걸도록 동작한다.  
> 하지만 JPA 의 경우에는 변경 감지를 통한 update 및 remove 를 통한 삭제 시에 실시간으로 데이터베이스에 쿼리가 가는 것이 아닌 영속성 컨텍스트가 flush 되어야
> 해당 쿼리들이 데이터베이스로 호출되기 때문에 lock 이 걸리는 시간이 최소화된다.  
> lock 의 주요 목적은 Repeatable read 를 통한 일관된 읽기와 Lost update 방지에 있다.  
> MySQL 의 InnoDB + MVCC 경우 Repeatable read 를 기본으로 지원하며 Lost update 를 방지하기 위해서 read-lock 및 write-lock 을 SQL 쿼리를 통해서
> 방지할 수 있다.  
> PostgreSQL 의 MVCC 의 경우 따로 lock 없이 Repeatable read 및 Lost update 를 방지하기 때문에 만약 DB 를 PostgreSQL 사용 시 아래 애플리케이션 단의
> lock 을 걸 필요는 없다. 다만 차후 DBMS 변경을 고려해서 PostgreSQL 을 사용하지만 미리 애플리케이션 단에서 lock 을 걸어두는 것도 방법이겠다.

### Optimistic Lock
> 낙관적 락은 이름 그대로 트랜잭션 대부분은 충돌이 발생하지 않는다고 낙관적으로 가정하는 방법이다.
> 이것은 데이터베이스의 락 기능을 사용하는 것이 아니라 JPA 가 제공하는 버전 관리 기능을 사용한다.
> 데이터베이스가 아닌 애플리케이션에서 제공하는 락이다.  
> 낙관적 락은 트랜잭션을 커밋하기 전까지는 트랜잭션의 충돌을 알 수 없다는 특징이 있다.

### Pessimistic Lock
> 비관적 락은 이름 그대로 트랜잭션 충돌이 발생한다고 가정하고 우선 락을 걸고 보는 방법이다. 이것은 데이터베이스가 제공하는 락 기능을 사용한다.
> `select for update` 구문 사용으로 다른 트랜잭션에서는 해당 row 에 read-lock 및 write-lock 를 모두 걸 수 없도록 write-lock 을 건다.  
> 
> **예시**  
> `select for update` 구문을 사용하여 조회하는 경우 동일한 레코드에 대해서 동시에 2개 이상의 트랜잭션이 읽기를 요청하는 경우 먼저 선점한 트랜잭션의 트랜잭션이
> 끝날 때 까지 다른 트랜잭션은 대기 상태가 된다. 그렇기 때문에 출금 및 송금, 문자 메시지의 안읽음 카운트 줄이기, 조회수 카운트 증가와 같은 경우 사용하는 것이 좋다.

### @Version
> JPA 가 제공하는 낙관적 락을 사용하려면 엔티티에 멤버변수를 추가하고 그 위에 @Version 애노테이션을 사용해서 버전 관리 기능을 추가해야 한다.  
> @Version 애노테이션을 지원하는 자료형은 Long, Integer, Short, Timestamp 이며 모두 Timestamp 제외 윈시 자료형도 가능하다.
>
> 해당 엔티티를 수정 후 커밋하면 @Version 멤버 변수의 숫자가 1씩 자동으로 증가한다.   
> 엔티티를 수정할 때 조회 시점의 버전과 수정 시점의 버전이 다르면 예외가 발생한다. 따라서 @Version 애노테이션 사용하면 여러 트랜잭션 커밋 중 최초 수정 커밋만 적용이 된다.
>
> 임베디드 타입 수정 시에도 version 이 증가한다. 단 연관관계 필드는 @ManyToOne 에서 연관관계 객체 자체가 변해야(외래키가 변해야) version 이 증가한다.  
> 벌크 연산은 영속성 컨텍스트와는 관계가 없음으로 version 이 증가하지 않으며 version 을 증가시키려면 SQL 에 version 을 증가시키도록 명시해야한다.

### @Lock
> Repository 의 메서드에 @Lock 애노테이션을 달아 락 옵션을 설정할 수 있다. 락 옵션은 낙관적 락 뿐 아니라 비관적 락 옵션 역시 설정할 수 있다.
> 락 옵션은 다음과 같다.  
> None: 락 옵션 없이 엔티티에 @Version 멤버 변수만 있어도 낙관적 락이 적용된다. 조회한 엔티티를 변경(수정 및 제거)할 경우에만 version 을 확인한다.  
> 그래서 커밋시 update 및 delete 문에 version 을 확인하는 쿼리가 추가되도록 동작한다.
>
> @Lock(LockModeType.OPTIMISTIC): 조회한 엔티티를 변경 외에 조회 시에도 version 을 확인한다.   
> 조회한 엔티티의 변경이 없이 커밋 시에도 추가 select 문을 통해서 조회한 엔티티의 version 정보를 확인하여 다른 트랜잭션으로 인한 변경 시 예외를 발생시킨다.  
> read-lock 을 애플리케이션 레벨에서 구현한 것이다.
>
> @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT): 조회만 해도 version 정보를 증가시키며 변경(수정/삭제) 시 추가로 version 정보를 증가한다.  
> 따라서 조회만 했을 시에도 version 이 한번 증가하고 변경(수정/삭제) 까지 하면 추가로 version 이 증가하여 2번 증가한다.  
> 일대다 다대일 관계에서 한 엔티티가 묶음으로 가지고 있는 엔티티를 추가로 가질 시에도 version 정보를 증가시켜 동시성 충돌을 예방한다.  
> write-lock 을 애플리케이션 레벨에서 구현한 것이다.
>
> @Lock(LockModeType.PESSIMISTIC_WRITE): 비관적 락을 거는 것으로 write-lock 을 DB 단에 거는 것이다.  
> lock 을 JPA 단에서 거는 것이 아닌 DB 단에서 거는 것이기 때문에 엔티티 조회 뿐만 아니라 스칼라(컬럼 일부만) 조회할 시에도 lock 이 동작한다.
>
> @Lock(LockModeType.PESSIMISTIC_READ): 비관적 락을 거는 것으로 read-lock 을 DB 단에 거는 것이다.  
> 일반적으로 잘 사용하지 않으며 데이터베이스 대부분은 방언에 의해 PESSIMISTIC_WRITE 로 동작한다.
>
> @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT): 비관적 락을 사용함과 동시에 낙관적 락 처럼 @Version 필드도 사용한다.  
> 조회만 했을 시에도 version 이 한번 증가하고 변경(수정/삭제) 까지 하면 추가로 version 이 증가하여 2번 증가한다.  
> `nowait` 를 지원하는 DB 에 대해서 for update nowait 옵션을 적용한다. PostgreSQL 및 MySQL 8.0 이상 버전에서 `for update nowait` 옵션이 적용된다.
>
> for update nowait: DB 의 row 에 한 트랜잭션에 write-lock(exclusive lock) 이 걸려있으면 다른 트랜잭션에서 해당 row 에 접근을 위해서
> write-lock 을 요청하면 이전 트랜잭션의 write-lock 이 unlock 될 때 까지 기다리는게 기본 동작이다.  
> `nowait` 옵션은 쿼리 실행 후 읽으려는 row 에 lock 걸려있으면 바로 트랜잭션 실패 처리를 한다.
>
> 비관적 락 사용시 교착 상태에 빠질 수 있음으로 락 타임아웃을 설정하는 것이 좋다.  
> 비관적 락 타임아웃은 각 DB 마다 해당 설정이 존재한다. (MySQL 에서는 innodb_lock_wait_timeout 이다)  
> @Repository 의 메서드에 `@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})` 를 통해서 설정할 수 있다.

### 참조사이트
> https://isntyet.github.io/jpa/JPA-%EB%B9%84%EA%B4%80%EC%A0%81-%EC%9E%A0%EA%B8%88(Pessimistic-Lock)/
> https://reiphiel.tistory.com/entry/understanding-jpa-lock
> https://sabarada.tistory.com/187
> https://stir.tistory.com/251
> https://kimdubi.github.io/mysql/skip_locked/

---

## 엔티티 그래프
### JPQL -> EntityGraph
> 기존에 엔티티 뿐만 아니라 해당 엔티티에 연관된 엔티티까지 함께 조회하고 싶은 경우에 JPQL 의 `join fetch` 를 사용하여 함께 조회하였다.    
> 만약 엔티티의 ID 를 통해서 조회하며 연관된 엔티티가 적을 경우에는 엔티티 그래프를 이용해서 조회하는 것도 코드를 줄이는데 도움이 된다.
>
> 예를 들어 User 를 ID 로 조회하는데 User 에 연관된 엔티티인 addresses 엔티티도 함께 조회를 한다고 가정해보자.
> ```java
> ...
> 
> @EntityGraph(attributePaths = {"addresses"}, type = EntityGraph.EntityGraphType.LOAD)
> Optional<User> findWithAddressesById(Long userId);
> ```
>
> EntityGraph.EntityGraphType 에는 `LOAD` 와 `FETCH` 가 있다.  
> FETCH: entity graph에 명시한 attribute는 EAGER로 패치하고, 나머지 attribute는 LAZY로 패치  
> LOAD: entity graph에 명시한 attribute는 EAGER로 패치하고, 나머지 attribute는 entity에 명시한 fetch type이나 디폴트 FetchType으로 패치

### 주의
> EntityGraph 로 함께 조회하는 엔티티는 모두 `outer join` 이어서 `inner join` 위주로 조회시에는 JPQL 을 사용하여야한다.

---

## 2차 캐시
### 개요
> 애플리케이션에서 공유하는 JPA 캐시를 공유 캐시(shared cache)라 하는데 일반적으로 2차 캐시(second level cache, L2 cache) 라 부른다.  
> 2차 캐시는 애플리케이션 범위의 캐시이며 애플리케이션을 종료할 때까지 캐시가 유지된다.  
> 2차 캐시는 1차 캐시와 데이터베이스 사이에 위치하는 캐시로 1차 캐시에서 데이터베이스에 쿼리를 요청하기 전에 2차 캐시에서 먼저 엔티티를 찾는 방식으로 동작한다.    
> 엔티티 클래스에 `@Cacheable` 애노테이션을 기재하면 해당 엔티티는 2차 캐시를 사용한다고 지정할 수 있다.  

### 주의
> JPA 책의 저자도 JPA 의 2차 캐시보다는 스프링 캐시를 더 추천하고 있음으로 2차 캐시에 존재만 알고 넘어가도록 하자.

### 참조사이트
> [강의에는 없는 내용이지만.. cache 관련해서 질문이 있습니다.](https://www.inflearn.com/questions/33629/%EA%B0%95%EC%9D%98%EC%97%90%EB%8A%94-%EC%97%86%EB%8A%94-%EB%82%B4%EC%9A%A9%EC%9D%B4%EC%A7%80%EB%A7%8C-cache-%EA%B4%80%EB%A0%A8%ED%95%B4%EC%84%9C-%EC%A7%88%EB%AC%B8%EC%9D%B4-%EC%9E%88%EC%8A%B5%EB%8B%88%EB%8B%A4)

---

## JSON 저장
> RDB 에 JSON 자료형 저장을 위한 JPA 활용을 정리한다.

### Converter
> 제네릭 객체를 JSON 으로 변환하여 저장해줄 Converter 를 만든다.
> 
> JsonConverter.java
> ```java
> @Converter
> public class JsonConverter<T> implements AttributeConverter<T, String> {
> 
>     private final ObjectMapper objectMapper;
> 
>     public JsonConverter() {
>         objectMapper = new ObjectMapper();
>     }
> 
>     @Override
>     public String convertToDatabaseColumn(T attribute) {
>         if (attribute == null) {
>             return null;
>         }
>         try {
>             return objectMapper.writeValueAsString(attribute);
>         } catch (Exception e) {
>             throw new RuntimeException(e);
>         }
>     }
> 
>     @Override
>     public T convertToEntityAttribute(String dbData) {
>         if (dbData != null && !dbData.isBlank()) {
>             Class<?> aClass =
>                     GenericTypeResolver.resolveTypeArgument(getClass(), JsonConverter.class);
>             try {
>                 return (T) objectMapper.readValue(dbData, aClass);
>             } catch (Exception e) {
>                 throw new RuntimeException(e);
>             }
>         }
>         return null;
>     }
> }
> ```
> 
> 위의 JsonConverter 를 상속받아 DataType1 객체를 JSON 으로 변환할 Converter 를 만든다.  
> DataType1ToJsonConverter.java
> ```java
> @Converter(autoApply = true)
> public class DataType1ToJsonConverter extends JsonConverter<DataType1> {
> }
> ```

### 엔티티
> @Column 애노테이션에 `columnDefinition = "json"` 설정한다.
> ```java
> @Column(name = "data_type", columnDefinition = "json")
> private DataType1 dataType;
> ```

---

## 테스트
### @DataJpaTest
> * `@DataJpaTest` : Spring 에서 JPA 관련 테스트 설정만 로드한다. DataSource 의 설정이 정상적인지, 
> 제대로 생성 수정 삭제 조회 하는지 등의 테스트가 가능하다.
> 
> * @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE):
> @DataJpaTest 를 사용하면 자동으로 EmbededDatabase 를 사용하기 때문에 내가 설정한 설정값들을 사용할 수 없다.  
> 이 설정을 replace 해서 해당 설정이 동작하지 않고, 내가 설정한 설정파일대로 만들어진 DataSoruce 가 Bean 으로 등록된다.  
> yml 이나 properties 에서 내가 설정한 설정파일대로 사용하려면 (내가 설정한 H2, MySQL,Oracle 등) NONE 옵션을 사용해서 사용해야 한다.    
>
> 예시
> ```java
> @DataJpaTest
> @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
> class MemberRepositoryTest {
>     @Autowired
>     MemberRepository memberRepository;
>     
>     @Test 
>     void save() { 
>         memberRepository.save(
>                Member.builder()
>                       .age(1)
>                       .email("email@email.com")
>                       .name("name")
>                       .build()
>         );
>     }
> }
> ```

### 참조사이트
> [Spring Boot JPA Test @DataJpaTest 기본 설정](https://0soo.tistory.com/40)

---

## 커서 기반 페이지네이션
### 오프셋 기반(Offset-based Pagination)
> ```sql
> select *
> from post
> order by create_at desc
> limit 10, 20;
> ```
> MySQL 기준으로 offset, limit 을 사용하여 쿼리를 한다. 직관적이고 구현도 간단하지만, 치명적인 문제점이 있다.
> 
> **문제점**
> * 데이터 중복 문제: 문제1. 각각의 페이지를 요청하는 사이에 데이터의 변화가 있는 경우 중복 데이터 노출
> 예를 들어, 1페이지에서 20개의 row를 불러와서 유저에게 1페이지를 띄워줬습니다. 고객이 1페이지의 상품들을 열심히 보고 있는 사이, 항상 열심히 일하고 있는 상품 운영팀에서 5개의 상품을 새로 올렸네요? 유저는 1페이지 상품들을 다 둘러보고 2페이지를 눌렀어요. 그럼 어떻게 될까요?  
> 유저는 이미 1페이지에 보았던 상품 20개중 마지막 5개를 다시 2페이지에서 만나게 됩니다.  
> 가끔 유저들의 활동이 활발한 커뮤니티에서 게시글을 쭉 읽다보면 이런걸 경험한 적이 있으실거에요. 그럼 '아, 이 사이트는 커서 기반 페이지네이션'이 구현되지 않았구나' 라고 생각하시면 됩니다.  
> 
> * 성능 저하 문제
> ```sql
> select *
> from post
> order by create_at desc
> limit 10
> offset 100000000;
> ```
> offset 값이 클 때 문제가 생긴다. 이유는 앞에 있는 모든 데이터를 읽어야 하기 때문이다.
> 그래서 위 쿼리는 앞에 1억개의 데이터를 읽고, 그 다음 10개 데이터를 읽어서 응답한다.
> 즉 뒤로 갈수록 읽어야 하는 데이터가 많아져 점점 느려진다.
> (성능 저하 문제는 둘째 치더라도, 데이터 중복문제는 해결할 수 없다. Cursor-based Pagination을 사용하면 위 문제점들을 모두 해결할 수 있다.)

### 커서 기반(Cursor-based Pagination)
> Cursor 개념을 사용한다. Cursor란 사용자에게 응답해준 마지막의 데이터의 식별자 값이 Cursor가 된다.
> 해당 Cursor를 기준으로 다음 n개의 데이터를 응답해주는 방식이다.
> 
> * 오프셋 기반 방식: 1억번~1억+10번 데이터 주세요. 라고 한다면 → 1억+10번개의 데이터를 읽음
> * 커서 기반 방식: 마지막으로 읽은 데이터(1억번)의 다음 데이터(1억+1번) 부터 10개의 데이터 주세요 → 10개의 데이터만 읽음
> 
> Cursor 기반 페이지네이션을 구현할 때 Cursor 중 하나는 반드시 유니크한 값을 가져야 한다.  
> 
> 커서 기반 페이징을 사용하여 schedules 테이블에서 데이터를 조회하는 쿼리를 작성할 때, 
> last_updated_time 컬럼을 기준으로 정렬하고, 마지막 조회된 last_updated_time 값을 기준으로 다음 페이지의 데이터를 불러옵니다. 
> 다음은 이러한 방식으로 커서 기반 페이징을 구현하는 SQL 쿼리의 예입니다.
> 
> **첫 페이지 데이터 조회하기**  
> ```sql
> Copy code
> -- 첫 번째 페이지 데이터 조회
> SELECT * FROM schedules
> ORDER BY last_updated_time DESC
> LIMIT 페이지당항목수;
> ```
> 이 쿼리는 schedules 테이블에서 last_updated_time을 기준으로 내림차순 정렬하여 처음부터 지정된 개수만큼의 데이터를 조회합니다.
> 
> **이후 페이지 데이터 조회하기**  
> ```sql
> -- 이후 페이지 데이터 조회 (마지막 조회된 last_updated_time을 '마지막업데이트시간'으로 사용)
> SELECT * FROM schedules
> WHERE last_updated_time < '마지막업데이트시간'
> ORDER BY last_updated_time DESC
> LIMIT 페이지당항목수;
> ```
> 이 쿼리는 이전 페이지의 마지막 데이터의 last_updated_time을 기준으로 삼아, 그 시간보다 이전에 업데이트된 데이터를 last_updated_time 내림차순으로 정렬하여 조회합니다.
> 이 방법은 대규모 데이터셋에서 OFFSET을 사용할 때 발생할 수 있는 성능 저하 문제를 해결할 수 있으며, 
> 페이징 처리를 효율적으로 수행할 수 있게 합니다. 커서 기반 페이징은 특히 실시간으로 업데이트되는 데이터셋이나, 매우 큰 데이터셋에서 유용하게 사용됩니다.
> 
> 여기서 cursor 가 되는 last_updated_time 의 경우 DATETIME 을 사용할 때 밀리초를 사용하지 않고 초까지만 사용하는 경우 Cursor 값이 유니크하지 않을
> 경우가 생길 수 있다. 해당 경우를 방지하기 위해서 밀리초 3자리까지 안전하게 사용하는 것도 방법이 될 수 있겠다. 밀리초 3자리까지 하면 사람이 생성하는
> 데이터의 경우에는 안전하다고 생각한다. 기계가 생성하는 데이터는 6자리는 되야 안전하다고 생각한다.

### 추가 의견
> 커서 기반 페이지네이션의 경우 커서로 지정한 칼럼의 값이 유니크하지 않은 경우 id(PK) 값과 함께 where 조건을 사용하거나 혹은 id + 커서 칼럼 으로 가공한
> 커서 값을 만들어서 사용하는 경우가 있었다.
> 
> 다만 주의해야할 점은 커서 기반 페이지네이션의 경우 다양한 칼럼에 대한 정렬 기능을 주기가 어렵다는 점이다. 다양한 칼럼에 대한 정렬 기능을 대처하려면
> 그에 맞는 커서 기반 페이지네이션 쿼리들을 준비해야하며, 그에 맞는 인덱스들이 필요하게 된다. 이는 해당 테이블에 대한 쓰기 성능을 크게 저하할 수 있기 때문에
> 커서 기반 페이지네이션을 사용할 경우 다양한 정렬 기능은 주지 않는 것이 좋아보인다. 또한 요즘 서비스들을 보면 다양한 정렬 기능을 주고 있지 않다.
> 
> 그래서 페이지네이션하려는 테이블의 값을 사람이 만들어내고 정렬은 DATETIME 값으로 하게 된다면 해당 칼럼의 타입을 DATETIME(3) 으로 주거나 
> DATETIME(6) 으로 줘서 굳이 id 와 묶어서 처리하지 않고 DATETIME 값으로만 처리하는 것이 성능상 좋아보인다.

### 참조사이트
> [Cursor based Pagination(커서 기반 페이지네이션)이란? - Querydsl로 무한스크롤 구현하기](https://velog.io/@znftm97/%EC%BB%A4%EC%84%9C-%EA%B8%B0%EB%B0%98-%ED%8E%98%EC%9D%B4%EC%A7%80%EB%84%A4%EC%9D%B4%EC%85%98Cursor-based-Pagination%EC%9D%B4%EB%9E%80-Querydsl%EB%A1%9C-%EA%B5%AC%ED%98%84%EA%B9%8C%EC%A7%80-so3v8mi2)  
> [커서 기반 페이지네이션 적용기](https://giron.tistory.com/131)

---
