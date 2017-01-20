package org.eientei.discord;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Alexander Tumin on 2016-11-23.
 */
@Repository
public interface MessageAdminRepository extends CrudRepository<Message, Long> {
    Message findFirstByMidOrderByTimeDesc(String mid);
}
