package practice.jpastarter.repositories.delete;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/07
 */
@NoRepositoryBean
public interface CommonRepository<T, ID extends Serializable> extends Repository<T, ID> {
//public interface CommonRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
}
