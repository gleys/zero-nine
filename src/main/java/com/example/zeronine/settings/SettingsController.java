package com.example.zeronine.settings;


import com.example.zeronine.settings.form.*;
import com.example.zeronine.settings.validator.PasswordFormValidator;
import com.example.zeronine.settings.validator.UsernameFormValidator;
import com.example.zeronine.user.security.CurrentUser;
import com.example.zeronine.user.repository.KeywordRepository;
import com.example.zeronine.user.User;
import com.example.zeronine.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.zeronine.utils.ResponseForm.Result;

import javax.validation.Valid;

import static com.example.zeronine.utils.ResponseForm.success;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PasswordFormValidator passwordFormValidator;
    private final UsernameFormValidator userNameFormValidator;
    private final KeywordRepository keywordRepository;
    private final ObjectMapper objectMapper;

    @InitBinder("passwordForm")
    public void passwordFormValid(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @InitBinder("usernameForm")
    public void setUserNameFormValid(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(userNameFormValidator);
    }

    @GetMapping("/profile")
    public String profileForm(@CurrentUser User user, Model model) {
        model.addAttribute(user);
        model.addAttribute(modelMapper.map(user, ProfileEditForm.class));
        return "settings/profile";
    }

    @PostMapping("/profile")
    public String editProfile(@CurrentUser User user, @Validated ProfileEditForm form,
                              BindingResult bindingResult, Model model, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(user);
            return "settings/profile";
        }

        userService.editProfile(user, form);
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");

        return "redirect:/settings/profile";
    }

    @GetMapping("/password")
    public String passwordForm(@CurrentUser User user, Model model) {
        model.addAttribute(new PasswordForm());
        model.addAttribute(user);
        return "settings/password";
    }

    @PostMapping("/password")
    public String changePassword(@CurrentUser User user, @Validated PasswordForm form, BindingResult bindingResult,
                                 Model model, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(user);
            return "settings/password";
        }

        boolean result = userService.changePassword(user, form.getPassword());

        if (!result) {
            model.addAttribute(user);
            bindingResult.rejectValue("password", "wrong.value", "????????? ??????????????? ????????? ??????????????????.");
            return "settings/password";
        }

        attributes.addFlashAttribute("message", "??????????????? ??????????????????.");
        return "redirect:/settings/password";
    }

    @GetMapping("/notifications")
    public String notificationsForm(@CurrentUser User user, Model model) {
        log.info("getUser = {}", user);
        model.addAttribute(user);
        model.addAttribute(modelMapper.map(user, NotificationsForm.class));
        return "settings/notifications";
    }

    @PostMapping("/notifications")
    public String setNotifications(@CurrentUser User user, @Validated NotificationsForm form, Model model,
                                   BindingResult bindingResult, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(user);
            return "settings/notifications";
        }
        log.info("notification form = {}", form);

        userService.setNotifications(form, user);
        attributes.addFlashAttribute("message", "?????? ????????? ??????????????????.");
        return "redirect:/settings/notifications";
    }

    @GetMapping("/account")
    public String accountForm(@CurrentUser User user, Model model) {
        model.addAttribute(user);
        model.addAttribute(modelMapper.map(user, UsernameForm.class));

        return "settings/account";
    }

    @PostMapping("/account")
    public String changeUsername(@CurrentUser User user, @Validated UsernameForm form,BindingResult bindingResult,
                                 Model model, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return "settings/account";
        }

        boolean result = userService.changeUsername(user, form.getName());
        attributes.addFlashAttribute("message", "????????? ?????????????????????.");

        return "redirect:/settings/account";
    }


    @GetMapping("/keywords")
    public String getKeywords(@CurrentUser User user, Model model) throws JsonProcessingException {
        model.addAttribute(user);
        List<String> keywords = userService.getKeywords(user);

        model.addAttribute("keywords", keywords);
        List<String> allKeywords = keywordRepository.findAll().stream().map(Keyword::getName).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allKeywords));

        return "settings/keywords";
    }

    /** response form
     * ?????? ?????? (@RequestBody ?????? ??????????????? Result<DTO> ??? ?????? ??? ??????.
     * Result<T> { HttpStatus status; T data; Error error; }
     * {
     *     status : 200,
     *     data : { 'keyword' : 'example' },
     *     error : null
     * }
     * ?????? ??????
     * {
     *     status : 400,
     *     data : null,
     *     error :
     * }
     * ?????? ?????? ??? exception resolver ??? catch ?????? ?????? ?????? ??????
     */
    @ResponseBody
    @PostMapping("/keywords/add")
    public Result<String> addKeyword(@CurrentUser User user, @RequestBody @Valid KeywordsForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getFieldErrors()
                    .stream().map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining("\n"));

            return success(message);
        }

        userService.addKeyword(user, form.getKeyword());
        return success("OK");
    }

    @ResponseBody
    @PostMapping("/keywords/remove")
    public Result<String> removeKeyword(@CurrentUser User user, @RequestBody @Validated KeywordsForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getFieldErrors()
                    .stream().map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining("\n"));

            return success(message);
        }

        userService.removeKeyword(user, form.getKeyword());
        return success("OK");
    }



}
