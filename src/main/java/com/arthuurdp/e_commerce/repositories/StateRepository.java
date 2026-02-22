package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
}
