package application.infrastructure.notification;

import application.domain.ports.out.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Output Adapter: development implementation of the NotificationService
 * port. Logs notifications to the console; replaceable by an email/SMS
 * provider without touching the domain.
 */
@Component
public class ConsoleNotificationAdapter implements NotificationService {

    private static final Logger LOG =
            LoggerFactory.getLogger(ConsoleNotificationAdapter.class);

    @Override
    public void notifyBuyer(String buyerEmail, String subject, String message) {
        LOG.info("[NOTIFY BUYER {}] {}: {}", buyerEmail, subject, message);
    }

    @Override
    public void notifySeller(String sellerEmail, String subject, String message) {
        LOG.info("[NOTIFY SELLER {}] {}: {}", sellerEmail, subject, message);
    }
}