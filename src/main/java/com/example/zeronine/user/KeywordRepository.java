package com.example.zeronine.user;

import com.example.zeronine.settings.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional(readOnly = true)
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByName(String name);

    @Query("select k from Keyword k where k.name in (:names)")
    List<Keyword> findByNames(@Param("names") List<String> names);

}
