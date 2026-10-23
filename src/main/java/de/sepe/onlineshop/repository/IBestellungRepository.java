package de.sepe.onlineshop.repository;

import de.sepe.onlineshop.entity.BestellungEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBestellungRepository extends JpaRepository<BestellungEntity, Integer> {

}
