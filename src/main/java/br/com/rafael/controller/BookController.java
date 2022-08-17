package br.com.rafael.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rafael.model.Book;
import br.com.rafael.proxy.CambioProxy;
import br.com.rafael.repository.BookRepository;

@RestController
@RequestMapping("book-service")
public class BookController {
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private BookRepository repository;
	
	@Autowired
	private CambioProxy proxy;
	
	@GetMapping(value = "/{id}/{currency}")
	public Book findBook(
			@PathVariable("id") Long id,
			@PathVariable("currency") String currency) {
		
		var book = repository.findById(id).get();
		
		if(book == null) {
			throw new RuntimeException("Book nao encontrado");
		}
		
		var cambio = proxy.getCambio(book.getPrice(), "USD", currency);
		var port = environment.getProperty("local.server.port");
		
		book.setEnviroment("Book port: "+ port + " Cambio port: " + cambio.getEnviroment());
		book.setPrice(cambio.getConvertedValue());
		return book;
	}
	/**@GetMapping(value = "/{id}/{currency}")
	public Book findBook(
			@PathVariable("id") Long id,
			@PathVariable("currency") String currency) {
		
		var book = repository.findById(id).get();
		if(book == null) {
			throw new RuntimeException("Book nao encontrado");
		}
		
		HashMap<String, String> params =new HashMap<>();
		params.put("amount", book.getPrice().toString());
		params.put("from", "USD");
		params.put("to", currency);
		
		var response = new RestTemplate().getForEntity("http://localhost:8000/cambio-service/" + "{amount}/{from}/{to}", Cambio.class, params);
		var port = environment.getProperty("local.server.port");
		
		var cambio = response.getBody();
		
		book.setEnviroment(port);
		book.setPrice(cambio.getConvertedValue());
		return book;
	}*/

}
