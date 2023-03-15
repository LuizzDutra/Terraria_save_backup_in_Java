import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Main {

    static String terrariaPath = System.getProperty("user.home") + "\\Documents\\My Games\\Terraria";
    public static String[] append(String[] stringArray, String newString){
        String[] arrayBuffer = new String[stringArray.length+1];
        for (int i = 0; i < stringArray.length; i++){
            arrayBuffer[i] = stringArray[i];
        }
        arrayBuffer[arrayBuffer.length-1] = newString;
        return arrayBuffer;
    }


    public static String[] GetPlayers(){

        File folder = new File(terrariaPath + "\\Players");

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
    public static String[] GetWorlds(){

        File folder = new File(terrariaPath + "\\Worlds");

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

    public static void BackupData(String dataName) throws IOException
    {
        System.out.println("Backing up data: " + dataName);

        String subDataName = dataName.substring(0, dataName.length()-4);

        FileChannel saveFile = null;
        FileChannel backupFile = null;

        String dataPath = null;
        String backupDataPath = null;

        File playerFolderFile = null;

        if (dataName.endsWith(".wld")){
            dataPath = terrariaPath + "\\Worlds\\" + dataName;
            backupDataPath = terrariaPath + "\\WorldsBackup\\" + dataName;
            new File(backupDataPath).createNewFile();
        }
        if (dataName.endsWith(".plr")){
            dataPath = terrariaPath + "\\Players\\" + dataName;
            backupDataPath = terrariaPath + "\\PlayersBackup\\" + dataName;
            new File(backupDataPath).createNewFile();
            playerFolderFile = new File(terrariaPath + "\\Players\\" + subDataName);
        }

        if(dataPath == null){return;}

        try{
            saveFile = new FileInputStream(dataPath).getChannel();
            backupFile = new FileOutputStream(backupDataPath).getChannel();

            backupFile.transferFrom(saveFile, 0, saveFile.size());

            if (playerFolderFile != null){
                new File(terrariaPath + "\\PlayersBackup\\" + subDataName).mkdir();
                File[] folderFiles = playerFolderFile.listFiles();
                for (int i = 0; i < folderFiles.length; i++){
                    if(folderFiles[i].isFile()){
                        FileChannel inputFile = new FileInputStream(folderFiles[i].getAbsolutePath()).getChannel();
                        FileChannel outputFile = new FileOutputStream(terrariaPath + "\\PlayersBackup\\" + subDataName + "\\" +folderFiles[i].getName()).getChannel();

                        outputFile.transferFrom(inputFile, 0, inputFile.size());

                    }


                }


            }

            JOptionPane.showMessageDialog(null, dataName + " backed up successfully.");

        }
        finally {

            if(saveFile != null){saveFile.close();}
            if(backupFile != null){backupFile.close();}

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

        new File(terrariaPath + "\\WorldsBackup").mkdir();
        new File(terrariaPath + "\\PlayersBackup").mkdir();


        String[] worlds = GetWorlds();
        String[] players = GetPlayers();

        String[][] fetchedData = {worlds, players};

        while(true) {
            int backupType = Integer.parseInt(JOptionPane.showInputDialog("1. Backup Worlds\n2. Backup Players")) - 1;

            if (backupType < 0 || backupType >= fetchedData.length){return;}

            int backupInput = Integer.parseInt(JOptionPane.showInputDialog(GetPromptString(fetchedData[backupType]))) - 1;

            if (backupInput >= 0 && backupInput < fetchedData[backupType].length) {
                BackupData(fetchedData[backupType][backupInput]);
            }else{
                return;
            }
        }


    }
}
