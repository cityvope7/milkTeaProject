package com.chinasofti.milktea.dao.milktea.milkmeamaoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.junit.Test;

import com.chinasofti.milktea.dao.framework.JdbcDao;
import com.chinasofti.milktea.dao.milktea.MilkTeaDao;
import com.chinasofti.milktea.entity.MilkTea;

/**
 * 
 * 
 *
 * @author wzh
 * @version 创建时间：2019年2月18日 下午2:23:07 类说明
 */
public class MilkTeaDaoImpl implements MilkTeaDao {
	private static Connection conn = JdbcDao.getConnection();
	static Scanner sc = new Scanner(System.in);

	/**
	 * 查询奶茶店所有信息。包括雇员和BOSS都可以看到的信息
	 */
	@Override
	public void findAll() {
		String sql = "SELECT DISTINCT *FROM milktea a,salesandincome b WHERE a.`id`=b.`id`;";
		int id = 0;
		String classes;
		String kinds;
		int price = 0;
		int stocks = 0;
		int salescount = 0;
		int money = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("id");
				classes = rs.getString("classes");
				kinds = rs.getString("kinds");
				price = rs.getInt("price");
				stocks = rs.getInt("stocks");
				salescount = rs.getInt("salescount");
				money = rs.getInt("money");
				ResultSetMetaData metaData = rs.getMetaData();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					if (i != 6) {
						System.out.print(metaData.getColumnName(i) + "\t");
					}
				}
				System.out.println();
				System.out.print(id + "\t");
				System.out.print(classes + "\t");
				System.out.print(kinds + "\t");
				System.out.print(price + "\t");
				System.out.print(stocks + "\t");
				System.out.print(salescount + "\t" + "\t");
				System.out.println(money + "\t");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 找到名称为classes奶茶的ID
	 */
	@Override
	public int findIdByClasses(String classes) {
		String sql = "SELECT * FROM milktea WHERE classes=?;";
		int id = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, classes);
			ResultSet rs = ps.executeQuery();
			rs.next();
			id = rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;

	}

	/**
	 * 给奶茶店补货，添加奶茶的品种，分类，价格，库存
	 */
	@Override
	public int addmilkTea(String classes, String kinds, double price, int stocks) {
//		int no = milkTea.getNo();
//		String classes = milkTea.getClasses();
//		String kinds = milkTea.getKinds();
		List<String> list = getClasses();
		if (list.contains(classes)) {
			System.out.println("该饮品已存在，请勿重复上架！");
			return 0;
		} else {
			String sql = "INSERT INTO milkTea (classes,kinds,price,stocks) VALUES(?,?,?,?);";
			int count = 0;
			int id = 0;
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, classes);
				ps.setString(2, kinds);
				ps.setDouble(3, price);
				ps.setInt(4, stocks);
				count = ps.executeUpdate();
				id = findIdByClasses(classes);
				count = updateIncomeById(id);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return count;
		}

	}

	/***
	 * 下架相关奶茶的销售信息
	 */
	@Override
	public int deleteById(int id) {
		String sql = "DELETE FROM milktea WHERE Id=?;";
		int count = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			count = ps.executeUpdate();
			deleteIncomeById(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 下架相关奶茶的库存信息
	 */
	public int deleteIncomeById(int id) {
		String sql = "DELETE FROM salesandincome WHERE id=?";
		int count = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 更新相关奶茶的销售额和销量信息
	 */
	public int updateIncomeById(int id) {
		String sql = "INSERT INTO salesandincome (id,salescount,money) VALUES (?,?,?);";
		int count = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.setInt(2, 0);
			ps.setInt(3, 0);
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 下架名称为classes的奶茶的相关信息
	 */
	@Override
	public int deleteByClasses(String classes) {
		if (classes == null || classes.equals("")) {
			return 0;
		}
		String sql = "delete from milktea where classes=?";
		int count = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, classes);
			count = ps.executeUpdate();
			int id = findIdByClasses(classes);
			deleteIncomeById(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 补货
	 */

	public int updateStocks(MilkTea milkTea, int amount) {

		String sql1 = "SELECT stocks FROM milktea WHERE id=?;";
		int count = 0;
		int total = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql1);
			ps.setInt(1, milkTea.getId());
			ResultSet executeQuery = ps.executeQuery();
			while (executeQuery.next()) {
				int stocks = executeQuery.getInt("stocks");
				total = stocks + amount;
			}
			updateMilkTeaStocks(milkTea.getId(), total);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 同上，为补货相关操作
	 */

	public int updateMilkTeaStocks(int id, int newStocks) {
		String sql = "UPDATE milktea SET stocks=? WHERE id=?;";
		int count = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, newStocks);
			ps.setInt(2, id);
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;

	}

	/**
	 * 执行补货动作
	 */
	@Override
	public int addStocks(int id, int cost, int amount) {
		MilkTea milkTea = new MilkTea(id);
		int count = updateStocks(milkTea, amount);
		String sql1 = "SELECT money FROM salesandincome WHERE id=?;";
		int money = 0;
		try {
			PreparedStatement ps1 = conn.prepareStatement(sql1);
			ps1.setInt(1, id);
			ResultSet rs = ps1.executeQuery();
			while (rs.next()) {
				money = rs.getInt("money");
				money -= cost * amount;
			}
			updateMoney(id, money);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

	/***
	 * 
	 * @param id
	 * @param money
	 * @return
	 */

	public int updateMoney(int id, int money) {
		String sql = "UPDATE salesandincome SET money=? WHERE id=?;";
		int count = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, money);
			ps.setInt(2, id);
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;

	}

	@Override
	public int sale(int id, int count) {
		String sql = "SELECT * FROM milktea WHERE id=?;";
		int cost = 0;
		int newstocks = 0;
		int count2 = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int price = rs.getInt("price");
				int stocks = rs.getInt("stocks");
				// 总花费
				cost = count * price;
				int money = getMoneyFromIncomeById(id);
				int newmoney = money + cost;
				updateMoney(id, newmoney);
				updateSalesCount(id, count);
				// 剩余库存
				newstocks = stocks - count;
				count2 = updateMilkTeaStocks(id, newstocks);
//				int updateSalesCount = updateSalesCount(id, count);
//				if(updateSalesCount==1) {
//					flag=true;
//				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count2;
	}

	@Override
	public int getMoneyFromIncomeById(int id) {
		String sql = "SELECT * FROM salesandincome WHERE id=?;";
		int money = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				money = rs.getInt("money");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return money;
	}

	@Override
	public int getPriceById(int id) {
		String sql = "SELECT price FROM milktea WHERE id=?;";
		int queryPrice = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				queryPrice = rs.getInt("price");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return queryPrice;

	}

	public void saleBills() {
		String sql = "SELECT DISTINCT *FROM milktea a,salesandincome b WHERE a.`id`=b.`id`;";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			System.out.print(metaData.getColumnName(2) + "\t");
			System.out.print(metaData.getColumnName(5) + "\t");
			System.out.print(metaData.getColumnName(7) + "\t");
			System.out.println(metaData.getColumnName(8) + "\t");
			while (rs.next()) {
				System.out.print(rs.getString("classes") + "\t");
				System.out.print(rs.getString("stocks") + "\t");
				System.out.print(rs.getInt("salescount") + "\t" + "\t");
				System.out.println(rs.getInt("money") + "\t");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void totalIncome() {
		String sql = "SELECT * FROM salesandincome;";
		int income = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			System.out.println("totalIncome");
			while (rs.next()) {
				int money = rs.getInt("money");
				income += money;
			}
			System.out.println(income);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public int updateSalesCount(int id, int count) {
		int salesCount = getSalesCount(id);
		int newsalesCount = salesCount + count;
		int executeUpdate = 0;
		String sql = "update salesandincome set salescount=? where id=?;";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, newsalesCount);
			ps.setInt(2, id);
			executeUpdate = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return executeUpdate;
	}

	@Override
	public int getSalesCount(int id) {
		String sql = "select salescount from salesandincome where id=?;";
		int salescount = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				salescount = rs.getInt("salescount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return salescount;
	}

	@Override
	public void selectMilkTeaCostumer() {
		String sql = "SELECT id,classes,kinds,price FROM milktea;";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				System.out.print(metaData.getColumnName(i) + "\t");
			}
			System.out.println();
			while (rs.next()) {
				int id = rs.getInt("id");
				String classes = rs.getString("classes");
				String kinds = rs.getString("kinds");
				int price = rs.getInt("price");
				System.out.print(id + "\t");
				System.out.print(classes + "\t");
				System.out.print(kinds + "\t");
				System.out.println(price + "\t");
			}
			System.out.println();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	@Override
	public void vipDay() {
//		String sql=""
//		Date date = new Date();
//		int date2 = date.getDate();
//		if(date2==18) {
//			
//		}
	}

	@Override
	public List<String> getClasses() {
		String sql = "SELECT classes FROM milktea;";
		List<String> list = new ArrayList<String>();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String classes = rs.getString("classes");
				list.add(classes);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void register(String username, String password) {
		List<String> list = getUserName();
		if (list.contains(username)) {
			System.out.println("此用户已被注册，请重新输入用户名!");
		} else {
			String sql = "INSERT INTO login (username,PASSWORD) VALUES (?,?);";
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, password);
				int count = ps.executeUpdate();
				if (count != 0) {
					System.out.println("注册成功！请登录！");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public List<String> getUserName() {
		List<String> list = new ArrayList<String>();
		String sql = "SELECT * FROM login;";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String username = rs.getString("username");
				list.add(username);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;

	}

	@Override
	public int storeOrderNumber(String orderNumber) {
		String sql = "INSERT INTO orderlist (ordernumber) VALUES (?);";
		int count = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, orderNumber);
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public void getOrder() {
		String sql = "SELECT ordernumber FROM orderlist;";
		List<String> list = new ArrayList<String>();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String orderNumber = rs.getString("ordernumber");
				list.add(orderNumber);
				Iterator<String> iterator = list.iterator();
				while (iterator.hasNext()) {
					System.out.println(iterator.next());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void login(String username, String password) {
		boolean flag = true;
		boolean flag2 = true;
		boolean flag3 = true;
		System.out.println("登陆成功！流浪地球奶茶店欢迎您的光临！请选择以下服务：");
		if ("root".equals(username)) {
			while (flag) {
				System.out.println("1.查看今日经营状况     2.查看今日总收入   3.查看销售账单     4.查看今日份订单    5.登出");
				int i = sc.nextInt();
				switch (i) {
				case 1:
					findAll();
					break;
				case 2:
					totalIncome();
					break;
				case 3:
					saleBills();
					break;
				case 4:
					getOrder();
					break;
				case 5:
					flag = false;
					break;
				default:
					System.out.println("输入有误！");
					break;
				}
			}
		} else if ("saler".equals(username)) {
			while (flag2) {
				System.out.println("1.查看所有奶茶价目表    2.选购奶茶   3.补货   4.下架奶茶   5.添加新品奶茶   6.登出");
				int input = sc.nextInt();
				switch (input) {
				case 1:
					selectMilkTeaCostumer();
					break;
				case 2:
					System.out.println("请输入奶茶编号");
					int i2 = sc.nextInt();
					System.out.println("请输入购买数量：");
					int j2 = sc.nextInt();
					sale(i2, j2);
					int queryPriceSaler=getPriceById(i2);
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String formatDate = simpleDateFormat.format(date);
					int hashCodeV = UUID.randomUUID().toString().hashCode();
					int absHashCodeV = Math.abs(hashCodeV);
					System.out.println("购买成功！共售出" + j2 + "件,共消费"+queryPriceSaler*j2+"元");
					System.out.println("您的订单号为："+formatDate + "   " + absHashCodeV);
					String salerOrderNumber = formatDate + "  "+ absHashCodeV;
					storeOrderNumber(salerOrderNumber);
					break;
				case 3:
					System.out.println("请输入要补货的奶茶id");
					int id3 = sc.nextInt();
					System.out.println("请输入奶茶的成本价");
					int costPrice3 = sc.nextInt();
					System.out.println("请输入补货的数量");
					int amount3 = sc.nextInt();
					int count3 = addStocks(id3, costPrice3, amount3);
					System.out.println(count3);
//					if (count3 !) {
//						
//						System.out.println("补货成功！");
//					}
					break;
				case 4:
					System.out.println("请输入要下架的奶茶id");
					int id4 = sc.nextInt();
					int count4 = deleteById(id4);
					if (count4 != 0) {
						System.out.println("下架成功！");
					}
					break;
				case 5:
					System.out.println("请输入奶茶名称：");
					sc = new Scanner(System.in);
					String classes = sc.nextLine();
					System.out.println("请输入类别：");
					String kinds = sc.nextLine();
					System.out.println("请输入单价：");
					int price = sc.nextInt();
					System.out.println("请输入上架数量：");
					int amount = sc.nextInt();
					int count = addmilkTea(classes, kinds, price, amount);
					if (count != 0) {
						System.out.println("上架成功！");
					}
					break;
				case 6:
					flag2 = false;
					break;
				default:
					System.out.println("请求的服务不存在！请重新输入！");
				}
			}
		} else {
			while (flag3) {
				System.out.println("1.查看所有奶茶价目表    2.选购奶茶   3.登出");
				int want = sc.nextInt();
				switch (want) {
				case 1:
					selectMilkTeaCostumer();
					break;
				case 2:
					System.out.println("请输入奶茶编号");
					int i2 = sc.nextInt();
					System.out.println("请输入购买数量：");
					int j2 = sc.nextInt();
					int queryPriceCustomer = getPriceById(i2);
					sale(i2, j2);
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String formatDate = simpleDateFormat.format(date);
					int hashCodeV = UUID.randomUUID().toString().hashCode();
					int absHashCodeV = Math.abs(hashCodeV);
					String customerOrderNumber = formatDate + "  "+absHashCodeV;
					System.out.println("购买成功！共售出" + j2 + "件,共消费:" + j2 * queryPriceCustomer + "元");
					System.out.println("您的订单号为：" + formatDate + "   " + absHashCodeV);
					storeOrderNumber(customerOrderNumber);
					break;
				case 3:
					flag3 = false;
					break;
				}
			}
		}
	}
}
