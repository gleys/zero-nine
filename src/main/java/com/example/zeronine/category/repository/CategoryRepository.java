package com.example.zeronine.category.repository;

import com.example.zeronine.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryRepository extends JpaRepository<Category, Long> {


}
