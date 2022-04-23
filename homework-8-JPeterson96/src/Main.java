import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.uwm.cs351.Realty;
import edu.uwm.cs351.Realty.Listing;

public class Main {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: Main <realty file>");
			System.exit(1);
		}
		new Main(args[0]).run();
	}
	
	private Realty realty;
	private String filename;
	private Scanner in;
	
	public Main(String name) {
		realty = new Realty();
		filename = name;
		in = new Scanner(System.in);
	}
	
	public void run() {
		readRealty();
		help();
		for (;;) {
			System.out.print("> ");
			String line = in.nextLine();
			if (line.equals("help")) help();
			else if (line.equals("list")) list();
			else if (line.equals("save")) writeRealty();
			else if (line.equals("quit")) break;
			else if (line.startsWith("find")) find(line);
			else if (line.startsWith("add")) add(line);
			else if (line.startsWith("remove")) remove(line);
			else error(line);
		}
	}

	private void help() {
		System.out.println("Realty System.  Listings: " + realty.size());
		System.out.println("Commands: ");
		System.out.println("  help - get this message");
		System.out.println("  list - print out entire set of listings");
		System.out.println("  quit - quite the program without saving");
		System.out.println("  save - save the realty listings in the file they were read from.");
		System.out.println("  find $N - show listings above given price.");
		System.out.println("  add $N:A - add listing for $N at address A");
		System.out.println("  remove $N:A - remove listing");
	}
	
	private static final int PAGE = 10;
	
	private void list() {
		listFrom(realty.getMin());
	}
	
	private void find(String line) {
		int dollar = line.indexOf('$');
		int floor = 0;
		if (dollar > 0) {
			try {
				floor = Integer.parseInt(line.substring(dollar+1));
			} catch (NumberFormatException e) {
				System.out.println("Badly formatted price:" + line.substring(dollar+1));
				dollar = -1;
			}
		}
		if (dollar < 0) {
			System.out.println("Example: find $10000");
			return;
		}
		Listing start = realty.getNext(floor);
		if (start == null) {
			System.out.println("No listings with price greater than " + floor);
		} else listFrom(start);
	}

	private void listFrom(Listing start) {
		int i = 0;
		for (Realty.Listing l = start; l != null; l = realty.getNext(l.getPrice())) {
			System.out.println(l);
			if (++i > PAGE) {
				System.out.print("Continue? [yes/no] ");
				if (in.nextLine().equalsIgnoreCase("no")) break;
				i = 0;
			}
		}
	}
	
	private void add(String line) {
		int dollar = line.indexOf('$');
		Realty.Listing listing = null;
		if (dollar > 0) {
			try {
				listing = Listing.fromString(line.substring(dollar));
			} catch (NumberFormatException e) {
				// muffle error
			}
		} 
		if (listing == null) {
			System.out.println("Listing in wrong format.  Use $10000: address");
			return;
		}
		if (realty.add(listing)) return;
		System.out.println("Listing with that price already present.");
	}
	
	private void remove(String line) {
		int dollar = line.indexOf('$');
		Realty.Listing listing = null;
		if (dollar > 0) {
			try {
				listing = Listing.fromString(line.substring(dollar));
			} catch (NumberFormatException e) {
				// muffle error
			}
		} 
		if (listing == null) {
			System.out.println("Listing in wrong format.  Use $10000: address");
			return;
		}
		System.out.println("'remove' functionality has been removed.");
		// if (realty.remove(listing)) return;
		// System.out.println("No listing with exact price and address exist.");
	}
	
	private void error(String line) {
		System.out.println("Error: cannot understand command, type 'help' for help.");
	}
	
	private void readRealty() {
		List<Realty.Listing> listings = new ArrayList<Realty.Listing>();
		try(Scanner s = new Scanner(new File(filename))) {
			while (s.hasNextLine()) {
				String line = s.nextLine();
				try {
					listings.add(Realty.Listing.fromString(line));
				} catch (NumberFormatException e) {
					System.out.println("Rejecting illformatted entry: " + line);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not open " + filename + " for reading.  Skipping.");
		}
		realty.addAll(listings.toArray(new Realty.Listing[listings.size()]), 0, listings.size());
	}
	
	private void writeRealty() {
		Realty.Listing[] listings = realty.toArray(null);
		try {
			Writer w = new FileWriter(filename);
			for (Realty.Listing l : listings) {
				w.write(l.toString());
				w.write('\n');
			}
			w.close();
			System.out.println("Listings successfully written");
		} catch (IOException e) {
			System.out.println("Problem: file may be partly written: " + e);
		}
	}
}
