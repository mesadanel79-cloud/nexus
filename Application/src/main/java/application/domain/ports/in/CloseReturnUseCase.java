package application.domain.ports.in;

import application.domain.models.Return;

/**
 * Input Port (Returns and Refunds): completes the return lifecycle when the
 * applicable conditions for closure have been satisfied.
 */
public interface CloseReturnUseCase {

    Return closeReturn(String returnId);
}