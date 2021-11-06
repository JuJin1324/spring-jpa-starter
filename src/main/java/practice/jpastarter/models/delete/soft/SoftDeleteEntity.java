package practice.jpastarter.models.delete.soft;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import practice.jpastarter.models.BaseEntity;
import practice.jpastarter.models.YnFlag;

import javax.persistence.*;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class SoftDeleteEntity extends BaseEntity {
    @Column(name = "DEL_FLAG", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private YnFlag delFlag;

    @PrePersist
    public void prePersist() {
        this.delFlag = YnFlag.N;
    }

    public void delete() {
        this.delFlag = YnFlag.Y;
    }

    public void unDelete() {
        this.delFlag = YnFlag.N;
    }

    public boolean isDeleted() {
        return YnFlag.Y.equals(this.delFlag);
    }
}
