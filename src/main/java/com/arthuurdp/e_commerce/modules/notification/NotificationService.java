package com.arthuurdp.e_commerce.modules.notification;

import com.arthuurdp.e_commerce.modules.notification.dtos.NotificationResponse;
import com.arthuurdp.e_commerce.modules.notification.mapper.NotificationMapper;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;

    public NotificationService(NotificationRepository notificationRepository, NotificationMapper mapper) {
        this.notificationRepository = notificationRepository;
        this.mapper = mapper;
    }

    @Transactional
    public void createNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(User user) {
        return mapper.toNotificationResponseList(notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()));
    }
}
