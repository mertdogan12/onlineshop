package de.sepe.onlineshop.repository;

import de.sepe.onlineshop.entity.KundenprofilEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IKundenprofilRepository extends JpaRepository<KundenprofilEntity, Integer> {


}
