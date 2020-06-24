package com.jdc.app.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jdc.app.entity.Author;
import com.jdc.app.entity.Book;
import com.jdc.app.entity.Category;
import com.jdc.app.util.ConnectionManager;

public class BookService {
	
	private static BookService INSTANCE;
	private static final String sqlSearch = "select b.id, b.name book_name, b.price price, b.released_date released_date, b.remark remark, c.id category_id, c.name category_name, a.id author_id, a.name author_name from book b join category c on b.category_id = c.id join author a on b.author_id = a.id where 1 = 1";
	
	private BookService() {}
	
	public static BookService getInstance() {
		if(null == INSTANCE)
			INSTANCE = new BookService();
		return INSTANCE;
	}
	
	public void add(Book book) {
		String sql = "Insert into book(name, price, released_date, remark, category_id, author_id) values(?, ?, ?, ?, ?, ?)";
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			
			stmt.setString(1, book.getName());
			stmt.setInt(2, book.getPrice());
			stmt.setDate(3, Date.valueOf(book.getReleaseDate()));
			stmt.setString(4, book.getRemark());
			stmt.setInt(5, book.getCategory().getId());
			stmt.setInt(6, book.getAuthor().getId());
			stmt.executeUpdate();
			
			ResultSet rs = stmt.getGeneratedKeys();
			while (rs.next()) {
				book.setId(rs.getInt(1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(Book book) {
		String sql = "update book set name = ?, price = ?, released_date = ?, remark = ?, category_id = ?, author_id = ? where id = ?";
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1, book.getName());
			stmt.setInt(2, book.getPrice());
			stmt.setDate(3, Date.valueOf(book.getReleaseDate()));
			stmt.setString(4, book.getRemark());
			stmt.setInt(5, book.getCategory().getId());
			stmt.setInt(6, book.getAuthor().getId());
			stmt.setInt(7, book.getId());
			stmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void delete(Book book) {
		String sql = "delete from book where id = ?";
		
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, book.getId());
			stmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Book> findAll() {
		return findByParams(null, null, null, null);
	}
	
	public List<Book> findByParams(Category category, Author authorName, LocalDate releaseDate, String bookName) {
		List<Book> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder(sqlSearch);
		List<Object> params = new LinkedList<>();
		
		if(null != category) {
			sb.append(" and c.name = ?");
			params.add(category.toString());
		}

		if(null != authorName) {
			sb.append(" and a.name = ?");
			params.add(authorName.toString());
		}
		
		if(null != releaseDate) {
			sb.append(" and b.released_date >= ?");
			params.add(Date.valueOf(releaseDate));
		}
		
		if(null != bookName  && !bookName.isEmpty()) {
			sb.append(" and b.name LIKE ?");
			params.add("%".concat(bookName).concat("%"));
		}
		
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sb.toString())){
			
			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(getObject(rs));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public Book getObject(ResultSet rs) throws SQLException {
		Book b = new Book();
		b.setId(rs.getInt("id"));
		b.setName(rs.getString("book_name"));
		b.setPrice(rs.getInt("price"));
		b.setReleaseDate(rs.getDate("released_date").toLocalDate());
		b.setRemark(rs.getString("remark"));
		
		Category c = new Category();
		c.setId(rs.getInt("category_id"));
		c.setName(rs.getString("category_name"));
		
		Author a = new Author();
		a.setId(rs.getInt("author_id"));
		a.setName(rs.getString("author_name"));
		
		b.setCategory(c);
		b.setAuthor(a);
		
		return b;
	}
	
	public void imgUpload(Book book) {
		String sql = "update book set book_image = ? where id = ?";
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1, book.getImage());
			stmt.setInt(2, book.getId());
			stmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getImage(Book book) {
		String sql = "select book_image from book where id = ?";
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){

			stmt.setInt(1, book.getId());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getString("book_image");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
