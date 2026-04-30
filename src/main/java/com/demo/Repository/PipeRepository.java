package com.demo.Repository;

import com.demo.Model.Pipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipeRepository extends JpaRepository<Pipe, Long> {
    Page<Pipe> findAll(Pageable pageable);
}
