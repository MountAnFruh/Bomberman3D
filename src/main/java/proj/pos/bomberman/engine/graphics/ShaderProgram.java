package proj.pos.bomberman.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author lwjgldev (angepasst von Andreas Fruhwirt)
 * @since 09.04.2018
 */
public class ShaderProgram {

  private final int programId;

  private int vertexShaderId;
  private int fragmentShaderId;

  private final Map<String, Integer> uniforms;

  public ShaderProgram() {
    programId = glCreateProgram();
    if (programId == 0) {
      throw new RuntimeException("Could not create Shader");
    }
    uniforms = new HashMap<>();
  }

  public void createUniform(String uniformName) {
    int uniformLocation = glGetUniformLocation(programId, uniformName);
    if (uniformLocation < 0) {
      throw new RuntimeException("Could not find uniform: " + uniformName);
    }
    uniforms.put(uniformName, uniformLocation);
  }

  public void createPointLightListUniform(String uniformName, int size) {
    for (int i = 0; i < size; i++) {
      createPointLightUniform(uniformName + "[" + i + "]");
    }
  }

  public void createPointLightUniform(String uniformName) {
    createUniform(uniformName + ".color");
    createUniform(uniformName + ".position");
    createUniform(uniformName + ".intensity");
    createUniform(uniformName + ".att.constant");
    createUniform(uniformName + ".att.linear");
    createUniform(uniformName + ".att.exponent");
  }

  public void createDirectionalLightUniform(String uniformName) {
    createUniform(uniformName + ".color");
    createUniform(uniformName + ".direction");
    createUniform(uniformName + ".intensity");
  }

  public void createMaterialUniform(String uniformName) {
    createUniform(uniformName + ".ambient");
    createUniform(uniformName + ".diffuse");
    createUniform(uniformName + ".specular");
    createUniform(uniformName + ".hasTexture");
    createUniform(uniformName + ".reflectance");
  }

  public void setUniform(String uniformName, Matrix4f value) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      // Dump the matrix into a float buffer
      FloatBuffer fb = stack.mallocFloat(16);
      value.get(fb);
      glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
    }
  }

  public void setUniform(String uniformName, int value) {
    glUniform1i(uniforms.get(uniformName), value);
  }

  public void setUniform(String uniformName, float value) {
    glUniform1f(uniforms.get(uniformName), value);
  }

  public void setUniform(String uniformName, Vector3f value) {
    glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
  }

  public void setUniform(String uniformName, Vector4f value) {
    glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
  }

  public void setUniform(String uniformName, PointLight[] pointLights) {
    int numLights = pointLights != null ? pointLights.length : 0;
    for (int i = 0; i < numLights; i++) {
      setUniform(uniformName, pointLights[i], i);
    }
  }

  public void setUniform(String uniformName, PointLight pointLight, int pos) {
    setUniform(uniformName + "[" + pos + "]", pointLight);
  }

  public void setUniform(String uniformName, PointLight pointLight) {
    setUniform(uniformName + ".color", pointLight.getColor());
    setUniform(uniformName + ".position", pointLight.getPosition());
    setUniform(uniformName + ".intensity", pointLight.getIntensity());
    PointLight.Attenuation att = pointLight.getAttenuation();
    setUniform(uniformName + ".att.constant", att.getConstant());
    setUniform(uniformName + ".att.linear", att.getLinear());
    setUniform(uniformName + ".att.exponent", att.getExponent());
  }

  public void setUniform(String uniformName, DirectionalLight dirLight) {
    setUniform(uniformName + ".color", dirLight.getColor());
    setUniform(uniformName + ".direction", dirLight.getDirection());
    setUniform(uniformName + ".intensity", dirLight.getIntensity());
  }

  public void setUniform(String uniformName, Material material) {
    setUniform(uniformName + ".ambient", material.getAmbientColor());
    setUniform(uniformName + ".diffuse", material.getDiffuseColor());
    setUniform(uniformName + ".specular", material.getSpecularColor());
    setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
    setUniform(uniformName + ".reflectance", material.getReflectance());
  }

  public void createVertexShader(String shaderCode) {
    vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
  }

  public void createFragmentShader(String shaderCode) {
    fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
  }

  protected int createShader(String shaderCode, int shaderType) {
    int shaderId = glCreateShader(shaderType);
    if (shaderId == 0) {
      throw new RuntimeException("Error creating shader. Type: " + shaderType);
    }

    glShaderSource(shaderId, shaderCode);
    glCompileShader(shaderId);

    if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
      throw new RuntimeException("Error compiling Shader code: "
              + glGetShaderInfoLog(shaderId, 1_024));
    }

    glAttachShader(programId, shaderId);

    return shaderId;
  }

  public void link() {
    glLinkProgram(programId);
    if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
      throw new RuntimeException("Error linking Shader code: "
              + glGetProgramInfoLog(programId, 1_024));
    }

    if (vertexShaderId != 0) {
      glDetachShader(programId, vertexShaderId);
    }
    if (fragmentShaderId != 0) {
      glDetachShader(programId, fragmentShaderId);
    }

    glValidateProgram(programId);
    if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
      System.err.println("Warning validating Shader code: "
              + glGetProgramInfoLog(programId, 1_024));
    }
  }

  public void bind() {
    glUseProgram(programId);
  }

  public void unbind() {
    glUseProgram(0);
  }

  public void cleanup() {
    unbind();
    if (programId != 0) {
      glDeleteProgram(programId);
    }
  }
}
