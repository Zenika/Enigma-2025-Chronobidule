package com.zenika.enigma.chronobidule.central;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zenika.enigma.chronobidule.central.prices.PricesInitialized;
import com.zenika.enigma.chronobidule.central.prices.PricesInitializer;
import com.zenika.enigma.chronobidule.central.prices.PricesRepository;
import com.zenika.enigma.chronobidule.central.prices.StorePriceFacade;
import com.zenika.enigma.chronobidule.central.prices.StoreProductPrice;
import com.zenika.enigma.chronobidule.central.products.Product;
import com.zenika.enigma.chronobidule.central.products.ProductsRepository;
import com.zenika.enigma.chronobidule.central.revenue.RevenueRepository;
import com.zenika.enigma.chronobidule.central.revenue.StoreRevenue;
import com.zenika.enigma.chronobidule.central.stores.Store;
import com.zenika.enigma.chronobidule.central.stores.StoreStatus;
import com.zenika.enigma.chronobidule.central.stores.StoresService;
import com.zenika.enigma.chronobidule.central.supply.StockInitializer;
import com.zenika.enigma.chronobidule.central.supply.StockRepository;
import com.zenika.enigma.chronobidule.central.supply.StoreStockEntry;
import com.zenika.enigma.chronobidule.central.supply.StoreStockFacade;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
	@Autowired StoresService stores;
	@Autowired ProductsRepository products;
	@Autowired StockRepository stocksRepository;
	@Autowired PricesRepository pricesRepository;
	@Autowired RevenueRepository revenues;
	@Autowired InetAddress centralAddress;
	@Autowired StockInitializer stockInitializer;
	@Autowired StoreStockFacade storeStockFacade;
	@Autowired StorePriceFacade storePriceFacade;
	
	@GetMapping @RequestMapping("{id}/send/stocks") public String sendStock(@PathVariable("id") Long id,  Model model) throws UnknownHostException {
		stores.findById(id).ifPresent(store -> {
			stockInitializer.generateStockForStore(store);
		});
		return String.format("redirect:/dashboard/%s/details", id);
	}

	@GetMapping @RequestMapping("{id}/send/prices") public String sendPrices(@PathVariable("id") Long id,  Model model) throws UnknownHostException {
		stores.findById(id).ifPresent(store -> {
			Collection<StoreProductPrice> prices = pricesRepository.findByStoreId(id);
			storePriceFacade.sendPricesToStore(store, prices);
		});
		return String.format("redirect:/dashboard/%s/details", id);
	}

	@GetMapping @RequestMapping("{id}/details") public String details(@PathVariable("id") Long id,  Model model) throws UnknownHostException {
		stores.findById(id).ifPresent(store -> {
			model.addAllAttributes(storeToRepresentation(store));
			model.addAttribute("prices", createPrices(store));
			model.addAttribute("ipAddress", centralAddress.getHostAddress());
		});
		return "details";
	}

	private Object createPrices(Store store) {
		Map<Long, Integer> stockContent = stocksRepository.findByStoreId(store.getId()).stream()
				.collect(Collectors.toMap(StoreStockEntry::getProductId, StoreStockEntry::getQuantity));
		Map<Long, BigDecimal> pricesContent = pricesRepository.findByStoreId(store.getId()).stream()
				.collect(Collectors.toMap(StoreProductPrice::getProductId, StoreProductPrice::getAmount));
		Collection<Product> allProducts = products.findAll();
		
		return allProducts.stream()
			.sorted(Comparator.comparing(Product::getName))
			.map(product -> 
				Map.of(
						"name", product.getName(),
						"stock", stockContent.getOrDefault(product.getId(), 0),
						"price", NumberFormat.getCurrencyInstance().format(pricesContent.getOrDefault(product.getId(), BigDecimal.ZERO))
						)
			)
			.collect(Collectors.toList());
	}

	@GetMapping @RequestMapping("table") public String table(Model model) throws UnknownHostException {
		List<Map<String, Object>> c = getStoresRepresentations();
		model.addAttribute("stores", c);
		model.addAttribute("ipAddress", centralAddress.getHostAddress());
		return "table";
	}

	private List<Map<String, Object>> getStoresRepresentations() {
		List<Map<String, Object>> c = stores.getStores()
			.stream()
			.sorted(Comparator.comparing(Store::getName)
					.thenComparing(Store::getStatus)
					.thenComparing(Store::getId))
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
