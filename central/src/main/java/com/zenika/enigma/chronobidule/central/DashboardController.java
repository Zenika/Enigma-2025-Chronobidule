package com.zenika.enigma.chronobidule.central;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoreStatus;
import com.zenika.enigma.chronobidule.central.stores.StoresService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
	@Autowired StoresService stores;
	
	@GetMapping @RequestMapping("table") public String table(Model model) {
		List<Map<String, Object>> c = stores.getStores()
			.stream()
			.map(this::storeToRepresentation)
			.collect(Collectors.toList());
		model.addAttribute("stores", c);
		return "table";
	}

	private Map<String, Object> storeToRepresentation(Store s) {
		return Map.of("id", s.getId(), "name", s.getName(), "baseUrl", s.getBaseUrl(), "status", s.getStatus(), "statusStyle", getStatusStyle(s.getStatus()));
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
