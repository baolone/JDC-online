package com.jdc.app.view;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import com.jdc.app.entity.Author;
import com.jdc.app.entity.Book;
import com.jdc.app.entity.Category;
import com.jdc.app.service.AuthorService;
import com.jdc.app.service.BookService;
import com.jdc.app.service.CategoryService;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class BookList {

	@FXML
	private ComboBox<Category> category;
	@FXML
	private ComboBox<Author> authorName;
	@FXML
	private DatePicker releaseDate;
	@FXML
	private TableView<Book> tblList;
	private CategoryService catService;
	private AuthorService authService;
	private BookService bookService;
	private Consumer<Book> listener;
	
	public void add() {
		BookEdit.show(null, this::listener);
	}
	
	public void search() {
		tblList.getItems().clear();
		List<Book> bookList = bookService.findByParams(category.getValue(), authorName.getValue(), releaseDate.getValue(), null);
		tblList.getItems().addAll(bookList);
	}
	
	public void clear() {
		category.setValue(null);
		authorName.setValue(null);
		releaseDate.setValue(null);
		tblList.getItems().clear();
	}
	
	private void loadCategory() {
		List<Category> catList = catService.findAll();
		category.getItems().addAll(catList);
	}
	
	private void loadAuthor() {
		List<Author> authList = authService.findAll();
		authorName.getItems().addAll(authList);
	}
	
	private void listener(Book book) {
		if(book.getId() == 0) {
			BookService.getInstance().add(book);
		} else {
			BookService.getInstance().update(book);
		}
		search();
	}
	
	private void edit() {
		Book book = tblList.getSelectionModel().getSelectedItem();
		BookEdit.show(book, this::listener);
		bookService.update(book);
		search();
	}
	
	private void imageUpload() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Book Cover");
		fc.setSelectedExtensionFilter(new ExtensionFilter("Book Cover", "*.jpg", "*.png"));
		fc.setInitialDirectory(new File(System.getProperty("user.home"), "Desktop"));
		File file = fc.showOpenDialog(category.getScene().getWindow());
		
		Book book = tblList.getSelectionModel().getSelectedItem();
		book.setImage(file.getAbsolutePath());
		bookService.imgUpload(book);
	}
	
	private void delete() {
		Book book = tblList.getSelectionModel().getSelectedItem();
		bookService.delete(book);
		search();
	}
	
	private void showDetail() {
		Book book = tblList.getSelectionModel().getSelectedItem();
		Detail.show(book);
	}
	
	private void createMenu() {
		MenuItem edit = new MenuItem("Edit");
		MenuItem delete = new MenuItem("Delete");
		MenuItem imgUpload = new MenuItem("Image Upload");
		MenuItem detail = new MenuItem("Show Detail");
		
		ContextMenu menu = new ContextMenu(edit, delete, imgUpload, detail);
		tblList.setContextMenu(menu);
		
		edit.setOnAction(e -> edit());
		delete.setOnAction(e -> delete());
		imgUpload.setOnAction(e -> imageUpload());
		detail.setOnAction(e -> showDetail());
	}
	
	@FXML
	private void initialize() {
		catService = CategoryService.getInstance();
		authService = AuthorService.getInstance();
		bookService = BookService.getInstance();
		loadCategory();
		loadAuthor();
		createMenu();
		search();
		
		category.valueProperty().addListener((a, b, c) -> search());
		authorName.valueProperty().addListener((a, b, c) -> search());
		releaseDate.valueProperty().addListener((a, b, c) -> search());
	}
}
