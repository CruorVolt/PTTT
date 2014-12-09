/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 */ 

package polar.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ListDialog extends JDialog
                        implements ActionListener {
    private static ListDialog dialog;
    private static String[] values = new String[2];
    private JComboBox playerOneBox, playerTwoBox;
    private JLabel label, playerOneLabel, playerTwoLabel;
    private JCheckBox autoBox;
    private JPanel panel;

    public static String[] showDialog(Component frameComp,
                                    Component locationComp,
                                    String labelText,
                                    String title,
                                    String[][] possibleValues,
                                    String[] initialValues,
                                    String longValue) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new ListDialog(frame,
                                locationComp,
                                labelText,
                                title,
                                possibleValues,
                                initialValues,
                                longValue);
        dialog.setVisible(true);
        return values;
    }

    private void setValue(String[] newValues) {
        values[0] = newValues[0];
        values[1] = newValues[1];
        playerOneBox.setSelectedItem(values[0]);
        playerTwoBox.setSelectedItem(values[1]);
    }

    private ListDialog(Frame frame,
                       Component locationComp,
                       String labelText,
                       String title,
                       Object[][] data,
                       String[] initialValues,
                       String longValue) {
        super(frame, title, true);

        setResizable(false);
        final JButton playButton = new JButton("Play!");
        playButton.setActionCommand("Set");
        playButton.addActionListener(this);
        getRootPane().setDefaultButton(playButton);

        playerOneBox = new JComboBox(data[0]);
        playerTwoBox = new JComboBox(data[1]);
        autoBox = new JCheckBox("Autoplay", false);

        playerOneBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    playButton.doClick();
                }
            }
        });

        panel = new JPanel();
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0};
		panel.setLayout(gridBagLayout);

        label = new JLabel(labelText);
		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.insets = new Insets(0, 0, 5, 0);
		labelConstraints.fill = GridBagConstraints.BOTH;
		labelConstraints.gridx = 0;
		labelConstraints.gridy = 0;
		panel.add(label, labelConstraints);

        playerOneLabel = new JLabel("Player 1 (\"X\")");
        label.setLabelFor(playerOneBox);
		GridBagConstraints labelOneConstraints = new GridBagConstraints();
		labelOneConstraints.insets = new Insets(0, 0, 5, 0);
		labelOneConstraints.fill = GridBagConstraints.BOTH;
		labelOneConstraints.gridx = 0;
		labelOneConstraints.gridy = 1;
		panel.add(playerOneLabel, labelOneConstraints);

        playerTwoLabel = new JLabel("Player 2 (\"O\")");
        label.setLabelFor(playerTwoBox);
		GridBagConstraints labelTwoConstraints = new GridBagConstraints();
		labelTwoConstraints.insets = new Insets(0, 0, 5, 0);
		labelTwoConstraints.fill = GridBagConstraints.BOTH;
		labelTwoConstraints.gridx = 0;
		labelTwoConstraints.gridy = 2;
		panel.add(playerTwoLabel, labelTwoConstraints);

		GridBagConstraints boxOneConstraints = new GridBagConstraints();
		boxOneConstraints.insets = new Insets(0, 0, 5, 0);
		boxOneConstraints.fill = GridBagConstraints.BOTH;
		boxOneConstraints.gridx = 1;
		boxOneConstraints.gridy = 1;
		panel.add(playerOneBox, boxOneConstraints);

		GridBagConstraints boxTwoConstraints = new GridBagConstraints();
		boxTwoConstraints.insets = new Insets(0, 0, 5, 0);
		boxTwoConstraints.fill = GridBagConstraints.BOTH;
		boxTwoConstraints.gridx = 1;
		boxTwoConstraints.gridy = 2;
		panel.add(playerTwoBox, boxTwoConstraints);

		GridBagConstraints boxConstraints = new GridBagConstraints();
		boxConstraints.insets = new Insets(0, 0, 5, 0);
		boxConstraints.gridx = 1;
		boxConstraints.gridy = 3;
		panel.add(autoBox, boxConstraints);

		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.insets = new Insets(0, 0, 5, 0);
		buttonConstraints.gridx = 1;
		buttonConstraints.gridy = 4;
		panel.add(playButton, buttonConstraints);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(panel, BorderLayout.CENTER);

        //Initialize values.
        setValue(initialValues);
        pack();
        setLocationRelativeTo(locationComp);
    }

    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        if ("Set".equals(e.getActionCommand())) {
            ListDialog.values[0] = (String)(playerOneBox.getSelectedItem());
            ListDialog.values[1] = (String)(playerTwoBox.getSelectedItem());
        }
        ListDialog.dialog.setVisible(false);
    }
}
