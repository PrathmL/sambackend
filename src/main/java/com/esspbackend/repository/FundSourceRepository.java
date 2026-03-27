package com.esspbackend.repository;

import com.esspbackend.entity.FundSource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FundSourceRepository extends JpaRepository<FundSource, Long> {
    List<FundSource> findByWorkId(Long workId);
}
