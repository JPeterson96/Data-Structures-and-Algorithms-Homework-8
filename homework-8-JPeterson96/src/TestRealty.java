import java.util.Comparator;

import edu.uwm.cs.junit.LockedTestCase;
import edu.uwm.cs351.Realty;


public class TestRealty extends LockedTestCase {
	protected <T> void assertException(Class<?> excClass, Runnable f) {
		try {
			f.run();
			assertFalse("Should have thrown an exception, not returned",true);
		} catch (RuntimeException ex) {
			if (!excClass.isInstance(ex)) {
				ex.printStackTrace();
				assertFalse("Wrong kind of exception thrown: "+ ex.getClass().getSimpleName(),true);
			}
		}		
	}

	Comparator<Integer> ascending = new Comparator<Integer>() {
		public int compare(Integer o1, Integer o2) {
			return o1 - o2;
		}
	};

	private Realty.Listing l(int p) { return new Realty.Listing(p, ""); }

	Realty r;
	Realty.Listing[] a, a0, a1, a2, a3, a4;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			assert r.size() == 42;
			assertTrue("Assertions not enabled.  Add -ea to VM Args Pane in Arguments tab of Run Configuration",false);
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		r = new Realty();
		a0 = new Realty.Listing[0];
		a1 = new Realty.Listing[1];
		a2 = new Realty.Listing[2];
		a3 = new Realty.Listing[3];
		a4 = new Realty.Listing[4];
	}

	public void test00() {
		assertEquals(0,r.size());
	}

	public void test10() {
		assertTrue(r.add(l(10)));
		assertEquals(1,r.size());
	}
	
	public void test11() {
		r.add(l(20)); // empty address
		assertFalse(r.add(l(20))); // ditto
		assertEquals(Tb(832502709), r.add(new Realty.Listing(20, "not empty")));
		assertEquals(Ti(1963099416),r.size());
	}
	
	public void test12() {
		r.add(l(40));
		assertTrue(r.add(l(30)));
		assertEquals(2,r.size());
	}
	
	public void test13() {
		r.add(l(41));
		r.add(l(31));
		
		assertFalse(r.add(l(41)));
		assertFalse(r.add(new Realty.Listing(31,"nowhere")));
		assertEquals(2,r.size());
	}
	
	public void test14() {
		r.add(l(32));
		assertTrue(r.add(l(42)));
		
		assertEquals(2,r.size());		
	}
	
	public void test15() {
		r.add(l(32));
		r.add(l(42));
		
		assertFalse(r.add(l(32)));
		assertFalse(r.add(new Realty.Listing(42,"nowhere")));
	}
	
	public void test16() {
		r.add(l(33));
		r.add(l(36));
		assertTrue(r.add(l(39)));
		
		assertEquals(3,r.size());
		
		assertFalse(r.add(l(39)));
		assertFalse(r.add(new Realty.Listing(39,"nowhere")));
	}
	
	public void test17() {
		assertTrue(r.add(l(55)));
		assertTrue(r.add(l(52)));
		assertTrue(r.add(l(53)));
		assertTrue(r.add(l(54)));
		assertTrue(r.add(l(58)));
		assertTrue(r.add(l(57)));
		assertTrue(r.add(l(56)));
		assertTrue(r.add(l(51)));
		assertTrue(r.add(l(59)));
		assertTrue(r.add(l(50)));
		
		for (int i=50; i < 60; ++i) {
			assertFalse("readding "+i, r.add(new Realty.Listing(i,"nowhere")));
		}
		
		assertEquals(10,r.size());
	}
	
	public void test18() {
		assertException(NullPointerException.class, () -> r.add(null));
	}
	
	public void test19() {
		r.add(l(42));
		assertException(NullPointerException.class, () -> r.add(null));		
	}

	
	/// test2x: testing getMin
	
	public void test20() {
		assertNull(r.getMin());
	}
	
	public void test21() {
		r.add(l(66));
		assertEquals(Ti(2098713454), r.getMin().getPrice());
	}
	
	public void test22() {
		r.add(l(66));
		r.add(l(69));
		assertEquals(Ti(1054310315), r.getMin().getPrice());
	}
	
	public void test23() {
		r.add(l(66));
		r.add(l(69));
		r.add(l(64));
		assertEquals(Ti(1214105002), r.getMin().getPrice());
	}
	
	public void test24() {
		r.add(l(26));
		r.add(l(29));
		r.add(l(24));
		r.add(l(28));
		assertEquals(24, r.getMin().getPrice());
	}
	
	public void test25() {
		r.add(l(6));
		r.add(l(9));
		r.add(l(4));
		r.add(l(8));
		r.add(l(5));
		assertEquals(4, r.getMin().getPrice());
	}

	public void test26() {
		r.add(l(6));
		r.add(l(9));
		r.add(l(4));
		r.add(l(8));
		r.add(l(5));
		Realty.Listing l3 = new Realty.Listing(3,"Here");
		r.add(l3);
		assertSame(l3, r.getMin());
	}

	public void test29() {
		r.add(l(6));
		r.add(l(9));
		r.add(l(4));
		r.add(l(8));
		r.add(l(5));
		r.add(l(1));
		r.add(l(2));
		assertEquals(1, r.getMin().getPrice());
	}
		
	
	/// test3x: testing getNext
	
	public void test30() {
		assertNull(r.getNext(70));
	}
	
	public void test31() {
		r.add(l(82));
		assertEquals(l(82),r.getNext(70));
		assertNull(r.getNext(83));
		assertEquals(l(82),r.getNext(81));
		assertNull(r.getNext(82));
	}
	
	public void test32() { 
		r.add(l(82));
		r.add(l(88));		
		assertNull(r.getNext(88));
		assertEquals(Ti(467609346),r.getNext(87).getPrice());
		assertEquals(l(88),r.getNext(86));
		assertEquals(l(88),r.getNext(82));
		assertEquals(Ti(563240632),r.getNext(81).getPrice());
	}
	
	public void test33() {
		r.add(l(82));
		r.add(l(88));	
		r.add(l(78));	
		assertEquals(Ti(570070519),r.getNext(70).getPrice());
		assertEquals(Ti(405517127),r.getNext(87).getPrice());
		assertEquals(l(88),r.getNext(82));
		assertEquals(l(82),r.getNext(81));
		assertEquals(Ti(1602331637),r.getNext(78).getPrice());
		assertEquals(l(78),r.getNext(77));
		assertEquals(l(78),r.getNext(76));
		assertNull(r.getNext(90));
	}
	
	public void test34() {
		r.add(l(82));
		r.add(l(88));	
		r.add(l(78));	
		r.add(l(86));		
		assertEquals(l(78),r.getNext(70));
		assertNull(r.getNext(90));
		assertNull(r.getNext(88));
		assertEquals(l(88),r.getNext(86));
		assertEquals(l(86),r.getNext(85));
		assertEquals(l(86),r.getNext(82));
		assertEquals(l(82),r.getNext(81));
		assertEquals(l(82),r.getNext(78));
		assertEquals(l(78),r.getNext(77));
	}
	
	public void test35() {
		r.add(l(82));
		r.add(l(88));	
		r.add(l(78));	
		r.add(l(86));
		r.add(l(80));	
		assertEquals(l(78),r.getNext(70));
		assertNull(r.getNext(90));
		assertNull(r.getNext(88));
		assertEquals(l(88),r.getNext(86));
		assertEquals(l(86),r.getNext(85));
		assertEquals(l(86),r.getNext(82));
		assertEquals(l(82),r.getNext(81));
		assertEquals(l(82),r.getNext(80));
		assertEquals(l(80),r.getNext(78));
	}
	
	public void test36() {
		r.add(l(82));
		r.add(l(88));	
		r.add(l(78));	
		r.add(l(86));
		r.add(l(80));
		r.add(l(72));	
		assertEquals(l(72),r.getNext(70));
		assertEquals(l(72),r.getNext(71));
		assertEquals(l(78),r.getNext(72));
		assertEquals(l(78),r.getNext(73));
		assertEquals(l(78),r.getNext(77));
		assertEquals(l(80),r.getNext(78));
		assertEquals(l(80),r.getNext(79));
		assertEquals(l(82),r.getNext(80));
		assertEquals(l(82),r.getNext(81));
		assertEquals(l(86),r.getNext(82));
		assertEquals(l(86),r.getNext(83));
		assertEquals(l(86),r.getNext(85));
		assertEquals(l(88),r.getNext(86));
		assertEquals(l(88),r.getNext(87));
		assertNull(r.getNext(88));
	}
	
	public void test37() {
		r.add(l(82));
		r.add(l(88));	
		r.add(l(78));	
		r.add(l(86));
		r.add(l(80));
		r.add(l(72));
		r.add(l(74));
		
		assertEquals(l(72),r.getNext(70));
		assertEquals(l(72),r.getNext(71));
		assertEquals(l(74),r.getNext(72));
		assertEquals(l(74),r.getNext(73));
		assertEquals(l(78),r.getNext(74));
		assertEquals(l(78),r.getNext(75));
		assertEquals(l(78),r.getNext(77));
		assertEquals(l(80),r.getNext(78));
		assertEquals(l(80),r.getNext(79));
		assertEquals(l(82),r.getNext(80));
		assertEquals(l(82),r.getNext(81));
		assertEquals(l(86),r.getNext(82));
		assertEquals(l(86),r.getNext(83));
		assertEquals(l(86),r.getNext(85));
		assertEquals(l(88),r.getNext(86));
		assertEquals(l(88),r.getNext(87));
		assertNull(r.getNext(88));
	}
	
	public void test38() {
		r.add(l(82));
		r.add(l(88));	
		r.add(l(78));	
		r.add(l(86));
		r.add(l(80));
		r.add(l(72));
		r.add(l(74));
		r.add(l(76));
		
		assertEquals(l(72),r.getNext(70));
		assertEquals(l(72),r.getNext(71));
		assertEquals(l(74),r.getNext(72));
		assertEquals(l(74),r.getNext(73));
		assertEquals(l(76),r.getNext(74));
		assertEquals(l(76),r.getNext(75));
		assertEquals(l(78),r.getNext(76));
		assertEquals(l(78),r.getNext(77));
		assertEquals(l(80),r.getNext(78));
		assertEquals(l(80),r.getNext(79));
		assertEquals(l(82),r.getNext(80));
		assertEquals(l(82),r.getNext(81));
		assertEquals(l(86),r.getNext(82));
		assertEquals(l(86),r.getNext(83));
		assertEquals(l(86),r.getNext(85));
		assertEquals(l(88),r.getNext(86));
		assertEquals(l(88),r.getNext(87));
		assertNull(r.getNext(88));		
	}
	
	public void test39() {
		r.add(l(82));
		r.add(l(88));	
		r.add(l(78));	
		r.add(l(86));
		r.add(l(80));
		r.add(l(72));
		r.add(l(74));
		r.add(l(76));
		r.add(l(70));
		
		assertEquals(l(72),r.getNext(70));
		assertEquals(l(72),r.getNext(71));
		assertEquals(l(74),r.getNext(72));
		assertEquals(l(74),r.getNext(73));
		assertEquals(l(76),r.getNext(74));
		assertEquals(l(76),r.getNext(75));
		assertEquals(l(78),r.getNext(76));
		assertEquals(l(78),r.getNext(77));
		assertEquals(l(80),r.getNext(78));
		assertEquals(l(80),r.getNext(79));
		assertEquals(l(82),r.getNext(80));
		assertEquals(l(82),r.getNext(81));
		assertEquals(l(86),r.getNext(82));
		assertEquals(l(86),r.getNext(83));
		assertEquals(l(86),r.getNext(85));
		assertEquals(l(88),r.getNext(86));
		assertEquals(l(88),r.getNext(87));
		assertNull(r.getNext(88));
		assertEquals(l(70),r.getNext(69));
		assertEquals(l(70),r.getNext(-100));		
	}

	public void test40() {
		for (int power : new int[]{128,64,32,16,8,4,2}) {
			for (int i=power; i < 256; i += 2*power) {
				r.add(l(i));
			}
		}
		
		assertEquals("evens.min",l(2),r.getMin());
		assertEquals("evens.getNext(-10)",l(2),r.getNext(-10));
		for (int i=0; i < 254; ++i) {
			assertEquals("evens.getNext(" + i + ")", l((i+2)&~1), r.getNext(i));
		}
		assertEquals("evens.getNext(254)",null,r.getNext(254));
		assertEquals("evens.getNext(255)",null,r.getNext(255));
	}

	
	/// test5x: testing toArray
	
	public void test50() {
		assertSame(a0,r.toArray(a0));
	}
	
	public void test51() {
		a = r.toArray(null);
		assertEquals(0, a.length);
	}
	
	public void test52() {
		a1[0] = l(3);
		assertSame(a1, r.toArray(a1));
		assertEquals(l(3), a1[0]);
	}
	
	public void test53() {
		Realty.Listing l17 = l(17), l37 = l(37);
		
		a4[0] = l17; a4[2] = l37;
		
		assertSame(a4,r.toArray(a4));
		
		assertSame(l17,a4[0]);
		assertNull(a4[1]);
		assertSame(l37,a4[2]);
	}
	
	public void test54() {
		Realty.Listing l18 = l(18), l26 = l(26);
		
		r.add(l26);
		
		a = r.toArray(null);
		assertEquals(1,a.length);
		assertSame(l26,a[0]);
		
		a = r.toArray(a0);
		assertEquals(1,a.length);
		assertSame(l26,a[0]);
		
		assertSame(a1,r.toArray(a1));
		assertSame(l26,a1[0]);
		
		a2[0] = null;
		a2[1] = null;
		assertSame(a2,r.toArray(a2));
		assertSame(l26,a2[0]);
		assertNull(a2[1]);
		
		a3[0] = l18;
		a3[1] = l18;
		a3[2] = l18;
		assertSame(a3,r.toArray(a3));
		assertSame(l26,a3[0]);
		assertSame(l18,a3[1]);
		assertSame(l18,a3[2]);
	}
	
	public void test55() {
		Realty.Listing l18 = l(18), l1 = l(1);
		
		r.add(l(36));
		r.add(l(39));
		
		a = r.toArray(null);
		assertEquals(2,a.length);
		assertEquals(Ti(1873457588),a[0].getPrice());
		assertEquals(Ti(1487651082),a[1].getPrice());
		
		a = r.toArray(a0);
		assertEquals(2,a.length);
		assertEquals(l(36),a[0]);
		assertEquals(l(39),a[1]);

		a1[0] = l1;
		a = r.toArray(a1);
		assertEquals(2,a.length);
		assertEquals(l(36),a[0]);
		assertEquals(l(39),a[1]);
		assertSame(l1,a1[0]);
		
		a2[1] = l1;
		assertSame(a2,r.toArray(a2));
		assertEquals(l(36),a2[0]);
		assertEquals(l(39),a2[1]);
		
		a3[1] = l18;
		assertSame(a3,r.toArray(a3));
		assertEquals(l(36),a3[0]);
		assertEquals(l(39),a3[1]);
		assertNull(a3[2]);
	}
	
	public void test56() {
		Realty.Listing l19 = l(19), l8 = l(8);
		
		r.add(l(46));
		r.add(l(49));
		r.add(l(44));
		
		a = r.toArray(null);
		assertEquals(3,a.length);
		assertEquals(Ti(987644334),a[0].getPrice());
		assertEquals(Ti(531919791),a[1].getPrice());
		assertEquals(Ti(599439462),a[2].getPrice());
		
		a = r.toArray(a0);
		assertEquals(3,a.length);
		assertEquals(l(44),a[0]);
		assertEquals(l(46),a[1]);
		assertEquals(l(49),a[2]);

		a = r.toArray(a1);
		assertEquals(3,a.length);
		assertEquals(l(44),a[0]);
		assertEquals(l(46),a[1]);
		assertEquals(l(49),a[2]);
		assertNull(a1[0]);
		
		a2[1] = l8;
		a2[0] = l19;
		a = r.toArray(a2);
		assertEquals(3,a.length);
		assertEquals(l(44),a[0]);
		assertEquals(l(46),a[1]);
		assertEquals(l(49),a[2]);
		assertSame(l19,a2[0]);
		assertSame(l8,a2[1]);
		
		a3[1] = l8;
		assertSame(a3,r.toArray(a3));
		assertEquals(l(44),a3[0]);
		assertEquals(l(46),a3[1]);
		assertEquals(l(49),a3[2]);
		
		a4[0] = l19;
		a4[2] = l8;
		a4[3] = l19;
		assertSame(a4,r.toArray(a4));
		assertEquals(l(44),a4[0]);
		assertEquals(l(46),a4[1]);
		assertEquals(l(49),a4[2]);
		assertSame(l19,a4[3]);
	}
	
	public void test59() {
		Realty.Listing l20 = l(20), l7 = l(7);
		
		r.add(l(56));
		r.add(l(59));
		r.add(l(54));
		r.add(l(58));
		r.add(l(55));
		r.add(l(51));
		r.add(l(52));
		r.add(l(53));
		r.add(l(50));
		
		a4[0] = l20;
		a4[1] = null;
		a4[2] = l7;
		a4[3] = l20;
		a = r.toArray(a4);
		assertEquals(9,a.length);
		assertEquals(l(50),a[0]);
		assertEquals(l(51),a[1]);
		assertEquals(l(52),a[2]);
		assertEquals(l(53),a[3]);
		assertEquals(l(54),a[4]);
		assertEquals(l(55),a[5]);
		assertEquals(l(56),a[6]);
		assertEquals(l(58),a[7]);
		assertEquals(l(59),a[8]);
		assertSame(l20,a4[0]);
		assertNull(a4[1]);
		assertSame(l7,a4[2]);
		assertSame(l20,a4[3]);
		
		a = r.toArray(null);
		assertEquals(9,a.length);
		assertEquals(l(50),a[0]);
		assertEquals(l(51),a[1]);
		assertEquals(l(52),a[2]);
		assertEquals(l(53),a[3]);
		assertEquals(l(54),a[4]);
		assertEquals(l(55),a[5]);
		assertEquals(l(56),a[6]);
		assertEquals(l(58),a[7]);
		assertEquals(l(59),a[8]);
		
		Realty.Listing[] b = a;
		
		a = r.toArray(a0);
		assertTrue(a != b);// don't reuse!
		assertEquals(9,a.length);
		assertEquals(l(50),a[0]);
		assertEquals(l(51),a[1]);
		assertEquals(l(52),a[2]);
		assertEquals(l(53),a[3]);
		assertEquals(l(54),a[4]);
		assertEquals(l(55),a[5]);
		assertEquals(l(56),a[6]);
		assertEquals(l(58),a[7]);
		assertEquals(l(59),a[8]);		
	}
	
	
	/// test6x: testing addAll
	
	public void test60() {
		r.add(l(42));
		assertEquals(0, r.addAll(a0, 0, 0));
		assertEquals(1,r.size());
	}
	
	public void test61() {
		r.add(l(42));
		a1[0] = l(16);
		
		assertEquals(0, r.addAll(a1, 0, 0));
		assertEquals(1, r.size());
		assertEquals(l(42), r.getMin());
		
		assertEquals(0, r.addAll(a1, 1, 1));
		assertEquals(1, r.size());
		assertEquals(l(42), r.getMin());
		
		assertEquals(1, r.addAll(a1, 0, 1));
		assertEquals(l(16), r.getMin());
		assertEquals(2, r.size());
	}
	
	public void test62() {
		r.add(l(42));
		a2[0] = new Realty.Listing(42, "somewhere");
		a2[1] = null;
		assertEquals(0, r.addAll(a2,  0,  1));
		assertEquals(l(42), r.getMin());
		assertEquals(1, r.size());
	}
	
	public void test63() {
		r.add(l(60));
		
		a3[0] = null;
		a3[1] = l(88);
		a3[2] = l(42);
		
		assertEquals(0, r.addAll(a3, 2, 2));
		
		assertEquals(2, r.addAll(a3, 1, 3));
		assertEquals(3, r.size());
		assertEquals(l(42), r.getMin());
	}
	
	public void test64() {
		a4[0] = l(53);
		a4[1] = l(64);
		a4[2] = l(88);
		a4[3] = l(64);
		
		assertEquals(3, r.addAll(a4, 0, 4));
		
		assertEquals(3, r.size());
		assertEquals(l(88), r.getNext(64));
	}
	
	public void test65() {
		a = new Realty.Listing[10];
		a[0] = null;
		a[1] = l(1);
		a[2] = l(2);
		a[3] = l(3);
		a[4] = null;
		a[5] = l(5);
		a[6] = l(6);
		a[7] = l(7);
		a[8] = l(8);
		a[9] = l(9);
		
		assertEquals(0, r.addAll(a, 4, 4));
		assertEquals(0, r.addAll(a, 7, 7));
		assertEquals(0, r.size());
		
		assertEquals(3, r.addAll(a, 1, 4));
		assertEquals(3, r.size());
		
		assertEquals(3, r.addAll(a, 6, 9));
		assertEquals(6, r.size());
		assertNull(r.getNext(8));
	}
	
	public void test69() {
		r.add(l(69));
		a = new Realty.Listing[] {l(10),l(5),l(100),l(7),l(8),l(20),l(15)};
		assertEquals(a.length, r.addAll(a, 0, a.length));
		assertEquals(a.length+1, r.size());
	}
}
