package com.example.mahua.funage.event;

import java.util.List;

import com.example.mahua.funage.projection.LoginActivity.ItemType;

public class ItemListEvent<T> {
	
	private ItemType type;
	
	private List<T> items;
	
	public ItemListEvent(List<T> items, ItemType type) {
		this.items = items;
		this.type = type;
	}
	
	public List<T> getItems() {
		return items;
	}
	
	public ItemType getType() {
		return type;
	}
	
	public void setType(ItemType type) {
		this.type = type;
	}
	
	public void setItems(List<T> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "ItemListEvent [type=" + type + ", items=" + items + "]";
	}
	
}
