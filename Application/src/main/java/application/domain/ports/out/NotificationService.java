package application.domain.ports.out;

/**
 * Output Port: notification contract for reaching buyers and sellers
 * through external channels (email/SMS). The domain defines what must be
 * notified; infrastructure decides how.
 */
public interface NotificationService {

    void notifyBuyer(String buyerEmail, String subject, String message);

    void notifySeller(String sellerEmail, String subject, String message);
}