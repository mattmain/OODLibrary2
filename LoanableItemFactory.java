public class LoanableItemFactory {
	private static final int BOOK = 1;
	private static final int PERIODICAL = 2;
	private static LoanableItemFactory singleton = null;

	private LoanableItemFactory() {
	}

	public static LoanableItemFactory instance() {
		if (singleton == null) {
			singleton = new LoanableItemFactory();
		}
		return singleton;
	}

	// code for Singleton
	public LoanableItem createLoanableItem(int type, String title,
			String author, String id) {
		switch (type) {
		case BOOK:
			return new Book(title, author, id);
			// case PERIODICAL:
			// return new Periodical(title, id);
		default:
			return null;
		}

	}

}