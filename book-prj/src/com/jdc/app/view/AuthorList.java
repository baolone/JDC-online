package com.jdc.app.view;

import java.util.List;

import com.jdc.app.entity.Author;
import com.jdc.app.service.AuthorService;
import com.jdc.app.util.TableFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;

public class AuthorList {

	@FXML
	private TextField name;
	@FXML
	private TextField age;
	@FXML
	private TextField country;
	@FXML
	private TableView<Author> tblList;
	@FXML
	private TableColumn<Author, String> nameCol;
	@FXML
	private TableColumn<Author, Integer> ageCol;
	@FXML
	private TableColumn<Author, String> countryCol;
	
	private AuthorService authService;
	
	
	public void add() {
		Author author = new Author();
		if(!name.getText().isEmpty())
			author.setName(name.getText());
		
		if(!age.getText().isEmpty())
			author.setAge(Integer.parseInt(age.getText()));
		
		if(!country.getText().isEmpty())
			author.setCountry(country.getText());
		
		authService.add(author);
		search();
		clear();
	}
	
	public void search() {
		tblList.getItems().clear();
		int age = this.age.getText().isEmpty() ? 0 : Integer.parseInt(this.age.getText());
		List<Author> authList = authService.findByParams(name.getText(), age, country.getText());
		tblList.getItems().addAll(authList);
	}
	
	public void clear() {
		name.clear();
		age.clear();
		country.clear();
	}
	
	@FXML
	private void initialize() {
		authService = AuthorService.getInstance();
		search();
		
		nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		nameCol.setOnEditCommit(e -> {
			Author data = e.getRowValue();
			data.setName(e.getNewValue());
			authService.update(data);
			search();
		});
		
		ageCol.setCellFactory(TextFieldTableCell.forTableColumn(new TableFormatter()));
		ageCol.setOnEditCommit(e -> {
			Author data = e.getRowValue();
			data.setAge(e.getNewValue());
			authService.update(data);
			search();
		});
		
		countryCol.setCellFactory(TextFieldTableCell.forTableColumn());
		countryCol.setOnEditCommit(e -> {
			Author data = e.getRowValue();
			data.setCountry(e.getNewValue());
			authService.update(data);
			search();
		});
		
		MenuItem delete = new MenuItem("Delete");
		tblList.setContextMenu(new ContextMenu(delete));
		delete.setOnAction(e -> {
			Author author = tblList.getSelectionModel().getSelectedItem();
			authService.delete(author);
			search();
		});
		
		name.textProperty().addListener((a, b, c) -> search());
		age.textProperty().addListener((a, b, c) -> search());
		country.textProperty().addListener((a, b, c) -> search());
	}
	
	
}
