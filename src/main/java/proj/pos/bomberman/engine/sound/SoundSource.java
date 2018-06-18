package proj.pos.bomberman.engine.sound;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

/**
 * @author lwjgldev (angepasst von Nico Prosser)
 * @since 11.06.2018
 */
public class SoundSource {

  private final int sourceId;

  public SoundSource(boolean loop, boolean relative){
    this.sourceId = alGenSources();

    if(loop){
      alSourcei(sourceId,AL_LOOPING,AL_TRUE);
    }
    if(relative){
      alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
    }
  }

  public void setBuffer(int bufferId){
    stop();
    alSourcei(sourceId, AL_BUFFER, bufferId);
  }

  public void setSpeed(Vector3f speed){
    alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z);
  }

  public void setPosition(Vector3f position){
    alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);
  }

  public void setGain(float gain){
    alSourcef(sourceId, AL_GAIN, gain);
  }

  public void setProperty(int param, float value){
    alSourcef(sourceId, param, value);
  }

  public int getSourceId() {
    return sourceId;
  }

  public void play(){
    alSourcePlay(sourceId);
  }

  public boolean isPlaying(){
    return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
  }

  public void pause(){
    alSourcePause(sourceId);
  }

  public void stop(){
    alSourceStop(sourceId);
  }

  public void cleanup(){
    stop();
    alDeleteSources(sourceId);
  }

}
