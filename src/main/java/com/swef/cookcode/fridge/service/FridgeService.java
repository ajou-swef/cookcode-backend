package com.swef.cookcode.fridge.service;

import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.fridge.domain.Fridge;
import com.swef.cookcode.fridge.domain.FridgeIngredient;
import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.fridge.dto.request.IngredCreateRequest;
import com.swef.cookcode.fridge.dto.response.IngredSimpleResponse;
import com.swef.cookcode.fridge.repository.FridgeIngredientRepository;
import com.swef.cookcode.fridge.repository.FridgeRepository;
import com.swef.cookcode.fridge.repository.IngerdientRepository;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.swef.cookcode.common.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FridgeService {

    private final FridgeRepository fridgeRepository;

    private final IngerdientRepository ingerdientRepository;

    private final FridgeIngredientRepository fridgeIngredientRepository;

    private final EntityManager entityManager;

    public Fridge signUpFridge(User user){
        Fridge newFridge = Fridge.builder()
                .owner(user)
                .build();
        return fridgeRepository.save(newFridge);
    }

    public Fridge getFridge(User user) {
        return fridgeRepository.findByOwner(user)
                .orElseThrow(() -> new NotFoundException(FRIDGE_NOT_FOUND));
    }

    public List<FridgeIngredient> getIngedsOfFridge(Fridge fridge) {

        String jpql = "SELECT fi FROM FridgeIngredient fi JOIN FETCH fi.ingred WHERE fi.fridge.id = :fridgeId";

        TypedQuery<FridgeIngredient> query = entityManager.createQuery(jpql, FridgeIngredient.class);
        query.setParameter("fridgeId", fridge.getId());

        return query.getResultList();
    }

    public FridgeIngredient addIngredToFridge(IngredCreateRequest ingredCreateRequest, Fridge fridge) {

        Ingredient ingred = ingerdientRepository.findById(ingredCreateRequest.getIngredId())
                .orElseThrow(() -> new NotFoundException(INGREDIENT_NOT_FOUND));

        FridgeIngredient fridgeIngredient = FridgeIngredient.builder()
                .fridge(fridge)
                .ingred(ingred)
                .quantity(ingredCreateRequest.getQuantity())
                .expiredAt(ingredCreateRequest.getExpiredAt())
                .build();

        return fridgeIngredientRepository.save(fridgeIngredient);
    }

    public void deleteIngredOfFridge(Fridge fridgeOfUser, Long fridgeIngredId) {
        Fridge fridgeOfIngred = getFridgeOfIngredient(fridgeIngredId);

        checkSameFridge(fridgeOfUser, fridgeOfIngred);

        fridgeIngredientRepository.deleteById(fridgeIngredId);
    }

    private void checkSameFridge(Fridge fridgeOfUser, Fridge fridgeOfIngred) {
        if(!fridgeOfUser.getId().equals(fridgeOfIngred.getId())){
            throw new NotFoundException(FRIDGE_INGRED_NOT_FOUND);
        }
    }

    private Fridge getFridgeOfIngredient(Long fridgeIngredId) {
        return fridgeIngredientRepository.findById(fridgeIngredId)
                .orElseThrow(() -> new NotFoundException(FRIDGE_INGRED_NOT_FOUND))
                .getFridge();
    }
}
