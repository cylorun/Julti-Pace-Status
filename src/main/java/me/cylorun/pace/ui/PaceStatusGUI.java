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
    private JCheckBox showEnterCount;
    private JCheckBox showEnterAvg;
    private JTextField usernameField;
    private JSpinner timePeriodSpinner;
    private JPanel mainPanel;
    private JButton saveButton;
    private boolean closed = false;

    public PaceStatusGUI() {

        setUpWindow();
        this.setTitle("Pace Status");
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
            this.showEnterCount.setEnabled(this.checkBoxEnabled());
            this.showEnterAvg.setEnabled(this.checkBoxEnabled());
            this.timePeriodSpinner.setEnabled(this.checkBoxEnabled());
        });

        this.usernameField.setText(options.username);
        this.showEnterAvg.setSelected(options.show_enter_avg);
        this.showEnterCount.setSelected(options.show_enter_count);
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
        this.showEnterCount.setEnabled(options.enabled);
        this.showEnterAvg.setEnabled(options.enabled);
        this.timePeriodSpinner.setEnabled(options.enabled);

        this.showEnterAvg.addActionListener(e -> this.saveButton.setEnabled(PaceStatusGUI.this.hasChanges()));
        this.showEnterCount.addActionListener(e -> this.saveButton.setEnabled(PaceStatusGUI.this.hasChanges()));
        this.timePeriodSpinner.addChangeListener(e -> this.saveButton.setEnabled(PaceStatusGUI.this.hasChanges()));

        this.saveButton.addActionListener(e -> this.save());
        this.saveButton.setEnabled(this.hasChanges());
        this.revalidate();
        this.setMinimumSize(new Dimension(300, 200));
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
        return (this.checkBoxEnabled() != options.enabled) ||
                (!Objects.equals(this.getKeyBoxText(), options.username)) ||
                (this.showEnterCount.isSelected() != options.show_enter_count) ||
                (this.showEnterAvg.isSelected() != options.show_enter_avg) ||
                ((int) this.timePeriodSpinner.getValue() != options.time_period);
    }

    private void save() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        options.enabled = this.checkBoxEnabled();
        options.username = this.getKeyBoxText();
        options.show_enter_count = this.showEnterCount.isSelected();
        options.show_enter_avg = this.showEnterAvg.isSelected();
        options.time_period = (int) this.timePeriodSpinner.getValue();
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
        this.mainPanel = new JPanel(new GridBagLayout());
        this.enabledCheckBox = new JCheckBox();
        this.usernameField = new JTextField(15);
        this.saveButton = new JButton("Save");
        this.showEnterCount = new JCheckBox();
        this.showEnterAvg = new JCheckBox();
        this.timePeriodSpinner = new JSpinner(new SpinnerNumberModel(PaceStatusOptions.getInstance().time_period, 0, Integer.MAX_VALUE, 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        this.mainPanel.add(new JLabel("Enabled"), gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        this.mainPanel.add(this.enabledCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        this.mainPanel.add(new JSeparator(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        this.mainPanel.add(new JLabel("MC Username"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.mainPanel.add(this.usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        this.mainPanel.add(new JLabel("Show Enter Count"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.mainPanel.add(this.showEnterCount, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        this.mainPanel.add(new JLabel("Show Enter Avg"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.mainPanel.add(this.showEnterAvg, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        this.mainPanel.add(new JLabel("Time Period (hours)"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.mainPanel.add(this.timePeriodSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        this.mainPanel.add(new JSeparator(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.NONE;
        this.mainPanel.add(this.saveButton, gbc);
    }
}
