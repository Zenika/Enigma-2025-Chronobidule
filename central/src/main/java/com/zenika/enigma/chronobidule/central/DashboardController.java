package com.zenika.enigma.chronobidule.central;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zenika.enigma.chronobidule.central.revenue.RevenueRepository;
import com.zenika.enigma.chronobidule.central.revenue.StoreRevenue;
import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoreStatus;
import com.zenika.enigma.chronobidule.central.stores.StoresService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
	@Autowired StoresService stores;
	@Autowired RevenueRepository revenues;
	@Autowired InetAddress centralAddress;
	
	@GetMapping @RequestMapping("table") public String table(Model model) throws UnknownHostException {
		List<Map<String, Object>> c = getStoresRepresentations();
		model.addAttribute("stores", c);
		model.addAttribute("ipAddress", centralAddress.getHostAddress());
		return "table";
	}

	private List<Map<String, Object>> getStoresRepresentations() {
		List<Map<String, Object>> c = stores.getStores()
			.stream()
			.map(this::storeToRepresentation)
			.collect(Collectors.toList());
		return c;
	}

	@GetMapping @RequestMapping("table/htmx") public String tableHtmx(Model model) {
		List<Map<String, Object>> c = getStoresRepresentations();
		model.addAttribute("stores", c);
		return "table :: storesTable";
	}
	private Map<String, Object> storeToRepresentation(Store s) {
		return Map.of(
				"id", s.getId(), 
				"name", s.getName(), 
				"baseUrl", s.getBaseUrl(), 
				"status", s.getStatus(), 
				"statusStyle", getStatusStyle(s.getStatus()),
				"revenue", formatStoreRevenue(s));
	}

	private String formatStoreRevenue(Store s) {
		BigDecimal amount = revenues.findByStoreId(s.getId())
			.stream()
			.map(StoreRevenue::getTotal)
			.findFirst()
			.orElse(BigDecimal.ZERO);
		return NumberFormat.getCurrencyInstance().format(amount);
	}

	private Object getStatusStyle(StoreStatus status) {
		return switch(status) {
		case REGISTERED -> "table-dark";
		case PRICES_INITIALIZED -> "table-light";
		case STOCK_INITIALIZED -> "table-info";
		case READY -> "table-success";
		};
	}
}
