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
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The collection class for Member objects
 * 
 * @author Brahma Dathan and Sarnath Ramnath
 * 
 */
public class MemberList extends ItemList<Member, String> {
	private static MemberList memberList;

	/*
	 * Private constructor for singleton pattern
	 */
	private MemberList() {
	}

	/**
	 * Supports the singleton pattern
	 * 
	 * @return the singleton object
	 */
	public static MemberList instance() {
		if (memberList == null) {
			return (memberList = new MemberList());
		} else {
			return memberList;
		}
	}

	/**
	 * Checks whether a member with a given member id exists.
	 * 
	 * @param memberId
	 *            the id of the member
	 * @return true iff member exists
	 * 
	 */
	@Override
	public Member search(String memberId) {
		return super.search(memberId);
	}

	/**
	 * Inserts a member into the collection
	 * 
	 * @param member
	 *            the member to be inserted
	 * @return true iff the member could be inserted. Currently always true
	 */
	public boolean insertMember(Member member) {
		return super.add(member);
	}

	/*
	 * Supports serialization
	 * 
	 * @param output the stream to be written to
	 */
	private void writeObject(java.io.ObjectOutputStream output)
			throws IOException {
		output.defaultWriteObject();
		output.writeObject(memberList);
	}

	/*
	 * Supports serialization
	 * 
	 * @param input the stream to be read from
	 */
	private void readObject(java.io.ObjectInputStream input)
			throws IOException, ClassNotFoundException {
		input.defaultReadObject();
		if (memberList == null) {
			memberList = (MemberList) input.readObject();
		} else {
			input.readObject();
		}
	}

	/**
	 * Gets members with holds
	 * 
	 * @return members with holds.
	 */
	public Iterator<Member> getMembersWithHolds() {
		LinkedList<Member> listOfMembers = new LinkedList<Member>();
		Iterator memberIterator = iterator();

		while (memberIterator.hasNext()) {
			Member member = (Member) memberIterator.next();
			if (member.getListOfHolds() != null)
				listOfMembers.add(member);
		}

		return listOfMembers.iterator();
	}

	/**
	 * Gets members with books borrowed
	 * 
	 * @return members with books borrowed
	 */
	public Iterator<Member> getMembersWithBooks() {
		LinkedList<Member> listOfMembers = new LinkedList<Member>();
		Iterator memberIterator = iterator();

		while (memberIterator.hasNext()) {
			Member member = (Member) memberIterator.next();
			if (member.getListOfBooks().size() > 0)
				listOfMembers.add(member);
		}

		return listOfMembers.iterator();
	}

}