package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable, Cloneable {
    /**
     * ID .
     */
    String id;
    /**
     * data written as bytes .
     */
    File content;
    /**
     * Name of dog.
     */
    String name;
    boolean isChanged;
    byte[] contents;
    boolean isStaged;
    boolean isDeleted;


    public Blob(File content, String name) {
        this.content = content; // content within the blob : "hello"
        this.name = name; // ex: hello.txt
        this.id = Utils.sha1(accessContent()); // assign blobs to sha1 ID after committing
        this.isChanged = false;
        contents = Utils.readContents(content);
        this.isDeleted = false;
        this.isStaged = false;

    }

    public Blob(String name) {
        this.name = name;
        this.isDeleted = false;
        this.isStaged = false;
        this.isChanged = false;
    }

    public byte[] accessContent() {
        return Utils.readContents(content); // returns all the content but in a byte array // use this in commit!!!
    }

    public String getBlobShaID() {
        return id;
    }

    public File getBlobsContent() {
        return content; // actual content of the blob
    }

    public String getNameofFile() {
        return name;
    }

    public byte[] getContentsAsBytes() {
        return contents;
    }

    @Override
    public Blob clone() {
        try {
            Blob clone = (Blob) super.clone();
            //  copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
