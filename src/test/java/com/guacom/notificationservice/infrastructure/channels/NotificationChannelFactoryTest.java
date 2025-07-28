package com.guacom.notificationservice.infrastructure.channels;

import com.guacom.notificationservice.domain.enums.NotificationChannel;
import com.guacom.notificationservice.domain.exceptions.NotificationException;
import com.guacom.notificationservice.domain.interfaces.NotificationChannelStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationChannelFactoryTest {

    @Mock
    private NotificationChannelStrategy emailStrategy;

    @Mock
    private NotificationChannelStrategy smsStrategy;

    @Mock
    private NotificationChannelStrategy pushStrategy;

    private NotificationChannelFactory factory;

    @BeforeEach
    void setUp() {
        when(emailStrategy.getChannelType()).thenReturn(NotificationChannel.EMAIL);
        when(smsStrategy.getChannelType()).thenReturn(NotificationChannel.SMS);
        when(pushStrategy.getChannelType()).thenReturn(NotificationChannel.PUSH);

        List<NotificationChannelStrategy> strategies = Arrays.asList(emailStrategy, smsStrategy, pushStrategy);
        factory = new NotificationChannelFactory(strategies);
    }

    @Test
    void getStrategy_ShouldReturnCorrectStrategy_ForEmailChannel() {
        // Act
        NotificationChannelStrategy strategy = factory.getNotificationChannelStrategy(NotificationChannel.EMAIL);

        // Assert
        assertEquals(emailStrategy, strategy);
    }

    @Test
    void getStrategy_ShouldReturnCorrectStrategy_ForSmsChannel() {
        // Act
        NotificationChannelStrategy strategy = factory.getNotificationChannelStrategy(NotificationChannel.SMS);

        // Assert
        assertEquals(smsStrategy, strategy);
    }

    @Test
    void getStrategy_ShouldReturnCorrectStrategy_ForPushChannel() {
        // Act
        NotificationChannelStrategy strategy = factory.getNotificationChannelStrategy(NotificationChannel.PUSH);

        // Assert
        assertEquals(pushStrategy, strategy);
    }

    @Test
    void getStrategy_ShouldThrowException_ForUnsupportedChannel() {
        // Act & Assert
        NotificationException exception = assertThrows(NotificationException.class, () ->
                factory.getNotificationChannelStrategy(null)
        );

        assertTrue(exception.getMessage().contains("No strategy found for channel"));
    }
}
