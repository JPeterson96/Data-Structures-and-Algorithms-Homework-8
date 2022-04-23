/*
 * Jamie Peterson
 * Comp Sci 351
 * 10/26/2021
 * Program 8
 */

package edu.uwm.cs351;

import java.util.Comparator;

import edu.uwm.cs.junit.LockedTestCase;

/**
 * List of properties for sale.
 * The properties are sorted by price.
 */

/**
 *  I went to the tutors on 10/27 from 4pm-6pm and bennet helped me make my code much more efficient 
 *  by telling me to watch the pre-recorded lectures. From those I realized that I was micro managing too 
 *  much and fixed my checkInRange method I made my checkInRange much more efficient and with less micro managing
 *  Luke also helped me by helping me realize what to put in the checkInRange call in wellFormed.
 *  
 *  I was also in tutoring from 12pm-4pm on 10/28 and yazan/ luke helped me with my add method as well as my getNext method. I 
 *  had most of the code already done before going to them. They helped me find the tiny bugs that prevented my 
 *  code from passing the tests.
 *  
 *  To reiterate, I ONLY talked to tutors. I did not talk about this assignment with anyone else. The tutors helped me and that is it.
 */
public class Realty {

	public static class Listing {
		private final int price;
		private final String address;
		
		public static final int CEILING = 2_000_000_000;
		
		/**
		 * Create a Realty Listing with the given price and location.
		 * @param price a non-negative integer less than {@link #CEILING}
		 * @param address arbitrary string describing the location (cannot be null)
		 * @throws IllegalArgumentException if price is negative
		 */
		public Listing(int price, String address) {
			if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
			if (price >= CEILING) throw new IllegalArgumentException("Price cannot be $2B or more");
			if (address == null) throw new NullPointerException("Address cannot be null");
			this.price = price;
			this.address = address;
		}
		
		public int getPrice() { return price; }
		public String getAddress() { return address; }
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof Listing) {
				Listing l = (Listing)o;
				return price == l.price && address.equals(l.address);
			} else return false;
		}
		
		@Override
		public int hashCode() {
			return price + address.hashCode();
		}
		
		public String toString() {
			return "$" + price + ":" + address;
		}
		
		/**
		 * Convert a string of the format $N:... into a {@link Listing}.
		 * @param s string to use (must not be null)
		 * @return a Listing (never null)
		 * @throws NumberFormatException
		 * If the string doesn't start with a dollar sign, or if there is no colon,
		 * or if the string between the dollar sign and the (first) colon is not a valid
		 * integer
		 * @throws IllegalArgumentException if N is not under the {@link CEILING}.
		 */
		public static Listing fromString(String s) throws NumberFormatException {
			int colon = s.indexOf(':');
			if (colon < 0) throw new NumberFormatException("Can't find end of price");
			if (s.charAt(0) != '$') throw new NumberFormatException("Price must be in US dollars.");
			return new Listing(Integer.parseInt(s.substring(1, colon)),s.substring(colon+1));
		}
	}
	
	private static class Node {
		Listing entry;
		Node left, right;
		Node (Listing r) { entry = r; }
	}
	
	private Node root;
	private int numListings;
	
	private static boolean doReport = true;
	
	/**
	 * Used to report an error found when checking the invariant.
	 * @param error string to print to report the exact error found
	 * @return false always
	 */
	private boolean report(String error) {
		if (doReport) System.out.println("Invariant error found: " + error);
		return false;
	}

	private int reportNeg(String error) {
		report(error);
		return -1;
	}
	
	/**
	 * Check that all listings in the subtree are in range.
	 * Report any errors.  If there is an error return a negative number.
	 * (Write "return reportNeg(...);" when detecting a problem.)
	 * Otherwise return the number of nodes in the subtree.
	 * Note that the range should be updated when doing recursive calls.
	 * 	@param lo all prices in the subtree rooted at r must be greater than this
	 * @param hi all prices in the subtree rooted at r must be less than this
	 * @return number of nodes in the subtree
	 */
	private int checkInRange(Node r, int lo, int hi) {
		// TODO: implement this method
		// handles null listing
		if (r == null) 
			return 0;
		// null data
		if (r.entry == null)
			return reportNeg("entry cant be null on a non null node");
		// out of bounds
		if (r.entry.price <= lo || r.entry.price >= hi)
			return reportNeg("node is outside of the range");
		// left errors
		if (checkInRange(r.left, lo, r.entry.price) == -1)
			return -1;
		// right errors
		if (checkInRange(r.right, r.entry.price, hi) == -1)
			return -1;
		
		// recursively traverses through the tree
		return 1 + checkInRange(r.left, lo, r.entry.price) + checkInRange(r.right, r.entry.price, hi); 
	}

	/**
	 * Check the invariant.  
	 * Returns false if any problem is found.  It uses
	 * {@link #report(String)} to report any problem.
	 * @return whether invariant is currently true.
	 */
	private boolean wellFormed() {
		// TODO: call checkInRange and check numListings
		if (checkInRange(root, 0, Listing.CEILING) < 0)
			return report("checkInRange returned an error");
		
		if (numListings != checkInRange(root, 0, Listing.CEILING))
			return report("numListing isnt set up correct");
		
		return true;
	}
	
	/**
	 * Create an empty set of listings.
	 */
	public Realty() {
		// TODO: Implement this constructor (BEFORE the assertion!)
		root = null;
		numListings = 0;
		
		assert wellFormed() : "invariant false at end of constructor";
	}
	
	
	/// Accessors
	
	public int size() {
		assert wellFormed() : "invariant false at start of size()";
		// TODO: Implement this method
		return numListings;
	}
	
	/**
	 * Return the lowest price realty listing.
	 * @return the lowest price realty listing or null if none at all
	 */
	public Listing getMin() {
		assert wellFormed() : "invariant false at start of getMin()";
		// TODO: Implement this method
		// empty listing
		if (size() == 0)
			return null;
		
		// keeps traversing to the left nodes until the smallest is reached
		Node min = null;
		for (Node p = root; p != null; p = p.left)
		{
			if (p.left == null)
				min = p;
		}
		
		return min.entry; 
	}
	
	// TODO: optional: you may wish to define a helper method or two
	// recursive helper method
	
	/**
	 * Gets the next lowest node in a branch
	 * @param node whose branch is being searched
	 * @return the next lowest node
	 */
	public Listing findMin(Node r) 
	{
		if(r == null)
			return null;
		
		for (Node n = r; n != null; n = n.left)
		{
			// finds the min of the current node
			if (n.left == null)
				return n.entry;
		}
		
		// if nothing was found...
		return null;
	}
	
	/**
	 * Get the next realty listing MORE than the given price.
	 * @param floor realty entry must be higher than this price.
	 * @return listing with next higher price, or null if no more.
	 */
	public Listing getNext(int floor) {
		assert wellFormed() : "invariant false at start of getNext()";
		// TODO: Implement this method
		// empty listing
		if (size() == 0)
			return null;
		
		// traverses through the right nodes until highest is reached
		Node p = root;
		Node lag = null;
		
		// traverses through the tree until the end of the desired branch
		while (p!= null) 
		{
			// matches floor
			if (p.entry.price == floor)
			{
				p = p.right;
			}
			
			// p is less so move to the right branch
			else if (p.entry.price < floor)
			{
				p = p.right;
			}
			
			// p is greater than floor, move left
			else if (p.entry.price > floor)
			{
				lag = p;
				p = p.left; 
			}
		}
		
		// special case for when the floor is higher than any node in the tree
		if (lag == null)
			return null;
		
		return lag.entry;
	}
	
	
	/// Mutators
	

	/**
	 * Add a new listing to the realty unless some other listing
	 * already is listed at the same price, in which case return false.
	 * @param l listing to add (must not be null)
	 * @return true if the listing was added.
	 * @throws NullPointerException if the listing is null
	 */
	public boolean add(Listing l) {
		assert wellFormed() : "invariant false at start of add()";
		boolean result = false;
		// TODO: Implement this method
		if (l == null)
			throw new NullPointerException("Cant add a null listing");
		
		Node lag = null;
		Node p = root;
		
		// cylce's through the list to get the right values for p and lag
		while (p != null)
		{	
			// sets empty list to root
			if (p.entry.price == l.price) 
				break;
			
			lag = p;
			
			//goes left
			if (p.entry.price > l.price)
				p = p.left;
			
			// goes right
			else
				p = p.right;
		}
		
		// if that value is already in the tree
		if (p != null)
			return false;
		
		else
		{
			p = new Node(l);
			// checks if list is empty, sets root node
			if (lag == null) 
				root = p;
			// adds the new point to the left of lag
			else if (lag.entry.price > l.price)
				lag.left = p;
			// adds the point to the right of lag
			else if (lag.entry.price < l.price)
				lag.right = p;
		}
		
		++numListings;
		result = true;
		
		assert wellFormed() : "invariant false at end of add";
		return result;
	}

	/**
	 * Add all the listings in the array into this Realty from the range [lo,hi).
	 * The elements are added recursively from the middle, so that
	 * if the array was sorted, the tree will be balanced.
	 * All the tree mutations should be done by add.
	 * The array should not include any nulls in it.
	 * Return number of listings actually added; some might not be added
	 * because they duplicate a price in a previously added listing.
	 * @param array source
	 * @param lo lower bound (usually 0)
	 * @param hi upper bound (using array.length), must be >= lo
	 * @return number of entries added
	 */
	public int addAll(Listing[] array, int lo, int hi) {
		// TODO (be efficient!)
		// NB: As long as you never change any fields (or call private methods)
		// You don't need to check the invariants.
		int added = 0;
		
		// checks for params out of bounds
		if (lo < 0 || hi > array.length)
			return added;
		
		// goes from lo to high adding array at i
		for (int i = lo; i < hi; i++)
		{
			if (add(array[i]))
			++added;
		}
		
		return added;
	}
	
	/**
	 * Copy all the listings (in sorted order) into the array
	 * at the given index.  Return the next index for (later) elements.
	 * This is a helper method for {@link #toArray(Listing[])}.
	 * @param array destination of copy
	 * @param r the subtree whose elements should be copied
	 * @param index the index to place the next element
	 * @return the next spot in the array to use after this subtree is done
	 */
	private int copyInto(Listing[] array, Node r, int index) {
		// TODO
		
		int i = index;
		
		// makes sure params dont go out of bounds
		if (r != null && i < array.length)
		{
			// recursively goes through left
			i = copyInto(array, r.left, i);
			array[i++] = r.entry;
			// recursively goes through right
			i = copyInto(array, r.right, i);
		}
		
		// this way wasnt working so I tried recursion which did work
//		Listing min = findMin(r); // min node in branch
//		int i = index;
//		Node next = new Node(min);
//		
//		while (next != null && min != null && i < array.length -1)
//		{
//			array[i] = min; 
//			++i;
//			
//			next.entry = getNext(min.price);
//		}
			
		return i; 
	}
	
	/**
	 * Return an array of all the listings in order.
	 * @param array to use unless null or too small
	 * @return array copied into
	 */
	public Listing[] toArray(Listing[] array) {
		assert wellFormed() : "invariant false at the start of toArray()";
		// TODO: Implement this method using copyInto
		// if either root or array is null or if array is empty
		if ((root != null && array == null) || (root != null && array.length == 0))
			array = new Listing[numListings];
		// creates an empty array if root and array are null
		else if (array == null && root == null)
			array = new Listing[0];
		
		else if ((root != null) && (array.length < numListings))
		{
			Listing[] newArray = new Listing[numListings];
			copyInto(newArray, root, 0);
			return newArray;
		}
		
		copyInto(array, root, 0);
		
		return array;
	}
	
	public static class TestInternals extends LockedTestCase {

		Realty self;
		Realty.Listing[] array;
		private static final int ARRAY_SIZE = 11;
		
		@Override
		protected void setUp() throws Exception {
			super.setUp();
			self = new Realty();
			self.root = null;
			self.numListings = 0;
			array = new Listing[ARRAY_SIZE];
			doReport = false;
		}

		protected void assertEqualsInRange(int result, Node n, int lo, int hi) {
			doReport = (result >= 0);
			assertEquals(result,self.checkInRange(n, lo, hi));
			doReport = false;
		}
		
		protected void assertWellFormed() {
			doReport = true;
			assertTrue(self.wellFormed());
			doReport = false;
		}
		
		private Realty.Listing l(int p) { return new Realty.Listing(p, ""); }
		
		/// testing checkInRange
		
		public void testC0() {
			// What does checkInRange(null, -10, 10) return ?
			assertEqualsInRange(Ti(1810977346), null, -10, 10);
			assertEqualsInRange(0, null, -1_000_000, -999_999);
			assertEqualsInRange(0, null, 10, 10);
			assertEqualsInRange(0, null, 10, -10);
		}
		
		public void testC1() {
			Node n = new Node(null);
			// What does checkInRange(n, -10, 10) return ?
			assertEqualsInRange(Ti(758109869), n, -10, 10);
			assertEqualsInRange(-1, n, -1_000_000, -999_999);
			assertEqualsInRange(-1, n, 10, 10);
			assertEqualsInRange(-1, n, 10, 100);
		}
		
		public void testC2() {
			Node n = new Node(l(15));
			// What does checkInRange(n, <first number>, <second number>) return ?
			assertEqualsInRange(Ti(1713987656), n, -10, 10);
			assertEqualsInRange(Ti(104042781), n, -20, 20);
			assertEqualsInRange(-1, n, -10, 15);
			assertEqualsInRange(1, n, -10, 16);
			assertEqualsInRange(1, n, 14, 20);
			assertEqualsInRange(-1, n, 15, 20);
			assertEqualsInRange(-1, n, 16, 20);
		}
		
		public void testC3() {
			Node n = new Node(l(15));
			n.right = new Node(l(42));
			assertEqualsInRange(-1, n, -10, 10);
			assertEqualsInRange(-1, n, -20, 20);
			// What does checkInRange(n, <first number>, <second number>) return ?
			assertEqualsInRange(Ti(93842678), n, -20, 42);
			assertEqualsInRange(Ti(516125291), n, -20, 43);
			assertEqualsInRange(2, n, -20, 100);
			assertEqualsInRange(-1, n, -10, 15);
			assertEqualsInRange(-1, n, -10, 16);
			assertEqualsInRange(-1, n, 14, 42);
			assertEqualsInRange(2, n, 14, 43);
			assertEqualsInRange(-1, n, 15, 100);
			assertEqualsInRange(-1, n, 16, 200);
		}
		
		public void testC4() {
			Node n = new Node(l(42));
			n.right = new Node(l(15));
			// What does checkInRange(n, <first number>, <second number>) return ?
			assertEqualsInRange(Ti(2004061645), n, 0, 100); 
			n.right = new Node(null);
			assertEqualsInRange(-1, n, 0, 100);
		}

		public void testC5() {
			Node n = new Node(l(50));
			n.left = new Node(l(42));
			assertEqualsInRange(-1, n, -10, 10);
			assertEqualsInRange(-1, n, -20, 50);
			assertEqualsInRange(-1, n, 42, 51);
			assertEqualsInRange(2, n, 41, 51);
			assertEqualsInRange(2, n, 0, 100);
			n.left = new Node(null);
			assertEqualsInRange(-1, n, 0, 100);
		}
		
		public void testC6() {
			Node n = new Node(l(15));
			n.left = new Node(l(8));
			n.right = new Node(l(25));
			assertEqualsInRange(-1, n, 2, 25);
			assertEqualsInRange(3, n, 3, 26);
			assertEqualsInRange(-1, n, 8, 30);
			n.left = new Node(l(18));
			assertEqualsInRange(-1, n, 3, 26);
			n.left = new Node(l(8));
			n.right = new Node(l(12));
			assertEqualsInRange(-1, n, 3, 26);
			n.right = n;
			assertEqualsInRange(-1, n, 3, 26);
			n.right = new Node(l(42));
			n.left = n;
			assertEqualsInRange(-1, n, 3, 26);
		}
		
		public void testC7() {
			Node n = new Node(l(15));
			n.left = new Node(l(8));
			n.left.right = new Node(l(25));
			assertEqualsInRange(-1, n, 0, 100);
			n.left.right.entry = l(4);
			assertEqualsInRange(-1, n, 0, 100);
			n.left.right.entry = l(8);
			assertEqualsInRange(-1, n, 0, 100);
			n.left.right.entry = l(15);
			assertEqualsInRange(-1, n, 0, 100);
			n.left.right.entry = null;
			assertEqualsInRange(-1, n, 0, 100);
			n.left.right.entry = l(12);
			assertEqualsInRange(3, n, 0, 100);
		}
		
		public void testC8() {
			Node n = new Node(l(15));
			n.right = new Node(l(25));
			n.right.left = new Node(l(8));
			assertEqualsInRange(-1, n, 0, 100);
			n.right.left.entry = l(42);
			assertEqualsInRange(-1, n, 0, 100);
			n.right.left.entry = l(15);
			assertEqualsInRange(-1, n, 0, 100);
			n.right.left.entry = l(25);
			assertEqualsInRange(-1, n, 0, 100);
			n.right.left.entry = null;
			assertEqualsInRange(-1, n, 0, 100);
			n.right.left.entry = l(18);
			assertEqualsInRange(3, n, 0, 100);
		}
		
		public void testC9() {
			Node n = new Node(l(42));
			n.left = new Node(l(15));
			n.left.right = new Node(l(33));
			Node test = new Node(l(25));
			n.left.right.left = test;
			assertEqualsInRange(4, n, 0, 100);
			test.entry = l(15);
			assertEqualsInRange(-1, n, 0, 100);
			test.entry = l(14);
			assertEqualsInRange(-1, n, 0, 100);
			test.entry = l(33);
			assertEqualsInRange(-1, n, 0, 100);
			test.entry = l(34);
			assertEqualsInRange(-1, n, 0, 100);
			test.entry = l(30);
			assertEqualsInRange(4, n, 0, 100);
			test.left = new Node(l(12));
			assertEqualsInRange(-1, n, 0, 100);
			test.left.entry = l(18);
			assertEqualsInRange(5, n, 0, 100);
		}

		public void testI0() {
			 assertEquals(Ti(1334513409),self.copyInto(array, null, 3));
			 for (int i=0; i < ARRAY_SIZE; ++i) {
				 assertNull(array[i]);
			 }
		}
		
		public void testI1() {
			assertEquals(31,self.copyInto(array, null, 31));
			assertEquals(-1,self.copyInto(array, null, -1));
			for (int i=0; i < ARRAY_SIZE; ++i) {
				assertNull(array[i]);
			}
		}

		public void testI2() {
			Node n = new Node(l(42));
			assertEquals(Ti(1060109897),self.copyInto(array, n, 4));
			for (int i=0; i < ARRAY_SIZE; ++i) {
				switch (i) {
				case 4:
					assertSame(n.entry,array[i]);
					break;
				default:
					assertNull(array[i]);
				}
			}
		}

		public void testI3() {
			Node n = new Node(l(42));
			n.left = new Node(l(15));
			assertEquals(Ti(917033082),self.copyInto(array, n, 2));
			for (int i=0; i < ARRAY_SIZE; ++i) {
				switch (i) {
				case 2:
					assertSame(n.left.entry,array[i]);
					break;
				case 3:
					assertSame(n.entry,array[i]);
					break;
				default:
					assertNull(array[i]);
				}
			}
		}
		
		public void testI9() {
			Node n = new Node(l(55));
			n.left = new Node(l(42));
			n.left.right = new Node(l(49));
			n.left.left = new Node(l(25));
			n.right = new Node(l(88));
			n.right.left = new Node(l(64));
			n.right.right = new Node(l(91));
			assertEquals(11,self.copyInto(array,  n,  4));
			for (int i=0; i < ARRAY_SIZE; ++i) {
				switch (i) {
				case 4:
					assertSame(n.left.left.entry,array[i]);
					break;
				case 5:
					assertSame(n.left.entry,array[i]);
					break;
				case 6:
					assertSame(n.left.right.entry,array[i]);
					break;
				case 7:
					assertSame(n.entry,array[i]);
					break;
				case 8:
					assertSame(n.right.left.entry,array[i]);
					break;
				case 9:
					assertSame(n.right.entry,array[i]);
					break;
				case 10:
					assertSame(n.right.right.entry,array[i]);
					break;
				default:
					assertNull(array[i]);
				}
			}
		}
		
		
		public void testW0() {
			assertWellFormed();
			self.numListings = 1;
			assertFalse(self.wellFormed());
		}
		
		public void testW1() {
			self.root = new Node(null);
			assertFalse(self.wellFormed());
			self.numListings = 1;
			assertFalse(self.wellFormed());
		}
		
		public void testW2() {
			self.root = new Node(l(34));
			assertFalse(self.wellFormed());
			self.numListings = 1;
			assertWellFormed();
			self.numListings = 2;
			assertFalse(self.wellFormed());
		}
		
		public void testW3() {
			self.numListings = 2;
			self.root = new Node(l(42));
			self.root.right = new Node(l(15));
			assertFalse(self.wellFormed());
			self.root.right.entry = l(88);
			assertWellFormed();
			self.root.right.entry = null;
			assertFalse(self.wellFormed());
		}
		
		
		public void testW4() {
			// cycles!
			self.numListings = 2;
			self.root = new Node(l(42));
			self.root.right = self.root;
			assertFalse(self.wellFormed());
			self.numListings = 1;
			assertFalse(self.wellFormed());
			self.root.right = null;
			self.root.left = self.root;
			assertFalse(self.wellFormed());
			self.numListings = 2;
			assertFalse(self.wellFormed());
		}
		
		public void testW5() {
			self.numListings = 3;
			self.root = new Node(l(15));
			self.root.left = new Node(l(8));
			self.root.right = new Node(l(25));
			assertWellFormed();
			self.numListings = 4;
			assertFalse(self.wellFormed());
			self.root.right.right = self.root.right;
			assertFalse(self.wellFormed());
		}
		
		public void testW6() {
			self.root = new Node(l(15));
			self.root.left = new Node(l(8));
			self.root.left.right = new Node(l(25));
			self.root.right = new Node(l(42));
			self.numListings = 1;
			assertFalse(self.wellFormed());
		}
		
		public void testW7() {
			Node n1 = new Node(l(31));
			Node n2 = new Node(l(32));
			Node n3 = new Node(l(33));
			n3.left = n1;
			n3.right = n2;
			self.root = n3;
			
			self.numListings = 3;			
			assertEquals(false, self.wellFormed());
			
			self.numListings = -1;
			assertEquals(false, self.wellFormed());
		}
		
		public void testW8() {
			Node n1 = new Node(l(81));
			Node n2 = new Node(l(82));
			Node n3 = new Node(l(83));
			Node n4 = new Node(l(84));
			Node n5 = new Node(l(85));
			Node n6 = new Node(l(86));
			Node n7 = new Node(l(87));
			
			n2.left = n1;
			n2.right = n3;
			n6.left = n5;
			n6.right = n7;
			n4.left = n2;
			n4.right = n6;
			
			self.root = n4;
			self.numListings = 7;
			assertWellFormed();
			
			self.numListings = 8;
			assertFalse(self.wellFormed());
			self.numListings = 6;
			assertFalse(self.wellFormed());
			self.numListings = 7;
			assertWellFormed();
			
			n3.entry = l(82);
			assertFalse(self.wellFormed());
			n3.entry = l(84);
			assertFalse(self.wellFormed());
		}
		
		public void testW9() {
			Node n1 = new Node(l(10));//number is the price
			Node n2 = new Node(l(20));
			Node n3 = new Node(l(30));
			Node n4 = new Node(l(40));
			Node n5 = new Node(l(50));
			Node n6 = new Node(l(60));
			Node n7 = new Node(l(70));
			Node n8 = new Node(l(80));
			Node n9 = new Node(l(90));
			
			self.root = n5;
			n5.left = n3;
			n3.right = n4;
			n3.left = n1;
			n1.right = n2;
			n5.right = n8;
			n8.left = n7;
			n7.left = n6;
			n8.right = n9;
			self.numListings = 9;
			assertWellFormed();
			
			self.numListings = 10;
			assertFalse(self.wellFormed());
			
			n1.left = new Node(l(15));
			assertFalse(self.wellFormed());
			n1.left = null;
			
			n2.left = new Node(l(05));
			assertFalse(self.wellFormed());
			n2.left = null;
			n2.right = new Node(l(35));
			assertFalse(self.wellFormed());
			n2.right = null;
			
			--self.numListings;
			assertTrue(self.wellFormed());
			++self.numListings;
			
			n4.left = new Node(l(25));
			assertFalse(self.wellFormed());
			n4.left = null;
			n4.right = new Node(l(55));
			assertFalse(self.wellFormed());
			n4.right = null;
			
			n6.left = new Node(l(45));
			assertFalse(self.wellFormed());
			n6.left = null;
			n6.right = new Node(l(75));
			assertFalse(self.wellFormed());
			n6.right = null;
			
			n7.right = new Node(l(85));
			assertFalse(self.wellFormed());
			n7.right = null;
			
			--self.numListings;
			assertWellFormed();
			++self.numListings;
			
			n9.left = new Node(l(75));
			assertFalse(self.wellFormed());
			n9.left = null;
			n9.right = new Node(l(85));
			assertFalse(self.wellFormed());
			n9.right = null;
			
			--self.numListings;
			assertWellFormed();			
		}
	}
}
