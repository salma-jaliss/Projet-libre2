package ma.cabinet.statistics.client;

import ma.cabinet.statistics.model.Billing;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "billing-service", url = "${application.config.billing-url:}")
public interface BillingServiceClient {
    @GetMapping("/api/factures")
    List<Billing> getAllBillings();
}
