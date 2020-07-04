package it.polimi.ingsw.server.set.up;


import java.io.*;
import java.util.Optional;

import static it.polimi.ingsw.server.costants.LogConstants.*;
import static it.polimi.ingsw.server.costants.NameConstants.CONFIG_FILE;
public class TakeDataFile {
    public TakeDataFile() {
    }

    public String getParameter(String parameter)   {
        String result = null;
        String configFile  = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        configFile = configFile.substring(0, configFile.lastIndexOf("/"));

        try (InputStream inputStream = new FileInputStream(configFile+CONFIG_FILE);InputStreamReader streamReader = new InputStreamReader(inputStream); BufferedReader input = new BufferedReader(streamReader)){
            Optional<String> optional =  input.lines()
                    .filter(line-> line.contains(parameter))
                    .map(line -> line.substring(parameter.length()+1))
                    .findFirst();
            if(optional.isPresent())
                result = optional.get();

        } catch (IOException e) {
            System.out.println(CONFIGURATION_FILE_ERROR);
        }
        return result;
    }

}

