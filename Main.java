import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Main {

    static String terrariaPath = System.getProperty("user.home") + "\\Documents\\My Games\\Terraria";
    static String playersPath = terrariaPath + "\\Players\\";
    static String worldsPath = terrariaPath + "\\Worlds\\";
    static String worldsBackupPath = terrariaPath + "\\WorldsBackup\\";
    static String playersBackupPath = terrariaPath + "\\PlayersBackup\\";


    public static String[] append(String[] stringArray, String newString){
        String[] arrayBuffer = new String[stringArray.length+1];
        for (int i = 0; i < stringArray.length; i++){
            arrayBuffer[i] = stringArray[i];
        }
        arrayBuffer[arrayBuffer.length-1] = newString;
        return arrayBuffer;
    }


    public static String[] GetPlayers(String searchPath){

        File folder = new File(searchPath);

        File[] tempArray = folder.listFiles();

        String[] playerStrings = {};

        for (int i = 0; i < tempArray.length; i++){
            String curFilename = tempArray[i].getName();
            if (curFilename.endsWith(".plr")){
                playerStrings = append(playerStrings, curFilename);
            }
        }

        return playerStrings;
    }
    public static String[] GetWorlds(String searchPath){

        File folder = new File(searchPath);

        File[] tempArray = folder.listFiles();

        String[] worldStrings = {};

        for (int i = 0; i < tempArray.length; i++){
            String curFilename = tempArray[i].getName();
            if (curFilename.endsWith(".wld")){
                worldStrings = append(worldStrings, curFilename);
            }
        }

        return worldStrings;
    }

    public static void LoadData(String dataName, boolean isBackup) throws IOException
    {

        boolean isPlayerData;

        String subDataName = dataName.substring(0, dataName.length()-4);

        if (dataName.endsWith(".plr")){isPlayerData = true;}
        else if (dataName.endsWith(".wld")){isPlayerData = false;}
        else{return;}

        FileChannel saveFile = null;
        FileChannel targetFile = null;

        FileChannel inputFile = null;
        FileChannel outputFile = null;


        String dataPath;
        String targetPath;

        //Calculates the right directories
        if (isBackup){
            if(isPlayerData){
                dataPath = playersPath + dataName;
                targetPath = playersBackupPath + dataName;

            }else {
                dataPath = worldsPath + dataName;
                targetPath = worldsBackupPath + dataName;
            }
        }else{
            if(isPlayerData){
                dataPath = playersBackupPath + dataName;
                targetPath = playersPath + dataName;

            }else {
                dataPath = worldsBackupPath + dataName;
                targetPath = worldsPath + dataName;
            }
        }

        if (dataPath == null){return;}

        try{
            saveFile = new FileInputStream(dataPath).getChannel();
            targetFile = new FileOutputStream(targetPath).getChannel();

            targetFile.transferFrom(saveFile, 0, saveFile.size());

            if (isPlayerData) {
                if (isBackup) {
                    new File(playersBackupPath + subDataName).mkdir();
                    File playerFolderFile = new File(playersPath + subDataName + "\\");
                    File[] folderFiles = playerFolderFile.listFiles();
                    if (folderFiles == null){return;}
                    for (int i = 0; i < folderFiles.length; i++) {
                        if (folderFiles[i].isFile()) {
                            inputFile = new FileInputStream(folderFiles[i].getAbsolutePath()).getChannel();
                            outputFile = new FileOutputStream(playersBackupPath + subDataName + "\\" + folderFiles[i].getName()).getChannel();

                            outputFile.transferFrom(inputFile, 0, inputFile.size());

                        }
                    }

                } else {
                    new File(playersPath + subDataName).mkdir();
                    File playerFolderFile = new File(playersBackupPath + subDataName + "\\");
                    File[] folderFiles = playerFolderFile.listFiles();
                    if (folderFiles == null){return;}
                    for (int i = 0; i < folderFiles.length; i++) {
                        if (folderFiles[i].isFile()) {
                            inputFile = new FileInputStream(folderFiles[i].getAbsolutePath()).getChannel();
                            outputFile = new FileOutputStream(playersPath + subDataName + "\\" + folderFiles[i].getName()).getChannel();

                            outputFile.transferFrom(inputFile, 0, inputFile.size());
                        }


                    }
                }
            }


            JOptionPane.showMessageDialog(null, "Operation was executed successfully");

        }finally {
            assert saveFile != null;
            saveFile.close();
            assert targetFile != null;
            targetFile.close();
            if (outputFile != null){outputFile.close();}
            if (inputFile != null){inputFile.close();}
        }


    }

    public static String GetPromptString(String[] stringArray){
        String returnString = "";

        for (int i = 0; i < stringArray.length; i++){
            returnString += (i + 1) +". "+ stringArray[i] + "\n";
        }
        return returnString;
    }

    public static void main(String[] args) throws IOException
    {

        //certifies that the backup folders exists.

        new File(worldsBackupPath).mkdir();
        new File(playersBackupPath).mkdir();



        while(true) {

            int operationType = Integer.parseInt(JOptionPane.showInputDialog("1. Backup\n2. Restore")) - 1;

            int backupType = -1;
            boolean isBackup;
            String[][] fetchedData;

            if (operationType == 0) {
                backupType = Integer.parseInt(JOptionPane.showInputDialog("1. Backup Worlds\n2. Backup Players")) - 1;
                fetchedData = new String[][]{GetWorlds(worldsPath), GetPlayers(playersPath)};
                isBackup = true;
            }else if (operationType == 1){
                backupType = Integer.parseInt(JOptionPane.showInputDialog("1. Restore Worlds\n2. Restore Players")) - 1;
                fetchedData = new String[][]{GetWorlds(worldsBackupPath), GetPlayers(playersBackupPath)};
                isBackup = false;
            }else{return;}

            if (backupType < 0 || backupType >= fetchedData.length){return;}

            int backupInput = Integer.parseInt(JOptionPane.showInputDialog(GetPromptString(fetchedData[backupType]))) - 1;

            if (backupInput >= 0 && backupInput < fetchedData[backupType].length) {
                LoadData(fetchedData[backupType][backupInput], isBackup);
            }else{
                return;
            }
        }


    }
}
