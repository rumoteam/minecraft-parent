package com.github.rumoteam.minecraft.plugin.altauth.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import com.github.rumoteam.minecraft.plugin.altauth.client.init.ClientInit;
import com.github.rumoteam.minecraft.plugin.altauth.client.object.Addr;
import com.github.rumoteam.minecraft.plugin.altauth.client.object.ServerInfo;
import com.github.rumoteam.minecraft.plugin.altauth.libs.request.AuthRequest;
import com.github.rumoteam.minecraft.plugin.altauth.libs.request.RegisterRequest;

public class Gui extends JFrame {
	public enum statusConnection {
		NOTCONNECTED, CONNECTION, CONNECTED
	}

	public enum statusProfile {
		NOTREGISTERED, NA, AUTH
	}

	private static final long serialVersionUID = 6467219959870868039L;
	private JTextField tfServer;
	private JButton btnNewButton;
	private JTextField tfUserName;
	public JButton btnAuth;

	public Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(418, 500);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);

		tfServer = new JTextField("example.org:50400");
		springLayout.putConstraint(SpringLayout.WEST, tfServer, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tfServer, 0, SpringLayout.EAST, getContentPane());
		tfServer.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, tfServer, 35, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tfServer, 80, SpringLayout.NORTH, getContentPane());
		getContentPane().add(tfServer);
		tfServer.setColumns(10);

		btnNewButton = new JButton(statusConnection.NOTCONNECTED.toString());
		springLayout.putConstraint(SpringLayout.WEST, btnNewButton, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnNewButton, 0, SpringLayout.EAST, getContentPane());
		btnNewButton.addActionListener(e -> connect());
		getContentPane().add(btnNewButton);

		tfUserName = new JTextField(ClientInit.config.getUserName());
		springLayout.putConstraint(SpringLayout.WEST, tfUserName, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tfUserName, 0, SpringLayout.EAST, getContentPane());
		tfUserName.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, tfUserName, 27, SpringLayout.SOUTH, tfServer);
		springLayout.putConstraint(SpringLayout.SOUTH, tfUserName, 72, SpringLayout.SOUTH, tfServer);
		getContentPane().add(tfUserName);
		tfUserName.setColumns(10);

		btnAuth = new JButton(statusProfile.NA.toString());
		btnAuth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				authAction();
			}

		});
		btnAuth.setEnabled(false);
		springLayout.putConstraint(SpringLayout.WEST, btnAuth, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnAuth, 0, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnNewButton, -29, SpringLayout.NORTH, btnAuth);
		springLayout.putConstraint(SpringLayout.NORTH, btnAuth, 315, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnAuth, -115, SpringLayout.SOUTH, getContentPane());
		getContentPane().add(btnAuth);

		JButton btnSaveConfig = new JButton("Save Config");
		btnSaveConfig.addActionListener(e -> {
			try {
				saveConfig();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, btnSaveConfig, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnSaveConfig, -255, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnSaveConfig, 0, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 21, SpringLayout.SOUTH, btnSaveConfig);
		springLayout.putConstraint(SpringLayout.NORTH, btnSaveConfig, 6, SpringLayout.SOUTH, tfUserName);
		getContentPane().add(btnSaveConfig);

	}

	private void connect() {
		try {
			Addr addr = getAddr();
			ClientInit.clientBootstrap.remoteAddress(new InetSocketAddress(addr.getHost(), addr.getPort()));

			switch (statusConnection.valueOf(btnNewButton.getText())) {
			case NOTCONNECTED:
				btnNewButton.setText(statusConnection.CONNECTION.toString());
				ClientInit.channelFuture = ClientInit.clientBootstrap.connect().sync();
				btnNewButton.setText(statusConnection.CONNECTED.toString());
				break;
			case CONNECTED:
				ClientInit.channelFuture.channel().disconnect();
				btnNewButton.setText(statusConnection.NOTCONNECTED.toString());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			btnNewButton.setText(statusConnection.NOTCONNECTED.toString());
			e.printStackTrace();
		}
	}

	private void saveConfig() throws IOException {
		Addr addr = getAddr();

		String userName = tfUserName.getText();
		String host = addr.getHost();
		int port = addr.getPort();

		ClientInit.config.getServerInfo().setHost(host);
		ClientInit.config.getServerInfo().setPort(port);
		ClientInit.config.setUserName(userName);
		ClientInit.saveConfig();
	}

	public Addr getAddr() {
		Addr aa = new Addr();
		String string = tfServer.getText();
		String[] dd = string.split(":");
		aa.setHost(dd[0]);
		aa.setPort(Integer.parseInt(dd[1]));
		return aa;
	}

	public void updateFields() {
		ServerInfo server = ClientInit.config.getServerInfo();
		String host = server.getHost();
		int port = server.getPort();
		tfServer.setText(host + ":" + port);

		tfUserName.setText(ClientInit.config.getUserName());
	}

	private void authAction() {
		statusProfile authStatus = statusProfile.valueOf(btnAuth.getText());

		if (authStatus == statusProfile.NOTREGISTERED) {
			RegisterRequest request = new RegisterRequest();

			request.userName = ClientInit.config.getUserName();
			request.key = ClientInit.config.privateKey;

			ClientInit.channelFuture.channel().writeAndFlush(request);

		} else if (authStatus == statusProfile.AUTH) {
			// AUTH
			AuthRequest request = new AuthRequest();
			request.user = ClientInit.config.getUserName();
			request.key = ClientInit.config.privateKey;
			ClientInit.channelFuture.channel().writeAndFlush(request);
		}
	}
}
