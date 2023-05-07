package starter.springjpa.domain.user.entity;

import javax.persistence.Converter;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2023/04/24
 * Copyright (C) 2023, Centum Factorial all rights reserved.
 */
@Converter(autoApply = true)
public class DataType2ToJsonConverter extends JsonConverter<DataType2> {
}
