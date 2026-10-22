package de.sepe.onlineshop.repository;

import de.sepe.onlineshop.entity.KategorieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IKategorieRepository extends JpaRepository<KategorieEntity, Integer> {

}
