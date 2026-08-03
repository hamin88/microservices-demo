package com.tpe.repository;

import com.tpe.model.Rule;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    List<Rule> findAll();
    Optional<Rule> findById(Long id);

    @Query("SELECT r FROM Rule r")
        @QueryHints(value = @QueryHint(name = "hibernate.jdbc.fetch_size", value = "1000"))
    Stream<Rule> streamAllRecords();

}
