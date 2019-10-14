package com.deduplication;
/**
 *
 *@author Raj
 */
import java.io.File;

public class BlobRemoveConcreteListener implements BlobRemoveListner {

        BlobStore blobStr;

        public BlobRemoveConcreteListener(BlobStore blobStore) {
                this.blobStr = blobStore;
        }

        @Override
        public void removeObject(BlobWrapper blob, boolean removeFromSha1) {
                // remove File from Storage
                File file = new File(blob.getObjectPath());
                file.delete();
                System.out.println("Deleting File :" + blob.objectPath);
                // remove from sha1map
                if (removeFromSha1) {
                        blobStr.removeFromSha1(blob);
                }
        }
}
