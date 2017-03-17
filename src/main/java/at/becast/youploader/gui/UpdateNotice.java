package at.becast.youploader.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.IOUtils;

import at.becast.youploader.util.DownloadingCountInputStream;
import at.becast.youploader.util.UTF8ResourceBundle;
import javax.swing.JLabel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JProgressBar;

public class UpdateNotice extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3277493043241496978L;
	private final JPanel contentPanel = new JPanel();
	//private static final Logger LOG = LoggerFactory.getLogger(UpdateNotice.class);
	private static final ResourceBundle LANG = UTF8ResourceBundle.getBundle("lang", Locale.getDefault());
	private JButton dlButton;
	private int length = 0;
	private String updatefile;
	private JButton cancelButton;
	private JProgressBar progressBar;

	/**
	 * Create the dialog.
	 */
	public UpdateNotice(String gitVersion,String updatefile, String changelog) {
		this.updatefile = updatefile;
		setTitle(LANG.getString("frmMain.newVersion.title"));
		setBounds(100, 100, 551, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(41dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/yp.png")));
		{
			JTextPane textPane = new JTextPane();
			textPane.setText(String.format(LANG.getString("frmMain.newVersion.Message"), gitVersion));
			textPane.setBackground(UIManager.getColor("Button.background"));
			contentPanel.add(textPane, "2, 2, fill, fill");
		}
		{
			JLabel lblChangelog = new JLabel(LANG.getString("UpdateNotice.changelog"));
			contentPanel.add(lblChangelog, "2, 4");
		}
		
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, "2, 6, fill, fill");
		{
			JTextPane txtChangelog = new JTextPane();
			txtChangelog.setContentType("text/html");
			txtChangelog.setText(changelog);
			scrollPane.setViewportView(txtChangelog);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				dlButton = new JButton(LANG.getString("UpdateNotice.download"));
				dlButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						//DesktopUtil.openBrowser("https://github.com/becast/youploader/releases");
						SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
							   @Override
							   protected Void doInBackground() throws Exception {
								download();
							    return null;
							   }
							   
							   @Override
							   protected void done() {
								   
							   }
							  };
							  
							  worker.execute();
					}
				});
				dlButton.setActionCommand("OK");
				getRootPane().setDefaultButton(dlButton);
			}
			{
				cancelButton = new JButton(LANG.getString("Button.cancel"));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
			}
			progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			progressBar.setVisible(false);
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(205)
						.addComponent(dlButton)
						.addGap(5)
						.addComponent(cancelButton))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.LEADING)
							.addGroup(Alignment.TRAILING, gl_buttonPane.createParallelGroup(Alignment.LEADING)
								.addComponent(dlButton)
								.addComponent(cancelButton))
							.addComponent(progressBar, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
	}
	
	private void download(){
		progressBar.setVisible(true);
		URL dl = null;
        File fl = null;
        OutputStream os = null;
        InputStream is = null;
        try {
            fl = new File(System.getProperty("user.home") + "/YouPloader/update.exe");
            dl = new URL("https://version.youploader.com/updates/" + this.updatefile);
            os = new FileOutputStream(fl);
            is = dl.openStream();

            DownloadingCountInputStream dcount = new DownloadingCountInputStream(os);
            dcount.setListener(this);
            length = Integer.parseInt(dl.openConnection().getHeaderField("Content-Length"));

            IOUtils.copy(is, dcount);

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		long downloaded = ((DownloadingCountInputStream) e.getSource()).getByteCount();
		progressBar.setValue((int) (((float) downloaded / length) * 100));
	}
}