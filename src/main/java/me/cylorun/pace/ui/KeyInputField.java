package me.cylorun.pace.ui;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KeyInputField extends JButton implements KeyListener {
    private boolean recording;
    private int key;
    private final String keyText;
    private final Consumer<Integer> consumer;

    public KeyInputField(String keyText, int value, Consumer<Integer> consumer) {
        super(keyText + ": " + KeyEvent.getKeyText(value));
        this.key = value;
        this.keyText = keyText;
        this.consumer = consumer;
        this.addKeyListener(this);

        this.addActionListener(e -> {
            if (!recording) {
                setText(keyText + ": ...");
                recording = true;
            } else {
                setText(KeyEvent.getKeyText(this.key));
                recording = false;
            }
        });
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (recording) {
            this.key = e.getKeyCode();
            setText(keyText + ": " + KeyEvent.getKeyText(this.key));
            recording = false;
            this.consumer.accept(this.key);
        }
    }

    public int getKey() {
        return key;
    }
}

