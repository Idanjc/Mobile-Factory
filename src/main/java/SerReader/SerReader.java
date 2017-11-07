package SerReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import bgu.spl.a2.sim.Product;

public class SerReader {

	public static void main(String[] args) {
		AtomicBoolean flag = new AtomicBoolean(false);

		System.out.println(String.format("%-20s", "----Deserializer----"));
		System.out.println("SPL171 deserializer for assignment 2");
		System.out.println();
		System.out.println();

		SerReader obj = new SerReader();

		ConcurrentLinkedQueue<Product> res = obj.deserialzeObject(args[0]);

		if (res == null) {
			System.out.println("error -> null object");
			System.out.println("ser file is corrupted!");
			System.out.println("final grade is zero  (╯°□°)╯︵ ┻━┻");
		} else {
			res.iterator().forEachRemaining((var) -> {
				flag.set(true);
				PrintPro(var);
				System.out.println();
			});
		}

		if (flag.get()) {
			System.out.println("*************************************************************************");
			System.out.println("*\t\t\tIt seems that you did it right!\t\t\t*");
			System.out.println("*\tbut we don't take any responsibility on any result! \\_(ʘ_ʘ)_/ \t*");
			System.out.println("*\t\t\t\tcompare your output!\t\t\t*");
			System.out.println("*\tCredits:\t\t\t\t\t\t\t*");
			System.out.println("*\t\t\t\t***M.Zidane***\t\t\t\t*");
			System.out.println("*\t\t\t\t***M.Sleiman***\t\t\t\t*");
			System.out.println("*************************************************************************");
		}
	}

	public ConcurrentLinkedQueue<Product> deserialzeObject(String filename) {

		ConcurrentLinkedQueue<Product> res = null;

		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {

			fin = new FileInputStream(filename);
			ois = new ObjectInputStream(fin);
			ConcurrentLinkedQueue<Product> concurrentLinkedQueue = (ConcurrentLinkedQueue<Product>) (ois.readObject());
			res = concurrentLinkedQueue;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return res;

	}

	private static void PrintPro(Product product) {
		System.out.println("ProductName: " + product.getName() + "  Product Id = " + product.getFinalId());

		System.out.println("PartsList {");
		if (product.getParts().size() > 0) {
			for (int i = 0; i < product.getParts().size(); i++) {
				PrintPro(product.getParts().get(i));
			}
		}
		System.out.println("}");

	}
}
