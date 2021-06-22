package com.igorlucas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.igorlucas.entity.Beer;

public interface BeerRepository extends JpaRepository<Beer, Long> {

    Optional<Beer> findByName(String name);
}