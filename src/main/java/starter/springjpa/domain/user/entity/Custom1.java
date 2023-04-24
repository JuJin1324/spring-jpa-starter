package starter.springjpa.domain.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2023/04/24
 * Copyright (C) 2023, Centum Factorial all rights reserved.
 */

@Entity
@DiscriminatorValue("CUSTOM1")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Custom1 extends AbstractCustomData {

    @Column(name = "data_type", columnDefinition = "json")
    private DataType1 dataType;

    public Custom1(DataType1 dataType) {
        this.dataType = dataType;
    }
}
