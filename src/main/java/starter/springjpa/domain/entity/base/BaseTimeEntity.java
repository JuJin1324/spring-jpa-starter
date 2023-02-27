package starter.springjpa.domain.entity.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/22
 */
@MappedSuperclass
@Getter
@Setter(AccessLevel.PROTECTED)

public abstract class BaseTimeEntity {
    @Column(name = "updated_time")
    private ZonedDateTime updatedTimeUTC;

    @PrePersist
    public void prePersist() {
        updatedTimeUTC = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    @PreUpdate
    public void preUpdate() {
        updatedTimeUTC = ZonedDateTime.now(ZoneId.of("UTC"));
    }
}
