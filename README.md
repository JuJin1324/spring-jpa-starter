# jpa-starter

## EqualsAndHashCode
### Entity
> Entity 에 @EqualsAndHashcode 적용 시 기존 Hibernate 의 구현에 따라 @Id 를 비교하도록 되어있음.  
> Schedule 에서 Member 정보를 받아 Schedule 의 종속 테이블인 ScheduleMember 에 추가 시 중복을 없애기 위해서  
> Set + @EqualsAndHashcode 를 고려했었음.  
> 하지만 추가 로직에서는 ScheduleMember 의 생성자 호출 방식으로 추가하기 때문에 @Id 가 없어서 중복 제거가 안됨.  
> 즉 중복 제거를 위해서 Set + @EqualsAndHashcode 를 사용할 수 없음.

### 복합키
> @Id 는 동일성 / 동등성 비교를 위해서 @EqualsAndHashcode 구현이 필요함.

## Repository
### JpaRepository vs CommonRepository
> 일반적인 Hard Delete 를 사용하게되면 Repository 를 구현시 JpaRepository 를 상속받아 구현할 수 있다. 
> 하지만 Soft Delete 시 JpaRepository 에서 기본 제공하는 findXXX 메서드를 그대로 사용하면 delFlag 를 필터할 수 없음으로 
> Repository 인터페이스를 상속한 CommonRepository 를 만들고 Repository 가 CommonRepository 상속하도록 하며 
> Repository 에 선언한 메서드들만 외부로 노출한다.
>
> Hard Delete 를 사용하는 경우에도 Repository 에 선언한 메서드만 외부로 노출하고 싶은 경우 CommonRepository 를 상속하여 사용한다.
