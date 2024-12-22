import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2024/day/21
 *
 */
public class Y24Day22 {
	
	
	public static void mainPart1(String inputfile) throws FileNotFoundException {

		try (Scanner scanner = new Scanner(new File(inputfile))) {
			long sum2000 = 0;
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if (line.isBlank()) {
					break;
				}
				long secret = Long.parseLong(line);
//				System.out.println("Secret: "+secret);
//				System.out.println("  next: "+nextSecret(secret));
				for (int i=0; i<10; i++) {
					secret = nextSecret(secret);
//					System.out.println("    "+(i+1)+":"+secret);
				}
				for (int i=0; i<2000-10; i++) {
					secret = nextSecret(secret);
				}
				sum2000 += secret;
//				System.out.println("  2000: "+secret);
			}
			System.out.println("SUMME 2000: "+sum2000);
		}
	}

	private static long nextSecret(long secret) {
		long mult = secret*64;
		secret = secret ^ mult;
		secret = (secret % 16777216);
		long div = secret/32;
		secret = secret ^ div;
		secret = (secret % 16777216);
		long mult2 = secret*2048;
		secret = secret ^ mult2;
		secret = (secret % 16777216);
		return secret;
	}

	
	record Changes(int c4, int c3, int c2, int c1) {
		@Override
		public String toString() {
			return "("+c4+","+c3+","+c2+","+c1+")";
		}

	}
	
	record PriceChanges(long value, int c4, int c3, int c2, int c1) {
		public PriceChanges next(long newValue) {
			return new PriceChanges(newValue, c3, c2, c1, (int)(newValue-value)); 
		}
		public Changes getChanges() {
			return new Changes(c4, c3, c2, c1);
		}
		@Override
		public String toString() {
			return "("+value+"|"+c4+","+c3+","+c2+","+c1+")";
		}
	}
	
	public static void mainPart2(String inputfile) throws FileNotFoundException {
		long sum2000 = 0;
		Changes watch = new Changes(2,-1,1,0);
		Map<Changes, Integer> sumBestPrices = new HashMap<>();
		try (Scanner scanner = new Scanner(new File(inputfile))) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if (line.isBlank()) {
					break;
				}
				long secret = Long.parseLong(line);
//				System.out.println("Secret: "+secret);
				PriceChanges changes = new PriceChanges(secret%10, 0, 0, 0, 0);
				long baseSecret = secret;
				for (int i=0; i<3; i++) {
					secret = nextSecret(secret);
					changes = changes.next(secret%10);
//					System.out.println((i+1)+": "+secret+" "+changes);
				}
				Map<Changes, Integer> bestPrices = new HashMap<>();
				for (int i=3; i<2000; i++) {
					secret = nextSecret(secret);
					changes = changes.next(secret%10);
					updateBestPrice(bestPrices, changes);
					if (changes.getChanges().equals(watch)) {
						System.out.println("     WATCH "+changes+" ("+secret+" = "+baseSecret+" x "+(i+1)+")");
					}
//					System.out.println((i+1)+": "+secret+" "+changes);
				}
				sum2000 += secret;
				Integer bp = bestPrices.get(watch);
				for (Entry<Changes, Integer> entry:bestPrices.entrySet()) {
					addBestPrice(sumBestPrices, entry.getKey(), entry.getValue());
				}
				if (bp != null) {
					System.out.println("   FOUND "+bestPrices.get(watch)+"   "+watch+": + "+bestPrices.get(watch)+" = "+sumBestPrices.get(watch));
				}
				
			}
		}
		int bestChangeValue = -1;
		Changes bestChange = null;
		for (Entry<Changes, Integer> entry:sumBestPrices.entrySet()) {
			if (entry.getValue()>bestChangeValue) {
				bestChangeValue = entry.getValue();
				bestChange = entry.getKey();
			}
		}
		System.out.println("BEST CHANGE: "+bestChange+" value: "+bestChangeValue);
		System.out.println("SUM2000: "+sum2000);
	}
	
	private static void updateBestPrice(Map<Changes, Integer> bestPrices, PriceChanges changes) {
		Integer bp = bestPrices.get(changes.getChanges());
		if (bp==null || (bp<changes.value)) {
			bestPrices.put(changes.getChanges(), (int)changes.value);
		}
	}

	private static void addBestPrice(Map<Changes, Integer> sumBestPrices, Changes changes, int bestValue) {
		Integer sbp = sumBestPrices.get(changes);
		if (sbp==null) {
			sbp = 0;
		}
		sumBestPrices.put(changes, sbp+bestValue);
	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I  ---");
//		mainPart1("exchange/day22/feri/input-example.txt");
//		mainPart1("exchange/day22/feri/input-example-2.txt");
		mainPart1("exchange/day22/feri/input.txt");     // = 14119253575
		System.out.println("---------------");
		System.out.println();
		System.out.println("--- PART II ---");
//		mainPart2("exchange/day22/feri/input-example.txt");
//		mainPart2("exchange/day22/feri/input-example-2.txt");
//		mainPart2("exchange/day22/feri/input-example-3.txt");
		mainPart2("exchange/day22/feri/input.txt");     // < 1641 (2,-1,1,0)
		System.out.println("---------------");
	}

}
