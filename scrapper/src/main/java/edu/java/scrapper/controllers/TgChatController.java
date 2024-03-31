package edu.java.scrapper.controllers;

import edu.java.scrapper.controllers.dto.ApiErrorResponse;
import edu.java.scrapper.exceptions.AlreadyRegisteredChatException;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.services.TgChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tg-chat")
@AllArgsConstructor
public class TgChatController {
    private final TgChatService tgChatService;

    @Operation(summary = "Зарегистрировать чат")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Повторная регистрация",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{id}")
    public ResponseEntity<?> registerChat(
        @PathVariable
        @Parameter(description = "The ID of the chat to register", required = true, schema = @Schema(type = "Integer"))
        Long id
    ) throws AlreadyRegisteredChatException {
        tgChatService.register(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить чат")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Чат не существует",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<?> deleteChat(
        @PathVariable
        @Parameter(description = "The ID of the chat to delete", required = true, schema = @Schema(type = "Integer"))
        Long id
    ) throws ChatNotFoundException {
        tgChatService.unregister(id);
        return ResponseEntity.ok().build();
    }
}
