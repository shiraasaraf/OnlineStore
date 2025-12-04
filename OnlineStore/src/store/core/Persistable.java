/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */
package store.core;

/**
 * Represents an object that can be saved to a file.
 * Classes implementing this interface must define how their data is written
 * to the specified file path.
 */
public interface Persistable {

    /**
     * Saves the object's data to a file located at the given path.
     * Implementing classes should define the file format and the writing process.
     *
     * @param path the file system path where the data should be saved
     */
    void saveToFile(String path);
}
