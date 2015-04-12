package sim.io;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 
 * 
 * @author mingchen
 * @date 2015.4.9
 * 
 *       this class defines the go and its parents
 */
public class parents implements Serializable {

	private String goId;
	private LinkedList<relationship> parents = null;

	/**
	 * @param goId
	 * @param parent
	 */
	public parents(String goId) {
		this.goId = goId;
		parents = new LinkedList<relationship>();
	}

	/**
	 * @return the goId
	 */
	public String getGoId() {
		return goId;
	}

	/**
	 * @param goId
	 *            the goId to set
	 */
	public void setGoId(String goId) {
		this.goId = goId;
	}

	/**
	 * @return the parents
	 */
	public LinkedList<relationship> getParents() {
		return parents;
	}

	/**
	 * @param parents
	 *            the parents to set
	 */
	public void addParents(relationship p) {
		parents.add(p);
	}

}
