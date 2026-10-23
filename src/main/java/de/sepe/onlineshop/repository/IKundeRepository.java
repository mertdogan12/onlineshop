package de.sepe.onlineshop.repository;

import de.sepe.onlineshop.entity.BestellungEntity;
import de.sepe.onlineshop.entity.KundeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IKundeRepository extends JpaRepository<KundeEntity, Integer> {

}
