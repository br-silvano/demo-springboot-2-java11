package com.example.demo.repositories.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class Sorteable {

	final String[] sort;
	final List<Order> orders = new ArrayList<Order>();

	private Sorteable(String[] sort) {
		this.sort = sort;

		if (sort[0].contains(",")) {
			for (String sortOrder : sort) {
				String[] _sort = sortOrder.split(",");
				orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
			}
		} else {
			orders.add(new Order(getSortDirection(sort[1]), sort[0]));
		}
	}
	
	public static Sorteable of(String[] sort) {
		return new Sorteable(sort);
	}

	private Sort.Direction getSortDirection(String direction) {
		if (direction.equals("asc")) {
			return Sort.Direction.ASC;
		} else if (direction.equals("desc")) {
			return Sort.Direction.DESC;
		}

		return Sort.Direction.ASC;
	}

	public String[] getSort() {
		return sort;
	}

	public List<Order> getOrders() {
		return orders;
	}

}
