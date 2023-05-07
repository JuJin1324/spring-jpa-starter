package starter.springjpa.domain.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import starter.springjpa.domain.user.entity.Custom1;
import starter.springjpa.domain.user.entity.Custom2;
import starter.springjpa.domain.user.entity.DataType1;
import starter.springjpa.domain.user.entity.DataType2;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2023/04/24
 * Copyright (C) 2023, Centum Factorial all rights reserved.
 */

@SpringBootTest
@Transactional
class CustomDataRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    CustomDataRepository customDataRepository;

    @Test
    void name() {
        Custom1 nick1 = new Custom1(new DataType1("nick1", 22));
        customDataRepository.save(nick1);

        Custom2 custom2 = new Custom2(new DataType2("nick2", 33, "col1"));
        customDataRepository.save(custom2);

        em.flush();
        em.clear();
    }
}
