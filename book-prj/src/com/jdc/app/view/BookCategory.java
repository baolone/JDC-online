package com.jdc.app.view;

import java.io.File;
import java.util.List;

import com.jdc.app.entity.Category;
import com.jdc.app.service.CategoryService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class BookCategory {

	@FXML
	private TextField name;
	@FXML
	private FlowPane catBoxHolder;
	
	private CategoryService catService;
	
	public void add() {
		Category c = new Category();
		c.setName(name.getText());
		catService.add(c);
		search();
	}
	
	public void upload() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose Category");
		fc.setSelectedExtensionFilter(new ExtensionFilter("Category", "*.txt", "*.csv", "*.tsv"));
		File file = fc.showOpenDialog(name.getScene().getWindow());
		catService.upload(file);
		search();
	}
	
	public void search() {
		catBoxHolder.getChildren().clear();
		List<Category> catList = catService.findByName(name.getText());
//		catList.stream().map(c-> new CategoryBox(c))
//						.forEach(c -> catBoxHolder.getChildren().addAll(c));
		
		catList.stream().map(CategoryBox::new)
						.forEach(catBoxHolder.getChildren()::addAll);
	}
	
	public void clear() {
		name.clear();
		catBoxHolder.getChildren().clear();
	}
	
	@FXML
	public void initialize() {
		catService = CategoryService.getInstance();
		search();
		
		name.textProperty().addListener((a, b, c) -> search());
		
		name.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.ENTER))
				add();
		});
	}
	
	private class CategoryBox extends HBox {
		CategoryBox(Category c) {
			getStyleClass().add("cat-box");
			Label catName = new Label(c.getName());
			getChildren().add(catName);
		}
	}
}
