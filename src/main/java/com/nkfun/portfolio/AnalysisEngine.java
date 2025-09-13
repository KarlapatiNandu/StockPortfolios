package com.nkfun.portfolio;

import javax.swing.*;
import java.io.File;


public class AnalysisEngine {
    
    // running the python analysis script for a given stock ticker
    public void runAnalysis(String tickerSymbol){
        System.out.println("Requesting analysis for " + tickerSymbol);
        try {
            // command to execute python analyzer.py TickerSymbol
            ProcessBuilder pb = new ProcessBuilder("python", "analyzer.py", tickerSymbol);

            // setting the working directory to the project's root folder
            pb.directory(new File(System.getProperty("user.dir")));

            // redirecting io errors to java console
            pb.inheritIO();

            // starting the process
            Process process = pb.start();
            int exitCode = process.waitFor(); // stops the java code until the python is done running

            if(exitCode == 0){
                System.out.println("Python script executed successfully.");
                displayChart(tickerSymbol);
            }
            else{
                System.err.println("Python script execution failed with exit code: " + exitCode);
            }

        } catch (Exception e) {
            System.err.println("An error occurred while running the analysis engine: " + e.getMessage());
        }
    }

    private void displayChart(String tickerSymbol){
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Stock Chart: " + tickerSymbol);
            ImageIcon icon = new ImageIcon(tickerSymbol + "_chart.png");
            JLabel label = new JLabel(icon);
            frame.add(label);
            frame.pack(); // sizing image to fit the window
            frame.setLocationRelativeTo(null); // centering the window
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });
    }

}
