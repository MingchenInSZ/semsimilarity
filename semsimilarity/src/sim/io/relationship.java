package sim.io;

import java.io.Serializable;

/**
 * @author: mingchen
 * @date :2015.4.9
 * 
 *       This class defines the relationship between go terms
 * 
 */

public class relationship implements Serializable {

	private String goId;
	private float intensity; // It reflects the intensity of the
	// relationship(is_a:0.8,part_of:0.6 as default)

	public relationship(String goId, float intensity) {
		this.goId = goId;
		this.intensity = intensity;
	}

	/**
	 * @return the goId
	 */
	public String getGoId() {
		return goId;
	}

	/**
	 * @return the intensity
	 */
	public float getIntensity() {
		return intensity;
	}

	/**
	 * @param goId
	 */
	public void setGoId(String goId) {
		this.goId = goId;
	}

	/**
	 * @param intensity
	 */
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}
