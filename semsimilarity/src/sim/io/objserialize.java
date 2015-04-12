package sim.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * 
 * 
 * @author mingchen
 * @date 2015.4.9
 */

public class objserialize {

	private String name = "";
	private ObjectOutputStream oos = null;

	public objserialize(String name) throws IOException {
		this.name = name;
		oos = new ObjectOutputStream(new FileOutputStream(name));
	}

	/**
	 * 
	 * @param obj
	 * 
	 *            writeobj to oos
	 */
	public void objOutput(Object obj) {
		try {
			oos.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @throws IOException
	 * 
	 *             close oos
	 */
	public void close() throws IOException {
		oos.close();
	}

	/**
	 * 
	 * @param name
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object readObj(String name) throws IOException,
	ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(name));
		return ois.readObject();
	}

	public static void main(String[] args) throws IOException,
	ClassNotFoundException {
		// Map<String, LinkedList<relationship>> pmap = (HashMap<String,
		// LinkedList<relationship>>) objserialize
		// .readObj("parents.out");

		HashMap<String, HashMap<String, Float>> dump = (HashMap<String, HashMap<String, Float>>)objserialize.readObj("dumpSVal.out");
		System.out.println(dump.containsKey("GO:0043027"));
		// semanticsim sem = new semanticsim();
		// for (String s : dump.keySet()) {
		// Map<String, Float> map = sem.semanticValue(s);
		// boolean tag = true;
		// for(String go:map.keySet())
		// {
		// if(!(dump.get(s).containsKey(go) &&
		// dump.get(s).get(go).floatValue()==map.get(go).floatValue()))
		// {
		// tag = false;
		// }
		// }
		// System.out.println(s + " " + tag);
		// }

	}
}
