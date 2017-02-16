import java.io.File;
import java.util.LinkedHashMap;
import java.util.Set;

public class FileCounter {

    public static void main(String[] args) {
        String baseDirName = "C:\\Users\\niewinskip\\Desktop";
        LinkedHashMap<String, File> partitions = new LinkedHashMap<String, File>();
        partitions = getPartitions(baseDirName);
        printPartitionInfo(partitions);

    }

    /**
     * @param baseDirName
     * @return
     */
    public static LinkedHashMap<String, File> getPartitions(String baseDirName) {

        LinkedHashMap<String, File> partitionSubfolders = new LinkedHashMap<String, File>();
        File baseDir = new File(baseDirName);
        File[] basePartitions = baseDir.listFiles();
        for (File basePartition : basePartitions) {
            //System.out.println("Base Partition: " + basePartition.getName());
            if (basePartition.isDirectory()) {
                File[] subPartitions = basePartition.listFiles();
                for (File subPartition : subPartitions) {
                    //System.out.println("Sub Partition: " + subPartition.getName());
                    if (subPartition.isDirectory()) {
                        if (subPartition.getName().equals(basePartition.getName())) {
                            partitionSubfolders.put(subPartition.getName(), subPartition);
                        }
                    } else {
                        System.out.println("Adding sub Partitions: " + subPartition.getName() + " is not a directory");
                    }
                }
            } else {
                System.out.println("Adding base Partitions: " + basePartition.getName() + " is not a directory");
            }
        }

        return partitionSubfolders;

    }

    /**
     * @param partitions
     */
    public static void printPartitionInfo(LinkedHashMap<String, File> partitions) {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<String, Integer>();
        Set<String> partitonKeys = partitions.keySet();
        for (String partitionName : partitonKeys) {
            System.out.println("****" + partitionName + "****");
            File partition = partitions.get(partitionName);
            //System.out.println("Partition: " + partition.getName());
            printFileInfo(partitionName, partition);
        }
        Set<String> extentionKeys = counts.keySet();
        for (String ext : extentionKeys) {
            System.out.println(ext + "=" + counts.get(ext));
        }
    }

    public static void printFileInfo(String partitionName, File partition) {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<String, Integer>();
        File[] versionFolders = partition.listFiles();
        if (partitionName.equals(partition.getName())) {
            for (File versionFolder : versionFolders) {
                if (versionFolder.isDirectory()) {
                    File[] files = versionFolder.listFiles();
                    for (File file : files) {
                        if (file.isFile()) {
                            String fileName = file.getName();
                            String extention = "NO_EXTENTION";
                            //System.out.println(fileName);
                            if (fileName.contains(".")) {
                                extention = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
                            }
                            int count = counts.get(extention) != null ? counts.get(extention) : 0;
                            //System.out.println("extention = " + counts);
                            counts.put(extention, ++count);
                        }
                    }
                } else {
                    System.out.println("File " + versionFolder.getName() + " is not a directory");
                }
            }
        } else {
            System.out.println("Partition File/Folder " + partition.getName() + " is not a valid directory");
        }

        Set<String> extentionKeys = counts.keySet();
        for (String ext : extentionKeys) {
            System.out.println(ext + "=" + counts.get(ext));
        }
    }

}
