package com.jdc.app.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jdc.app.entity.Author;
import com.jdc.app.entity.Book;
import com.jdc.app.entity.Category;
import com.jdc.app.entity.Sale;
import com.jdc.app.entity.SaleDTO;
import com.jdc.app.entity.SaleDetail;
import com.jdc.app.util.ConnectionManager;

public class SaleService {

	private static SaleService INSTANCE;
	
	private SaleService() {}
	
	public static SaleService getInstance() {
		if(null == INSTANCE)
			INSTANCE = new SaleService();
		return INSTANCE;
	}
	
	public void insert(SaleDTO saleDTO) {
		String saleInsert = "insert into sale (sale_date, sale_time, tax) values (?, ?, ?)";
		String detailInsert = "insert into sale_detail (quantity, unit_price, book_id, sale_id, book_category_id, book_author_id) values (?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement insertSale = conn.prepareStatement(saleInsert, Statement.RETURN_GENERATED_KEYS);
				PreparedStatement insertDetail = conn.prepareStatement(detailInsert)){
			
			Sale sale = saleDTO.getSale();
			
			insertSale.setDate(1, Date.valueOf(sale.getSaleDate()));
			insertSale.setTime(2, Time.valueOf(sale.getSaleTime()));
			insertSale.setInt(3, sale.getTax());
			insertSale.executeUpdate();
			
			ResultSet rs = insertSale.getGeneratedKeys();
			while(rs.next()) {
				sale.setId(rs.getInt(1));
				List<SaleDetail> details = saleDTO.getDetails();
				
				for (SaleDetail sd : details) {
					insertDetail.setInt(1, sd.getQuantity());
					insertDetail.setInt(2, sd.getUnitPrice());
					insertDetail.setInt(3, sd.getBook().getId());
					insertDetail.setInt(4, sd.getSale().getId());
					insertDetail.setInt(5, sd.getCategory().getId());
					insertDetail.setInt(6, sd.getAuthor().getId());
					
					insertDetail.addBatch();
				}
				
				insertDetail.executeBatch();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(SaleDTO saleDTO) {
		String insertSD = "insert into sale_detail (quantity, unit_price, book_id, sale_id, book_category_id, book_author_id) values (?, ?, ?, ?, ?, ?)";
		String updateSD = "update sale_detail set quantity = ?, unit_price = ? where id = ?";
		String deleteSD = "delete from sale_detail where id = ?";
		
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement sdInsert = conn.prepareStatement(insertSD);
				PreparedStatement sdUpdate = conn.prepareStatement(updateSD);
				PreparedStatement sdDelete = conn.prepareStatement(deleteSD)){
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public List<Sale> findSale(LocalDate dateFrom, LocalDate dateTo) {
		String sql = "SELECT * FROM sale where 1 = 1";
		
		List<Sale> result = new ArrayList<>();
		StringBuilder sb = new StringBuilder(sql);
		List<Object> params = new LinkedList<>();
		
		if(null != dateFrom && dateFrom.isBefore(dateTo)) {
			sb.append(" and sale_date >= ?");
			params.add(Date.valueOf(dateFrom));
		}
		
		if(null != dateTo && dateTo.isAfter(dateFrom)) {
			sb.append(" and sale_date <= ?");
			params.add(Date.valueOf(dateTo));
		}
		
		try(Connection conn = ConnectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sb.toString())){
			
			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Sale s = new Sale();
				s.setId(rs.getInt("id"));
				s.setSaleDate(rs.getDate("sale_date").toLocalDate());
				s.setSaleTime(rs.getTime("sale_time").toLocalTime());
				result.add(s);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public List<SaleDetail> findSaleDetail(Category category, String bookName, LocalDate dateFrom, LocalDate dateTo) {
		String sql = "select c.name category_name, b.name book_name, a.name author_name, s.tax sale_tax, sd.id sd_id \r\n" + 
				" sd.sale_id sale_id, sd.book_id book_id, sd.book_category_id book_category_id, sd.book_author_id book_author_id, sd.unit_price unit_price, sd.quantity quantity, sum(sd.unit_price * sd.quantity + s.tax) total from sale_detail sd \r\n" + 
				" join book b on sd.book_id = b.id join sale s on sd.sale_id = s.id join category c on sd.book_category_id = c.id\r\n" + 
				" join author a on sd.book_author_id = a.id where 1 = 1";
		
		List<SaleDetail> result = new ArrayList<>();
		StringBuilder sb = new StringBuilder(sql);
		List<Object> params = new LinkedList<>();
		
		if(null != category) {
			sb.append(" and c.name like ?");
			params.add(category.getName());
		}
		
		if(null != dateFrom && dateFrom.isBefore(dateTo)) {
			sb.append(" and s.sale_date >= ?");
			params.add(Date.valueOf(dateTo));
		}
		
		if(null != dateTo && dateTo.isAfter(dateFrom)) {
			sb.append(" and s.sale_date <= ?");
			params.add(Date.valueOf(dateFrom));
		}
		
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sb.toString())){
			
			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SaleDetail sd = new SaleDetail();
				sd.setId(rs.getInt("sd_id"));
				sd.setQuantity(rs.getInt("quantity"));
				sd.setUnitPrice(rs.getInt("unit_price"));
				
				Sale s = new Sale();
				s.setId(rs.getInt("sale_id"));
				s.setTax(rs.getInt("sale_tax"));
				
				Book b = new Book();
				b.setId(rs.getInt("book_id"));
				b.setName("book_name");
				
				Category c = new Category();
				c.setId(rs.getInt("book_category_id"));
				c.setName(rs.getString("category_name"));
				
				Author a = new Author();
				a.setId(rs.getInt("book_author_id"));
				a.setName(rs.getString("author_name"));
				
				sd.setAuthor(a);
				sd.setBook(b);
				sd.setCategory(c);
				sd.setSale(s);
				
				result.add(sd);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
