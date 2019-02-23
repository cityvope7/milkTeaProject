package com.chinasofti.milktea.entity;

/**
 * 
 * 
*
* @author wzh
* @version 创建时间：2019年2月18日 下午2:23:07
* 类说明
 */
public class MilkTea {
	private int id;
	private String classes;
	private String kinds;
	private int price;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClasses() {
		return classes;
	}
	public void setClasses(String classes) {
		this.classes = classes;
	}
	public String getKinds() {
		return kinds;
	}
	public void setKinds(String kinds) {
		this.kinds = kinds;
	}


	@Override
	public String toString() {
		return "MilkTea [锟斤拷牛锟?" + id +   ", 锟斤拷锟狡ｏ拷" + classes +   ", 锟斤拷锟?" + kinds +   ", 锟桔革拷" + price +   "]";
	}
	public MilkTea(int id, String classes, String kinds, int price) {
		super();
		this.id = id;
		this.classes = classes;
		this.kinds = kinds;
		this.price = price;
	}
	public MilkTea(String classes, String kinds) {
		super();
		this.classes = classes;
		this.kinds = kinds;
	}
	public MilkTea(String classes, String kinds, int price) {
		super();
		this.classes = classes;
		this.kinds = kinds;
		this.price = price;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public MilkTea(int id) {
		super();
		this.id = id;
	}
	public MilkTea(String classes) {
		super();
		this.classes = classes;
	}

	
}
