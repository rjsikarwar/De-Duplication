package com.deduplication;
/**
*
*@author Raj
*/
public class BlobWrapper {
        public String sha1;
        public String objectPath;
        public int referenceCount = 0;
        public BlobRemoveListner blobRemoveListner;

        public BlobWrapper(String sha1, String objectPath, BlobRemoveListner blobRemoveListner) {
                this.sha1 = sha1;
                this.objectPath = objectPath;
                referenceCount = 1;
                this.blobRemoveListner = blobRemoveListner;
        }

        public String getSha1() {
                return sha1;
        }

        public String getObjectPath() {
                return objectPath;
        }

        public synchronized void incrementReference() {
                referenceCount++;
        }

        public synchronized void removerReference(Boolean removeFromSha1) {
                referenceCount--;
                if (referenceCount == 0) {
                        System.out.println("BlobPath :" + objectPath + " ,SHA1 :" + sha1);
                        blobRemoveListner.removeObject(this, removeFromSha1);
                }
        }

}
