package application.adapters.out.persistence.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.Shipment;
import application.domain.ports.out.ShipmentRepository;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (in-memory): development implementation of the
 * ShipmentRepository port shared by the shipment creation, dispatch and
 * tracking services.
 */
@Component
public class ShipmentRepositoryAdapter implements ShipmentRepository {

    private final Map<String, Shipment> shipmentsById =
            new ConcurrentHashMap<>();

    @Override
    public Shipment save(Shipment shipment) {
        shipmentsById.put(shipment.getShipmentId(), shipment);
        return shipment;
    }

    @Override
    public Optional<Shipment> findById(String shipmentId) {
        return Optional.ofNullable(shipmentsById.get(shipmentId));
    }

    @Override
    public List<Shipment> findAll() {
        return List.copyOf(shipmentsById.values());
    }

    @Override
    public List<Shipment> findByOrderId(Integer orderId) {
        return shipmentsById.values().stream()
                .filter(shipment -> shipment.getOrder().getOrderId()
                        .equals(orderId))
                .toList();
    }
}
