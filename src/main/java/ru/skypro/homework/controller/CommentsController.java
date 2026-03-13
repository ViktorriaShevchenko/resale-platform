package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Комментарии")
public class CommentsController {

    private final CommentService commentService;

    @Operation(
            summary = "Получение комментариев объявления",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/ads/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable Integer id) {
        return ResponseEntity.ok(commentService.getCommentsByAdId(id));
    }

    @Operation(
            summary = "Добавление комментария к объявлению",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PostMapping("/ads/{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Integer id,
                                              @RequestBody CreateOrUpdateComment comment,
                                              Authentication authentication) {
        return ResponseEntity.ok(commentService.addComment(id, authentication.getName(), comment));
    }

    @Operation(
            summary = "Удаление комментария",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @DeleteMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer adId,
                                              @PathVariable Integer commentId,
                                              Authentication authentication) {
        commentService.deleteComment(adId, commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Обновление комментария",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PatchMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer adId,
                                                 @PathVariable Integer commentId,
                                                 @RequestBody CreateOrUpdateComment comment,
                                                 Authentication authentication) {
        return ResponseEntity.ok(commentService.updateComment(adId, commentId,
                authentication.getName(), comment));
    }
}
