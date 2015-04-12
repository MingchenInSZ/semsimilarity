package sim.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.obolibrary.oboformat.parser.OBOFormatParser;
public class oboparse {
	private static final String fpath = "D:\\tmp\\bioinfo\\aftermid\\onto\\";

	/**
	 * 
	 * @param file
	 * @throws IOException
	 * 
	 *             dump all the parents go terms and the obsolete terms
	 * 
	 */
	private void parse(String file) throws IOException {
		OBODoc obo = new OBOFormatParser().parse(new File(file));
		objserialize pout = new objserialize("parents.out");
		objserialize oout = new objserialize("obsolete.out");
		objserialize unigo = new objserialize("ungoes.out");
		LinkedList<String> llist = new LinkedList<String>();
		LinkedList<String> ungo = new LinkedList<String>();
		Map<String, LinkedList<relationship>> pmap = new HashMap<String, LinkedList<relationship>>();
		for (Frame c : obo.getTermFrames()) {

			if (c.getTagValue("is_obsolete") != null) {
				System.out.println("is_obsolete "
						+ c.getTagValue("is_obsolete"));
				llist.add(c.getTagValue("is_obsolete").toString());
			} else { // if the term is not obsolete
				ungo.add(c.getId());

				LinkedList<relationship> pts = new LinkedList<relationship>();
				// store all the relationships
				if (c.getTagValue("is_a") != null) {
					// extract all the is_a relationship
					for (Object obj : c.getTagValues("is_a")) {
						relationship rels = new relationship(obj.toString(),
								(float) 0.8);
						pts.add(rels);
						System.out.println("is_a " + obj.toString());
					}
				}
				if (c.getTagValue("relationship") != null) {
					// extract all the part_of relationship
					for (Clause cc : c
							.getClauses(OboFormatTag.TAG_RELATIONSHIP)) {
						relationship rels = new relationship(cc.getValue2()
								.toString(), (float) 0.6);
						pts.add(rels);
						System.out.println("part_of  " + cc.getValue2());
					}
				}
				pmap.put(c.getId(), pts); // put the (key,value) pair
			}
			System.out.println("---------------------------");
		}
		// dump all the objects
		oout.objOutput(llist);
		oout.close();
		pout.objOutput(pmap);
		pout.close();
		unigo.objOutput(ungo);
		unigo.close();

	}

	public static void main(String[] args) throws IOException {
		new oboparse().parse(fpath + "go-basic.obo");
	}
}
