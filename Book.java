/**
 * 
 * @author Brahma Dathan and Sarnath Ramnath
 * @Copyright (c) 2010
 
 * Redistribution and use with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - the use is for academic purpose only
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Neither the name of Brahma Dathan or Sarnath Ramnath
 *     may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * The authors do not make any claims regarding the correctness of the code in this module
 * and are not responsible for any loss or damage resulting from its use.  
 */
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents a single book
 * 
 * @author Brahma Dathan and Sarnath Ramnath
 * 
 */
public class Book extends LoanableItem implements Serializable,
		Matchable<String> {
	private static final long serialVersionUID = 1L;
	private String author;
	private GregorianCalendar dueDate = new GregorianCalendar();
	// private String borrowedBy = "";
	private boolean borrowed = false;
	private boolean hasHold = false;

	/**
	 * Creates a book with the given id, title, and author name
	 * 
	 * @param title
	 *            book title
	 * @param author
	 *            author name
	 * @param id
	 *            book id
	 */
	public Book(String title, String author, String id) {
		super(title, id);
		this.author = author;

	}

	/**
	 * Marks the book as issued to a member
	 * 
	 * @param member
	 *            the borrower
	 * @return true iff the book could be issued. True currently
	 */
	@Override
	public boolean issue(Member member) {
		if (super.issue(member)) {
			dueDate.add(Calendar.MONTH, 1);
			return true;
		}
		return false;
	}

	/**
	 * Getter for author
	 * 
	 * @return author name
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * String form of the book
	 * 
	 */
	@Override
	public String toString() {
		return super.toString() + " author " + author;
	}

	/**
	 * Implements the accept method of the Visitor pattern.
	 * 
	 * @param visitor
	 *            the Visitor that will process the Book object
	 */
	// @Override
	// public void accept(LoanableItemVisitor visitor) {
	// visitor.visit(this);
	// }
	public void placeHold(Hold hold) {
		super.placeHold(hold);
	}

	@Override
	public boolean matches(String key) {
		String id = super.getId();
		if (id == key) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isBorrowed() {
		if (this.borrowedBy == null) {
			return false;
		}
		return true;
	}

	public boolean hasHold() {
		return super.hasHold();
	}

	public String getTitle() {
		return super.getTitle();
	}
}