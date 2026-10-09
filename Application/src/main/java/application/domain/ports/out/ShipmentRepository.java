package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.Shipment;

/**
 * Output Port: persistence of Shipment Domain Models. Shipment creation,
 * dispatch and tracking share this repository instead of keeping private
 * registries inside each service.
 */
public interface ShipmentRepository {

    Shipment save(Shipment shipment);

    Optional<Shipment> findById(String shipmentId);

    List<Shipment> findAll();

    List<Shipment> findByOrderId(Integer orderId);
}
