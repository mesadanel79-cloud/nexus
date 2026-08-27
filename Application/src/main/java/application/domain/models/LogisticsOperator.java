package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * LogisticsOperator - Domain Model.
 *
 * Responsible for the physical operation of warehouses and dispatch:
 * product registration, inventory management and shipment handling.
 *
 * Relationships:
 * - A LogisticsOperator registers Product instances.
 * - A LogisticsOperator manages Inventory and handles Shipment instances.
 */
public class LogisticsOperator extends InternalStaff {

    public LogisticsOperator(String identifier, String fullName, String email,
                             UserStatus status) {
        super(identifier, fullName, email, SystemRole.OPERADOR_LOGISTICO, status);
    }
}