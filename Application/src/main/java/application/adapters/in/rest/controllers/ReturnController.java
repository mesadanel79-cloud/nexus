package application.adapters.in.rest.controllers;

import application.adapters.in.rest.requests.RequestReturnRequest;
import application.domain.enums.ReturnReason;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.ports.in.ApproveReturnUseCase;
import application.domain.ports.in.RequestReturnUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Input Adapter (REST): exposes return and refund endpoints.
 */
@RestController
@RequestMapping("/api/v1/returns")
public class ReturnController {

    private final RequestReturnUseCase requestReturnUseCase;
    private final ApproveReturnUseCase approveReturnUseCase;

    public ReturnController(RequestReturnUseCase requestReturnUseCase,
                            ApproveReturnUseCase approveReturnUseCase) {
        this.requestReturnUseCase = requestReturnUseCase;
        this.approveReturnUseCase = approveReturnUseCase;
    }

    @PostMapping
    public ResponseEntity<Return> requestReturn(
            @RequestBody RequestReturnRequest request) {
        Return returnRequest = requestReturnUseCase.requestReturn(
                request.getBuyerId(),
                request.getOrderId(),
                ReturnReason.valueOf(request.getReason()));
        return ResponseEntity.ok(returnRequest);
    }

    @PostMapping("/{returnId}/review")
    public ResponseEntity<Return> markUnderReview(@PathVariable String returnId) {
        return ResponseEntity.ok(approveReturnUseCase.markUnderReview(returnId));
    }

    @PostMapping("/{returnId}/approval")
    public ResponseEntity<Refund> approve(@PathVariable String returnId) {
        return ResponseEntity.ok(approveReturnUseCase.approveReturn(returnId));
    }

    @PostMapping("/{returnId}/rejection")
    public ResponseEntity<Return> reject(@PathVariable String returnId) {
        return ResponseEntity.ok(approveReturnUseCase.rejectReturn(returnId));
    }
}