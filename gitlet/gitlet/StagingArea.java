package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

public class StagingArea implements Serializable {
    TreeMap<String, Blob> files;

    public StagingArea(Commit lastCommit) {
        files = (TreeMap<String, Blob>) lastCommit.files.clone();
    }

//    public void add(String fileName) {
//
//        List<String> allFiles = Utils.plainFilenamesIn(Repository.CWD);
//        int left = 0;
//        int right = allFiles.size();
//        boolean isFound = false;
//        while (left <= right) {
//            int mid = left + (right - left) / 2;
//            int comparison = allFiles.get(mid).compareTo(fileName);
//            if (comparison < 0) {
//                left = mid + 1;
//            } else if (comparison > 0) {
//                right = mid - 1;
//            } else {
//                isFound = true;
//                break;
//            }
//
//        }
//        if (isFound) {
//            File f = Utils.join(Repository.CWD, fileName);
//            Blob b = new Blob(f, fileName);
//            this.files.put(fileName, b);
//            b.isStaged = true;
//        } else {
//            throw Utils.error("File does not exist.");
//        }
//
//    }




}
