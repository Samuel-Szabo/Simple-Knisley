/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.protonmail.sarahszabo.knisleyformattool.core;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages all citations stored on the disk.
 *
 * @author Sarah Szabo <PhysicistSarah@Gmail.com>
 */
public class CitationDiskManager {

    /**
     * The path to the citations data file.
     */
    public static final Path citationsPath = Paths.get("Citations"),
            filePath = Paths.get(citationsPath.toString(), "Citations.dat");

    /**
     * Creates a new {@link CitationDiskManager}
     *
     * @throws java.io.IOException If we couldn't create the folder/file
     */
    public CitationDiskManager() throws IOException {
        Files.createDirectories(citationsPath);
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    /**
     * Stores a citation on the disk.
     *
     * @param citation The citation to store
     */
    public void storeCitation(String citation) throws IOException {
        try (FileWriter fw = new FileWriter(filePath.toFile(), true)) {
            fw.append(citation + "\n");
        }
    }

    /**
     * Gets the last 10 citations.
     *
     * @return The last 10 citations, this list is immutable
     */
    public List<String> getLast10Citations() {
        try (Scanner scanner = new Scanner(filePath.toFile())) {
            List<String> list = new ArrayList<>(10);
            while (scanner.hasNextLine()) {
                list.add(scanner.nextLine());
            }
            Collections.reverse(list);
            return Collections.unmodifiableList(list.subList(0, list.size() < 10 ? list.size() : 10));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CitationDiskManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
