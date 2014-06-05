package com.deduplication;
/**
*
* @author Raj
*/
public interface BlobRemoveListner {
        void removeObject(BlobWrapper blob, boolean removeFromSha1);
}
