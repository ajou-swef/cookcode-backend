package com.swef.cookcode.fridge.controller;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.entity.CurrentUser;
import com.swef.cookcode.fridge.domain.Fridge;
import com.swef.cookcode.fridge.domain.FridgeIngredient;
import com.swef.cookcode.fridge.dto.request.IngredCreateRequest;
import com.swef.cookcode.fridge.dto.response.IngredCreateResponse;
import com.swef.cookcode.fridge.dto.response.FridgeResponse;
import com.swef.cookcode.fridge.service.FridgeService;
import com.swef.cookcode.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/fridge")
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<FridgeResponse>> getFridge(@CurrentUser User user){

        Fridge fridge = fridgeService.getFridge(user);

        List<FridgeIngredient> ingredsOfFridge = fridgeService.getIngedsOfFridge(fridge);
        FridgeResponse data = FridgeResponse.from(ingredsOfFridge);

        ApiResponse response = ApiResponse.builder()
                .message("냉장고 조회 성공")
                .status(OK.value())
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/ingred")
    public ResponseEntity<ApiResponse<IngredCreateResponse>> createIngredient(
            @CurrentUser User user, @RequestBody IngredCreateRequest ingredCreateRequest){

        Fridge fridge = fridgeService.getFridge(user);

        FridgeIngredient fridgeIngredient = fridgeService.addIngredToFridge(ingredCreateRequest, fridge);

        IngredCreateResponse data = IngredCreateResponse.from(fridgeIngredient);

        ApiResponse response = ApiResponse.builder()
                .message("식재료 등록 성공")
                .status(OK.value())
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}
