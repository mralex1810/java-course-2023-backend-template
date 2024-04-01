package edu.java.scrapper.controllers;

import edu.java.scrapper.controllers.dto.AddLinkRequest;
import edu.java.scrapper.controllers.dto.ApiErrorResponse;
import edu.java.scrapper.controllers.dto.LinkResponse;
import edu.java.scrapper.controllers.dto.ListLinksResponse;
import edu.java.scrapper.controllers.dto.RemoveLinkRequest;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.exceptions.LinkNotFoundException;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.services.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("links")
public class LinkController {
    private final LinkService linkService;

    @Operation(summary = "Получить все отслеживаемые ссылки")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылки успешно получены",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ListLinksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ListLinksResponse> getLinks(
        @RequestHeader("Tg-Chat-Id")
        @Parameter(description = "The ID of the chat to get its links",
                   required = true,
                   schema = @Schema(type = "Integer"))
        Long tgChatId
    ) {
        var links = linkService.listAll(tgChatId).stream()
            .map(it -> new LinkResponse(it.id(), URI.create(it.uri())))
            .toList();
        return ResponseEntity.ok().body(new ListLinksResponse(links, links.size()));
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
    @PostMapping
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
    @DeleteMapping
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
}
