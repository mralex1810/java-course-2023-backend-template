package edu.java.controllers;

import edu.java.controllers.dto.AddLinkRequest;
import edu.java.controllers.dto.ApiErrorResponse;
import edu.java.controllers.dto.LinkResponse;
import edu.java.controllers.dto.ListLinksResponse;
import edu.java.controllers.dto.RemoveLinkRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapperController {

    public static final String DUMMY_SITE = "https://blabla.com";

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
    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<?> registerChat(
        @PathVariable
        @Parameter(description = "The ID of the chat to register", required = true, schema = @Schema(type = "Integer"))
        Long id
    ) {
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
    ) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить все отслеживаемые ссылки")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылки успешно получены",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ListLinksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getLinks(
        @RequestHeader("Tg-Chat-Id")
        @Parameter(description = "The ID of the chat to get its links",
                   required = true,
                   schema = @Schema(type = "Integer"))
        Long tgChatId
    ) {
        return ResponseEntity.ok().body(new ListLinksResponse(List.of(), 0));
    }

    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = LinkResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/links")
    public ResponseEntity<LinkResponse> addLink(
        @RequestBody AddLinkRequest addLinkRequest,
        @RequestHeader("Tg-Chat-Id")
        @Parameter(description = "The ID of the chat to add the link to",
                   required = true,
                   schema = @Schema(type = "Integer"))
        Long tgChatId
    ) {
        return ResponseEntity.ok().body(new LinkResponse(0L, URI.create(DUMMY_SITE)));
    }

    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = LinkResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ссылка не найдена",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/links")
    public ResponseEntity<LinkResponse> removeLink(
        @RequestBody RemoveLinkRequest removeLinkRequest,
        @RequestHeader("Tg-Chat-Id")
        @Parameter(description = "The ID of the chat to remove the link from",
                   required = true,
                   schema = @Schema(type = "Integer"))
        Long tgChatId
    ) {
        return ResponseEntity.ok().body(new LinkResponse(0L, URI.create(DUMMY_SITE)));
    }
}
