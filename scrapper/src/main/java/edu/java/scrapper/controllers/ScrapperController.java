package edu.java.scrapper.controllers;

import edu.java.scrapper.controllers.dto.AddLinkRequest;
import edu.java.scrapper.controllers.dto.ApiErrorResponse;
import edu.java.scrapper.controllers.dto.LinkResponse;
import edu.java.scrapper.controllers.dto.ListLinksResponse;
import edu.java.scrapper.controllers.dto.RemoveLinkRequest;
import edu.java.scrapper.exceptions.AlreadyRegisteredChatException;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.exceptions.LinkNotFoundException;
import edu.java.scrapper.exceptions.ResourceNotFoundException;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.services.LinkService;
import edu.java.scrapper.services.TgChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@AllArgsConstructor
public class ScrapperController {

    private final LinkService linkService;
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
    @PostMapping("/tg-chat/{id}")
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
    ) throws UnsupportedLinkException {
        var link = linkService.add(tgChatId, addLinkRequest.link());
        return ResponseEntity.ok().body(new LinkResponse(link.id(), URI.create(link.uri())));
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
    ) throws LinkNotFoundException, ChatNotFoundException {
        var link = linkService.remove(tgChatId, removeLinkRequest.link());
        return ResponseEntity.ok().body(new LinkResponse(link.id(), URI.create(link.uri())));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String error = e.getName() + " should be of type " + e.getRequiredType().getName();

        return new ApiErrorResponse(error, HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(AlreadyRegisteredChatException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiErrorResponse handleResourceConflict(AlreadyRegisteredChatException e) {
        return new ApiErrorResponse("Chat already registered", HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleResourceNotFound(ResourceNotFoundException e) {
        return new ApiErrorResponse("Resource not found", HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleRuntimeException(RuntimeException e) {
        return new ApiErrorResponse(
            "An unexpected error occurred. Please try again later.",
            HttpStatus.INTERNAL_SERVER_ERROR,
            e
        );
    }
}
