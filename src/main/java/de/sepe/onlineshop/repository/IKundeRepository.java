package de.sepe.onlineshop.repository;

import de.sepe.onlineshop.entity.KundeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IKundeRepository extends JpaRepository<KundeEntity, Integer> {

}
