package proj.pos.bomberman.engine.graphics;

import org.joml.Vector3f;

/**
 * @author lwjgldev (angepasst von Nico Prosser)
 * @since 25.04.2018
 */
public class SceneLight {

  private Vector3f ambientLight;

  private PointLight[] pointLightList;

  private DirectionalLight directionalLight;

  public Vector3f getAmbientLight() {
    return ambientLight;
  }

  public void setAmbientLight(Vector3f ambientLight) {
    this.ambientLight = ambientLight;
  }

  public PointLight[] getPointLightList() {
    return pointLightList;
  }

  public void setPointLightList(PointLight[] pointLightList) {
    this.pointLightList = pointLightList;
  }

  public DirectionalLight getDirectionalLight() {
    return directionalLight;
  }

  public void setDirectionalLight(DirectionalLight directionalLight) {
    this.directionalLight = directionalLight;
  }
}
