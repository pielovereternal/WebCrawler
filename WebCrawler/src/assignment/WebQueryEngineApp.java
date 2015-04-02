package assignment;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class WebQueryEngineApp extends JFrame implements Runnable {
    private static final long serialVersionUID = 1486773007936651954L;

    protected WebIndex index = null;
    protected WebQueryEngine engine;

    private URL indexURL;
    private final Font font;
    
    private JPanel panel;
    private JButton searchButton;
    private JTextField queryText;
    private JTextPane resultsPane;

    private JScrollPane scroller;

    public WebQueryEngineApp(URL indexURL) {
        super();
        font = new Font(Font.SANS_SERIF, Font.PLAIN, 30);
        panel = new JPanel(new GridBagLayout());

        JLabel picLabel;
        try {
            BufferedImage myPicture = ImageIO.read(new File("tsoogle.png"));
            picLabel = new JLabel(new ImageIcon(myPicture));
        } catch (IOException | IllegalArgumentException e1) {
            picLabel = new JLabel("TSoogle");
        }

        searchButton = new JButton("Search!");
        searchButton.setFont(font);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runQuery();
            }
        });

        queryText = new JTextField(50);
        queryText.setFont(font);
        queryText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runQuery();
            }
        });

        resultsPane = new JTextPane();
        resultsPane.setFont(font);
        resultsPane.setEditable(false);
        resultsPane.setContentType("text/html");

        final Desktop desktop = Desktop.getDesktop();

        resultsPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    try {
                        System.out.println("Opening " + hle.getURL());
                        desktop.browse(hle.getURL().toURI());
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(queryText, constraints);

        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(searchButton, constraints);

        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        scroller = new JScrollPane(resultsPane);
        panel.add(scroller, constraints);

        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(picLabel, constraints);

        add(panel);

        this.indexURL = indexURL;
    }

    private void runQuery() {
        long s = System.currentTimeMillis();
        String result = query(queryText.getText());
        long e = System.currentTimeMillis();
        resultsPane.setText("<html><b>Query time: "+(e-s)+"ms</b> (includes load time on first query)<br>"+result+"</html>");
        //scroller.scrollRectToVisible(new Rectangle(0, 0, 0, 0));
    }

    public String query(String query) {
        try {
            try {
                loadIndex();
            } catch (IllegalStateException e) {
                return "<h4>" + e.getMessage() + "</h4>";
            }

            Collection<URL> c = engine.query(query);
            if (c == null)
                return "<h4>There was an error in the query engine.</h4>";

            StringBuffer result = new StringBuffer();

            result.append("Results:<br>");

            for (URL url : c) {
                result.append("<a href=\"" + url
                        + "\" onClick=\"parent.location='" + url + "'\">" + url
                        + "</a><br>");
            }
            return result.toString();
        } catch (Throwable e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            ps.println("An error occured in Java code:<br/>");
            ps.println("<pre>");
            e.printStackTrace(ps);
            ps.println("</pre>");
            return baos.toString();
        }
    }

    private void loadIndex() throws IOException, ClassNotFoundException {
        if (index == null) {
            index = (WebIndex)Index.load(indexURL);
            if (index == null)
                throw new IllegalStateException("Unable to load index: "
                        + indexURL);
            engine = new WebQueryEngine();
            engine.useWebIndex(index);
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        String indexName;
        if (args.length >= 1)
            indexName = args[0];
        else
            indexName = "index.db";
        URL baseURL = new File(System.getProperty("user.dir")).toURI().toURL();
        EventQueue.invokeLater(new WebQueryEngineApp(new URL(baseURL, indexName)));
    }

    @Override
    public void run() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
