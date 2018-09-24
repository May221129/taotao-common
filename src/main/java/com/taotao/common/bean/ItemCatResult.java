package com.taotao.common.bean;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 最终的类目数据，用于返回给前台系统的。
 */
public class ItemCatResult {
	
	/**
	 * 这个注解是Jackson的注解，
	 * 作用：指定这个对象转化为json数据时的名称为"data"。
	 */
	@JsonProperty("data")
	private List<ItemCatData> itemCats = new ArrayList<ItemCatData>();

	public List<ItemCatData> getItemCats() {
		return itemCats;
	}

	public void setItemCats(List<ItemCatData> itemCats) {
		this.itemCats = itemCats;
	}

}
