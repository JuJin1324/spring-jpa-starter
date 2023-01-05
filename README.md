# jpa-starter

## Database
### MySQL 8.0.31 - Docker
> 프로젝트 디렉터리의 docker/mysql 이동 후 start-mysql.sh 실행  

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

---
