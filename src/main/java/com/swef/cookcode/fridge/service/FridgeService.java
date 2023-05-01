package com.swef.cookcode.fridge.service;

import com.swef.cookcode.fridge.domain.Fridge;
import com.swef.cookcode.fridge.domain.FridgeIngredient;
import com.swef.cookcode.fridge.repository.FridgeRepository;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FridgeService {

    private final FridgeRepository fridgeRepository;

    private final EntityManager entityManager;

    public Fridge signUpFridge(User user){
        Fridge newFridge = Fridge.builder()
                .owner(user)
                .build();
        return fridgeRepository.save(newFridge);
    }

    public Optional<Fridge> getFridge(User user) {
        return fridgeRepository.findByOwner(user);
    }

    public List<FridgeIngredient> getIngedsOfFridge(Fridge fridge) {
        String jpql = "SELECT fi FROM FridgeIngredient fi JOIN FETCH fi.ingred WHERE fi.fridge.id = :fridgeId";

        TypedQuery<FridgeIngredient> query = entityManager.createQuery(jpql, FridgeIngredient.class);
        query.setParameter("fridgeId", fridge.getId());

        return query.getResultList();
    }
}
