package com.jdc.app.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.function.Consumer;

import com.jdc.app.entity.Author;
import com.jdc.app.entity.Book;
import com.jdc.app.entity.Category;
import com.jdc.app.service.AuthorService;
import com.jdc.app.service.BookService;
import com.jdc.app.service.CategoryService;
import com.jdc.app.util.ApplicationException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BookEdit {

	@FXML
	private Label title;
	@FXML
	private Label info;
	@FXML
	private ComboBox<Category> category;
	@FXML
	private ComboBox<Author> authorName;
	@FXML
	private TextField bookName;
	@FXML
	private TextField price;
	@FXML
	private DatePicker releaseDate;
	@FXML
	private TextArea remark;
	private Book book;
	
	private static Stage stage;
	
	private Consumer<Book> saveHandler;
	
	
	public static void show(Book book, Consumer<Book> saveHandler) {
		try {
			FXMLLoader loader = new FXMLLoader(BookEdit.class.getResource("BookEdit.fxml"));
			stage = new Stage();
			Parent view = loader.load();
			BookEdit controller = loader.getController();
			controller.saveHandler = saveHandler;
			controller.setData(book);
			stage.setScene(new Scene(view));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			
			boolean bookIsNull = null == book;
			
			if(bookIsNull) {
				book = new Book();
			}
			
			book.setCategory(category.getValue());
			if(null == category.getValue()) 
				throw new ApplicationException("Please select category!");
			
			book.setAuthor(authorName.getValue());
			if(null == authorName.getValue())
				throw new ApplicationException("Please select author name!");
			
			book.setName(bookName.getText());
			if(bookName.getText().isEmpty())
				throw new ApplicationException("Please enter book name!");
			
			
			if(price.getText().isEmpty())
				throw new ApplicationException("Please enter book price!");
			book.setPrice(Integer.parseInt(price.getText()));
			
			book.setReleaseDate(releaseDate.getValue());
			if(null == releaseDate.getValue())
				throw new ApplicationException("Please select date");
			
			book.setRemark(remark.getText());
			
			
			saveHandler.accept(book);
			
			close();
			
		} catch (Exception e) {
			info.setText(e.getMessage());
		}
	}
	
	private void setData(Book book) throws FileNotFoundException {
		this.book = book;
		if(null != book) {
			category.setValue(book.getCategory());
			authorName.setValue(book.getAuthor());
			bookName.setText(book.getName());
			price.setText(String.valueOf(book.getPrice()));
			releaseDate.setValue(book.getReleaseDate());
			remark.setText(book.getRemark());
			
			title.setText("EDIT BOOK");
			stage.setTitle("EDIT BOOK");
			stage.getIcons().add(new Image(new FileInputStream("edit.png")));
		} else {
			title.setText("ADD BOOK");
			stage.setTitle("ADD BOOK");
			stage.getIcons().add(new Image(new FileInputStream("add.png")));
		}
	}
	
	public void close() {
		info.getScene().getWindow().hide();
	}
	
	@FXML
	private void initialize() {
		category.getItems().addAll(CategoryService.getInstance().findAll());
		authorName.getItems().addAll(AuthorService.getInstance().findAll());
	}
}
