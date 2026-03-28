package com.esspbackend.repository;

import com.esspbackend.entity.PunchListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PunchListItemRepository extends JpaRepository<PunchListItem, Long> {
    List<PunchListItem> findByWorkId(Long workId);
    List<PunchListItem> findByWorkIdAndStatus(Long workId, String status);
}
