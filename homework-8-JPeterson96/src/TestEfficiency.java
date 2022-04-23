import java.util.Random;

import junit.framework.TestCase;
import edu.uwm.cs351.Realty;


public class TestEfficiency extends TestCase { 
	Realty b;
    private Random random;
    
    private static final int POWER = 20; // 1/2 million entries
    private static final int TESTS = 100000;
    
    private Realty.Listing l(int p) { return new Realty.Listing(p, ""); }


	protected void setUp() throws Exception {
		super.setUp();
		random = new Random();
		try {
			assert b.size() == TESTS : "cannot run test with assertions enabled";
		} catch (NullPointerException ex) {
			throw new IllegalStateException("Cannot run test with assertions enabled");
		}
		b = new Realty();
		int max = (1 << (POWER)); // 2^(POWER) = 2 million
		for (int power = POWER; power > 1; --power) {
			int incr = 1 << power;
			for (int i=1 << (power-1); i < max; i += incr) {
				b.add(l(i));
			}
		}
	}
    
    @Override
    protected void tearDown() {
    	b = null;
    }

    public void testSize() {
    	for (int i=0; i < TESTS; ++i) {
    		assertEquals((1<<(POWER-1))-1,b.size());
    	}
    }
    
    public void testMin() {
    	for (int i=0; i < TESTS; ++i) {
    		assertEquals(l(2),b.getMin());
    	}
    }
    
    public void testNext() {
    	for (int i=0; i < TESTS; ++i) {
    		int r = random.nextInt(TESTS);
    		assertEquals(l(r*2+2),b.getNext(r*2));
    	}
    }

    public void testToArray() {
    	Realty.Listing[] a = b.toArray(null);
    	assertEquals((1<<(POWER-1))-1,a.length);
    	int max = 1 << POWER;
    	for (int i=2; i < max; i += 2) {
    		assertEquals(l(i),a[i/2-1]);
    	}
    	b = new Realty();
    	b.addAll(a, 0, a.length);
    	testSize();
    	testMin();
    	testNext();
    }

}
