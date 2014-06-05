package com.deduplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
*
* @author Raj
*/

public class DeDuplicationDriverClass {

        /**
         * @param args
         */
        public static void main(String[] args) {
                File file = new File("/home/raj/qubole_input/");
                BlobStore blobStore = BlobStore.getInstance();
                List<Integer> intIds = new LinkedList<Integer>();
                int count = 0;
                while (count < 5) {
                        if (count == 5) {
                                break;
                        }
                        try {
                                traverse(blobStore, file, intIds);
                        } catch (Exception exce) {
                                exce.printStackTrace();
                                // break;
                        }
                        count++;
                }

                try {
                        Thread.currentThread().sleep(10000);
                } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                for (Integer idItr : intIds) {
                        blobStore.remove(idItr);
                }

        }

        private static void traverse(BlobStore blobStore, File file, List<Integer> intIds)
                        throws NoSuchAlgorithmException, FileNotFoundException {
                for (File file01 : file.listFiles()) {
                        if (file01.isFile()) {
                                intIds.add(blobStore.put(new FileInputStream(file01)));
                                System.out.println("Latest Id got " + intIds.get(intIds.size() - 1));
                        }
                }
        }

}
