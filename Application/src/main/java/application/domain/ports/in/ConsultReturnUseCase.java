package application.domain.ports.in;

import application.domain.models.Return;

/**
 * Input Port (Returns and Refunds): retrieves information about a return
 * request according to the requesting user's permissions.
 */
public interface ConsultReturnUseCase {

    Return consultReturn(String returnId);
}