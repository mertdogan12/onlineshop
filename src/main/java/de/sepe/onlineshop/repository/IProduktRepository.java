package de.sepe.onlineshop.repository;

import de.sepe.onlineshop.entity.ProduktEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProduktRepository extends JpaRepository<ProduktEntity, Integer> {

}
