package com.jdc.app.service;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.jdc.app.entity.Category;
import com.jdc.app.util.ConnectionManager;

public class CategoryService {

	private static CategoryService INSTANCE;
	private final String insert = "insert into category (name) values (?)";
	private final String update = "update category name = ? where id = ?";
	private final String delete = "delete from category where id = ?";

	public CategoryService() {}
	
	public static CategoryService getInstance() {
		if(INSTANCE == null) 
			INSTANCE = new CategoryService();
		return INSTANCE;
	}
	
	public void add(Category c) {
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement stmt = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, c.getName());
			stmt.executeUpdate();

			ResultSet rs = stmt.getGeneratedKeys();
			while (rs.next()) {
				c.setId(rs.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(Category c) {
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement stmt = con.prepareStatement(update)) {

			stmt.setString(1, c.getName());
			stmt.setInt(2, c.getId());
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(Category c) {
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement stmt = con.prepareStatement(delete)) {

			stmt.setInt(1, c.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Category> findAll() {
		String find = "select * from category where 1 = 1";
		
		List<Category> list = new ArrayList<>();

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement stmt = con.prepareStatement(find)) {

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next())
				list.add(getObject(rs));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Category> findByName(String name) {
		String find = "select * from category where 1 = 1";
		List<Category> list = new ArrayList<>();
		boolean isConcat = null != name && !name.isEmpty();
		
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement stmt = con.prepareStatement(isConcat ? find.concat(" and name like ?") : find)) {
			
			if(isConcat)
				stmt.setString(1, "%".concat(name).concat("%"));
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
				list.add(getObject(rs));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private Category getObject(ResultSet rs) throws SQLException {
		Category c = new Category();
		c.setId(rs.getInt("id"));
		c.setName(rs.getString("name"));
		return c;
	}
	
	public void upload(File file) {
		try {
			Files.readAllLines(file.toPath())
				 .stream()
				 .map(Category::new)
				 .forEach(this::add);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
