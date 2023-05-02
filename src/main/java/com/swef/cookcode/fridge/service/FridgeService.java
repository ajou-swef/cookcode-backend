package com.swef.cookcode.fridge.service;

import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.fridge.domain.Fridge;
import com.swef.cookcode.fridge.domain.FridgeIngredient;
import com.swef.cookcode.fridge.domain.Ingredient;
import com.swef.cookcode.fridge.dto.request.IngredCreateRequest;
import com.swef.cookcode.fridge.dto.request.IngredUpdateRequest;
import com.swef.cookcode.fridge.repository.FridgeIngredientRepository;
import com.swef.cookcode.fridge.repository.FridgeRepository;
import com.swef.cookcode.fridge.repository.IngerdientRepository;
import com.swef.cookcode.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.swef.cookcode.common.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FridgeService {

    private final FridgeRepository fridgeRepository;

    private final IngerdientRepository ingerdientRepository;

    private final FridgeIngredientRepository fridgeIngredientRepository;

    private final EntityManager entityManager;

    @Transactional
    public Fridge signUpFridge(User user){
        Fridge newFridge = Fridge.builder()
                .owner(user)
                .build();
        return fridgeRepository.save(newFridge);
    }

    @Transactional(readOnly = true)
    public Fridge getFridgeOfUser(User user) {
        return fridgeRepository.findByOwner(user)
                .orElseThrow(() -> new NotFoundException(FRIDGE_NOT_FOUND));
    }

    @Transactional
    public List<FridgeIngredient> getIngedsOfFridge(Fridge fridge) {

        String jpql = "SELECT fi FROM FridgeIngredient fi JOIN FETCH fi.ingred WHERE fi.fridge.id = :fridgeId";

        TypedQuery<FridgeIngredient> query = entityManager.createQuery(jpql, FridgeIngredient.class);
        query.setParameter("fridgeId", fridge.getId());

        return query.getResultList();
    }

    @Transactional
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

    @Transactional
    public void deleteIngredOfFridge(Long fridgeIngredId) {
        fridgeIngredientRepository.deleteById(fridgeIngredId);
    }

    @Transactional
    public void updateFridgeIngred(Long fridgeIngredId, IngredUpdateRequest ingredUpdateRequest) {
        FridgeIngredient fridgeIngredient = fridgeIngredientRepository.findById(fridgeIngredId)
                .orElseThrow(() -> new NotFoundException(FRIDGE_INGRED_NOT_FOUND));

        System.out.println(ingredUpdateRequest.getExpiredAt());

        fridgeIngredient.updateQuantity(ingredUpdateRequest.getQuantity());
        fridgeIngredient.updateExpiredAt(ingredUpdateRequest.getExpiredAt());


        fridgeIngredientRepository.save(fridgeIngredient);
    }

    public void validateIngredIsInFridge(User user, Long fridgeIngredId) {
        Fridge fridgeOfUser = getFridgeOfUser(user);

        Fridge fridgeOfIngred = getFridgeOfIngredient(fridgeIngredId);

        checkSameFridge(fridgeOfUser, fridgeOfIngred);
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
