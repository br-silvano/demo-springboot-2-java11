package com.example.demo.services;

import java.time.Instant;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Order;
import com.example.demo.entities.Payment;
import com.example.demo.entities.enums.OrderStatus;
import com.example.demo.repositories.OrderItemRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.util.Sorteable;
import com.example.demo.services.exceptions.ResourceBadRequestException;
import com.example.demo.services.exceptions.ResourceNotFoundException;

@Service
public class OrderService {

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	public Page<com.example.demo.entities.Order> findAll(int page, int size, String[] sort) {
		Sorteable sorting = Sorteable.of(sort);
		Pageable paging = PageRequest.of(page, size, Sort.by(sorting.getOrders()));
		return orderRepository.findAll(paging);
	}

	public Order findById(Long id) {
		Optional<com.example.demo.entities.Order> obj = orderRepository.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundException(id));
	}

	@Transactional
	public Order create(com.example.demo.entities.Order obj) {
		obj.setClient(userService.Authenticated());
		obj.setMoment(Instant.now());
		obj.setOrderStatus(OrderStatus.WATTING_PAYMENT);
		orderRepository.save(obj);
		obj.getItems().forEach(item -> item.setOrder(obj));
		orderItemRepository.saveAll(obj.getItems());
		return obj;
	}

	public Order cancel(Long id, Order obj) {
		try {
			if (id.longValue() != obj.getId().longValue()) {
				throw new ResourceBadRequestException();
			}
			Order entity = orderRepository.getById(id);
			OrderStatus changeStatus = OrderStatus.CANCELED;
			if (!(entity.getOrderStatus().equals(OrderStatus.WATTING_PAYMENT)
					|| entity.getOrderStatus().equals(OrderStatus.PAID))) {
				throw new ResourceBadRequestException(String.format("Order with status %s cannot be changed to %s",
						entity.getOrderStatus().name(), changeStatus.name()));
			}
			entity.setOrderStatus(changeStatus);
			return orderRepository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		} catch (ResourceBadRequestException e) {
			throw new ResourceBadRequestException(e.getMessage());
		}
	}

	public Order paid(Long id, Order obj) {
		try {
			if (id.longValue() != obj.getId().longValue()) {
				throw new ResourceBadRequestException();
			}
			Order entity = orderRepository.getById(id);
			OrderStatus changeStatus = OrderStatus.PAID;
			if (!entity.getOrderStatus().equals(OrderStatus.WATTING_PAYMENT)) {
				throw new ResourceBadRequestException(String.format("Order with status %s cannot be changed to %s",
						entity.getOrderStatus().name(), changeStatus.name()));
			}
			Payment pay = new Payment(null, Instant.now(), entity);
			entity.setPayment(pay);
			entity.setOrderStatus(changeStatus);
			return orderRepository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		} catch (ResourceBadRequestException e) {
			throw new ResourceBadRequestException(e.getMessage());
		}
	}

	public Order shipped(Long id, Order obj) {
		try {
			if (id.longValue() != obj.getId().longValue()) {
				throw new ResourceBadRequestException();
			}
			Order entity = orderRepository.getById(id);
			OrderStatus changeStatus = OrderStatus.SHIPPED;
			if (!entity.getOrderStatus().equals(OrderStatus.PAID)) {
				throw new ResourceBadRequestException(String.format("Order with status %s cannot be changed to %s",
						entity.getOrderStatus().name(), changeStatus.name()));
			}
			entity.setOrderStatus(changeStatus);
			return orderRepository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		} catch (ResourceBadRequestException e) {
			throw new ResourceBadRequestException(e.getMessage());
		}
	}

	public Order delivered(Long id, Order obj) {
		try {
			if (id.longValue() != obj.getId().longValue()) {
				throw new ResourceBadRequestException();
			}
			Order entity = orderRepository.getById(id);
			OrderStatus changeStatus = OrderStatus.DELIVERED;
			if (!entity.getOrderStatus().equals(OrderStatus.SHIPPED)) {
				throw new ResourceBadRequestException(String.format("Order with status %s cannot be changed to %s",
						entity.getOrderStatus().name(), changeStatus.name()));
			}
			entity.setOrderStatus(changeStatus);
			return orderRepository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		} catch (ResourceBadRequestException e) {
			throw new ResourceBadRequestException(e.getMessage());
		}
	}

}
