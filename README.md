# jpa-starter

## Prepare
### h2 database
> dbname: practice-starter

## application.yml
### spring.jpa.properties.hibernate.enable_lazy_load_no_trans
> 트랜잭션 범위가 끝나면 영속성 컨텍스트가 함께 종료되는 것이 JPA의 기본 전략이다.  
> enable_lazy_load_no_trans 옵션은 OSIV와 비슷하기는 한데, 영속성 컨텍스트가 종료되어도, 새로운 데이터베이스 커넥션을 획득해서 지연로딩을 
> 가능하게 해준다. 이 방법은 여러번 지연로딩이 있으면 그때마다 각각 새로운 데이터베이스 커넥션을 획득하기 때문에 성능상 매우 좋지 않다.
> 해당 기능은 사용하지 않는다.

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

## Service
### Create
> Hard Delete 의 경우 생성자 + repository.save() 메서드로 간단하게 해결된다.  
> 
> Soft Delete 의 경우 생성할 때 받는 인수 중에서 PK 외에 식별이 될만한 인수를 받는 경우 
> Repository 에서 where 절에 delFlag = 'N' 항목을 껴넣지 않고 해당 식별자 항목만 넣어서 select 를 돌린다.
> 예를 들어 SdMember 의 경우 phone 을 통해서 회원 식별이 가능하여 Repository 에 findOneByPhoneWithDeleted() 메서드를 만들었다.
> ```java
> // SdMemberRepository.java
> 
> @Query("select m from SdMember m where m.phone = :phone")
> Optional<SdMember> findOneByPhoneWithDeleted(@Param("phone") String phone);
> ```
> 해당 메서드를 통해서 SdMemberService 에서 map + orElseGet 을 통해서 중복 검사 및 save 를 진행한다.
> ```java
> // SdMemberService.java
> 
> @Override
> public Long createMember(String name, Integer age, String phone) {
>     return memberRepository.findOneByPhoneWithDeleted(phone)
>             .map(member -> {
>                 // 중복 검사
>                 if (!member.isDeleted()) {
>                     throw new ResourceDuplicateException();
>                 }
>                 member.unDelete();
>                 member.update(name, age, phone);
>                 return member.getId();
>             })
>             .orElseGet(() -> {
>                 SdMember newMember = memberRepository.save(new SdMember(name, age, phone));
>                 return newMember.getId();
>             });
> }
> ```

### Delete
> Hard Delete 의 경우 repository.deleteById 를 통해서 간단하게 해결된다.
> 
> Soft Delete 의 경우 repository 를 호출하지 않고 DelFlag 항목을 'N' -> 'Y' 로 바꿔주는 delete() 메서드 호출로
> 변경감지를 통한 update 문만 호출하도록 구현한다.
> ```java
> // SdMemberService.java
>
> @Override
> public void deleteMember(Long memberId) {
>   SdMember member = memberRepository.findOneById(memberId)
>       .orElseThrow(ResourceNotFoundException::new);
>   member.delete();
> } 
> ```
