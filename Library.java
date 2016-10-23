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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Implementation of the Library system. It acts as a Facade for all operations.
 * 
 * @author mh6624pa
 * 
 */
public class Library implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int ITEM_NOT_FOUND = 1;
	public static final int ITEM_NOT_ISSUED = 2;
	public static final int ITEM_HAS_HOLD = 3;
	public static final int ITEM_ISSUED = 4;
	public static final int HOLD_PLACED = 5;
	public static final int NO_HOLD_FOUND = 6;
	public static final int OPERATION_COMPLETED = 7;
	public static final int OPERATION_FAILED = 8;
	public static final int NO_SUCH_MEMBER = 9;
	public static final int NO_BOOK_CHECKED_OUT = 10;

	public static final int BOOK = 1;
	public static final int PERIODICAL = 2;
	private Catalog catalog;
	private MemberList memberList;
	private static Library library;

	/**
	 * Private for the singleton pattern Creates the catalog and member
	 * collection objects
	 */
	private Library() {
		catalog = Catalog.instance();
		memberList = MemberList.instance();
	}

	/**
	 * Supports the singleton pattern
	 * 
	 * @return the singleton object
	 */
	public static Library instance() {
		if (library == null) {
			MemberIdServer.instance(); // instantiate all singletons
			return (library = new Library());
		} else {
			return library;
		}
	}

	/**
	 * Organizes the operations for adding a loanable item
	 * 
	 * @param type
	 *            the type of item to be created
	 * @param title
	 *            item title
	 * @param author
	 *            author name (for applicable items
	 * @param id
	 *            item id
	 * @return the Book object created
	 */
	public LoanableItem addLoanableItem(int type, String title, String author,
			String id) {
		LoanableItem item = LoanableItemFactory.instance().createLoanableItem(
				type, title, author, id);
		if (catalog.insertLoanableItem(item)) {
			return (item);
		}
		return null;
	}

	/**
	 * Organizes the operations for adding a member
	 * 
	 * @param name
	 *            member name
	 * @param address
	 *            member address
	 * @param phone
	 *            member phone
	 * @return the Member object created
	 */
	public Member addMember(String name, String address, String phone) {
		Member member = new Member(name, address, phone);
		if (memberList.insertMember(member)) {
			return (member);
		}
		return null;
	}

	/**
	 * Organizes the placing of a hold
	 * 
	 * @param memberSeqNum
	 *            seqNum specifying member
	 * @param bookSeqNum
	 *            seqNum specifying book
	 * @param duration
	 *            for how long the hold should be valid in days
	 * @return indication on the outcome
	 */
	public int placeHold(int memSeqNum, int bookSeqNum, int duration) {
		if (bookSeqNum == -1) {
			return library.NO_BOOK_CHECKED_OUT;
		}

		Iterator<LoanableItem> item2 = catalog.getBorrowedBookList();
		Iterator<Member> memIterator = memberList.iterator();
		Member member = null;
		LoanableItem book = null;

		for (int i = 0; i <= memSeqNum; i++) {
			member = (Member) memIterator.next();
		}
		for (int j = 0; j <= bookSeqNum; j++) {
			book = (LoanableItem) item2.next();
		}
		return placeHold(member.getId(), book.getId(), duration);

	}

	/**
	 * Organizes the placing of a hold
	 * 
	 * @param memberId
	 *            member's id
	 * @param itemId
	 *            item's id
	 * @param duration
	 *            for how long the hold should be valid in days
	 * @return indication on the outcome
	 */
	public int placeHold(String memberId, String itemId, int duration) {
		LoanableItem item = catalog.search(itemId);
		if (item == null) {
			return (ITEM_NOT_FOUND);
		}
		if (item.getBorrower() == null) {
			return (ITEM_NOT_ISSUED);
		}
		Member member = memberList.search(memberId);
		if (member == null) {
			return (NO_SUCH_MEMBER);
		}
		Hold hold = new Hold(member, item, duration);
		item.placeHold(hold);
		member.placeHold(hold);
		return (HOLD_PLACED);
	}

	/**
	 * Searches for a given member
	 * 
	 * @param memberId
	 *            id of the member
	 * @return true iff the member is in the member list collection
	 */
	public Member searchMembership(String memberId) {
		return memberList.search(memberId);
	}

	public Member processHold(int bookSeqNum) {
		Iterator<Book> item = catalog.getBooksWithHolds();
		LoanableItem heldItem = null;

		for (int i = 0; i <= bookSeqNum; i++) {
			heldItem = item.next();
		}
		return processHold(heldItem.getId());
	}

	/**
	 * Processes holds for a single item
	 * 
	 * @param itemId
	 *            id of the item
	 * @return the member who should be notified
	 */
	public Member processHold(String itemId) {
		LoanableItem item = catalog.search(itemId);
		if (item == null) {
			return (null);
		}
		Hold hold = item.getNextHold();
		if (hold == null) {
			return (null);
		}
		hold.getMember().removeHold(itemId);
		hold.getLoanableItem().removeHold(hold.getMember().getId());
		return (hold.getMember());
	}

	/**
	 * Removes a hold for a specific item and member combination
	 * 
	 * @param memSeqNum
	 *            user input of member
	 * @param bookSeqNum
	 *            user input of item
	 * @return result of the operation
	 */
	public int removeHold(int memSeqNum, int bookSeqNum) {
		Iterator<Member> memIterator = memberList.iterator();
		Iterator<LoanableItem> bookIterator = catalog.iterator();

		Member member = null;
		LoanableItem item = null;

		for (int i = 0; i <= memSeqNum; i++) {
			member = (Member) memIterator.next();
		}
		for (int i = 0; i <= bookSeqNum; i++) {
			item = (LoanableItem) bookIterator.next();
		}
		return removeHold(member.getId(), item.getId());
	}

	/**
	 * Removes a hold for a specific item and member combination
	 * 
	 * @param memberId
	 *            id of the member
	 * @param itemId
	 *            item id
	 * @return result of the operation
	 */
	public int removeHold(String memberId, String itemId) {
		Member member = memberList.search(memberId);
		if (member == null) {
			return (NO_SUCH_MEMBER);
		}
		LoanableItem item = catalog.search(itemId);
		if (item == null) {
			return (ITEM_NOT_FOUND);
		}
		return member.removeHold(itemId) && item.removeHold(memberId) ? OPERATION_COMPLETED
				: NO_HOLD_FOUND;
	}

	/**
	 * Removes all out-of-date holds
	 */
	private void removeInvalidHolds() {
		for (Iterator<LoanableItem> catalogIterator = catalog.iterator(); catalogIterator
				.hasNext();) {
			for (Iterator<Hold> iterator = catalogIterator.next().getHolds(); iterator
					.hasNext();) {
				Hold hold = iterator.next();
				if (!hold.isValid()) {
					hold.getLoanableItem().removeHold(hold.getMember().getId());
					hold.getMember().removeHold(hold.getLoanableItem().getId());
				}
			}
		}
	}

	/**
	 * Organizes the issuing of an item
	 * 
	 * @param memberSeqNum
	 *            seqNum specifying member
	 * @param bookSeqNum
	 *            seqNum specifying book
	 * @return the item issued
	 */

	public void issueLoanableItem(int memSeqNum, int bookSeqNum) {
		Iterator<Member> memIterator = memberList.iterator();
		Iterator<LoanableItem> bookIterator = catalog.getBooksNotBorrowed();

		Member member = null;
		LoanableItem item = null;

		for (int i = 0; i <= memSeqNum; i++) {
			member = (Member) memIterator.next();
		}
		for (int i = 0; i <= bookSeqNum; i++) {
			item = (LoanableItem) bookIterator.next();
		}

		System.out.println(member.getId() + ":" + item.getId());
		issueLoanableItem(member.getId(), item.getId());

	}

	/**
	 * Organizes the issuing of an item
	 * 
	 * @param memberId
	 *            member id
	 * @param itemId
	 *            item id
	 * @return the item issued
	 */
	public LoanableItem issueLoanableItem(String memberId, String itemId) {
		LoanableItem item = catalog.search(itemId);

		if (item == null) {
			System.out.println("Item Null: Unable to issue book");
			return (null);
		}
		if (item.getBorrower() != null) {
			System.out.println("borrower Null: Unable to issue book");
			return (null);
		}
		Member member = memberList.search(memberId);

		if (member == null) {
			System.out.println("member Null: Unable to issue book");
			return (null);
		}
		if (!(item.issue(member) && member.issue(item))) {
			return null;
		}
		System.out.println("Success");
		return (item);
	}

	/**
	 * Renews an item
	 * 
	 * @param memberSeqNum
	 *            seqNum specifying member
	 * @param bookSeqNum
	 *            seqNum specifying book
	 * @return the book renewed
	 */

	public LoanableItem renewItems(int bookSeqNum, int memSeqNum) {
		Iterator<Member> memIterator = memberList.iterator();
		Iterator<LoanableItem> bookIterator = catalog.iterator();

		Member member = null;
		LoanableItem item = null;

		for (int i = 0; i <= memSeqNum; i++) {
			member = (Member) memIterator.next();
		}
		for (int i = 0; i <= bookSeqNum; i++) {
			item = (LoanableItem) bookIterator.next();
		}

		return (renewItem(item.getId(), member.getId()));

	}

	/**
	 * Renews an item
	 * 
	 * @param itemId
	 *            id of the item to be renewed
	 * @param memberId
	 *            member id
	 * @return the book renewed
	 */
	public LoanableItem renewItem(String itemId, String memberId) {
		LoanableItem loanableItem = catalog.search(itemId);
		if (loanableItem == null) {
			return (null);
		}
		Member member = memberList.search(memberId);
		if (member == null) {
			return (null);
		}
		if ((loanableItem.renew(member) && member.renew(loanableItem))) {
			return (loanableItem);
		}
		return (null);
	}

	/**
	 * Returns an iterator to the loanable items issued to a member
	 * 
	 * @param memberId
	 *            member id
	 * @return iterator to the collection
	 */
	public Iterator<LoanableItem> getLoanableItems(String memberId) {
		Member member = memberList.search(memberId);
		if (member == null) {
			return (null);
		} else {
			return (member.getLoanableItemsIssued());
		}
	}

	public int removeLoanableItems(int bookSeqNum) {
		Iterator<LoanableItem> bookIterator = catalog.getBooksNotBorrowed();

		LoanableItem item = null;

		for (int i = 0; i <= bookSeqNum; i++) {
			item = bookIterator.next();
		}

		return removeLoanableItem(item.getId());

	}

	/**
	 * Removes a specific book from the catalog
	 * 
	 * @param itemId
	 *            id of the item
	 * @return a code representing the outcome
	 */
	public int removeLoanableItem(String itemId) {
		LoanableItem item = catalog.search(itemId);
		if (item == null) {
			return (ITEM_NOT_FOUND);
		}
		if (item.hasHold()) {
			return (ITEM_HAS_HOLD);
		}
		if (item.getBorrower() != null) {
			return (ITEM_ISSUED);
		}
		if (catalog.removeLoanableItem(itemId)) {
			return (OPERATION_COMPLETED);
		}
		return (OPERATION_FAILED);
	}

	/**
	 * Returns a single loanable item to the library
	 * 
	 * @param itemId
	 *            id of the item to be returned
	 * @return a code representing the outcome
	 */
	public int returnLoanableItem(String itemId) {
		LoanableItem loanableItem = catalog.search(itemId);
		if (loanableItem == null) {
			return (ITEM_NOT_FOUND);
		}
		Member member = loanableItem.returnItem();
		if (member == null) {
			return (ITEM_NOT_ISSUED);
		}
		if (!(member.returnItem(loanableItem))) {
			return (OPERATION_FAILED);
		}
		if (loanableItem.hasHold()) {
			return (ITEM_HAS_HOLD);
		}
		return (OPERATION_COMPLETED);
	}

	/**
	 * Returns an iterator to the transactions for a specific member on a
	 * certain date
	 * 
	 * @param memberSeqNum
	 *            seqNum specifying member
	 * @param date
	 *            date of issue
	 * @return iterator to the collection
	 */
	public Iterator<Transaction> getTransactions(int memSeqNum, Calendar date) {
		Iterator<Member> memIterator = memberList.iterator();
		Member member = null;

		for (int i = 0; i <= memSeqNum; i++) {
			member = (Member) memIterator.next();
		}

		return getTransactions(member.getId(), date);
	}

	/**
	 * Returns an iterator to the transactions for a specific member on a
	 * certain date
	 * 
	 * @param memberId
	 *            member id
	 * @param date
	 *            date of issue
	 * @return iterator to the collection
	 */
	public Iterator<Transaction> getTransactions(String memberId, Calendar date) {
		Member member = memberList.search(memberId);
		if (member == null) {
			return (null);
		}
		return member.getTransactions(date);
	}

	/*
	 * public void processLoanableItems(LoanableItemVisitor visitor) { for
	 * (Iterator<LoanableItem> itemIterator = catalog.iterator(); itemIterator
	 * .hasNext();) { itemIterator.next().accept(visitor); } }
	 */

	/**
	 * Retrieves a deserialized version of the library from disk
	 * 
	 * @return a Library object
	 */
	public static Library retrieve() {
		try {
			FileInputStream file = new FileInputStream("LibraryData");
			ObjectInputStream input = new ObjectInputStream(file);
			input.readObject();
			MemberIdServer.retrieve(input);
			return library;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return null;
		}
	}

	/**
	 * Serializes the Library object
	 * 
	 * @return true iff the data could be saved
	 */
	public static boolean save() {
		ObjectOutputStream output = null;
		try {
			FileOutputStream file = new FileOutputStream("LibraryData");
			output = new ObjectOutputStream(file);
			output.writeObject(library);
			output.writeObject(MemberIdServer.instance());
			output.close();
			return true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
	}

	/**
	 * Writes the object to the output stream
	 * 
	 * @param output
	 *            the stream to be written to
	 */
	private void writeObject(java.io.ObjectOutputStream output)
			throws IOException {
		output.defaultWriteObject();
		output.writeObject(library);
	}

	/**
	 * Reads the object from a given stream
	 * 
	 * @param input
	 *            the stream to be read
	 */
	private void readObject(java.io.ObjectInputStream input)
			throws IOException, ClassNotFoundException {
		input.defaultReadObject();
		if (library == null) {
			library = (Library) input.readObject();
		} else {
			input.readObject();
		}
	}

	/**
	 * String form of the library
	 * 
	 */
	@Override
	public String toString() {
		return catalog + "\n" + memberList;
	}

	/**
	 * 
	 * @return all members in the memberList as an iterator object
	 */
	public Iterator<Member> getMembers() {
		return memberList.iterator();
	}

	/**
	 * 
	 * @return all members with holds in the memberList as an iterator object
	 */
	public Iterator<Member> getMembersWithHolds() {
		return memberList.getMembersWithHolds();
	}

	/**
	 * 
	 * @return all members with books in the memberList as an iterator object
	 */
	public Iterator<Member> getMembersWithBooks() {
		return memberList.getMembersWithBooks();
	}

	/**
	 * 
	 * @return all books in the catalog as an iterator object
	 */
	public Iterator<LoanableItem> getBooks() {
		return catalog.iterator();
	}

	/**
	 * 
	 * @return all issued books that a member has checked out as an iterator
	 *         object
	 */
	public Iterator<LoanableItem> getBooksIssued(int memSeqNum) {
		Member member = null;
		Iterator<Member> memberIterator = memberList.iterator();

		for (int i = 0; i <= memSeqNum; i++) {
			member = (Member) memberIterator.next();
		}

		return member.getBooksIssued();
	}

	/**
	 * 
	 * @return all issued books that a member has checked out with holds as an
	 *         iterator object
	 */
	public Iterator<LoanableItem> getBooksIssuedWithHolds(int memSeqNum) {
		Member member = null;
		Iterator<Member> memberIterator = memberList.iterator();

		for (int i = 0; i <= memSeqNum; i++) {
			member = (Member) memberIterator.next();
		}

		return member.getBooksIssuedWithHolds();
	}

	/**
	 * Returns the items that are available to be loaned.
	 * 
	 * @param memSeqNum
	 *            Input from user specifying member in memberList
	 * @return Returns list of items that are available to be loaned.
	 */
	public Iterator<LoanableItem> getLoanableItems(int memSeqNum) {
		Member selectedMember = null;
		Iterator<LoanableItem> loanedItems;
		Iterator<Member> memberIterator = memberList.iterator();

		for (int i = 0; i < memSeqNum; i++) {
			selectedMember = (Member) memberIterator.next();
		}
		loanedItems = selectedMember.getLoanableItemsIssued();
		return loanedItems;
	}

	/**
	 * Gets books that are currently borrowed.
	 * 
	 * @return books that are currently borrowed.
	 */

	public Iterator<LoanableItem> getBorrowedBooks() {
		return catalog.getBorrowedBookList();
	}

	/**
	 * Gets books with holds
	 * 
	 * @return books with holds.
	 */

	public Iterator<Book> getBooksWithHolds() {
		Iterator<Book> booksHeld = catalog.getBooksWithHolds();
		if (booksHeld == null) {
			return (null);
		}
		return catalog.getBooksWithHolds();
	}

	public Iterator<LoanableItem> getBooksNotBorrowed() {
		Iterator<LoanableItem> booksNotBorrowed = catalog.getBooksNotBorrowed();
		if (booksNotBorrowed == null) {
			return (null);
		}
		return booksNotBorrowed;
	}

}