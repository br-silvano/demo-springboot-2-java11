package com.example.demo.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.entities.Order;
import com.example.demo.services.OrderService;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderResource {
	
	@Autowired
	private OrderService service;
	
	@PreAuthorize("hasAuthority('READ')")
	@GetMapping
	public ResponseEntity<Page<Order>> findAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,desc") String[] sort) {
		return ResponseEntity.ok().body(service.findAll(page, size, sort));
	}
	
	@PreAuthorize("hasAuthority('READ')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<Order> findById(@PathVariable Long id) {
		Order obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	@PreAuthorize("hasAuthority('WRITE')")
	@PostMapping
	public ResponseEntity<Order> create(@RequestBody Order obj) {
		obj = service.create(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).body(obj);
	}
	
	@PreAuthorize("hasAuthority('WRITE')")
	@PutMapping(value = "/{id}/cancel")
	public ResponseEntity<Order> cancel(@PathVariable Long id, @RequestBody Order obj) {
		obj = service.cancel(id, obj);
		return ResponseEntity.ok(obj);
	}
	
	@PreAuthorize("hasAuthority('WRITE')")
	@PutMapping(value = "/{id}/paid")
	public ResponseEntity<Order> paid(@PathVariable Long id, @RequestBody Order obj) {
		obj = service.paid(id, obj);
		return ResponseEntity.ok(obj);
	}
	
	@PreAuthorize("hasAuthority('WRITE')")
	@PutMapping(value = "/{id}/shipped")
	public ResponseEntity<Order> shipped(@PathVariable Long id, @RequestBody Order obj) {
		obj = service.shipped(id, obj);
		return ResponseEntity.ok(obj);
	}
	
	@PreAuthorize("hasAuthority('WRITE')")
	@PutMapping(value = "/{id}/delivered")
	public ResponseEntity<Order> delivered(@PathVariable Long id, @RequestBody Order obj) {
		obj = service.delivered(id, obj);
		return ResponseEntity.ok(obj);
	}
	
}
