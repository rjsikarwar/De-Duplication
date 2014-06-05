package com.deduplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
*
* @author Raj
*/
public class BlobStore implements Serializable {
        private static BlobStore instance;
        private AtomicInteger atomicInteger;
        // keyMap for Intid and BlobStoreMap
        private HashMap<Integer, BlobWrapper> blobMap;
        // SHA1 and BlobWrapperMap assuming SHA1 is always unique for sequence of Bytes/Text
        private Map<String, BlobWrapper> blobSha1Map;
        private BlobRemoveListner blobRemoveListner;
        private ExecutorService duplicateRemoverExecuter;

        private static final String filePath = "/home/raj/quboleTest/";

        private BlobStore() {
                atomicInteger = new AtomicInteger();
                blobMap = new HashMap<Integer, BlobWrapper>();
                blobSha1Map = new ConcurrentHashMap<String, BlobWrapper>();
                duplicateRemoverExecuter = Executors.newFixedThreadPool(25);
        }

        public static BlobStore getInstance() {
                if (instance == null) {
                        synchronized (BlobStore.class) {
                                if (instance == null) {
                                        instance = new BlobStore();
                                        instance.blobRemoveListner = new BlobRemoveConcreteListener(instance);
                                }
                        }
                }
                return instance;
        }

        public Integer put(InputStream inputStream) throws NoSuchAlgorithmException {
                // generateId
                Integer uniqueId = atomicInteger.getAndIncrement();
                // save input Stream to some path
                String path = filePath + System.currentTimeMillis() + "_" + uniqueId;
                try {
                        BlobWrapper blobWrapper = saveInputStreamAndGetHandler(inputStream, path, blobRemoveListner);

                        // BlobWrapper blobWrapper = new BlobWrapper(sha1, path, blobRemoveListner);
                        blobMap.put(uniqueId, blobWrapper);
                        DuplicateFileRemover duplicateFileRemover = new DuplicateFileRemover(blobWrapper, uniqueId);
                        duplicateRemoverExecuter.execute(duplicateFileRemover);
                        // duplicateRemoverExecuter.

                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return uniqueId;

        }

        public InputStream get(Integer uniqueId) throws FileNotFoundException {
                if (!blobMap.containsKey(uniqueId)) {
                        return null;
                }
                BlobWrapper blobWrapper = blobMap.get(uniqueId);
                // read the path and generate OutputStream and Pass the Stream to the user
                InputStream inputStream = new FileInputStream(blobWrapper.getObjectPath());
                return inputStream;
        }

        public void remove(Integer uniqueId) {
                if (!blobMap.containsKey(uniqueId)) {
                        throw new AssertionError("key didn't exist");
                }
                BlobWrapper blobWrapper = blobMap.remove(uniqueId);
                blobWrapper.removerReference(true);
        }

        private BlobWrapper saveInputStreamAndGetHandler(InputStream inputStream, String filePath,
                        BlobRemoveListner blobRemoveListner) throws IOException, NoSuchAlgorithmException {
                OutputStream outputStream = new FileOutputStream(new File(filePath));
                int read = 0;
                byte[] bytes = new byte[1024];
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                while ((read = inputStream.read(bytes)) != -1) {
                        md.update(bytes);
                        outputStream.write(bytes, 0, read);
                }
                outputStream.close();
                inputStream.close();
                String sha1Key = byteArray2Hex(md.digest());
                BlobWrapper blobWrapper = new BlobWrapper(sha1Key, filePath, blobRemoveListner);
                System.out.println("BlobPath :" + filePath + " ,SHA1 :" + sha1Key);
                return blobWrapper;
        }

        private static String byteArray2Hex(final byte[] hash) {
                Formatter formatter = new Formatter();
                for (byte b : hash) {
                        formatter.format("%02x", b);
                }
                return formatter.toString();
        }

        protected synchronized void removeDuplicate(BlobWrapper blobWrapper, Integer uniqueId) {
                if (blobSha1Map.containsKey(blobWrapper.getSha1())) {
                        BlobWrapper oldBlobWrapper = blobSha1Map.get(blobWrapper.getSha1());
                        oldBlobWrapper.incrementReference();
                        blobMap.put(uniqueId, oldBlobWrapper);
                        blobWrapper.removerReference(false);
                } else {
                        blobSha1Map.put(blobWrapper.getSha1(), blobWrapper);
                }
        }

        protected synchronized void removeFromSha1(BlobWrapper blobWrapper) {

                blobSha1Map.remove(blobWrapper.getSha1());
        }

        public static class DuplicateFileRemover implements Runnable {
                private BlobWrapper blobWrapper;
                private Integer uniqueId;

                public DuplicateFileRemover(BlobWrapper blobWrapper, Integer uniqueId) {
                        this.blobWrapper = blobWrapper;
                        this.uniqueId = uniqueId;
                }

                @Override
                public void run() {
                        instance.removeDuplicate(blobWrapper, uniqueId);
                }
        }

}
