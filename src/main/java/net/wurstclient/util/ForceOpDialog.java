/*
 * Copyright (C) 2014 - 2020 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;

public class ForceOpDialog extends JDialog
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		}catch(ReflectiveOperationException | UnsupportedLookAndFeelException e)
		{
			throw new RuntimeException(e);
		}
		
		new ForceOpDialog();
	}
	
	private final ArrayList<Component> components = new ArrayList<>();
	
	public ForceOpDialog()
	{
		super((JFrame)null, "ForceOP", false);
		setAlwaysOnTop(true);
		setSize(512, 248);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		addListSection();
		
		JSeparator sepListSpeed = new JSeparator();
		sepListSpeed.setLocation(4, 56);
		sepListSpeed.setSize(498, 4);
		add(sepListSpeed);
		
		addSpeedSection();
		
		JSeparator sepSpeedStart = new JSeparator();
		sepSpeedStart.setLocation(4, 132);
		sepSpeedStart.setSize(498, 4);
		add(sepSpeedStart);
		
		addStartSection();
		
		loadPWList();
		update();
		setVisible(true);
		toFront();
	}
	
	private void addListSection()
	{
		JLabel lPWList = new JLabel("Password list");
		lPWList.setLocation(4, 4);
		lPWList.setSize(lPWList.getPreferredSize());
		add(lPWList);
		
		JRadioButton rbDefaultList = new JRadioButton("default", true);
		rbDefaultList.setLocation(4, 24);
		rbDefaultList.setSize(rbDefaultList.getPreferredSize());
		add(rbDefaultList);
		
		JRadioButton rbTXTList = new JRadioButton("TXT file", false);
		rbTXTList.setLocation(
			rbDefaultList.getX() + rbDefaultList.getWidth() + 4, 24);
		rbTXTList.setSize(rbTXTList.getPreferredSize());
		add(rbTXTList);
		
		ButtonGroup bgList = new ButtonGroup();
		bgList.add(rbDefaultList);
		bgList.add(rbTXTList);
		
		JButton bTXTList = new JButton("browse");
		bTXTList.setLocation(rbTXTList.getX() + rbTXTList.getWidth() + 4, 24);
		bTXTList.setSize(bTXTList.getPreferredSize());
		bTXTList.setEnabled(rbTXTList.isSelected());
		add(bTXTList);
		
		JButton bHowTo = new JButton("How to use");
		bHowTo.setFont(new Font(bHowTo.getFont().getName(), Font.BOLD, 16));
		bHowTo.setSize(bHowTo.getPreferredSize());
		bHowTo.setLocation(506 - bHowTo.getWidth() - 32, 12);
		add(bHowTo);
	}
	
	private void addSpeedSection()
	{
		JLabel lSpeed = new JLabel("Speed");
		lSpeed.setLocation(4, 64);
		lSpeed.setSize(lSpeed.getPreferredSize());
		add(lSpeed);
		
		JLabel lDelay1 = new JLabel("Delay between attempts:");
		lDelay1.setLocation(4, 84);
		lDelay1.setSize(lDelay1.getPreferredSize());
		add(lDelay1);
		
		JSpinner spDelay = new JSpinner();
		spDelay.setToolTipText("<html>"
			+ "50ms: Fastest, doesn't bypass AntiSpam plugins<br>"
			+ "1000ms: Recommended, bypasses most AntiSpam plugins<br>"
			+ "10000ms: Slowest, bypasses all AntiSpam plugins" + "</html>");
		spDelay.setModel(new SpinnerNumberModel(1000, 50, 10000, 50));
		spDelay.setLocation(lDelay1.getX() + lDelay1.getWidth() + 4, 84);
		spDelay.setSize(60, (int)spDelay.getPreferredSize().getHeight());
		add(spDelay);
		
		JLabel lDelay2 = new JLabel("ms");
		lDelay2.setLocation(spDelay.getX() + spDelay.getWidth() + 4, 84);
		lDelay2.setSize(lDelay2.getPreferredSize());
		add(lDelay2);
		
		JCheckBox cbDontWait = new JCheckBox(
			"<html>Don't wait for \"<span style=\"color: red;\"><b>Wrong password!</b></span>\" messages</html>",
			false);
		cbDontWait
			.setToolTipText("Increases the speed but can cause inaccuracy.");
		cbDontWait.setLocation(4, 104);
		cbDontWait.setSize(cbDontWait.getPreferredSize());
		add(cbDontWait);
	}
	
	private void addStartSection()
	{
		JLabel lName = new JLabel("Username: error");
		lName.setLocation(4, 140);
		lName.setSize(lName.getPreferredSize());
		add(lName);
		
		JLabel lPasswords = new JLabel("Passwords: error");
		lPasswords.setLocation(4, 160);
		lPasswords.setSize(lPasswords.getPreferredSize());
		add(lPasswords);
		
		JLabel lTime = new JLabel("Estimated time: error");
		lTime.setLocation(4, 180);
		lTime.setSize(lTime.getPreferredSize());
		add(lTime);
		
		JLabel lAttempts = new JLabel("Attempts: error");
		lAttempts.setLocation(4, 200);
		lAttempts.setSize(lAttempts.getPreferredSize());
		add(lAttempts);
		
		JButton bStart = new JButton("Start");
		bStart.setFont(new Font(bStart.getFont().getName(), Font.BOLD, 18));
		bStart.setLocation(506 - 192 - 12, 144);
		bStart.setSize(192, 66);
		bStart.addActionListener(e -> startForceOP());
		add(bStart);
	}
	
	@Override
	public Component add(Component comp)
	{
		components.add(comp);
		return super.add(comp);
	}
	
	private void loadPWList()
	{
		// TODO
	}
	
	private void startForceOP()
	{
		components.forEach(c -> c.setEnabled(false));
		System.out.println("start");
	}
	
	private void update()
	{
		// TODO
	}
}
