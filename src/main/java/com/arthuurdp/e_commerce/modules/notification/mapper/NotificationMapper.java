package com.arthuurdp.e_commerce.modules.notification.mapper;

import com.arthuurdp.e_commerce.modules.notification.Notification;
import com.arthuurdp.e_commerce.modules.notification.dtos.NotificationResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toNotificationResponse(Notification notification);
    List<NotificationResponse> toNotificationResponseList(List<Notification> notifications);
}
