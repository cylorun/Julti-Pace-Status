package me.cylorun.pace;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;

public class PaceStatusGUI extends JFrame {
    private JTextField usernameField;
    private JCheckBox checkBox;

    public PaceStatusGUI() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        checkBox = new JCheckBox();
        usernameField = new JTextField();
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(300, 200);
        this.setLayout(new FlowLayout());
        this.add(checkBox);
        this.add(usernameField);
        usernameField.setText(options.username);
        checkBox.setSelected(options.enabled);

        checkBox.addActionListener(e -> saveOptions());

        usernameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                saveOptions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                saveOptions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

    }

    private void saveOptions() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        options.enabled = checkBox.isSelected();
        options.username = usernameField.getText();
        try {
            PaceStatusOptions.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

