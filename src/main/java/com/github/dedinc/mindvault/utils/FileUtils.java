package com.github.dedinc.mindvault.utils;

import com.github.dedinc.mindvault.core.Manager;
import com.github.dedinc.mindvault.core.Session;
import com.github.dedinc.mindvault.core.Time;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileUtils {
    public static File showLoadSessionDialog(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Session");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json"));
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static Session loadSession(File file) {
        return Manager.loadSession(file.getAbsolutePath());
    }

    public static File showSaveSessionDialog(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Session");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json"));
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getAbsolutePath();
            if (!fileName.endsWith(".json")) {
                fileName += ".json";
            }
            return new File(fileName);
        }
        return null;
    }

    public static void saveSession(Session session, File file) {
        Manager.saveSession(session, file.getAbsolutePath());
    }

    public static Session createNewSession() {
        long startTypeTime = Time.getUnix();
        String typeSpeedText = JOptionPane.showInputDialog(null, "Type 'hello my friends' to detect your type speed");
        return new Session(startTypeTime, typeSpeedText);
    }

    public static File showSelectFileDialog(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static void addCardsFromFile(File file, Session session) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] parts = line.split("-");
                if (parts.length == 2) {
                    String question = parts[0].trim();
                    String answer = parts[1].trim();
                    session.addCard(question, answer);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage());
        }
    }
}