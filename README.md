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


