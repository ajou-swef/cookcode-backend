package com.swef.cookcode.fridge.controller;

import com.swef.cookcode.common.ApiResponse;
import com.swef.cookcode.common.entity.CurrentUser;
import com.swef.cookcode.common.error.exception.NotFoundException;
import com.swef.cookcode.fridge.domain.Fridge;
import com.swef.cookcode.fridge.domain.FridgeIngredient;
import com.swef.cookcode.fridge.dto.FridgeResponse;
import com.swef.cookcode.fridge.service.FridgeService;
import com.swef.cookcode.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.swef.cookcode.common.ErrorCode.FRIDGE_NOT_FOUND;

@RestController
@RequestMapping("/api/v1/fridge")
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<FridgeResponse>> getFridge(@CurrentUser User user){

        Fridge fridge = fridgeService.getFridge(user)
                .orElseThrow(() -> new NotFoundException(FRIDGE_NOT_FOUND));

        List<FridgeIngredient> ingredsOfFridge = fridgeService.getIngedsOfFridge(fridge);
        FridgeResponse data = FridgeResponse.from(ingredsOfFridge);

        ApiResponse response = ApiResponse.builder()
                .message("냉장고 조회 성공")
                .status(200)
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}
