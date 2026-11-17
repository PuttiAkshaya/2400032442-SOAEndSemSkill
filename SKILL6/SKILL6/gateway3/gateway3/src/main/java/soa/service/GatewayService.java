package soa.service;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class GatewayService {
	DiscoveryClient DC;
	RestTemplate RT;
	public GatewayService(DiscoveryClient DC) {
		this.DC=DC;
		this.RT=new RestTemplate();
	}
	private String getBookServiceUrl() {
		List<ServiceInstance> instances=DC.getInstances("library");
		if(instances.isEmpty()) {
			throw new RuntimeException("library-SERVICE is not available");
		}
		return instances.get(0).getUri().toString();
	}
	public ResponseEntity<String> addBook(String requestBody) {
		String url = getBookServiceUrl() + "/api/books";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request =
				new HttpEntity<>(requestBody,headers);
		return RT.exchange(url,HttpMethod.POST,request,String.class);
		}
	// VIEW ALL BOOKS
	public ResponseEntity<String> getAllBooks() {
		String url =getBookServiceUrl() + "/api/books";
		return RT.exchange(
				url, HttpMethod.GET,
				null,String.class
				);
		}
	// VIEW BOOK BY ID
	public ResponseEntity<String> getBookById(Long id) {
	    String url = getBookServiceUrl() + "/api/books/" + id;

	    try {
	        return RT.exchange(
	                url,
	                HttpMethod.GET,
	                null,
	                String.class
	        );
	    }
	    catch (HttpClientErrorException.NotFound e) {
	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body("Book not found");
	    }
	}
	// UPDATE BOOK
	public ResponseEntity<String> updateBook(Long id,String requestBody) {
		String url = getBookServiceUrl() + "/api/books/" + id;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(requestBody,headers);
		return RT.exchange(
				url, HttpMethod.PUT,
				request, String.class
				);
		}
	// DELETE BOOK
	public ResponseEntity<String> deleteBook(Long id) {
		String url =getBookServiceUrl() + "/api/books/" + id;
		return RT.exchange( url, HttpMethod.DELETE, null, String.class );
		}
	}
