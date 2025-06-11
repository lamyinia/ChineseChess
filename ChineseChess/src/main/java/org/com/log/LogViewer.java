package org.com.log;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LogViewer extends JFrame {
    private JTextArea textArea = new JTextArea();

    public LogViewer() {
        setTitle("应用程序日志");
        setSize(800, 600);
        add(new JScrollPane(textArea));
        setVisible(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                loadLogFile();
            }
        }).start();
    }

    private void loadLogFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("logs/app.log"));
            textArea.setText(String.join("\n", lines));
        } catch (IOException ex) {
            textArea.setText("无法加载日志文件: " + ex.getMessage());
        }
    }
}
