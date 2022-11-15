package com.example.zeronine.comment;

import com.example.zeronine.comment.form.CommentForm;
import com.example.zeronine.user.CurrentUser;
import com.example.zeronine.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{orderId}")
    public ResponseEntity writeComment(@CurrentUser User user, @PathVariable Long orderId,
                                       @RequestBody CommentForm form, BindingResult bindingResult) {
        log.info("writeComment = {}", orderId);
        if (bindingResult.hasErrors()) {
            log.info("error = {}", bindingResult);
            //TODO: 400 에러 반환
            return ResponseEntity.badRequest().build();
        }

        commentService.append(user, orderId, form);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/delete/{commentId}")
    public ResponseEntity deleteComment(@CurrentUser User user, @PathVariable Long orderId, @PathVariable Long commentId) {
        commentService.delete(user, orderId, commentId);
        return ResponseEntity.ok().build();
    }

}
