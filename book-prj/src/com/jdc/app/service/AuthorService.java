package com.jdc.app.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jdc.app.entity.Author;
import com.jdc.app.util.ConnectionManager;

public class AuthorService {

	private static AuthorService INSTANCE;
	
	private AuthorService() {}
	
	public static AuthorService getInstance() {
		if(null == INSTANCE) 
			INSTANCE = new AuthorService();
		return INSTANCE;
	}
	
	public void add(Author a) {
		String sql = "INSERT INTO author(name, age, country) VALUES(?, ?, ?)";
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			
			stmt.setString(1, a.getName());
			stmt.setInt(2, a.getAge());
			stmt.setString(3, a.getCountry());
			stmt.executeUpdate();
			
			ResultSet rs = stmt.getGeneratedKeys();
			while (rs.next()) {
				a.setId(rs.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(Author a) {
		String sql = "update author set name = ?, age = ?, country = ? where id = ?";
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)){
			
			stmt.setString(1, a.getName());
			stmt.setInt(2, a.getAge());
			stmt.setString(3, a.getCountry());
			stmt.setInt(4, a.getId());
			stmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void delete(Author a) {
		String sql = "delete from author where id = ?";
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, a.getId());
			stmt.executeUpdate(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Author> findAll() {
		return findByParams(null, 0, null);
	}
	
	public List<Author> findByParams(String name, int age, String country) {
		String sql = "select * from author where 1 = 1";
		List<Author> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder(sql);
		List<Object> params = new LinkedList<>();
		
		if(null != name && !name.isEmpty()) {
			sb.append(" and name like ?");
			params.add("%".concat(name).concat("%"));
		}
				
		if(age < 0) {
			sb.append(" and age >= ?");
			params.add(age);
		}
		
		if(null != country && !country.isEmpty()) {
			sb.append(" and country like ?");
			params.add("%".concat(country).concat("%"));
		}
		
		try (Connection conn = ConnectionManager.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sb.toString())){
			
			for(int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}
			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Author a = new Author();
				a.setId(rs.getInt("id"));
				a.setName(rs.getString("name"));
				a.setAge(rs.getInt("age"));
				a.setCountry(rs.getString("country"));
				list.add(a);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
}
