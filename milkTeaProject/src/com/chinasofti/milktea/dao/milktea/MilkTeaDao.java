package com.chinasofti.milktea.dao.milktea;

import java.util.List;

/**
 * 
 * 
*
* @author wzh
* @version 创建时间：2019年2月18日 下午2:23:07
* 类说明
 */
public interface MilkTeaDao {
	void findAll();
	int findIdByClasses(String classes);
	int addmilkTea(String classes,String kinds,double price,int stocks);
	int deleteById(int id);
	int deleteByClasses(String classes);
	int addStocks(int id,int cost,int amount);
	int sale(int id,int amount);
	int getMoneyFromIncomeById(int id);
	int getPriceById(int id);
	void saleBills();
	void totalIncome();
	int updateSalesCount(int id,int count);
	int getSalesCount(int id);
	void selectMilkTeaCostumer();
	void vipDay();
	List getClasses();
	void register(String username,String password);
	int storeOrderNumber(String orderNumber);
	void getOrder();
}
