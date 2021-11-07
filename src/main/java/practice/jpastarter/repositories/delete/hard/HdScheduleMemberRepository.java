package practice.jpastarter.repositories.delete.hard;

import practice.jpastarter.models.delete.hard.HdScheduleMember;
import practice.jpastarter.repositories.delete.CommonRepository;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/07
 */
public interface HdScheduleMemberRepository extends CommonRepository<HdScheduleMember, Long> {
    void delete(HdScheduleMember entity);
}
