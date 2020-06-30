package com.jdc.app.view;

import java.util.List;

import com.jdc.app.entity.Book;
import com.jdc.app.entity.Category;
import com.jdc.app.entity.SaleDetail;
import com.jdc.app.service.BookService;
import com.jdc.app.service.CategoryService;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class Home {

	@FXML
	private ComboBox<Category> category;
	@FXML
	private TextField bookName;
	@FXML
	private TableView<SaleDetail> tblList;
	@FXML
	private TableView<Book> tblBook;
	private CategoryService catService;
	private BookService bookService;
	
	public void addToCart(MouseEvent event) {
		if(event.getClickCount() == 2) {
			Book book = tblBook.getSelectionModel().getSelectedItem();
			
			if(null != book) {
				SaleDetail sd = tblList.getItems().stream().filter(saleDetail -> saleDetail.getBookID() == book.getId()).findFirst().orElse(null);
				
				if(null == sd) {
					sd = new SaleDetail();
					sd.setBookID(book.getId());
					sd.setBookName(book.getName());
					sd.setUnitPrice(book.getPrice());
					sd.setCategory(book.getCategory());
					sd.setAuthor(book.getAuthor());
					sd.setQuantity(1);
					tblList.getItems().addAll(sd);
				}
				
				else {
					sd.setQuantity(sd.getQuantity() + 1);
					tblList.refresh();
				}
			}
		}
	}
	
	public void search() {
		tblBook.getItems().clear();
		List<Book> bookList = bookService.findByParams(category.getValue(), null, null, bookName.getText());
		tblBook.getItems().addAll(bookList);
		
	}
		
	private void loadCategory() {
		List<Category> catList = catService.findAll();
		category.getItems().addAll(catList);
	}
	
	@FXML
	public void initialize() {
		catService = CategoryService.getInstance();
		bookService = BookService.getInstance();
		loadCategory();
		search();
		
		category.valueProperty().addListener((a, b, c) -> search());
		bookName.textProperty().addListener((a, b, c) -> search());
//		System.out.println(catService.findAll());
	}
}
