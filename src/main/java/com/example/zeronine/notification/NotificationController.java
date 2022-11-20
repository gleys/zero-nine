package com.example.zeronine.notification;

import com.example.zeronine.notification.respository.NotificationRepository;
import com.example.zeronine.user.security.CurrentUser;
import com.example.zeronine.user.User;
import com.example.zeronine.utils.ResponseForm.Result;
import static com.example.zeronine.utils.ResponseForm.success;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping
    public String getNotifications(@CurrentUser User user, Model model) {
        List<Notification> notifications = notificationRepository.findByUserAndCheckedOrderByCreatedAtDesc(user, false);

        long checkedCount = notificationRepository.countByUserAndChecked(user, true);
        long unCheckedCount = notifications.size();

        notificationService.markAsCheck(notifications);

        setModel(user, model, notifications, checkedCount, unCheckedCount, true);
        return "notification/list";
    }

    @GetMapping("/old")
    public String getOldNotifications(@CurrentUser User user, Model model) {
        List<Notification> notifications = notificationRepository.findByUserAndCheckedOrderByCreatedAtDesc(user, true);

        long checkedCount = notifications.size();
        long unCheckedCount = notificationRepository.countByUserAndChecked(user, false);

        setModel(user, model, notifications, checkedCount, unCheckedCount, false);
        return "notification/list";
    }

    @ResponseBody
    @DeleteMapping
    public Result<String> deleteNotifications(@CurrentUser User user) {
        log.info("delete notification");

        notificationRepository.deleteByUserAndChecked(user, true);

        return success("OK");
    }

    private void setModel(User user, Model model, List<Notification> notifications, long checkedCount, long unCheckedCount, boolean isNew) {

        model.addAttribute(user);
        model.addAttribute("checkedCount", checkedCount);
        model.addAttribute("unCheckedCount", unCheckedCount);
        model.addAttribute("notifications", notifications);
        model.addAttribute("isNew", isNew);
    }

}
