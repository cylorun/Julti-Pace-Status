package me.cylorun.pace.ui;

import me.cylorun.pace.PaceStatusOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

public class PaceStatusGUI extends JFrame {
    private static PaceStatusGUI instance = null;
    private JCheckBox enabledCheckBox;
    private JTextField usernameField;
    private JPanel mainPanel;
    private JButton saveButton;
    private boolean closed = false;

    public PaceStatusGUI() {

        setUpWindow();
        this.setTitle("PaceMan Tracker");
        this.setContentPane(this.mainPanel);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                PaceStatusGUI.this.onClose();
            }
        });

        PaceStatusOptions options = PaceStatusOptions.getInstance();
        this.enabledCheckBox.setSelected(options.enabled);
        this.enabledCheckBox.addActionListener(e -> {
            this.saveButton.setEnabled(this.hasChanges());
            this.usernameField.setEnabled(this.checkBoxEnabled());

        });
        this.usernameField.setText(options.username);
        this.usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    PaceStatusGUI.this.save();
                }
                PaceStatusGUI.this.saveButton.setEnabled(PaceStatusGUI.this.hasChanges());
            }

        });
        this.usernameField.setEnabled(options.enabled);
        this.saveButton.addActionListener(e -> this.save());
        this.saveButton.setEnabled(this.hasChanges());
        this.revalidate();
        this.setMinimumSize(new Dimension(300, 140));
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }

    public static PaceStatusGUI open(Point initialLocation) {
        if (instance == null || instance.isClosed()) {
            instance = new PaceStatusGUI();
            if (initialLocation != null) {
                instance.setLocation(initialLocation);
            }
        } else {
            instance.requestFocus();
        }
        return instance;
    }

    private boolean hasChanges() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        return (this.checkBoxEnabled() != options.enabled) || (!Objects.equals(this.getKeyBoxText(), options.username));
    }

    private void save() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        options.enabled = this.checkBoxEnabled();
        options.username = this.getKeyBoxText();
        try {
            PaceStatusOptions.save();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        this.saveButton.setEnabled(this.hasChanges());
    }

    private boolean checkBoxEnabled() {
        return this.enabledCheckBox.isSelected();
    }

    private String getKeyBoxText() {
        return this.usernameField.getText();
    }

    public boolean isClosed() {
        return this.closed;
    }

    private void onClose() {
        this.closed = true;
    }

    private void setUpWindow() {
        mainPanel = new JPanel();
        enabledCheckBox = new JCheckBox();
        usernameField = new JTextField();
        saveButton = new JButton("save");

        mainPanel.add(new JLabel("Enabled"));
        mainPanel.add(enabledCheckBox);
        mainPanel.add(new JLabel("MC username"));
        mainPanel.add(usernameField);
        mainPanel.add(saveButton);
    }
}
