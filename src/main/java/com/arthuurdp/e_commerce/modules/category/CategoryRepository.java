package com.arthuurdp.e_commerce.modules.category;

import com.arthuurdp.e_commerce.modules.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
