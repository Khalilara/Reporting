package com.demo.Repository.Channel;

import com.demo.Model.Channel.ResellerCateg;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface ResellerCategRepository extends JpaRepository<ResellerCateg,Long> {
// Méthode existante - pour les valeurs uniques
    Optional<ResellerCateg> findByResellerName(String resellerName);
    
    // Nouvelle méthode - pour gérer les doublons
    List<ResellerCateg> findAllByResellerName(String resellerName);

}
