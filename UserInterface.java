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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * 
 * This class implements the user interface for the Library project. The
 * commands are encoded as integers using a number of static final variables. A
 * number of utility methods exist to make it easier to parse the input.
 * 
 */
public class UserInterface {
	private static UserInterface userInterface;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(
			System.in));
	private static Library library;
	private static final int EXIT = 0;
	private static final int ADD_MEMBER = 1;
	private static final int ADD_BOOKS = 2;
	private static final int ISSUE_BOOKS = 3;
	private static final int RETURN_BOOKS = 4;
	private static final int RENEW_BOOKS = 5;
	private static final int REMOVE_BOOKS = 6;
	private static final int PLACE_HOLD = 7;
	private static final int REMOVE_HOLD = 8;
	private static final int PROCESS_HOLD = 9;
	private static final int GET_TRANSACTIONS = 10;
	private static final int SAVE = 11;
	private static final int RETRIEVE = 12;
	private static final int PRINT_FORMATTED = 13;
	private static final int HELP = 14;

	/**
	 * Made private for singleton pattern. Conditionally looks for any saved
	 * data. Otherwise, it gets a singleton Library object.
	 */
	private UserInterface() {
		if (yesOrNo("Look for saved data and  use it?")) {
			retrieve();
		} else {
			library = Library.instance();
		}
	}

	/**
	 * Supports the singleton pattern
	 * 
	 * @return the singleton object
	 */
	public static UserInterface instance() {
		if (userInterface == null) {
			return userInterface = new UserInterface();
		} else {
			return userInterface;
		}
	}

	/**
	 * Gets a token after prompting
	 * 
	 * @param prompt
	 *            - whatever the user wants as prompt
	 * @return - the token from the keyboard
	 * 
	 */
	public String getToken(String prompt) {
		do {
			try {
				System.out.println(prompt);
				String line = reader.readLine();
				StringTokenizer tokenizer = new StringTokenizer(line, "\n\r\f");
				if (tokenizer.hasMoreTokens()) {
					return tokenizer.nextToken();
				}
			} catch (IOException ioe) {
				System.exit(0);
			}
		} while (true);
	}

	/**
	 * Queries for a yes or no and returns true for yes and false for no
	 * 
	 * @param prompt
	 *            The string to be prepended to the yes/no prompt
	 * @return true for yes and false for no
	 * 
	 */
	private boolean yesOrNo(String prompt) {
		String more = getToken(prompt
				+ " (Y|y)[es] or anything else for no  -1 for no and to get to main menu");

		if (more.charAt(0) != 'y' && more.charAt(0) != 'Y') {
			return false;
		}

		if (more.charAt(0) == '-') {
			process();
		}
		return true;
	}

	/**
	 * Converts the string to a number
	 * 
	 * @param prompt
	 *            the string for prompting
	 * @return the integer corresponding to the string
	 * 
	 */
	public int getNumber(String prompt) {
		do {
			try {
				String item = getToken(prompt);
				Integer number = Integer.valueOf(item);
				return number.intValue();
			} catch (NumberFormatException nfe) {
				System.out.println("Please input a number ");
			}
		} while (true);
	}

	/**
	 * Prompts for a date and gets a date object
	 * 
	 * @param prompt
	 *            the prompt
	 * @return the data as a Calendar object
	 */
	public Calendar getDate(String prompt) {
		do {
			try {
				Calendar date = new GregorianCalendar();
				String item = getToken(prompt);
				DateFormat dateFormat = SimpleDateFormat
						.getDateInstance(DateFormat.SHORT);
				date.setTime(dateFormat.parse(item));
				return date;
			} catch (Exception fe) {
				System.out.println("Please input a date as mm/dd/yy");
			}
		} while (true);
	}

	/**
	 * Prompts for a command from the keyboard
	 * 
	 * @return a valid command
	 * 
	 */
	public int getCommand() {
		do {
			try {
				int value = Integer.parseInt(getToken("Enter command:" + HELP
						+ " for help"));
				if (value >= EXIT && value <= HELP) {
					return value;
				}
			} catch (NumberFormatException nfe) {
				System.out.println("Enter a number");
			}
		} while (true);
	}

	/**
	 * Displays the help screen
	 * 
	 */
	public void help() {
		System.out
				.println("Enter a number between 0 and 12 as explained below:");
		System.out.println(EXIT + " to Exit\n");
		System.out.println(ADD_MEMBER + " to add a member");
		System.out.println(ADD_BOOKS + " to  add books");
		System.out.println(ISSUE_BOOKS + " to  issue books to a  member");
		System.out.println(RETURN_BOOKS + " to  return books ");
		System.out.println(RENEW_BOOKS + " to  renew books ");
		System.out.println(REMOVE_BOOKS + " to  remove books");
		System.out.println(PLACE_HOLD + " to  place a hold on a book");
		System.out.println(REMOVE_HOLD + " to  remove a hold on a book");
		System.out.println(PROCESS_HOLD + " to  process holds");
		System.out.println(GET_TRANSACTIONS + " to  print transactions");
		System.out.println(SAVE + " to  save data");
		System.out.println(RETRIEVE + " to  retrieve");
		System.out.println(PRINT_FORMATTED + " to  print items formatted");
		System.out.println(HELP + " for help");
	}

	/**
	 * Method to be called for adding a member. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for adding the
	 * member.
	 * 
	 */
	public void addMember() {
		String name = getToken("Enter member name");
		String address = getToken("Enter address");
		String phone = getToken("Enter phone");
		Member result;
		result = library.addMember(name, address, phone);
		if (result == null) {
			System.out.println("Could not add member");
		}
		System.out.println(result);
	}

	/**
	 * Method to be called for adding a loanable item. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for adding the
	 * item.
	 * 
	 */
	public void addLoanableItems() {
		LoanableItem result;
		do {
			int type;
			do {
				type = getNumber("Enter " + Library.BOOK + " for book or "
						+ Library.PERIODICAL + " for periodical");
			} while (type != Library.BOOK && type != Library.PERIODICAL);
			String title = getToken("Enter title");
			String author = "";
			if (type == Library.BOOK) {
				author = getToken("Enter author");
			}
			String id = getToken("Enter id");
			result = library.addLoanableItem(type, title, author, id);
			if (result != null) {
				System.out.println(result);
			} else {
				System.out.println("Book could not be added");
			}
			if (!yesOrNo("Add more books?")) {
				break;
			}
		} while (true);
	}

	/**
	 * Method to be called for issuing books. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for issuing
	 * books.
	 * 
	 */
	public void issueLoanableItems() {
		int memSeqNum = 0;
		int bookSeqNum = 0;

		Iterator<Member> members = library.getMembers();
		memSeqNum = getSeqNum(members);

		if (memSeqNum != -1) {

			Iterator<LoanableItem> books = library.getBooksNotBorrowed();
			bookSeqNum = getSeqNum(books);

			if (bookSeqNum != -1) {

				library.issueLoanableItem(memSeqNum, bookSeqNum);

			}
		}
	}

	public int getSeqNum(Iterator iterator) {
		int seqNum = 0;
		while (iterator.hasNext()) {
			System.out.println("Seq Num: " + seqNum + " "
					+ iterator.next().toString());
			seqNum++;
		}
		try {
			seqNum = Integer
					.parseInt(getToken("Enter Sequence Number or -1 to cancel."));
		} catch (Exception e) {
			System.out.println("You did not enter a number");
		}
		return seqNum;
	}

	/**
	 * Method to be called for renewing books. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for renewing
	 * books.
	 * 
	 */

	public void renewLoanableItems() {
		int memSeqNum = 0;
		int bookSeqNum = 0;

		Iterator<Member> members = library.getMembersWithBooks();
		memSeqNum = getSeqNum(members);

		LoanableItem result;

		if (memSeqNum != -1) {

			do {
				Iterator<LoanableItem> issuedItems = library
						.getBooksIssued(memSeqNum);
				bookSeqNum = getSeqNum(issuedItems);

				if (bookSeqNum == -1) {
					break;
				} else {

					result = library.renewItems(bookSeqNum, memSeqNum);

					if (result != null) {
						System.out.println("Book Renewed");
						System.out.println(result);
					} else {
						System.out.println("Book is not renewable");
					}

					if (!yesOrNo("renew more books?")) {
						break;
					}

				}

			} while (true);
		}

	}

	/**
	 * Method to be called for returning books. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for returning
	 * books.
	 * 
	 */
	public void returnLoanableItems() {
		int result;
		do {
			String bookID = getToken("Enter book id");
			result = library.returnLoanableItem(bookID);
			switch (result) {
			case Library.ITEM_NOT_FOUND:
				System.out.println("No such Book in Library");
				break;
			case Library.ITEM_NOT_ISSUED:
				System.out.println(" Book  was not checked out");
				break;
			case Library.ITEM_HAS_HOLD:
				System.out.println("Book has a hold");
				break;
			case Library.OPERATION_FAILED:
				System.out.println("Book could not be returned");
				break;
			case Library.OPERATION_COMPLETED:
				System.out.println(" Book has been returned");
				break;
			default:
				System.out.println("An error has occurred");
			}
			if (!yesOrNo("Return more books?")) {
				break;
			}
		} while (true);
	}

	/**
	 * Method to be called for removing books. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for removing
	 * books.
	 * 
	 */

	public void removeLoanableItems() {
		int bookSeqNum = 0;
		do {
			Iterator books = library.getBooksNotBorrowed();
			bookSeqNum = getSeqNum(books);
			if (bookSeqNum != -1) {
				break;
			} else {
				int result = library.removeLoanableItems(bookSeqNum);

				if (result == library.ITEM_NOT_FOUND) {
					System.out.println("ITEM_NOT_FOUND");
					removeLoanableItems();
				}
				if (result == library.ITEM_HAS_HOLD) {
					System.out.println("ITEM_HAS_HOLD");
					removeLoanableItems();
				}
				if (result == library.ITEM_ISSUED) {
					System.out.println("ITEM_ISSUED");
					removeLoanableItems();
				}
				if (result == library.OPERATION_COMPLETED) {
					System.out.println("OPERATION_COMPLETED");
					if (!yesOrNo("Remove more books?")) {
						process();
					} else {
						System.out.println("OPERATION_FAILED");
						removeLoanableItems();
					}
				}
			}
		} while (true);
	}

	/**
	 * Method to be called for placing a hold. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for placing a
	 * hold.
	 * 
	 */

	public void placeHold() {
		int memSeqNum = 0;
		int bookSeqNum = 0;

		Iterator<Member> members = library.getMembers();
		memSeqNum = getSeqNum(members);

		if (memSeqNum != -1) {

			Iterator<LoanableItem> books = library.getBorrowedBooks();
			bookSeqNum = getSeqNum(books);

			if (bookSeqNum != -1) {

				int duration = getNumber("Enter duration of hold");

				int result = library.placeHold(memSeqNum, bookSeqNum, duration);
				switch (result) {
				case Library.ITEM_NOT_FOUND:
					System.out.println("No such Book in Library");
					break;
				case Library.ITEM_NOT_ISSUED:
					System.out.println(" Book is not checked out");
					break;
				case Library.NO_SUCH_MEMBER:
					System.out.println("Not a valid member ID");
					break;
				case Library.HOLD_PLACED:
					System.out.println("A hold has been placed");
					break;
				case Library.NO_BOOK_CHECKED_OUT:
					System.out
							.println("There are no books checked out so a hold cannot be placed.");
					break;
				default:
					System.out.println("An error has occurred");
				}
			} else {
				process();
			}
		} else {
			process();
		}
	}

	/**
	 * Method to be called for removing a holds. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for removing a
	 * hold.
	 * 
	 */

	// TODO
	public void removeHold() {
		int memSeqNum = 0;
		int bookSeqNum = 0;

		Iterator<Member> members = library.getMembers();
		memSeqNum = getSeqNum(members);
		if (memSeqNum != -1) {

			Iterator<LoanableItem> books = library
					.getBooksIssuedWithHolds(memSeqNum);
			bookSeqNum = getSeqNum(books);
			if (bookSeqNum != -1) {

				int result = library.removeHold(memSeqNum, bookSeqNum);
				switch (result) {
				case Library.ITEM_NOT_FOUND:
					System.out.println("No such Book in Library");
					break;
				case Library.NO_SUCH_MEMBER:
					System.out.println("Not a valid member ID");
					break;
				case Library.OPERATION_COMPLETED:
					System.out.println("The hold has been removed");
					break;
				default:
					System.out.println("An error has occurred");
				}
			} else {
				process();
			}
		} else {
			process();
		}

	}

	/**
	 * Method to be called for processing books. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for processing
	 * books.
	 * 
	 */

	public void processHolds() {
		Member result;
		int bookSeqNum = 0;
		do {
			Iterator<Book> booksHeld = library.getBooksWithHolds();
			bookSeqNum = getSeqNum(booksHeld);
			if (bookSeqNum != -1) {
				break;
			} else {
				result = library.processHold(bookSeqNum);
				if (result != null) {
					System.out.println(result);
				} else {
					System.out.println("No valid holds left");
				}
				if (!yesOrNo("Process more books?")) {
					break;
				}
			}
		} while (true);
	}

	/**
	 * Method to be called for displaying transactions. Prompts the user for the
	 * appropriate values and uses the appropriate Library method for displaying
	 * transactions.
	 * 
	 */
	public void getTransactions() {

		int memSeqNum = 0;

		Iterator<Member> members = library.getMembers();
		memSeqNum = getSeqNum(members);
		if (memSeqNum != -1) {
			Calendar date = getDate("Please enter the date for which you want records as mm/dd/yy");

			Iterator<Transaction> result = null;
			result = library.getTransactions(memSeqNum, date);

			if (result == null) {
				System.out.println("Invalid Member ID");
			} else {
				while (result.hasNext()) {
					Transaction transaction = result.next();
					System.out.println(transaction.getType() + "   "
							+ transaction.getTitle() + "\n");
				}
				System.out.println("\n  There are no more transactions \n");
			}
		}
	}

	/**
	 * Method to be called for saving the Library object. Uses the appropriate
	 * Library method for saving.
	 * 
	 */
	private void save() {
		if (Library.save()) {
			System.out
					.println(" The library has been successfully saved in the file LibraryData \n");
		} else {
			System.out.println(" There has been an error in saving \n");
		}
	}

	/**
	 * Method to be called for retrieving saved data. Uses the appropriate
	 * Library method for retrieval.
	 * 
	 */
	private void retrieve() {
		try {
			Library tempLibrary = Library.retrieve();
			if (tempLibrary != null) {
				System.out
						.println(" The library has been successfully retrieved from the file LibraryData \n");
				library = tempLibrary;
			} else {
				System.out.println("File doesnt exist; creating new library");
				library = Library.instance();
			}
		} catch (Exception cnfe) {
			cnfe.printStackTrace();
		}
	}

	/**
	 * 
	 * Prints the items in a unique format for each type of item.
	 */
	public void printFormatted() {
		// library.processLoanableItems(PrintFormat.instance());
	}

	/**
	 * Orchestrates the whole process. Calls the appropriate method for the
	 * different functionalties.
	 * 
	 */
	public void process() {
		int command;
		help();
		while ((command = getCommand()) != EXIT) {
			switch (command) {
			case ADD_MEMBER:
				addMember();
				break;
			case ADD_BOOKS:
				addLoanableItems();
				break;
			case ISSUE_BOOKS:
				issueLoanableItems();
				break;
			case RETURN_BOOKS:
				returnLoanableItems();
				break;
			case REMOVE_BOOKS:
				removeLoanableItems();
				break;
			case RENEW_BOOKS:
				renewLoanableItems();
				break;
			case PLACE_HOLD:
				placeHold();
				break;
			case REMOVE_HOLD:
				removeHold();
				break;
			case PROCESS_HOLD:
				processHolds();
				break;
			case GET_TRANSACTIONS:
				getTransactions();
				break;
			case SAVE:
				save();
				break;
			case RETRIEVE:
				retrieve();
				break;
			case PRINT_FORMATTED:
				printFormatted();
				break;
			case HELP:
				help();
				break;
			}
		}
	}

	/**
	 * The method to start the application. Simply calls process().
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		UserInterface.instance().process();
	}
}