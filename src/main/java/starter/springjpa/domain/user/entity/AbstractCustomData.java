package starter.springjpa.domain.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2023/04/24
 * Copyright (C) 2023, Centum Factorial all rights reserved.
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "custom_data")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AbstractCustomData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
