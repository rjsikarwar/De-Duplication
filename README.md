De-Duplication
==============

Dealing with the duplicate data store at server side

Now a days lots of duplicate data is being uploaded to any storage based cloud or server.
So to deal with duplication, there are some already existing techniques are there. One of them is De-Duplication.

Essentially what de-duplication is 

In computing, data deduplication is a specialized data compression technique for eliminating duplicate copies of repeating data. Related and somewhat synonymous terms are intelligent (data) compression and single-instance (data) storage. This technique is used to improve storage utilization and can also be applied to network data transfers to reduce the number of bytes that must be sent. In the deduplication process, unique chunks of data, or byte patterns, are identified and stored during a process of analysis. As the analysis continues, other chunks are compared to the stored copy and whenever a match occurs, the redundant chunk is replaced with a small reference that points to the stored chunk. Given that the same byte pattern may occur dozens, hundreds, or even thousands of times (the match frequency is dependent on the chunk size), the amount of data that must be stored or transferred can be greatly reduced.[1]


Ref : http://en.wikipedia.org/wiki/Data_deduplication


I Implemented deduplication in My way. 
Implementation Explaination and files.

Assumptions :
     (i) I have generated SHA-1 Key from the byteStream. So I assumed
         that, if two SHA-1 keys for two objects are same, that means both the
         objects are same.
     
     (ii) Here, I have written the code in java. So Instead of "char* blobString",
          I have used "InputeStream blobObject", in the functions.
          
          Hence I have implemented :
                       Integer put(InputStream blobObject);
                       InputStream get(Integer id);
                       void remove(Integer id);
         
         

Description of Classes :

       (i). BlobStore.java 
	     : get, put, remove all the functions are implemented in this class
      (ii). BlobWrapper.java
              : It is a wrapper around blob Object, Contain sha1 (sha key), objectPath (file path),
               referenceCount (Used in Removal) and blobRemoveListner
              
     (iii). BlobRemoveListner.java 
              :Interface to Remove the object from blobstore once the referencecount is zero.
      
      (iv). BlobRemoveConcreteListener.java
              : Implementation of BlobRemoveListner.java 
      (v). DeDuplicationDriverClass
              : Few basic test cases are written in this class
	
