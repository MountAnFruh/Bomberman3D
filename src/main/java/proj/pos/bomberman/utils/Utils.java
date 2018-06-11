package proj.pos.bomberman.utils;

import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

  /**
   * Holt sich die Resource auf dem Pfad {@code filePath} und gibt ihren Inhalt zurück
   *
   * @param filePath Der Dateipfad
   * @return Den Inhalt der Resource
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static String loadResource(String filePath) throws IOException, FileNotFoundException {
    String result = null;
    try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(filePath)) {
      if (in == null) throw new FileNotFoundException();
      Scanner scanner = new Scanner(in, "UTF-8");
      result = scanner.useDelimiter("\\A").next();
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return result;
  }

  /**
   * Holt sich die Resource auf dem Pfad {@code filePath} und gibt ihre Zeilen zurück
   *
   * @param filePath Der Dateipfad
   * @return Die Zeilen der Resource
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static List<String> readAllLines(String filePath) throws IOException, FileNotFoundException {
    List<String> list = new ArrayList<>();
    try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(filePath)) {
      if (in == null) throw new FileNotFoundException();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
        String line;
        while ((line = br.readLine()) != null) {
          list.add(line);
        }
      }
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return list;
  }

  /**
   * Wandelt eine Float List in ein float Array um
   *
   * @param list Die Liste, welche zum umwandeln ist
   * @return float Array mit selben Inhalt
   */
  public static float[] floatListToArray(List<Float> list) {
    int size = list != null ? list.size() : 0;
    float[] floatArr = new float[size];
    for (int i = 0; i < size; i++) {
      floatArr[i] = list.get(i);
    }
    return floatArr;
  }

   /**
   * Wandelt die eingegebene ressource in einen ByteBuffer um, wird zum
    * konvertieren von Sounddatein benötigt  (mp3 -> pcm   z.B.)
   *
   * @param resource Die Resource die es umzuwandeln gilt
    *@param bufferSize die gewünschte Buffersize
   * @return Bytebuffer der resource
   */
  public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
    ByteBuffer buffer;

    Path path = Paths.get(resource);
    if (Files.isReadable(path)) {
      try (SeekableByteChannel fc = Files.newByteChannel(path)) {
        buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
        while (fc.read(buffer) != -1) ;
      }
    } else {
      try (
              InputStream source = Utils.class.getResourceAsStream(resource);
              ReadableByteChannel rbc = Channels.newChannel(source)) {
        buffer = BufferUtils.createByteBuffer(bufferSize);

        while (true) {
          int bytes = rbc.read(buffer);
          if (bytes == -1) {
            break;
          }
          if (buffer.remaining() == 0) {
            buffer = resizeBuffer(buffer, buffer.capacity() * 2);
          }
        }
      }
    }

    buffer.flip();
    return buffer;
  }

  /**
   * Ändert die größe des ByteBuffers
   * @param buffer Buffer welcher geändert werden soll
   * @param newCapacity die neue Größe
   * @return Den Buffer mit der neuen Größe
   */
  private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
    ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
    buffer.flip();
    newBuffer.put(buffer);
    return newBuffer;
  }
}
