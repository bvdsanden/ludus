package org.ludus.backend.games.benchmarking;

import org.ludus.backend.games.GameGraph;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Bram van der Sanden
 */
public class Serializer {

    public static String serialize(GameGraph graph, String fileName)
            throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").format(new Date());
        String outputName = fileName + "_" + timeStamp;
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputName));
        out.writeObject(graph);
        out.close();
        return outputName;
    }

    public static GameGraph load(String fileName)
            throws IOException, ClassNotFoundException {
        System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        final GameGraph restoredGraph = (GameGraph) in.readObject();
        in.close();
        return restoredGraph;
    }

}
