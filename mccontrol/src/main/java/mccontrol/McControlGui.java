package mccontrol;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import lombok.Getter;

public class McControlGui extends JFrame {
	private static final long serialVersionUID = 7138706631891908102L;

	enum shellType {
		MC, SYS
	}

	private JTextField tfCmd;
	private @Getter JTextField tfRealKey;
	private @Getter JTextArea taOutput;
	private JButton btnMc;

	public McControlGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1300, 500);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);

		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 60, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, getContentPane());
		getContentPane().add(scrollPane);

		tfCmd = new JTextField();
		tfCmd.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.WEST, tfCmd, 0, SpringLayout.WEST, scrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, tfCmd, -5, SpringLayout.NORTH, scrollPane);
		springLayout.putConstraint(SpringLayout.EAST, tfCmd, 0, SpringLayout.EAST, getContentPane());
		getContentPane().add(tfCmd);
		tfCmd.setColumns(10);

		tfRealKey = new JTextField();
		tfRealKey.setHorizontalAlignment(SwingConstants.CENTER);
		tfRealKey.setEditable(false);
		springLayout.putConstraint(SpringLayout.WEST, tfRealKey, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tfRealKey, -6, SpringLayout.NORTH, tfCmd);
		springLayout.putConstraint(SpringLayout.EAST, tfRealKey, 302, SpringLayout.WEST, getContentPane());
		getContentPane().add(tfRealKey);
		tfRealKey.setColumns(10);

		JButton btnExecute = new JButton("execute");
		btnExecute.addActionListener(e -> {
			try {
				StringBuilder url = new StringBuilder();
				String inst = McControlHeader.getConfig().getUrl();
				url.append(inst);

				shellType type = shellType.valueOf(btnMc.getText());
				if (type.equals(shellType.MC)) {
					url.append("?rbm=");
				} else if (type.equals(shellType.SYS)) {
					url.append("?rbs=");
				}
				// command
				String cmd = Utils.encryptXBin(tfCmd.getText(), McControlHeader.getRealKey());
				url.append(cmd);

				String rawData = Utils.getLineFromUrl(url.toString());
				if (rawData == null) {
					taOutput.setText("Null data");
					return;
				}
				if (rawData.equals("ok")) {
					taOutput.setText("OK");
				} else {
					String data = Utils.decryptXBin(rawData, McControlHeader.getRealKey());
					taOutput.setText(data);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});
		springLayout.putConstraint(SpringLayout.SOUTH, btnExecute, -6, SpringLayout.NORTH, tfCmd);
		springLayout.putConstraint(SpringLayout.EAST, btnExecute, 0, SpringLayout.EAST, scrollPane);

		taOutput = new JTextArea();
		scrollPane.setViewportView(taOutput);
		getContentPane().add(btnExecute);

		btnMc = new JButton(shellType.MC.toString());
		btnMc.addActionListener(e -> {
			String text = btnMc.getText();
			shellType type = shellType.valueOf(text);
			if (type.equals(shellType.MC)) {
				btnMc.setText(shellType.SYS.toString());
			} else if (type.equals(shellType.SYS)) {
				btnMc.setText(shellType.MC.toString());
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, btnMc, 32, SpringLayout.EAST, tfRealKey);
		springLayout.putConstraint(SpringLayout.SOUTH, btnMc, -6, SpringLayout.NORTH, tfCmd);
		getContentPane().add(btnMc);
	}
}
