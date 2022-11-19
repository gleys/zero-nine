package com.example.zeronine.comment;

import com.example.zeronine.comment.form.CommentForm;
import com.example.zeronine.user.CurrentUser;
import com.example.zeronine.user.User;
import com.example.zeronine.utils.ResponseForm.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static com.example.zeronine.utils.ResponseForm.success;

@Slf4j
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{orderId}")
    public Result<String> writeComment(@CurrentUser User user, @PathVariable Long orderId,
                                        @RequestBody CommentForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getFieldErrors()
                    .stream().map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(message);
        }

        commentService.append(user, orderId, form);
        return success("OK");
    }

    @PostMapping("/{orderId}/delete/{commentId}")
    public Result<String> deleteComment(@CurrentUser User user, @PathVariable Long orderId, @PathVariable Long commentId) {
        commentService.delete(user, orderId, commentId);
        return success("OK");
    }

}
