package io.konveyor.demo.gateway.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.konveyor.demo.gateway.command.ProductCommand;
import io.konveyor.demo.gateway.model.OrderItem;
import io.konveyor.demo.gateway.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import rx.Observable;

@Component
public class InventoryRepository extends GenericRepository{
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${hystrix.threadpool.ProductsThreads.coreSize}")
	private int threadSize;
	
	@Value("${services.inventory.url}")
	String inventoryServiceURL;
	
	public List<OrderItem> getProductDetails(List<OrderItem> items){
		List<OrderItem> detailedItems = new ArrayList<>();
		for(int index= 0; index < items.size();) {
			List<Observable<OrderItem>> observables = new ArrayList<>();
			int batchLimit = Math.min( index + threadSize, items.size() );
			for( int batchIndex = index; batchIndex < batchLimit; batchIndex++ )
			{
				observables.add( new ProductCommand( items.get(batchIndex), inventoryServiceURL, restTemplate ).toObservable() );
			}
			Observable<OrderItem[]> zipped = Observable.zip( observables, objects->
			{
				OrderItem[] detailed = new OrderItem[objects.length];
				for( int batchIndex = 0; batchIndex < objects.length; batchIndex++ )
				{
					detailed[batchIndex] = (OrderItem)objects[batchIndex];
				}
				return detailed;
			} );
			Collections.addAll( detailedItems, zipped.toBlocking().first() );
			index += threadSize;
		}
		return detailedItems;
	}
	
	// @HystrixCommand(commandKey = "AllProducts", fallbackMethod = "getFallbackProducts", commandProperties = {
    //         @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
	// })
	public List<Product> findAll(Pageable pageable) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(inventoryServiceURL)
				.queryParam("page", pageable.getPageNumber())
				.queryParam("size", pageable.getPageSize())
				.queryParam("sort", getSortString(pageable));
		ResponseEntity<List<Product>> responseEntity = 
				  restTemplate.exchange(
						  builder.toUriString(),
						  HttpMethod.GET,
						  null,
						  new ParameterizedTypeReference<List<Product>>() {}
				  );
		List<Product> orders = responseEntity.getBody();
		return orders;
	}
	
	// @HystrixCommand(commandKey = "Products", fallbackMethod = "getFallbackProduct", commandProperties = {
    //         @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
	// })
	public Product getProductById(Long id) {
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(inventoryServiceURL)
				.pathSegment( "{product}");		
		Product p = restTemplate.getForObject(
				builder.buildAndExpand(id).toUriString(), 
				Product.class);
		//Trigger fallback if no result is obtained.
		if (p == null) {
			throw new RuntimeException();
		}
		return p;
	}
	
	public List<Product> getFallbackProducts(Pageable pageable, Throwable e) {
		return new ArrayList<Product>();
	}
	
	public Product getFallbackProduct(Long id, Throwable e) {
		Product p = new Product();
		p.setId(id);
		p.setName("Unknown");
		p.setDescription("Unknown");	
		return p;
	}

}
