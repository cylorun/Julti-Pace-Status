package me.cylorun.pace.ui;

import me.cylorun.pace.PaceStatusOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaceStatusGUI extends JFrame {
    private static PaceStatusGUI instance = null;
    private JCheckBox enabledCheckBox;
    private JCheckBox showEnterCount;
    private JCheckBox showEnterAvg;
    private JCheckBox autoDeafen;
    private JTextField usernameField;
    private JSpinner timePeriodSpinner;
    private JPanel mainPanel;
    private JButton saveButton;
    private JComboBox<String> splitBox;
    private JSpinner minutesSpinner;
    private KeyInputField hotkeyField;
    private JPanel deafenPanel;
    private Map<String, String> splits = new HashMap<>();

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
            this.usernameField.setEnabled(this.isStatusEnabled());
            this.showEnterCount.setEnabled(this.isStatusEnabled());
            this.showEnterAvg.setEnabled(this.isStatusEnabled());
            this.timePeriodSpinner.setEnabled(this.isStatusEnabled());
            this.autoDeafen.setEnabled(this.isStatusEnabled());
        });

        this.usernameField.setText(options.username);
        this.showEnterAvg.setSelected(options.show_enter_avg);
        this.showEnterCount.setSelected(options.show_enter_count);
        this.autoDeafen.setSelected(options.auto_deafen);
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

        this.showEnterAvg.addActionListener(e -> this.saveButton.setEnabled(this.hasChanges()));
        this.showEnterCount.addActionListener(e -> this.saveButton.setEnabled(this.hasChanges()));
        this.timePeriodSpinner.addChangeListener(e -> this.saveButton.setEnabled(this.hasChanges()));
        this.autoDeafen.addChangeListener(e -> this.saveButton.setEnabled(this.hasChanges()));
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

    public static PaceStatusGUI getInstance() {
        if (instance == null) {
            return open(null);
        }
        return instance;
    }

    private boolean hasChanges() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        return (this.isStatusEnabled() != options.enabled) ||
                (!Objects.equals(this.getKeyBoxText(), options.username)) ||
                (this.showEnterCount.isSelected() != options.show_enter_count) ||
                (this.showEnterAvg.isSelected() != options.show_enter_avg) ||
                ((int) this.timePeriodSpinner.getValue() != options.time_period) ||
                this.hotkeyField.getKey() != options.auto_deafen_hotkey ||
                this.autoDeafen.isSelected() != options.auto_deafen ||
                (int) this.minutesSpinner.getValue() != options.auto_deafen_time ||
                this.changedSplit();
    }

    private boolean changedSplit() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        for (Map.Entry<String, String> e : this.splits.entrySet()) {
            if (e.getValue().equals(this.splitBox.getSelectedItem())) {
                return !e.getKey().equals(options.auto_deafen_split);
            }
        }

        return false;
    }

    private void save() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        options.enabled = this.isStatusEnabled();
        options.username = this.getKeyBoxText();
        options.show_enter_count = this.showEnterCount.isSelected();
        options.show_enter_avg = this.showEnterAvg.isSelected();
        options.time_period = (int) this.timePeriodSpinner.getValue();
        options.auto_deafen_hotkey = this.hotkeyField.getKey();
        options.auto_deafen = this.autoDeafen.isSelected();

        try {
            PaceStatusOptions.save();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        this.saveButton.setEnabled(this.hasChanges());
    }

    private boolean isStatusEnabled() {
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

    private void toggleDeafenConfig(boolean enabled) {
        deafenPanel.setVisible(enabled);
        this.pack();
    }

    private void setUpWindow() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();

        this.splits.put("rsg.enter_nether", "Nether");
        this.splits.put("rsg.enter_bastion", "Bastion");
        this.splits.put("rsg.enter_fortress", "Fortress");
        this.splits.put("rsg.first_portal", "First Portal");
        this.splits.put("rsg.second_portal", "Second Portal");
        this.splits.put("rsg.enter_stronghold", "Stronghold");
        this.splits.put("rsg.enter_end", "End");

        this.mainPanel = new JPanel(new GridBagLayout());
        this.enabledCheckBox = new JCheckBox();
        this.usernameField = new JTextField(15);
        this.saveButton = new JButton("Save");
        this.showEnterCount = new JCheckBox();
        this.showEnterAvg = new JCheckBox();
        this.autoDeafen = new JCheckBox();
        this.timePeriodSpinner = new JSpinner(new SpinnerNumberModel(PaceStatusOptions.getInstance().time_period, 0, Integer.MAX_VALUE, 1));
        this.minutesSpinner = new JSpinner(new SpinnerNumberModel(options.auto_deafen_time, 0, 60, 1));

        this.minutesSpinner.addChangeListener((e) -> {
            this.saveButton.setEnabled(this.hasChanges());
        });

        this.hotkeyField = new KeyInputField("Hotkey", options.auto_deafen_hotkey, (v) -> {
            this.saveButton.setEnabled(this.hasChanges());
        });

        this.splitBox = new JComboBox<>(this.splits.values().toArray(new String[0]));
        this.splitBox.setSelectedItem(splits.get(options.auto_deafen_split));
        this.splitBox.addActionListener((e -> {
            this.saveButton.setEnabled(this.hasChanges());
        }));
        this.deafenPanel = new JPanel(new GridBagLayout());
        this.deafenPanel.setVisible(options.auto_deafen);

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
        gbc.gridy++;
        gbc.gridwidth = 3;
        this.mainPanel.add(new JSeparator(), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        this.mainPanel.add(new JLabel("MC Username"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.mainPanel.add(this.usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        this.mainPanel.add(new JLabel("Show Enter Count"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.mainPanel.add(this.showEnterCount, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        this.mainPanel.add(new JLabel("Show Enter Avg"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.mainPanel.add(this.showEnterAvg, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        this.mainPanel.add(new JLabel("Time Period (hours)"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.mainPanel.add(this.timePeriodSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        this.mainPanel.add(new JSeparator(), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel info = new JLabel("Auto deafen discord");
        info.setToolTipText("Automatically deafens you on discord when you reach a certain split");
        this.mainPanel.add(info, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.mainPanel.add(this.autoDeafen, gbc);
        this.autoDeafen.addActionListener(e -> this.toggleDeafenConfig(this.autoDeafen.isSelected()));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        this.mainPanel.add(this.deafenPanel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.NONE;
        this.mainPanel.add(this.saveButton, gbc);

        setUpDeafenPanel();
    }

    private void setUpDeafenPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        this.deafenPanel.add(new JLabel("Split"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.deafenPanel.add(this.splitBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        this.deafenPanel.add(new JLabel("Minutes"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.deafenPanel.add(this.minutesSpinner, gbc);

        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        this.deafenPanel.add(this.hotkeyField, gbc);
    }
}
