package practice.jpastarter.models;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/09/01
 *
 * createdDate 혹은 lastModifiedDate 혹은 CreatedBy/LastModifiedBy 가
 * Service 레이어에서 비지니스 로직에 관여하는 부분이 있다면
 * 현재 Entity 를 상속 받는 Entity는 POJO 테스트가 불가능해짐으로
 * JpaBaseEntity 에서 @PrePersist / @PreUpdate 방식으로 사용하여야 한다.
 *
 * CreatedDate/LastModifiedDate/CreatedBy/LastModifiedBy 애노테이션은 비지니스 로직에 해당 칼럼들이 안들어갈 경우에만 사용한다.
 */

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
