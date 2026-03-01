package main_default;

import productions.*;
import requests.Request;
import requests.RequestsHolder;
import users.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static javax.swing.JOptionPane.getRootFrame;
import static javax.swing.JOptionPane.showMessageDialog;

public class MyGUI extends JFrame{

    // false for CLI and true for GUI
    private static AtomicReference<RunMode> running_mode = new AtomicReference<>(RunMode.NONE);
    public static User user;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private static JFrame main_frame;
    public static int width_button = 140, heigth_button = 80;
    public static List<String> list_operations;
    public static List<?> save_search_list;

    // init all the panels
    public static JPanel panel_left;
    public static JPanel panel_right;
    public static JPanel panel_up;
    public static JPanel panel_center;

    public static class ModernColors {
        public static final Color PRIMARY = new Color(66, 133, 244);
        public static final Color PRIMARY_DARK = new Color(51, 103, 214);
        public static final Color ACCENT = new Color(234, 67, 53);
        public static final Color BACKGROUND = new Color(248, 249, 250);
        public static final Color SURFACE = Color.WHITE;
        public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
        public static final Color TEXT_SECONDARY = new Color(95, 99, 104);
        public static final Color BORDER = new Color(218, 220, 224);
        public static final Color HOVER = new Color(241, 243, 244);
        public static final Color SIDEBAR_BG = new Color(32, 33, 36);
        public static final Color SIDEBAR_HOVER = new Color(48, 49, 52);
    }

    public static RunMode check_running_mode() {
        // create and set up the JFrame
        JFrame frame = new JFrame("IMDB setup");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new java.awt.Dimension(400, 300));
        frame.setSize(new java.awt.Dimension(400, 300));

        // set the window in the middle of the screen
        frame.setLocationRelativeTo(null);

        // create a JPanel to hold the buttons
        JPanel panel = new JPanel();

        JLabel jLabel1 = new JLabel();
        JButton jButton1 = new JButton();
        JButton jButton2 = new JButton();

        // create the button for CLI

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel1.setText("Choose the mode to open IMDB.");

        jButton1.setText("Open terminal");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                running_mode.set(RunMode.CLI);
                frame.dispose();
            }
        });
        jButton1.addMouseListener(new CustomMouseAdapter());

        // create the button for GUI
        jButton2.setText("Open window");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(57, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton2))
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(57, 57, 57))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(56, 56, 56)
                                .addComponent(jLabel1)
                                .addGap(50, 50, 50)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(122, Short.MAX_VALUE))
        );

        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                running_mode.set(RunMode.GUI);
                frame.dispose();
            }
        });
        jButton2.addMouseListener(new CustomMouseAdapter());

        // add the panel to the content pane of the frame
        frame.getContentPane().add(panel);

        // set the frame to be visible
        frame.setVisible(true);
        frame.pack();

        // wait for the user to click a button
        while (frame.isVisible()) {
            try {
                Thread.sleep(100); // sleep to avoid busy waiting
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return running_mode.get();
    }

    public static User open_imdb_window() {
        // Create a frame
        JFrame frame = new JFrame("IMDB");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        // add an action listener for the window
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // update the files
                IMDB.update_json_files("resources/input/");
                // close the window
                frame.dispose();
            }
        });

        // Create labels and text fields
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        // Create login button
        JButton loginButton = new JButton("Login");

        // Create constraints for GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Add components to the frame with constraints
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(loginButton, gbc);

        User[] loggedInUser = {null};
        // Add ActionListener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean login = false;

                // Perform login validation here
                String username = emailField.getText();
                char[] password = passwordField.getPassword();
                user = performLogin(username, new String(password));

                // If login is successful, change window color to black
                if (user != null) {
                    frame.dispose();

                    create_UI();
                } else {
                    // Handle unsuccessful login (show an error message, etc.)
                    showMessageDialog(frame, "Login failed. Please try again.");
                }
            }
        });

        // make the frame visible
        frame.setVisible(true);

        // return the logged-in user
        return user;
    }

    private static User performLogin(String email, String password) {
        User user = find_user_by_email(email);

        if (user == null)
            return null;

        if (user.getPassword().equals(password) == true)
            return user;
        return null;
    }

    public static void create_UI() {
        main_frame = new JFrame("IMDB");
        main_frame.setSize(1000, 800);
        main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_frame.setLocationRelativeTo(null);
        main_frame.setLayout(new BorderLayout());

        main_frame.setSize(1000, 800);

        main_frame.getContentPane().setBackground(ModernColors.BACKGROUND);

        // create the list of operations based on the user
        operations();

        int num_op = list_operations.size();
        // create the JPanels
        panel_left = new JPanel();
        panel_right = new JPanel();
        panel_up = new JPanel();
        panel_center = new JPanel();

        // set the sizes
        panel_left.setPreferredSize(new Dimension(160, num_op * heigth_button));
        panel_right.setPreferredSize(new Dimension(150, 100));
        panel_center.setPreferredSize(new Dimension(100, 100));
        panel_up.setPreferredSize(new Dimension(100, 100));

        // set the colors
        panel_center.setBackground(ModernColors.BACKGROUND);
        panel_right.setBackground(ModernColors.BACKGROUND);

        // the left panel
        manage_left_panel(panel_left);
        JScrollPane scrollPane_left = new JScrollPane(panel_left);
        scrollPane_left.setPreferredSize(new Dimension(160, 100));
        scrollPane_left.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane_left.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // the right panel
        manage_right_panel();
        JScrollPane scrollPane_right = new JScrollPane(panel_right);
        scrollPane_right.setPreferredSize(new Dimension(160, 100));
        scrollPane_right.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane_right.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // the upper panel
        manage_up_panel();

        // add them to frame
        main_frame.add(scrollPane_left, BorderLayout.WEST);
        main_frame.add(panel_up, BorderLayout.NORTH);
        main_frame.add(panel_center, BorderLayout.CENTER);
        main_frame.add(scrollPane_right, BorderLayout.EAST);

        main_frame.setLocationRelativeTo(null);
        main_frame.setVisible(true);
    }

    private static void manage_left_panel(JPanel panel_left) {
        int num_op = list_operations.size();
        panel_left.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));
        panel_left.setBackground(new Color(45, 45, 48));

        for (int i = 0; i < num_op; i++) {
            JButton button = createStyledButton(list_operations.get(i));
            button.setPreferredSize(new Dimension(width_button, heigth_button));

            ButtonListener buttonListener = new ButtonListener(list_operations.get(i));
            button.addActionListener(buttonListener);
            button.addMouseListener(new CustomMouseAdapter());

            panel_left.add(button);
        }
    }

    // add this helper method
    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(new Color(66, 133, 244));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(80, 80, 85));
                } else {
                    g2.setColor(new Color(60, 60, 65));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        return button;
    }

    // this method is called every time the experience changes
    private static void manage_up_panel() {
        panel_up.removeAll();
        panel_up.setLayout(new BorderLayout());
        panel_up.setBackground(Color.WHITE);

        // Create modern header panel
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Gradient background
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(79, 70, 229),
                        getWidth(), 0, new Color(99, 102, 241)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 120));

        // Left side - Logo and title
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        leftHeader.setOpaque(false);

        JLabel titleLabel = new JLabel("IMDB Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        leftHeader.add(titleLabel);

        // Right side - User info card
        JPanel rightHeader = new JPanel();
        rightHeader.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightHeader.setOpaque(false);

        JPanel userCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Semi-transparent white background
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Border
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2.dispose();
            }
        };
        userCard.setLayout(new BoxLayout(userCard, BoxLayout.Y_AXIS));
        userCard.setOpaque(false);
        userCard.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel usernameLabel = new JLabel(user.username);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel expLabel = new JLabel("Experience: " + user.getExperientaAsString());
        expLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        expLabel.setForeground(new Color(255, 255, 255, 200));
        expLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userCard.add(usernameLabel);
        userCard.add(Box.createVerticalStrut(5));
        userCard.add(expLabel);

        rightHeader.add(userCard);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        panel_up.add(headerPanel, BorderLayout.CENTER);
    }

    // method for creating the GUI interface
    public MyGUI() {
        user = null;
    }

    // cleanup the environment
    public void cleanup() {
    }

    // method to create the UI based on the account type
    public static User find_user_by_email(String email) {
        for (User user : IMDB.user_list) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    // method that creates a list of operations for the left buttons
    public static void operations() {
        list_operations = new ArrayList<>();

        // add basic operations that are for all the users
        list_operations.add("View all");
        list_operations.add("Search");
        list_operations.add("View favorites");

        if (user instanceof Regular) {
            list_operations.add("Create a request");
            list_operations.add("View requests");
            list_operations.add("Add review");
            list_operations.add("My reviews");
        } else if (user instanceof Contributor) {
            list_operations.add("Create a request");
            list_operations.add("View requests");
            list_operations.add("Add production");
            list_operations.add("Add actor");
            list_operations.add("Delete from system");
            list_operations.add("Update Prod. details");
            list_operations.add("Update Actor details");
        } else if (user instanceof Admin) {
            list_operations.add("Add production");
            list_operations.add("Add actor");
            list_operations.add("Delete from system");
            list_operations.add("Update Prod. details");
            list_operations.add("Update Actor details");
            list_operations.add("View requests");
            list_operations.add("Add user");
            list_operations.add("Delete user");
        }

        // add logout option
        list_operations.add("Logout");
        list_operations.add("Exit");
    }

    // method to clear the data from a certain panel
    public static void clear_panel(JPanel panel) {
        panel.removeAll();
    }

    // method to show all the notifications into the right panel

    public static void manage_right_panel () {
        panel_right.removeAll();

        int numberOfNotifications = user.notifications.size();
        // split the panel
        JPanel up = new JPanel();
        JPanel down = new JPanel();

        // create a JSplitPane and set its properties
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, up, down);
        splitPane.setDividerLocation(30);

        // set both sides of the split to be non-resizable
        splitPane.setResizeWeight(0.5);
        splitPane.setEnabled(false);

        // add the JSplitPane to the right panel
        panel_right.setLayout(new BorderLayout());
        panel_right.add(splitPane, BorderLayout.CENTER);

        // add into the down panel the list of buttons
        down.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        down.setPreferredSize(new Dimension(100, (numberOfNotifications + 1) * heigth_button + 10));

        int index = 1;
        // add buttons into the down section
        Iterator<String> iterator =  user.notifications.iterator();
        while (iterator.hasNext()) {
            String notification = iterator.next();

            // Load the icon ONCE outside the button
            ImageIcon gmailIcon = loadImageIcon("input/photos/gmail.png");

            // Create button with JUST the icon - simplest approach
            JButton button = new JButton();
            if (gmailIcon != null) {
                // Scale the image to fit nicely
                Image scaledImage = gmailIcon.getImage().getScaledInstance(100, 60, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
            }

            button.setPreferredSize(new Dimension(130, heigth_button));
            button.setContentAreaFilled(false);
            button.setBorderPainted(true);
            button.setFocusPainted(false);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));

            // add an action listener
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Create a JTextArea and display element.toString()
                    JTextArea textArea = new JTextArea(notification.toString());
                    textArea.setEditable(false);
                    textArea.setLineWrap(true);  // Enable line wrapping
                    textArea.setWrapStyleWord(true);  // Wrap at word boundaries
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(400, 150)); // Set preferred size

                    // Load the open gmail icon (mark as read)
                    ImageIcon icon_open = loadImageIcon("input/photos/open_gmail.png");

                    if (icon_open != null) {
                        Image scaledOpenImage = icon_open.getImage().getScaledInstance(100, 60, Image.SCALE_SMOOTH);
                        button.setIcon(new ImageIcon(scaledOpenImage));
                    }

                    // Create a "Delete" button for the JOptionPane
                    JButton deleteSystem = new JButton("Delete notification");

                    // Add an action listener to the "Delete" button
                    deleteSystem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent deleteEvent) {
                            // delete the notification
                            user.notifications.remove(notification);
                            manage_right_panel();

                            // Close the JOptionPane after deletion
                            Window window = SwingUtilities.getWindowAncestor(textArea);
                            if (window != null) {
                                window.dispose();
                            }
                        }
                    });

                    // create a JPanel to hold the "Delete" button
                    JPanel buttonPanel = new JPanel();
                    buttonPanel.add(deleteSystem);

                    // Create a JPanel to hold the scroll pane and button panel
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());
                    panel.add(scrollPane, BorderLayout.CENTER);
                    panel.add(buttonPanel, BorderLayout.SOUTH);

                    // Create a JOptionPane to show the scroll pane and "Delete" button
                    showMessageDialog(null, panel, "Notification", JOptionPane.PLAIN_MESSAGE);
                }
            });

            down.add(button);
        }

        // add text field in the up panel
        JTextArea text = new JTextArea("Notifications");
        text.setFont(new Font("Segoe UI", Font.BOLD, 16));
        text.setEditable(false);
        up.add(text);

        // create a scroll pane and add the button panel to it
        JScrollPane scrollPane = new JScrollPane(down);

        // add the scrollPane to the JSplitPane
        splitPane.setBottomComponent(scrollPane);

        // revalidate and repaint the right panel
        panel_right.revalidate();
        panel_right.repaint();
    }

    // Keep the same helper methods:
    private static ImageIcon loadImageIcon(String resourcePath) {
        try {
            java.net.URL imgURL = MyGUI.class.getClassLoader().getResource(resourcePath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                System.out.println("Loaded image from ClassLoader: " + resourcePath + " (Width: " + icon.getIconWidth() + ", Height: " + icon.getIconHeight() + ")");
                return icon;
            } else {
                System.err.println("Could not find image in resources: " + resourcePath);
                return null;
            }
        } catch (Exception ex) {
            System.err.println("Error loading image from ClassLoader: " + ex.getMessage());
            return null;
        }
    }

    // method that creates lists with the optionsfor each type
    public static AtomicInteger create_lists(List<JMenuItem> productions,
                                    List<JMenuItem> actors,
                                    List<JMenuItem> movies,
                                    List<JMenuItem> series,
                                    List<JMenuItem> all, JButton button) {
        // create the integer that stores the type of sort
        AtomicInteger sort_type = new AtomicInteger();
        sort_type.set(0); // default values

        JMenuItem option = new JMenuItem("Alphabetical");
        option.addActionListener(e -> {sort_type.set((sort_type.get() / 100) * 100 + 1); button.setText("Alphabetical");}); // if the number is negative set to -1
        productions.add(option);
        actors.add(option);
        movies.add(option);
        series.add(option);
        all.add(option); // the only option for all

        option = new JMenuItem("Ranking");
        option.addActionListener(e -> {sort_type.set((sort_type.get() / 100) * 100 + 2); button.setText("Ranking");});
        productions.add(option);
        movies.add(option);
        actors.add(option);
        series.add(option);

        option = new JMenuItem("Release date");
        option.addActionListener(e -> {sort_type.set((sort_type.get() / 100) * 100 + 3); button.setText("Release date");});
        productions.add(option);
        movies.add(option);
        series.add(option);

        option = new JMenuItem("Number of appearances");
        option.addActionListener(e -> {sort_type.set((sort_type.get() / 100) * 100 + 4); button.setText("Number of appearances");});
        actors.add(option);

        option = new JMenuItem("Duration");
        option.addActionListener(e -> {sort_type.set((sort_type.get() / 100) * 100 + 5); button.setText("Duration");});
        movies.add(option);

        option = new JMenuItem("Number of Episodes");
        option.addActionListener(e -> {sort_type.set((sort_type.get() / 100) * 100 + 6); button.setText("Number of Episodes");});
        series.add(option);

        option = new JMenuItem("Number of Ratings");
        option.addActionListener(e -> {sort_type.set((sort_type.get() / 100) * 100 + 7); button.setText("Number of Ratings");});
        series.add(option);
        movies.add(option);
        productions.add(option);

        return sort_type;
    }

    // method that returns an image and the name based on the type
    public static List<Object> return_info_for_display(Object object) {
        // the first element is the name/info and then the photo for background
        List<Object> list = new ArrayList<>();
        // TODO add image
        list.add(return_name(object));
        if (object instanceof Movie) {

            return list;
        }
        if (object instanceof Series) {

            return list;
        }
        if (object instanceof Actor) {

            return list;
        }
        if (object instanceof Request) {

            return list;
        }
        if (object instanceof User) {

            return list;
        }
        if (object instanceof User) {
            return list;
        }

        list.add("ERROR");
        return list;
    }

    // method to display in the center panel the desired list
    public static void displayList(List<?> list, String type) {
        panel_center.removeAll();

        // split the panel
        JPanel up = new JPanel();
        JPanel down = new JPanel();

        // create a JSplitPane and set its properties
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, up, down);
        splitPane.setDividerLocation(100);

        // set both sides of the split to be non-resizable
        splitPane.setResizeWeight(0.5); // equal size for both sides
        splitPane.setEnabled(false);

        // add the JSplitPane to the central panel
        panel_center.setLayout(new BorderLayout());
        panel_center.add(splitPane, BorderLayout.CENTER);

        // call function to add into the upper part of the screen based on type
        if (type.equals("SEARCH")) {
            displaySearch(list, up);
        } else if (type.equals("FILTER")) {
            displayFilters(list, up);
        } else if (type.equals("CUSTOM SEARCH")) {
            custom_search(list, up);
        }

        // add into the down panel the list of buttons
        down.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        down.setPreferredSize(new Dimension(200, (list.size() + 1) * (heigth_button + 3)));

        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();

            // get the name and photo
            List<Object> aux = return_info_for_display(element);
            JButton button = new JButton(aux.get(0).toString());
            button.setPreferredSize(new Dimension(600, heigth_button));

            // add an action listener
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // the panel for the new window
                    JPanel panel = new JPanel();
                    // create a JTextArea and display element.toString()
                    JTextArea textArea = new JTextArea(element.toString());
                    textArea.setEditable(false);
                    textArea.setLineWrap(true);  // Enable line wrapping
                    textArea.setWrapStyleWord(true);  // Wrap at word boundaries
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(400, 150)); // Set preferred size

                    // create a "Delete" button for the JOptionPane
                    JButton deleteSystem = new JButton("Delete from system");
                    JButton deleteFavorites = new JButton("Delete from favorites");
                    JButton addFavorites = new JButton("Add in favorites");
                    JButton solveRequest = new JButton("Mark as solved");
                    JButton addRating = new JButton("Add rating");
                    JButton deleteRating = new JButton("Delete rating");
                    // Add an action listener to the "Delete" button
                    deleteSystem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent deleteEvent) {
                            // delete from the system
                            displayList(Operations.delete(element, list), type);

                            // close the JOptionPane after deletion
                            Window window = SwingUtilities.getWindowAncestor(textArea);
                            if (window != null) {
                                window.dispose();
                            }
                        }
                    });
                    deleteFavorites.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent deleteEvent) {
                            user.favorites.remove(element);

                            // add the user as an observer
                            if (element instanceof Actor)
                                ((Actor) element).production_observers.remove(user);
                            if (element instanceof Production)
                                ((Production) element).production_observers.remove(user);

                            // move the favorites into a list
                            List<?> aux_list = new ArrayList<>(user.favorites);

                            displayList(aux_list, type);

                            // close the JOptionPane after deletion
                            Window window = SwingUtilities.getWindowAncestor(textArea);
                            if (window != null) {
                                window.dispose();
                            }
                        }
                    });
                    addFavorites.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent deleteEvent) {
                            user.addFavorite(element);

                            // add the user as an observer
                            if (element instanceof Actor)
                                ((Actor) element).production_observers.add(user);
                            if (element instanceof Production)
                                ((Production) element).production_observers.add(user);

                            // close the JOptionPane after deletion
                            Window window = SwingUtilities.getWindowAncestor(textArea);
                            if (window != null) {
                                window.dispose();
                            }
                        }
                    });
                    solveRequest.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent deleteEvent) {
                            Operations.solve_request((Request) element);
                            list.remove(element);

                            displayList(list, type);
                            // close the JOptionPane after deletion
                            Window window = SwingUtilities.getWindowAncestor(textArea);
                            if (window != null) {
                                window.dispose();
                            }
                        }
                    });
                    addRating.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent deleteEvent) {
                            JPanel panelRating = new JPanel();
                            panelRating.setLayout(null);

                            JLabel gradeText = new JLabel("Select a value:");
                            gradeText.setBounds(30, 30, 100, 20);
                            gradeText.setFont(new Font("Segoe UI", Font.BOLD, 14));
                            panelRating.add(gradeText);

                            JLabel commentText = new JLabel("Write a comment:");
                            commentText.setBounds(30, 70, 120, 20);
                            commentText.setFont(new Font("Segoe UI", Font.BOLD, 14));
                            panelRating.add(commentText);

                            JTextArea commentField = new JTextArea();
                            commentField.setBounds(170, 70, 250, 60);
                            JScrollPane scrollPane1 = new JScrollPane(commentField);
                            scrollPane1.setBounds(170, 70, 250, 60);
                            panelRating.add(scrollPane1);

                            // create the menu button
                            JButton menuButton = new JButton("None");
                            menuButton.setBounds(200, 30, 100, 20);

                            // create the first menu
                            JPopupMenu popupMenu = new JPopupMenu();

                            // add all the possible values
                            for (int i = 1; i <= 10; i++) {
                                JMenuItem menuItem = new JMenuItem(i + "");
                                menuItem.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        // change the text on the button
                                        menuButton.setText(((JMenuItem) e.getSource()).getText());
                                    }
                                });
                                popupMenu.add(menuItem);
                            }
                            menuButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    popupMenu.show(menuButton, 0, menuButton.getHeight());
                                }
                            });
                            // add the button to the panel
                            panelRating.add(menuButton);
                            JDialog dialog = new JDialog((Frame) null, "Element Details", true);

                            // add the create button
                            JButton createRating = new JButton("Create review");
                            createRating.setFont(new Font("Segoe UI", Font.BOLD, 16));
                            createRating.setBounds(120, 160, 200, 40);
                            // add action listener
                            createRating.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    // check if valid info
                                    if (menuButton.getText().equals("None") || commentField.getText().isEmpty())
                                        return;

                                    // only the regular user can add a rating
                                    ((Regular) user).addRating(element,
                                            Integer.parseInt(menuButton.getText()),
                                            commentField.getText());

                                    // close the window
                                    dialog.dispose();
                                    // close the other window
                                    getRootFrame().dispose();

                                    manage_up_panel();
                                    displayList(list, type);
                                }
                            });
                            panelRating.add(createRating);

                            // Create a custom JDialog

                            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                            dialog.getContentPane().add(panelRating);

                            // Set the size of the dialog
                            dialog.setSize(450, 250); // Set your preferred size here

                            // Center the dialog on the screen
                            dialog.setLocationRelativeTo(null);

                            // Show the dialog
                            dialog.setVisible(true);
                        }
                    });
                    deleteRating.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent deleteEvent) {
                            Rating rating_to_delete = null;
                            // search the rating added by this the logged user to this production
                            if (element instanceof Production) {
                                for (Rating rating : ((Production) element).getRating()) {
                                    if (rating.getUsername().equals(user.username)) {
                                        rating_to_delete = rating;
                                        break;
                                    }
                                }
                            }
                            if (element instanceof Actor) {
                                for (Rating rating : ((Actor) element).getRating()) {
                                    if (rating.getUsername().equals(user.username)) {
                                        rating_to_delete = rating;
                                        break;
                                    }
                                }
                            }

                            Operations.delete_rating(rating_to_delete, list);
                            manage_up_panel();
                            displayList(list, type);
                        }
                    });

                    // Create a JPanel to hold the "Delete" button
                    JPanel buttonPanel = new JPanel();

                    // check to display the addFavorited button
                    if ((element instanceof Production || element instanceof Actor) &&
                            user.favorites.contains(element) == false) { // check that it doesn't already exist
                        buttonPanel.add(addFavorites);
                    }

                    // check to display the solveRequest button which can be done only by the user that has to solve it
                    if (element instanceof Request) {
                        // cast to request
                        Request r = (Request) element;

                        if (user.username.equals(r.to) ||
                                (r.to.equals("ADMIN") && user.getTypeAsString().equals("Admin"))) {
                            buttonPanel.add(solveRequest);
                        }
                    }

                    // if the button is to delete a request
                    if (element instanceof Request &&
                            ((RequestsHolder.requestList.contains(element) && user instanceof Admin) || // if it is for all the Admins
                            (user.username.equals(((Request) element).to) ||
                                    user.username.equals(((Request) element).username)))){ // if it is by the user that created or is intended to
                        buttonPanel.add(deleteSystem);
                    }
                    // if the button is to delete production/actor
                    if ((element instanceof Production || element instanceof Actor) &&
                            user.getTypeAsString().equals("Regular") == false && // check if it is the correct user type
                            ((Staff) user).getUserCollection().contains(element) == true) { // if the user can modify
                        System.out.println("Sterge productie");
                        buttonPanel.add(deleteSystem);
                    }
                    // if the button is to delete a user
                    if (element instanceof User && user.getTypeAsString().equals("Admin")) {
                        buttonPanel.add(deleteSystem);
                    }
                    if ((element instanceof Production || element instanceof Actor) && user.favorites.contains(element)){
                        buttonPanel.add(deleteFavorites);
                    }
                    // verify to add a rating for production
                    if (element instanceof Production && ((Production)element).checkRating(user, 0) == true && user instanceof Regular) {
                        buttonPanel.add(addRating);
                    }
                    // verify to add a rating for actor
                    if (element instanceof Actor && ((Actor)element).checkRating(user, 0) == true && user instanceof Regular) {
                        buttonPanel.add(addRating);
                    }
                    // verify to delete a rating for actor
                    if (element instanceof Actor && ((Actor) element).checkRating(user, 0) == false && user instanceof Regular){
                        buttonPanel.add(deleteRating);
                    }
                    // verify to delete a rating for production
                    if (element instanceof Production && ((Production) element).checkRating(user, 0) == false && user instanceof Regular){
                        buttonPanel.add(deleteRating);
                    }
                    // Create a JPanel to hold the scroll pane and button panel
                    panel.setLayout(new BorderLayout());
                    panel.add(scrollPane, BorderLayout.CENTER);
                    panel.add(buttonPanel, BorderLayout.SOUTH);


                    showMessageDialog(null, panel, "Element Details", JOptionPane.PLAIN_MESSAGE);
                }
            });
            button.addMouseListener(new CustomMouseAdapter());

            down.add(button);
        }

        // create a scroll pane and add the button panel to it
        JScrollPane scrollPane = new JScrollPane(down);

        // add the scrollPane to the JSplitPane
        splitPane.setBottomComponent(scrollPane);

        // revalidate and repaint the central panel
        panel_center.revalidate();
        panel_center.repaint();
    }

    public static void displaySearch(List<?> list, JPanel up) {
        up.removeAll();
        up.setLayout(null);

        // add search bar onto the screen
        JTextField searchBar = new JTextField();
        searchBar.setBounds(200, 40, 250, 20);
        up.add(searchBar);

        // add text label
        JLabel searchLabel = new JLabel("Search by name");
        searchLabel.setBounds(30, 40, 250, 20);
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD & Font.ITALIC, 15));
        up.add(searchLabel);

        // add search button
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        searchButton.setBounds(460, 40, 150, 20);
        searchButton.addMouseListener(new CustomMouseAdapter());
        // add action listener
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Object> new_list = new ArrayList<>();
                if (searchBar.getText().isEmpty()) {
                    // if no text has been entered
                    displayList(save_search_list, "SEARCH");
                    return;
                }

                // add into the new list only the elements that match the search
                for (Object object : list) {
                    if (return_name(object).toLowerCase().contains(searchBar.getText().toLowerCase()))
                        new_list.add(object);
                }
                displayList(new_list, "SEARCH");
            }
        });
        up.add(searchButton);

        up.repaint();
    }

    public static int return_type(List<?> list) {
        int type = 0;
        int actors = 1;
        int series = 2;
        int movies = 4;

        if (list.size() == 0)
            return 0;

        // traverse all the objects from the list
        for (Object obj : list) {
            if (obj instanceof Actor)
                type = type | actors;
            if (obj instanceof Series)
                type = type | series;
            if (obj instanceof Movie)
                type = type | movies;

            // if all the types have been found
            if (type == (actors | series | movies))
                break; // for optimisation
        }

        if (type == 0)
            System.out.println("Error in function return_type");

        return type;
    }

    // this method receives a sorted list
    public static void displayFilters(List<?> list, JPanel up) {
        up.removeAll();
        up.setLayout(null);

        /*
         * the list that this method received has multiple objects so i create
         * a number that describes what types of objects it has to display certain
         * filters : 1 - actors, 2 - series, 4 - movies
         */
        int type = return_type(list);

        JLabel text = new JLabel("Minimum rating");
        text.setFont(new Font("Segoe UI", Font.BOLD, 14));
        text.setBounds(10, 0, 150, 20);
        up.add(text);

        JSlider rating = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);
        rating.setMajorTickSpacing(1);
        rating.setPaintTicks(true);
        rating.setPaintLabels(true);
        rating.setBounds(10, 20, 150, 40);
        up.add(rating);

        text = new JLabel("Minimum year");
        text.setFont(new Font("Segoe UI", Font.BOLD, 14));
        text.setBounds(10, 77, 100, 20);

        JTextField year = new JTextField();
        year.setBounds(110, 80, 70, 16);
        year.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // add search button
        JButton searchButton = new JButton("Filter");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        searchButton.setBounds(530, 0, 160, 60);
        searchButton.addMouseListener(new CustomMouseAdapter());

        JButton genre = new JButton("Add genre");
        genre.setBounds(290, 0, 100, 20);
        genre.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPopupMenu popupMenuGenre = new JPopupMenu();
        Production.Genre[] allGenres = Production.Genre.values();

        JTextArea genre_area = new JTextArea();

        for (Production.Genre aux : allGenres) {
            JMenuItem option = new JMenuItem(aux.toString());

            option.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (genre_area.getText().contains(aux.toString()) == false)
                        genre_area.setText(genre_area.getText().concat(aux.toString() + " | "));
                }
            });
            popupMenuGenre.add(option);
        }

        genre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the popup menu at the button's location
                popupMenuGenre.show(genre, 0, genre.getHeight());
            }
        });

        JButton delete_genre = new JButton("Delete genre");
        delete_genre.setBounds(180, 0, 100, 20);
        delete_genre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        delete_genre.addMouseListener(new CustomMouseAdapter());
        delete_genre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = genre_area.getText();
                // Split the input string by '|'
                String[] words = input.split(" \\| ");

                // Check if there is at least one word before deleting
                if (words.length > 0) {
                    // Reconstruct the string without the last word and the trailing '|'
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < words.length - 1; i++) {
                        result.append(words[i]);
                        if (i < words.length - 1) {
                            result.append(" | ");
                        }
                    }
                    genre_area.setText(result.toString());
                } else {
                    // If there's only one word or the input is empty, return the original string
                    genre_area.setText(input);
                }
            }
        });

        genre_area.setBounds(180, 30, 200, 60);
        genre_area.setFont(new Font("Segoe UI", Font.BOLD, 13));
        genre_area.setLineWrap(true);
        genre_area.setEditable(false);
        genre_area.addMouseListener(new CustomMouseAdapter());

        // add number of performances
        JLabel performances_text = new JLabel("Minimum number of performances");
        performances_text.setFont(new Font("Segoe UI", Font.BOLD, 13));
        performances_text.setBounds(180, 0, 240, 20);

        JTextField performances = new JTextField();
        performances.setFont(new Font("Segoe UI", Font.BOLD, 13));
        performances.setBounds(400, 0, 50, 20);

        JLabel duration_text = new JLabel("Min. duration in minutes: ");
        duration_text.setFont(new Font("Segoe UI", Font.BOLD, 13));
        duration_text.setBounds(390, 77, 170, 20);

        JTextField duration = new JTextField();
        duration.setFont(new Font("Segoe UI", Font.BOLD, 12));
        duration.setBounds(555, 79, 50, 16);

        // display each filter
        if ((type | 1) != type && type != 0) {
            // display for movies and series
            up.add(text);
            up.add(year);
            up.add(genre);
            up.add(genre_area);
            up.add(delete_genre);
        }

        // only for movies
        if (type == 4) {
            up.add(duration_text);
            up.add(duration);
        }

        // only for actors
        if (type == 1) {
            up.add(performances_text);
            up.add(performances);
        }
        // add action listener
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Object> aux_list = new ArrayList<>(list);
                // filter the list
                // minimum release year
                if (up.getComponentZOrder(year) != -1 &&
                        year.getText().isEmpty() == false &&
                        isNumber(year.getText(), 1800, 2050) != -1) {
                    aux_list = filter_year(aux_list, isNumber(year.getText(), 1800, 2050));
                }
                // minimum rating
                if (up.getComponentZOrder(rating) != -1) {
                    aux_list = filter_rating(aux_list, rating.getValue());
                }
                // genres
                if (up.getComponentZOrder(genre_area) != -1 &&
                        genre_area.getText().isEmpty() == false) {
                    aux_list = filter_genre(aux_list, parse_genre(genre_area.getText()));
                }
                // minimum number of performances
                if (up.getComponentZOrder(performances) != -1 &&
                        performances.getText().isEmpty() == false &&
                        isNumber(performances.getText(), 0, 100) != -1) {
                    aux_list = filter_performances(aux_list, isNumber(performances.getText(), 0, 100));
                }
                // by minimum duration only for movies
                if (up.getComponentZOrder(duration) != -1 &&
                        duration.getText().isEmpty() == false &&
                        isNumber(duration.getText(), 0, 1000) != -1) {
                    aux_list = filter_duration(aux_list, isNumber(duration.getText(), 0, 1000));
                }
                displayList(aux_list, "FILTER");
            }
        });
        up.add(searchButton);

        up.repaint();
    }

    public static List<Production.Genre> parse_genre(String text) {
        List<Production.Genre> list = new ArrayList<>();
        String[] genre_list = text.split(" | ");
        for (String string : genre_list) {
            if (string.contains("|") == false && string.contains(" ") == false) {
                list.add(Production.Genre.valueOf(string));
            }
        }

        return list;
    }

    // method that returns the name based on the type
    public static String return_name(Object object) {
        if (object instanceof Actor)
            return ((Actor) object).name;
        if (object instanceof Production)
            return ((Production) object).title;
        if (object instanceof User)
            return ((User) object).username;
        if (object instanceof Request)
            return ((Request) object).getType();
        if (object instanceof Rating) // return the username and the grade
            return ((Rating) object).getUsername() + " : " + ((Rating) object).getNota();

        System.out.println("Unknown type of object");
        return "";
    }

    // sorts
    public static List<Object> sort(int type) {
        Iterator<Object> iterator;
        List<Object> list = new ArrayList<>();
        // check for the type of list
        switch (Math.abs(type) / 100) {
            case 1: // for productions
                list.addAll(IMDB.production_list);
                break;
            case 2: // for actors
                list.addAll(IMDB.actor_list);
                break;
            case 3: // for Movies
                list.addAll(IMDB.production_list);

                // use iterator to remove series
                iterator = list.iterator();
                while (iterator.hasNext()) {
                    Object production = iterator.next();
                    if (production instanceof Series) {
                        iterator.remove();
                    }
                }
                break;
            case 4: // for series
                list.addAll(IMDB.production_list);

                // use iterator to remove movies
                iterator = list.iterator();
                while (iterator.hasNext()) {
                    Object production = iterator.next();
                    if (production instanceof Movie) {
                        iterator.remove();
                    }
                }
                break;
            case 5: // for all
                // add all the lists together
                list.addAll(IMDB.production_list);
                list.addAll(IMDB.actor_list);
                break;
        }

        // sort the lists
        int operation = type % 100;

        // perform the sort operation
        if (Math.abs(operation) == 1) {
            Collections.sort(list, Comparator.comparing(o -> {
                if (o instanceof Production) {
                    return ((Production) o).getTitle();
                } else if (o instanceof Actor) {
                    return ((Actor) o).name; // Assuming Actor has a getName() method
                }
                return "";
            }));
        } else if (Math.abs(operation) == 2) { // sort by rank
            Collections.sort(list, Comparator.comparingDouble(o -> {
                if (o instanceof Production) {
                    return ((Production) o).getAverageRating();
                } else if (o instanceof Actor) {
                    return ((Actor) o).averageRating;
                }
                return 0.0;
            }));
        } else if (Math.abs(operation) == 3) { // sort by release year
            Collections.sort(list, Comparator.comparingInt(o -> {
                if (o instanceof Movie) {
                    return ((Movie) o).releaseYear;
                } else if (o instanceof Series) {
                    return ((Series) o).releaseYear;
                }
                return 0;
            }));
        } else if (Math.abs(operation) == 4) { // sort by number of appearances
            Collections.sort(list, Comparator.comparingInt(o -> {
                if (o instanceof Actor) {
                    return ((Actor) o).getNumberOfAppearances();
                }
                return 0;
            }));
        } else if (Math.abs(operation) == 5) { // sort by duration - only for movies
            Collections.sort(list, Comparator.comparing(o -> {
                if (o instanceof Movie) {
                    return ((Movie) o).duration;
                }
                return "";
            }));
        } else if (Math.abs(operation) == 6) { // sort by number of episodes
            Collections.sort(list, Comparator.comparingInt(o -> {
                if (o instanceof Series)
                    return ((Series) o).numberOfEpisodes;
                return 0;
            }));
        } else if (Math.abs(operation) == 7) { // sort by number of ratings
            Collections.sort(list, Comparator.comparingInt(o -> {
                if (o instanceof Production)
                    return ((Production) o).getNumberOfRatings();
                return 0;
            }));
        }

        if (operation < 0) {
            Collections.reverse(list);
        }

        return list;
    }

    public static List<Object> filter_rating(List<Object> list, double rating) {
        List<Object> aux = new ArrayList<>(list);

        for (Object obj : list) {
            if (obj instanceof Actor && ((Actor) obj).averageRating < rating) {
                aux.remove(obj);
            }
            if (obj instanceof Production && ((Production) obj).averageRating < rating) {
                aux.remove(obj);
            }
        }

        return aux;
    }

    public static List<Object> filter_duration(List<Object> list, int duration) {
        List<Object> aux = new ArrayList<>(list);

        // TODO continue
        /*
        for (Object obj : list) {
            if (obj instanceof Actor && ((Actor) obj).averageRating < rating) {
                aux.remove(obj);
            }
            if (obj instanceof Production && ((Production) obj).averageRating < rating) {
                aux.remove(obj);
            }
        }*/

        return aux;
    }

    public static List<Object> filter_seasons(List<Object> list, int number_of_seasons) {
        List<Object> aux = new ArrayList<>(list);

        /*
        for (Object obj : list) {
            if (obj instanceof Actor && ((Actor) obj).averageRating < rating) {
                aux.remove(obj);
            }
            if (obj instanceof Production && ((Production) obj).averageRating < rating) {
                aux.remove(obj);
            }
        }*/

        return aux;
    }

    public static List<Object> filter_year(List<Object> list, int year) {
        List<Object> aux = new ArrayList<>(list);

        for (Object obj : list) {
            if (obj instanceof Movie && ((Movie) obj).releaseYear < year) {
                aux.remove(obj);
            }
            if (obj instanceof Series && ((Series) obj).releaseYear < year) {
                aux.remove(obj);
            }
        }

        return aux;
    }

    public static List<Object> filter_genre(List<Object> list, List<Production.Genre> genres) {
        List<Object> aux = new ArrayList<>(list);

        for (Object obj : list) {
            if (obj instanceof Production && ((Production) obj).genre.containsAll(genres) == false) {
                aux.remove(obj);
            }
        }

        return aux;
    }

    public static List<Object> filter_performances(List<Object> list, int num) {
        List<Object> aux = new ArrayList<>(list);

        for (Object obj : list) {
            if (obj instanceof Actor && ((Actor) obj).performances.size() < num) {
                aux.remove(obj);
            }
        }

        return aux;
    }

    // methods that perform all the possible operations
    public static void view_all() {
        AtomicInteger sort_type; // type of operation
        AtomicInteger reverse = new AtomicInteger(1); // if it is reversed

        // clear the center panel
        clear_panel(panel_center);
        panel_center.setLayout(null);

        // create buttons
        JLabel text = new JLabel("Select which type of content to view:");
        JButton production_button = new JButton("Productions");
        JButton actor_button = new JButton("Actors");
        JButton movie_button = new JButton("Movies");
        JButton series_button = new JButton("Series");
        JButton all_button = new JButton("All");

        text.setBounds(110, 40, 400, 40); // Set bounds for the text label
        text.setFont(new Font("Segoe UI", Font.BOLD, 20));

        production_button.setBounds(200, 100, 150, 60); // Set bounds for the production button
        actor_button.setBounds(360, 100, 150, 60); // Set bounds for the actor button
        movie_button.setBounds(200, 200, 150, 60);
        series_button.setBounds(360, 200, 150, 60);
        all_button.setBounds(300, 300, 150, 60);

        // set a bigger font
        production_button.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        actor_button.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        movie_button.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        series_button.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        all_button.setFont(new Font("Segoe UI", Font.ITALIC, 16));

        // Add components to the panel
        panel_center.add(text);
        panel_center.add(production_button);
        panel_center.add(actor_button);
        panel_center.add(movie_button);
        panel_center.add(series_button);
        panel_center.add(all_button);

        // create the popup button
        JButton popupButton = new JButton("None");
        JPopupMenu popupMenu = new JPopupMenu();
        // create the list of sort options for the production
        List<JMenuItem> list_op_production = new ArrayList<>();
        List<JMenuItem> list_op_actors = new ArrayList<>();
        List<JMenuItem> list_op_movies = new ArrayList<>();
        List<JMenuItem> list_op_series = new ArrayList<>();
        List<JMenuItem> list_op_all = new ArrayList<>();

        // create check box for reverse sort
        JCheckBox reverseSortButton = new JCheckBox("Reverse");
        reverseSortButton.setBounds(400, 400, 130, 30);
        reverseSortButton.setFont(new Font("Segoe UI", 0, 16));
        reverseSortButton.addItemListener(e -> reverse.set((reverse.get() == 1) ? -1 : 1));
        panel_center.add(reverseSortButton);

        // populate the lists
        sort_type = create_lists(list_op_production, list_op_actors, list_op_movies, list_op_series, list_op_all, popupButton);

        // add action listener to display the options
        popupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(popupButton, 0, popupButton.getHeight());
            }
        });
        popupButton.setBounds(230, 400, 170, 30);
        panel_center.add(popupButton);

        // create sort by text label and add it onto the screen
        JLabel sortText = new JLabel("Sort by: ");
        sortText.setBounds(110, 400, 90, 30);
        sortText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel_center.add(sortText);

        // add show button
        JButton showButton = new JButton("Show");
        showButton.setBounds(250, 550, 150, 60);
        showButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel_center.removeAll();
                panel_center.revalidate();
                panel_center.repaint();
                List<Object> list = sort(sort_type.get() * reverse.get());
                save_search_list = list;
                // perform the desired sort
                displayList(list, "FILTER");
            }
        });
        panel_center.add(showButton);

        // add ActionListener to the production button
        production_button.addActionListener(e -> {
            popupMenu.removeAll();
            // color the button and the other to the normal color
            production_button.setBackground(Color.BLUE);
            actor_button.setBackground(Color.WHITE);
            movie_button.setBackground(Color.WHITE);
            series_button.setBackground(Color.WHITE);
            all_button.setBackground(Color.WHITE);

            sort_type.set(sort_type.get() % 100 + 100); // 1** for productions
            // add options for productions
            for (JMenuItem item : list_op_production) {
                popupMenu.add(item);
            }
            panel_center.add(popupButton);

            panel_center.revalidate();
            panel_center.repaint();
        });
        production_button.addMouseListener(new CustomMouseAdapter());

        // add ActionListener to the actor button
        actor_button.addActionListener(e -> {
            popupMenu.removeAll();
            // color the button and the other to the normal color
            production_button.setBackground(Color.WHITE);
            actor_button.setBackground(Color.BLUE);
            movie_button.setBackground(Color.WHITE);
            series_button.setBackground(Color.WHITE);
            all_button.setBackground(Color.WHITE);
            sort_type.set(sort_type.get() % 100 + 200); // 2** for actors
            // add options for actors
            for (JMenuItem item : list_op_actors) {
                popupMenu.add(item);
            }
            panel_center.add(popupButton);

            panel_center.revalidate();
            panel_center.repaint();
        });
        actor_button.addMouseListener(new CustomMouseAdapter());

        // add ActionListener to the movie button
        movie_button.addActionListener(e -> {
            // color the button and the other to the normal color
            production_button.setBackground(Color.WHITE);
            actor_button.setBackground(Color.WHITE);
            movie_button.setBackground(Color.BLUE);
            series_button.setBackground(Color.WHITE);
            all_button.setBackground(Color.WHITE);
            popupMenu.removeAll();
            sort_type.set(sort_type.get() % 100 + 300); // 3** for movies
            // add options for actors
            for (JMenuItem item : list_op_movies) {
                popupMenu.add(item);
            }
            panel_center.add(popupButton);

            panel_center.revalidate();
            panel_center.repaint();
        });
        movie_button.addMouseListener(new CustomMouseAdapter());

        // add ActionListener to the series button
        series_button.addActionListener(e -> {
            // color the button and the other to the normal color
            production_button.setBackground(Color.WHITE);
            actor_button.setBackground(Color.WHITE);
            movie_button.setBackground(Color.WHITE);
            series_button.setBackground(Color.BLUE);
            all_button.setBackground(Color.WHITE);
            popupMenu.removeAll();
            sort_type.set(sort_type.get() % 100 + 400); // 4** for series
            // add options for actors
            for (JMenuItem item : list_op_series) {
                popupMenu.add(item);
            }
            panel_center.add(popupButton);

            panel_center.revalidate();
            panel_center.repaint();
        });
        series_button.addMouseListener(new CustomMouseAdapter());

        // add ActionListener to the actor button
        all_button.addActionListener(e -> {
            // color the button and the other to the normal color
            production_button.setBackground(Color.WHITE);
            actor_button.setBackground(Color.WHITE);
            movie_button.setBackground(Color.WHITE);
            series_button.setBackground(Color.WHITE);
            all_button.setBackground(Color.BLUE);
            popupMenu.removeAll();
            sort_type.set(sort_type.get() % 100 + 500); // 5** for all
            // add options for actors
            for (JMenuItem item : list_op_all) {
                popupMenu.add(item);
            }
            panel_center.add(popupButton);

            panel_center.revalidate();
            panel_center.repaint();
        });
        all_button.addMouseListener(new CustomMouseAdapter());

        // repaint the panel to reflect the changes
        panel_center.revalidate();
        panel_center.repaint();
    }

    public static void custom_search(List<?> list, JPanel up) {
            up.removeAll();
            up.setLayout(null);
            List<Object> aux_list = new ArrayList<>(list);

            // add search bar onto the screen
            JTextField searchBar = new JTextField();
            searchBar.setBounds(200, 20, 250, 20);
            up.add(searchBar);

            // add text label
            JLabel searchLabel = new JLabel("Search by name");
            searchLabel.setBounds(30, 20, 250, 20);
            searchLabel.setFont(new Font("Segoe UI", Font.BOLD & Font.ITALIC, 15));
            up.add(searchLabel);

            // add search button
            JButton searchButton = new JButton("Search");
            searchButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
            searchButton.setBounds(460, 20, 150, 20);
            searchButton.addMouseListener(new CustomMouseAdapter());

            // add under the search bar options to select only certain types
            JCheckBox actors = new JCheckBox("Actors");
            actors.setFont(new Font("Segoe UI", Font.BOLD, 14));
            actors.setBounds(30, 50, 150, 40);
            actors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // add or remove all the actors
                if (actors.isSelected()) {
                    aux_list.addAll(IMDB.actor_list);
                } else {
                    aux_list.removeAll(IMDB.actor_list);
                }
            }
        });
            up.add(actors);

            JCheckBox movies = new JCheckBox("Movies");
            movies.setFont(new Font("Segoe UI", Font.BOLD, 14));
            movies.setBounds(200, 50, 150, 40);
            movies.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    // make a list with all the movies
                    List<Object> list_movies = new ArrayList<>();
                    for (Object obj : IMDB.production_list) {
                        if (obj instanceof Movie) {
                            list_movies.add(obj);
                        }
                    }
                    // add or remove all the actors
                    if (movies.isSelected()) {
                        aux_list.addAll(list_movies);
                    } else {
                        aux_list.removeAll(list_movies);
                    }
                }
            });
            up.add(movies);

            JCheckBox series = new JCheckBox("Series");
            series.setFont(new Font("Segoe UI", Font.BOLD, 14));
            series.setBounds(360, 50, 150, 40);
            series.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // make a list with all the movies
                List<Object> list_series = new ArrayList<>();
                for (Object obj : IMDB.production_list) {
                    if (obj instanceof Series) {
                        list_series.add(obj);
                    }
                }
                // add or remove all the actors
                if (series.isSelected()) {
                    aux_list.addAll(list_series);
                } else {
                    aux_list.removeAll(list_series);
                }
            }
        });
            up.add(series);

            // based on the objects from the initial list check the buttons
            for (Object obj : list) {
                if (obj instanceof Movie)
                    movies.setSelected(true);
                else if (obj instanceof Series)
                    series.setSelected(true);
                else if (obj instanceof Actor)
                    actors.setSelected(true);
                else
                    System.out.println("Error unknown type in function custom_search");
            }
            // add action listener
            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<Object> new_list = new ArrayList<>();
                    if (searchBar.getText().isEmpty()) {
                        // if no text has been entered
                        displayList(aux_list, "CUSTOM SEARCH");
                        return;
                    }

                    // add into the new list only the elements that match the search
                    for (Object object : aux_list) {
                        if (return_name(object).toLowerCase().contains(searchBar.getText().toLowerCase()))
                            new_list.add(object);
                    }
                    displayList(new_list, "CUSTOM SEARCH");
                }
            });
            up.add(searchButton);

            up.repaint();
    }

    public static void search() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.removeAll();
        panel_center.setLayout(null);

        // start with an empty list
        List<Object> aux = new ArrayList<>();
        save_search_list = aux;

        List<Object> list = new ArrayList<>();

        displayList(list, "CUSTOM SEARCH");

        panel_center.repaint();
    }

    public static void create_a_request() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.setLayout(null);

        JLabel text = new JLabel("Select the type of request: ");
        text.setFont(new Font("Segoe UI", Font.BOLD, 16));
        text.setBounds(100, 80, 220, 30);
        panel_center.add(text);

        JButton showPopupMenuButton = new JButton("None");
        showPopupMenuButton.setBounds(340, 80, 150, 30);

        // add the buttons
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem button1 = new JMenuItem("MOVIE_ISSUE");
        JMenuItem button2 = new JMenuItem("ACTOR_ISSUE");
        JMenuItem button3 = new JMenuItem("DELETE_ACCOUNT");
        JMenuItem button4 = new JMenuItem("OTHERS");

        // add action listeners to the menu items
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel_center.repaint();
                showPopupMenuButton.setText("MOVIE_ISSUE");
                panel_center.repaint();
            }

        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel_center.repaint();
                showPopupMenuButton.setText("ACTOR_ISSUE");
                // add the list of actors
                panel_center.repaint();
            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //create_a_request();
                showPopupMenuButton.setText("DELETE_ACCOUNT");
                panel_center.repaint();
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //create_a_request();
                showPopupMenuButton.setText("OTHERS");
                panel_center.repaint();
            }
        });

        // Add menu items to the popup menu
        popupMenu.add(button1);
        popupMenu.add(button2);
        popupMenu.add(button3);
        popupMenu.add(button4);

        // Add action listener to the button to show the popup menu
        showPopupMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(showPopupMenuButton, 0, showPopupMenuButton.getHeight());
            }
        });

        panel_center.add(showPopupMenuButton);

        // add other fields for the description
        JLabel text2 = new JLabel("Description");
        text2.setFont(new Font("Segoe UI", Font.BOLD, 17));
        text2.setBounds(100, 200, 100, 30);
        panel_center.add(text2);

        JTextArea descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        descriptionTextArea.setBounds(200, 200, 300, 100);

        JButton createRequest = new JButton("Create request");
        createRequest.setBounds(200, 400, 300, 80);
        createRequest.setFont(new Font("Segoe UI", Font.ITALIC, 17));
        createRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (descriptionTextArea.getText().isEmpty())
                    return;
                if (showPopupMenuButton.getText().equals("None"))
                    return;
                String description = description = descriptionTextArea.getText();
                String to;

                if (showPopupMenuButton.getText().equals("MOVIE_ISSUE")) {
                    String title = "";
                    int number = 0;

                    for (Production production : IMDB.production_list) {
                        if (description.toLowerCase().contains(production.title.toLowerCase())) {
                            number++;
                            //to  =
                        }
                    }
                    if (title.equals(""))
                        return;
                } else if (showPopupMenuButton.getText().equals("ACTOR_ISSUE")) {
                    String title = "";
                    int number = 0;
                    for (Actor actor : IMDB.actor_list) {
                        if (description.toLowerCase().contains(actor.name.toLowerCase())) {
                            number++;
                            title = actor.name;
                        }
                    }

                    if (number >= 2)

                    if (title.equals(""))
                        return;
                }


                // TODO create request
                /*
                Request request = new Request(description,
                                                user.username,
                                                )*/
            }
        });
        panel_center.add(createRequest);

        JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextArea);
        descriptionScrollPane.setBounds(200, 200, 300, 100);

        panel_center.add(descriptionScrollPane);
        panel_center.repaint();
    }

    public static void delete_user() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.setLayout(null);

        // save the initial list
        save_search_list = IMDB.user_list;
        displayList(IMDB.user_list, "SEARCH");

        panel_center.repaint();
    }

    public static void view_favorites() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.setLayout(null);

        List<?> list = new ArrayList<>(user.favorites);
        // save the initial list
        save_search_list = list;
        displayList(list, "SEARCH");

        panel_center.repaint();
    }

    public static void add_user() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.removeAll();
        panel_center.setLayout(null);

        AtomicInteger account_type_number = new AtomicInteger(0);

        // name field
        JLabel text = new JLabel("Account type: ");
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setBounds(100, 200, 150, 30);
        panel_center.add(text);

        JButton account_type = new JButton("None");
        account_type.setBounds(250, 200, 150, 30);
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Regular");
        JMenuItem menuItem2 = new JMenuItem("Contributor");
        JMenuItem menuItem3 = new JMenuItem("Admin");
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        popupMenu.add(menuItem3);
        menuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                account_type.setText("Regular");
                account_type_number.set(1);
            }
        });
        menuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                account_type.setText("Contributor");
                account_type_number.set(2);
            }
        });
        menuItem3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                account_type.setText("Admin");
                account_type_number.set(3);
            }
        });
        panel_center.add(account_type);
        account_type.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the popup menu at the button's location
                popupMenu.show(account_type, 0, account_type.getHeight());
            }
        });

        // name field
        text = new JLabel("Name: ");
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setBounds(100, 80, 150, 30);
        panel_center.add(text);

        // country field
        text = new JLabel("Country: ");
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setBounds(100, 120, 150, 30);
        panel_center.add(text);

        // gender field
        text = new JLabel("Gender: ");
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setBounds(100, 160, 150, 30);
        panel_center.add(text);

        // gender field
        text = new JLabel("Age: ");
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setBounds(100, 240, 150, 30);
        panel_center.add(text);

        // email field
        text = new JLabel("Email: ");
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setBounds(100, 280, 150, 30);
        panel_center.add(text);

        // birthday field
        text = new JLabel("Birthday: ");
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setBounds(100, 320, 150, 30);
        panel_center.add(text);

        JTextField name = new JTextField();
        name.setBounds(250, 80, 150, 30);
        panel_center.add(name);

        JTextField country = new JTextField();
        country.setBounds(250, 120, 150, 30);
        panel_center.add(country);

        JTextField gender = new JTextField();
        gender.setBounds(250, 160, 150, 30);
        panel_center.add(gender);

        JTextField age = new JTextField();
        age.setBounds(250, 240, 150, 30);
        panel_center.add(age);

        JTextField email = new JTextField();
        email.setBounds(250, 280, 150, 30);
        panel_center.add(email);

        JTextField birthday = new JTextField();
        birthday.setBounds(250, 320, 150, 30);
        birthday.setText("dd.mm.yyyy");// specify the format
        birthday.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (birthday.getText().equals("dd.mm.yyyy")) {
                    birthday.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (birthday.getText().isEmpty()) {
                    birthday.setText("dd.mm.yyyy");
                }
            }
        });
        panel_center.add(birthday);

        // add user button
        JButton addUserButton = new JButton("Add User");
        addUserButton.setBounds(250, 400, 120, 30);
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (account_type_number.get() != 0 && !name.getText().isEmpty() && !country.getText().isEmpty() &&
                        !gender.getText().isEmpty() && !age.getText().isEmpty() && isNumber(age.getText(), 10, 140) != -1 &&
                        !birthday.getText().isEmpty() && isDOB(birthday.getText(), "dd.MM.yyyy") != null) {
                    String account = "";

                    System.out.println("creat account");

                    switch (account_type_number.get()) {
                        case 1:
                            account = "Regular";
                            break;
                        case 2:
                            account = "Contributor";
                            break;
                        case 3:
                            account = "Admin";
                            break;
                    }
                    // create a new user object using factory design pattern
                    User new_user = UserFactory.factory(User.returnEnum(account));
                    new_user.setExperience(0);
                    new_user.setTip(account);
                    new_user.username = User.generateUsername(name.getText());

                    Credentials cred = new Credentials(email.getText(),
                                                        User.generatePassword(name.getText()));
                    // set information
                     new_user.setInformation(cred,
                                            name.getText(),
                                            country.getText(),
                                            isNumber(age.getText(), 10, 140),
                                            gender.getText(),
                                            isDOB(birthday.getText(), "dd.MM.yyyy"));


                    // set credentials
                    new_user.setCredentials(cred);
                    IMDB.user_list.add(new_user);
                } else {
                    showMessageDialog(null, "The information you entered is not correct!");
                }
            }
        });

        panel_center.add(addUserButton);
        panel_center.repaint();
    }

    public static void add_production() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.removeAll();
        panel_center.setLayout(null);

        // the button to add a production
        JButton addProduction = new JButton("Add Production");

        JLabel text = new JLabel("Type");
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setBounds(30, 40, 150, 40);
        panel_center.add(text);

        // Create a button with a dropdown menu
        JButton menuButton = new JButton("Select Type");
        JPopupMenu popupMenu = new JPopupMenu();

        // Create menu items
        JMenuItem op1 = new JMenuItem("Movie");
        JMenuItem op2 = new JMenuItem("Series");

        JLabel num_f_seasons_label = new JLabel("Number of seasons");
        JTextField num_of_seasons = new JTextField();
        JButton set_seasons = new JButton("Add seasons");

        // list of the text areas for the seasons
        List<JTextArea> seasons = new ArrayList<>();

        JLabel duration_label = new JLabel("Duration: ");
        duration_label.setBounds(400, 40, 140, 40);
        duration_label.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JTextField duration = new JTextField();
        duration.setBounds(500, 40, 150, 40);
        duration.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JTextArea text_label = new JTextArea("For each season enter in \nthe box on each \nline: episodeName/duration");
        text_label.setEditable(false);
        text_label.setLineWrap(true);
        text_label.setWrapStyleWord(true);
        text_label.setBounds(440, 240, 200, 60);
        text_label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        op1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuButton.setText("Movie");
                addProduction.setText("Add movie");
                panel_center.add(duration);
                panel_center.add(duration_label);

                panel_center.remove(text_label);
                panel_center.remove(num_f_seasons_label);
                panel_center.remove(num_of_seasons);
                panel_center.remove(set_seasons);

                panel_center.validate();
                panel_center.repaint();
            }
        });
        op2.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Assuming menuButton and addProduction are correctly initialized
                menuButton.setText("Series");
                addProduction.setText("Add series");
                panel_center.remove(duration_label);
                panel_center.remove(duration);

                // Assuming panel_center is correctly set up and has a layout manager
                // show the number of seasons field
                num_f_seasons_label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                num_f_seasons_label.setBounds(420, 120, 150, 40);
                panel_center.add(num_f_seasons_label);

                num_of_seasons.setFont(new Font("Segoe UI", Font.BOLD, 13));
                num_of_seasons.setBounds(550, 120, 80, 40);
                panel_center.add(num_of_seasons);

                set_seasons.setFont(new Font("Segoe UI", Font.BOLD, 13));
                set_seasons.setBounds(520, 160, 150, 50);
                panel_center.add(text_label);
                set_seasons.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(() -> {
                            int n = isNumber(num_of_seasons.getText(), 1, 300);
                            if (n == -1)
                                return;

                            // remove all from the list
                            seasons.clear();

                            JPanel textAreasPanel = new JPanel(new GridLayout(n, 1));

                            // Create and add n text areas to the panel
                            for (int i = 0; i < n; i++) {
                                JTextArea textArea = new JTextArea(5, 20);
                                textArea.setLineWrap(true);
                                textArea.setWrapStyleWord(true);
                                seasons.add(textArea);
                                JScrollPane aux = new JScrollPane(textArea);
                                aux.setPreferredSize(new Dimension(200, 100));  // Set preferred size
                                textAreasPanel.add(aux);
                            }

                            // Create a JScrollPane and add the panel with text areas to it
                            JScrollPane scrollPane = new JScrollPane(textAreasPanel);
                            scrollPane.setPreferredSize(new Dimension(300, 500));  // Set preferred size

                            // Show the JOptionPane with the scrollable area
                            JOptionPane.showMessageDialog(null, scrollPane, "Set Seasons", JOptionPane.PLAIN_MESSAGE);
                        });
                    }
                });
                panel_center.add(set_seasons);

                // Assuming panel_center needs to be validated and repainted
                panel_center.validate();
                panel_center.repaint();
            }
        });

        // Add menu items to the popup menu
        popupMenu.add(op1);
        popupMenu.add(op2);

        // Attach the popup menu to the button
        menuButton.setComponentPopupMenu(popupMenu);

        // Add ActionListener to show the popup menu when the button is clicked
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(menuButton, 0, menuButton.getHeight());
            }
        });

        // Set the bounds for the button
        menuButton.setBounds(150, 40, 150, 40);

        // Add the button to the panel
        panel_center.add(menuButton);

        JLabel title_label = new JLabel("Title");
        title_label.setBounds(30, 80, 130, 40);
        title_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel_center.add(title_label);

        JTextField title = new JTextField();
        title.setBounds(150, 80, 150, 40);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel_center.add(title);

        JLabel plot_label = new JLabel("Plot");
        plot_label.setBounds(30, 120, 130, 40);
        plot_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel_center.add(plot_label);

        JTextArea plot = new JTextArea();
        plot.setBounds(150, 120, 250, 50);
        plot.setLineWrap(true);
        plot.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane plotScrollPane = new JScrollPane(plot);
        plotScrollPane.setBounds(150, 120, 250, 50);
        panel_center.add(plotScrollPane);

        JLabel directors_label = new JLabel("Directors");
        directors_label.setBounds(30, 180, 140, 40);
        directors_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel_center.add(directors_label);

        JLabel directors_label_2 = new JLabel("Use ', ' to separate");
        directors_label_2.setBounds(30, 200, 140, 40);
        directors_label_2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel_center.add(directors_label_2);

        JTextArea directors = new JTextArea();
        directors.setBounds(150, 190, 250, 130);
        directors.setLineWrap(true);
        directors.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel_center.add(directors);

        JLabel actors_label = new JLabel("Actors");
        actors_label.setBounds(30, 330, 140, 40);
        actors_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel_center.add(actors_label);

        JLabel actors_label_2 = new JLabel("Use ', ' to separate");
        actors_label_2.setBounds(30, 350, 140, 40);
        actors_label_2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel_center.add(actors_label_2);

        JTextArea actors = new JTextArea();
        actors.setBounds(150, 340, 250, 130);
        actors.setLineWrap(true);
        actors.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel_center.add(actors);

        JButton genre = new JButton("Add genre");
        genre.setBounds(30, 500, 120, 40);
        genre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel_center.add(genre);

        JPopupMenu popupMenuGenre = new JPopupMenu();
        Production.Genre[] allGenres = Production.Genre.values();

        JTextArea genre_area = new JTextArea();

        for (Production.Genre aux : allGenres) {
            JMenuItem option = new JMenuItem(aux.toString());

            option.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (genre_area.getText().contains(aux.toString()) == false)
                        genre_area.setText(genre_area.getText().concat(aux.toString() + " | "));
                }
            });
            popupMenuGenre.add(option);
        }

        genre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the popup menu at the button's location
                popupMenuGenre.show(genre, 0, genre.getHeight());
            }
        });

        JButton delete_genre = new JButton("Delete genre");
        delete_genre.setBounds(30, 550, 120, 40);
        delete_genre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        delete_genre.addMouseListener(new CustomMouseAdapter());
        delete_genre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = genre_area.getText();
                // Split the input string by '|'
                String[] words = input.split(" \\| ");

                // Check if there is at least one word before deleting
                if (words.length > 0) {
                    // Reconstruct the string without the last word and the trailing '|'
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < words.length - 1; i++) {
                        result.append(words[i]);
                        if (i < words.length - 1) {
                            result.append(" | ");
                        }
                    }
                    genre_area.setText(result.toString());
                } else {
                    // If there's only one word or the input is empty, return the original string
                    genre_area.setText(input);
                }
            }
        });
        panel_center.add(delete_genre);

        genre_area.setBounds(150, 500, 250, 130);
        genre_area.setFont(new Font("Segoe UI", Font.BOLD, 13));
        genre_area.setLineWrap(true);
        genre_area.setEditable(false);
        genre_area.addMouseListener(new CustomMouseAdapter());
        panel_center.add(genre_area);

        JLabel year_label = new JLabel("Release year: ");
        year_label.setBounds(400, 80, 140, 40);
        year_label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel_center.add(year_label);

        JTextField year = new JTextField();
        year.setBounds(500, 80, 150, 40);
        year.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel_center.add(year);

        addProduction.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addProduction.setBounds(430, 400, 200, 80);
        addProduction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check if a field is empty
                if (title.getText().isEmpty() || plot.getText().isEmpty())
                    return;

                // check the type
                if (menuButton.getText().equals("Select Type") == true)
                    return;

                // check the actors and directors
                if (actors.getText().isEmpty() || directors.getText().isEmpty())
                    return;

                String chars = "1234567890-=[]{}'|;:.?/<>~!@#$%^&*()";
                String actorText = actors.getText();
                String directorText = directors.getText();

                // check for valid input
                for (int i = 0; i < chars.length(); i++) {
                    char aux = chars.charAt(i);
                    if (actorText.contains(String.valueOf(aux)) ||
                            directorText.contains(String.valueOf(aux))) {
                        return;
                    }
                }
                // check for genre
                if (genre_area.getText().isEmpty())
                    return;
                int release_year = isNumber(year.getText(), 1500, 2100);
                if (year.getText().isEmpty() || release_year == -1)
                    return;


                // get the actor and the director
                String[] actor_list = actorText.split(", ");
                String[] director_list = directorText.split(", ");
                String[] genre_list = genre_area.getText().split(" | ");

                // verify based on the type
                if (menuButton.getText().equals("Movie")) {
                    // check for duration
                    if (duration.getText().isEmpty() || duration.getText().contains("minute") == false)
                        return;

                    // create the movie object
                    Movie movie = new Movie();
                    movie.title = title.getText();
                    movie.plot = plot.getText();
                    movie.releaseYear = release_year;
                    movie.duration = duration.getText();
                    movie.averageRating = 0;
                    movie.type = "Movie";

                    for (String string : actor_list) {
                        movie.actors.add(string);
                    }
                    for (String string : director_list) {
                        movie.directors.add(string);
                    }
                    for (String string : genre_list) {
                        if (string.contains("|") == false && string.contains(" ") == false) {
                            movie.genre.add(Production.Genre.valueOf(string));
                        }
                    }
                    IMDB.production_list.add(movie);

                    // add the movie into the user's contribution list
                    ((Staff) user).addProductionSystem(movie);
                } else if (menuButton.getText().equals("Series")) {
                    if (isNumber(num_of_seasons.getText(), 1, 300) == -1)
                        return;
                    int number = isNumber(num_of_seasons.getText(), 1, 300);

                    Series series = new Series();
                    // create the series object
                    series.title = title.getText();
                    series.plot = plot.getText();
                    series.releaseYear = release_year;
                    series.averageRating = 0;
                    series.numberSeasons = number;
                    series.type = "Series";

                    for (String string : actor_list) {
                        series.actors.add(string);
                    }
                    for (String string : director_list) {
                        series.directors.add(string);
                    }
                    for (String string : genre_list) {
                        if (string.contains("|") == false && string.contains(" ") == false) {
                            series.genre.add(Production.Genre.valueOf(string));
                        }
                    }

                    // for each season box check if empty
                    for (int i = 0; i < number; i++) {
                        if (seasons.get(i).getText().isEmpty())
                            return;
                        else {
                            // if it is not empty add it
                            String[] episodes = seasons.get(i).getText().split("\n");

                            List<Episode> list = new ArrayList<>();

                            for (String string : episodes) {
                                String[] ep_string = string.split("/");
                                System.out.println();
                                list.add(new Episode(ep_string[0], ep_string[1]));
                            }

                            series.addEpisodes("Season " + (i + 1), list);
                        }
                    }
                    IMDB.production_list.add(series);

                    // add the movie into the user's contribution list
                    ((Staff) user).addProductionSystem(series);
                }

                manage_up_panel();
                // in the end call the same function to clean up
                add_production();
            }
        });
        panel_center.add(addProduction);

        // Repaint the panel
        panel_center.repaint();
    }

    public static void add_actor() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.removeAll();
        panel_center.setLayout(null);

        // label for the name
        JLabel name_label = new JLabel("Enter the name");
        name_label.setBounds(100, 30, 150, 30);
        name_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel_center.add(name_label);

        JTextField name = new JTextField();
        name.setBounds(300, 30, 250, 30);
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel_center.add(name);

        // label for the biography
        JLabel bio_label = new JLabel("Biography");
        bio_label.setBounds(100, 70, 150, 30);
        bio_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel_center.add(bio_label);

        JTextArea bio = new JTextArea();
        bio.setLineWrap(true);
        bio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bio.setBounds(300, 70, 300, 150);
        bio.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> checkTextLength(e.getDocument()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> checkTextLength(e.getDocument()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> checkTextLength(e.getDocument()));
            }

            private void checkTextLength(Document document) {
                int maxLength = 300; // Set your desired maximum length
                if (document.getLength() > maxLength) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            document.remove(maxLength, document.getLength() - maxLength);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        });
        panel_center.add(bio);

        // add other fields for the description
        JLabel text2 = new JLabel("Add performances (one per line)");
        text2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text2.setBounds(30, 250, 250, 30);  // Adjusted the Y position
        panel_center.add(text2);

        text2 = new JLabel("Format: name, type (Movie/Series)");
        text2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text2.setBounds(30, 300, 250, 30);  // Adjusted the Y position
        panel_center.add(text2);

        JTextArea performances = new JTextArea();
        performances.setLineWrap(true);
        performances.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane1 = new JScrollPane(performances);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane1.setBounds(300, 250, 250, 300);  // Adjusted the Y position
        panel_center.add(scrollPane1);

        // add create actor button
        JButton addActor = new JButton("Add actor");
        addActor.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addActor.setBounds(90, 440, 150, 50);
        addActor.addMouseListener(new CustomMouseAdapter());
        addActor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (performances.getText().isEmpty() || bio.getText().isEmpty() || name.getText().isEmpty())
                    return;

                Actor actor = new Actor();
                actor.name = name.getText();
                actor.biography = bio.getText();

                String text = performances.getText();
                String[] lines = text.split("\\n");

                for (String line : lines) {
                    String[] words = line.split(", ");

                    int index = 1;
                    String name = "", type = "";
                    for (String word : words) {
                        if (index == 1)
                            name = word;
                        if (index == 2)
                            type = word;
                        if (index == 2 && (word.equals("Movie") == false && word.equals("Series") == false)) {
                            System.out.println("wrong format");
                            return;
                        }
                        index++;
                    }
                    if (index != 3) {
                        System.out.println("Not enough numbers");
                        return;
                    }

                    actor.add(name, type);
                }
                IMDB.actor_list.add(actor);

                // add this user as the one that is responsible
                ((Staff) user).addActorSystem(actor);
                manage_up_panel();
                add_actor();
            }
        });
        panel_center.add(addActor);

        panel_center.repaint();
    }

    public static void delete_from_system() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.removeAll();
        panel_center.setLayout(null);

        // display only the productions/actors that this user can delete
        List<?> list = new ArrayList<>(((Staff)user).getUserCollection());

        // save the initial list
        save_search_list = list;
        displayList(list, "SEARCH");

        panel_center.repaint();
    }

    public static void view_reviews() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.removeAll();
        panel_center.setLayout(null);

        // display only the productions/actors that this user can delete
        List<Object> list = new ArrayList<>();

        for (Production production : IMDB.production_list) {
            for (Rating rating : production.getRating()) {
                if (rating.getUsername().equals(user.username))
                    list.add(rating);
            }
        }

        for (Actor actor : IMDB.actor_list) {
            for (Rating rating : actor.getRating()) {
                if (rating.getUsername().equals(user.username))
                    list.add(rating);
            }
        }

        // save the initial list
        save_search_list = list;
        displayList(list, "SEARCH");

        panel_center.repaint();
    }

    public static void update_production() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.removeAll();
        panel_center.setLayout(null);

        List<Object> list = new ArrayList<>();
        for (Object object : ((Staff) user).getUserCollection()) {
            if (object instanceof Production)
                list.add(object);
        }
        // save the initial list
        save_search_list = list;

        panel_center.removeAll();

        // split the panel
        JPanel up = new JPanel();
        JPanel down = new JPanel();

        // create a JSplitPane and set its properties
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, up, down);
        splitPane.setDividerLocation(100);

        // set both sides of the split to be non-resizable
        splitPane.setResizeWeight(0.5); // Equal size for both sides
        splitPane.setEnabled(false);

        // add the JSplitPane to the central panel
        panel_center.setLayout(new BorderLayout());
        panel_center.add(splitPane, BorderLayout.CENTER);

        // call function to add into the upper part of the screen based on type
        displaySearch(list, up);

        // add into the down panel the list of buttons
        down.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        down.setPreferredSize(new Dimension(200, (list.size() + 1) * (heigth_button + 3)));

        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();

            // get the name and photo
            List<Object> aux = return_info_for_display(element);
            JButton button = new JButton(aux.get(0).toString());
            button.setPreferredSize(new Dimension(600, heigth_button));

            // add an action listener
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // clear the center panel
                    clear_panel(panel_center);
                    panel_center.removeAll();
                    panel_center.setLayout(null);

                    // the button to add a production
                    JButton modifyProduction = new JButton("Modify Production");

                    JLabel text = new JLabel("Type");
                    text.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    text.setBounds(30, 40, 150, 40);
                    panel_center.add(text);

                    // Create a button with a dropdown menu
                    JButton menuButton = new JButton("Select Type");
                    JPopupMenu popupMenu = new JPopupMenu();

                    JLabel num_f_seasons_label = new JLabel("Number of seasons");
                    JTextField num_of_seasons = new JTextField();
                    JButton set_seasons = new JButton("Add seasons");

                    // list of the text areas for the seasons
                    List<JTextArea> seasons = new ArrayList<>();

                    JLabel duration_label = new JLabel("Duration: ");
                    duration_label.setBounds(400, 40, 140, 40);
                    duration_label.setFont(new Font("Segoe UI", Font.BOLD, 13));

                    JTextField duration = new JTextField();
                    duration.setBounds(500, 40, 150, 40);
                    duration.setFont(new Font("Segoe UI", Font.BOLD, 13));

                    JTextArea text_label = new JTextArea("For each season enter in \nthe box on each \nline: episodeName/duration");
                    text_label.setEditable(false);
                    text_label.setLineWrap(true);
                    text_label.setWrapStyleWord(true);
                    text_label.setBounds(440, 240, 200, 60);
                    text_label.setFont(new Font("Segoe UI", Font.BOLD, 14));

                    if (element instanceof Movie) {
                        menuButton.setText("Movie");
                        modifyProduction.setText("Modify movie");
                        panel_center.add(duration);
                        panel_center.add(duration_label);

                        panel_center.remove(text_label);
                        panel_center.remove(num_f_seasons_label);
                        panel_center.remove(num_of_seasons);
                        panel_center.remove(set_seasons);

                        panel_center.validate();
                        panel_center.repaint();
                        }
                    if (element instanceof Series) {
                        // Assuming menuButton and addProduction are correctly initialized
                        menuButton.setText("Series");
                        modifyProduction.setText("Modify series");
                        panel_center.remove(duration_label);
                        panel_center.remove(duration);

                        // Assuming panel_center is correctly set up and has a layout manager
                        // show the number of seasons field
                        num_f_seasons_label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        num_f_seasons_label.setBounds(420, 120, 150, 40);
                        panel_center.add(num_f_seasons_label);

                        num_of_seasons.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        num_of_seasons.setBounds(550, 120, 80, 40);
                        num_of_seasons.setText(((Series) element).numberSeasons + "");
                        panel_center.add(num_of_seasons);

                        set_seasons.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        set_seasons.setBounds(520, 160, 150, 50);
                        panel_center.add(text_label);
                        set_seasons.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    SwingUtilities.invokeLater(() -> {
                                        int n = isNumber(num_of_seasons.getText(), 1, 300);
                                        if (n == -1)
                                            return;

                                        // remove all from the list
                                        seasons.clear();

                                        JPanel textAreasPanel = new JPanel(new GridLayout(n, 1));

                                        // Create and add n text areas to the panel
                                        for (int i = 0; i < n; i++) {
                                            JTextArea textArea = new JTextArea(5, 20);
                                            textArea.setLineWrap(true);
                                            textArea.setWrapStyleWord(true);
                                            seasons.add(textArea);
                                            JScrollPane aux = new JScrollPane(textArea);
                                            aux.setPreferredSize(new Dimension(200, 100));  // Set preferred size
                                            textAreasPanel.add(aux);
                                        }
                                        int index = 0;
                                        String str;
                                        // set the episodes
                                        for (Map.Entry<String, List<Episode>> entry : ((Series) element).getSeasons().entrySet()) {
                                            List<Episode> episodeList = entry.getValue();
                                            str = "";
                                            for (Episode episode : episodeList) {
                                                // put each pair on one line
                                                str += episode.getEpisodeName() + "/" + episode.getDuration() + "\n";
                                            }
                                            seasons.get(index).setText(str);
                                            index++;
                                        }

                                        // Create a JScrollPane and add the panel with text areas to it
                                        JScrollPane scrollPane = new JScrollPane(textAreasPanel);
                                        scrollPane.setPreferredSize(new Dimension(300, 500));  // Set preferred size

                                        // Show the JOptionPane with the scrollable area
                                        JOptionPane.showMessageDialog(null, scrollPane, "Set Seasons", JOptionPane.PLAIN_MESSAGE);
                                    });
                                }
                            });
                        panel_center.add(set_seasons);

                        // Assuming panel_center needs to be validated and repainted
                        panel_center.validate();
                        panel_center.repaint();
                    }

                    // Attach the popup menu to the button
                    menuButton.setComponentPopupMenu(popupMenu);

                    // Set the bounds for the button
                    menuButton.setBounds(150, 40, 150, 40);

                    // Add the button to the panel
                    panel_center.add(menuButton);

                    JLabel title_label = new JLabel("Title");
                    title_label.setBounds(30, 80, 130, 40);
                    title_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    panel_center.add(title_label);

                    JTextField title = new JTextField();
                    title.setBounds(150, 80, 150, 40);
                    title.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    title.setText(((Production) element).title);
                    panel_center.add(title);

                    JLabel plot_label = new JLabel("Plot");
                    plot_label.setBounds(30, 120, 130, 40);
                    plot_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    panel_center.add(plot_label);

                    JTextArea plot = new JTextArea();
                    plot.setBounds(150, 120, 250, 50);
                    plot.setLineWrap(true);
                    plot.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    plot.setText(((Production) element).plot);
                    JScrollPane plotScrollPane = new JScrollPane(plot);
                    plotScrollPane.setBounds(150, 120, 250, 50);
                    panel_center.add(plotScrollPane);

                    JLabel directors_label = new JLabel("Directors");
                    directors_label.setBounds(30, 180, 140, 40);
                    directors_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    panel_center.add(directors_label);

                    JLabel directors_label_2 = new JLabel("Use ', ' to separate");
                    directors_label_2.setBounds(30, 200, 140, 40);
                    directors_label_2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    panel_center.add(directors_label_2);

                    JTextArea directors = new JTextArea();
                    directors.setBounds(150, 190, 250, 130);
                    directors.setLineWrap(true);
                    directors.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    String str = "";
                    for (String aux : ((Production) element).directors) {
                        str += aux + ", ";
                    }

                    if (str.length() >= 2) {
                        // remove the last two characters
                        String aux = str.substring(0, str.length() - 2);
                        str = aux;
                    }
                    directors.setText(str);
                    panel_center.add(directors);

                    JLabel actors_label = new JLabel("Actors");
                    actors_label.setBounds(30, 330, 140, 40);
                    actors_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    panel_center.add(actors_label);

                    JLabel actors_label_2 = new JLabel("Use ', ' to separate");
                    actors_label_2.setBounds(30, 350, 140, 40);
                    actors_label_2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    panel_center.add(actors_label_2);

                    JTextArea actors = new JTextArea();
                    actors.setBounds(150, 340, 250, 130);
                    actors.setLineWrap(true);
                    actors.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    str = "";
                    for (String aux : ((Production) element).actors) {
                        str += aux + ", ";
                    }

                    if (str.length() >= 2) {
                        // remove the last two characters
                        String aux = str.substring(0, str.length() - 2);
                        str = aux;
                    }
                    actors.setText(str);
                    panel_center.add(actors);

                    JButton genre = new JButton("Add genre");
                    genre.setBounds(30, 500, 120, 40);
                    genre.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    panel_center.add(genre);

                    JPopupMenu popupMenuGenre = new JPopupMenu();
                    Production.Genre[] allGenres = Production.Genre.values();

                    JTextArea genre_area = new JTextArea();
                    str = "";
                    for (Production.Genre aux : ((Production) element).genre) {
                        str += aux.name() + " | ";
                    }
                    genre_area.setText(str);

                    for (Production.Genre aux : allGenres) {
                        JMenuItem option = new JMenuItem(aux.toString());

                        option.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (genre_area.getText().contains(aux.toString()) == false)
                                    genre_area.setText(genre_area.getText().concat(aux.toString() + " | "));
                            }
                        });
                        popupMenuGenre.add(option);
                    }

                    genre.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Show the popup menu at the button's location
                            popupMenuGenre.show(genre, 0, genre.getHeight());
                        }
                    });

                    JButton delete_genre = new JButton("Delete genre");
                    delete_genre.setBounds(30, 550, 120, 40);
                    delete_genre.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    delete_genre.addMouseListener(new CustomMouseAdapter());
                    delete_genre.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String input = genre_area.getText();
                            // Split the input string by '|'
                            String[] words = input.split(" \\| ");

                            // Check if there is at least one word before deleting
                            if (words.length > 0) {
                                // Reconstruct the string without the last word and the trailing '|'
                                StringBuilder result = new StringBuilder();
                                for (int i = 0; i < words.length - 1; i++) {
                                    result.append(words[i]);
                                    if (i < words.length - 1) {
                                        result.append(" | ");
                                    }
                                }
                                genre_area.setText(result.toString());
                            } else {
                                // If there's only one word or the input is empty, return the original string
                                genre_area.setText(input);
                            }
                        }
                    });
                    panel_center.add(delete_genre);

                    genre_area.setBounds(150, 500, 250, 130);
                    genre_area.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    genre_area.setLineWrap(true);
                    genre_area.setEditable(false);
                    genre_area.addMouseListener(new CustomMouseAdapter());
                    panel_center.add(genre_area);

                    JLabel year_label = new JLabel("Release year: ");
                    year_label.setBounds(400, 80, 140, 40);
                    year_label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    panel_center.add(year_label);

                    JTextField year = new JTextField();
                    year.setBounds(500, 80, 150, 40);
                    year.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    if (element instanceof Movie) {
                        year.setText(((Movie) element).releaseYear + "");
                        duration.setText(((Movie) element).duration + "");
                    } else if (element instanceof Series) {
                        year.setText(((Series) element).releaseYear + "");
                        num_of_seasons.setText(((Series) element).numberSeasons + "");
                    }
                    panel_center.add(year);

                    modifyProduction.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    modifyProduction.setBounds(430, 400, 200, 80);
                    modifyProduction.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // check if a field is empty
                            if (title.getText().isEmpty() || plot.getText().isEmpty())
                                return;

                            // check the type
                            if (menuButton.getText().equals("Select Type") == true)
                                return;

                            // check the actors and directors
                            if (actors.getText().isEmpty() || directors.getText().isEmpty())
                                return;

                            String chars = "1234567890-=[]{}'|;:.?/<>~!@#$%^&*()";
                            String actorText = actors.getText();
                            String directorText = directors.getText();

                            // check for valid input
                            for (int i = 0; i < chars.length(); i++) {
                                char aux = chars.charAt(i);
                                if (actorText.contains(String.valueOf(aux)) ||
                                        directorText.contains(String.valueOf(aux))) {
                                    return;
                                }
                            }
                            // check for genre
                            if (genre_area.getText().isEmpty())
                                return;
                            int release_year = isNumber(year.getText(), 1500, 2100);
                            if (year.getText().isEmpty() || release_year == -1)
                                return;

                            // get the actor and the director
                            String[] actor_list = actorText.split(", ");
                            String[] director_list = directorText.split(", ");
                            String[] genre_list = genre_area.getText().split(" | ");

                            // verify based on the type
                            if (menuButton.getText().equals("Movie")) {
                                // check for duration
                                if (duration.getText().isEmpty() || duration.getText().contains("minute") == false)
                                    return;

                                // create the movie object
                                Movie movie = ((Movie) element);
                                movie.title = title.getText();
                                movie.plot = plot.getText();
                                movie.releaseYear = release_year;
                                movie.duration = duration.getText();
                                movie.averageRating = 0;
                                movie.type = "Movie";

                                movie.actors.clear();
                                for (String string : actor_list) {
                                    movie.actors.add(string);
                                }
                                movie.directors.clear();
                                for (String string : director_list) {
                                    movie.directors.add(string);
                                }
                                movie.genre.clear();
                                for (String string : genre_list) {
                                    if (string.contains("|") == false && string.contains(" ") == false) {
                                        movie.genre.add(Production.Genre.valueOf(string));
                                    }
                                }
                            } else if (menuButton.getText().equals("Series")) {
                                if (isNumber(num_of_seasons.getText(), 1, 300) == -1)
                                    return;
                                int number = isNumber(num_of_seasons.getText(), 1, 300);

                                Series series = (Series) element;
                                // create the series object
                                series.title = title.getText();
                                series.plot = plot.getText();
                                series.releaseYear = release_year;
                                series.averageRating = 0;
                                series.numberSeasons = number;
                                series.type = "Series";

                                series.actors.clear();
                                for (String string : actor_list) {
                                    series.actors.add(string);
                                }
                                series.directors.clear();
                                for (String string : director_list) {
                                    series.directors.add(string);
                                }
                                series.genre.clear();
                                for (String string : genre_list) {
                                    if (string.contains("|") == false && string.contains(" ") == false) {
                                        series.genre.add(Production.Genre.valueOf(string));
                                    }
                                }
                                int i;
                                // the list of seasons isn't initialized until the user pressed add season
                                if (seasons.size() != 0) {
                                    for (i = 0; i < number; i++) {
                                        if (seasons.get(i).getText().isEmpty())
                                            return;
                                    }
                                    series.getSeasons().clear();

                                    // for each season box check if empty
                                    for (i = 0; i < number; i++) {

                                        // if it is not empty add it
                                        String[] episodes = seasons.get(i).getText().split("\n");

                                        List<Episode> list = new ArrayList<>();

                                        for (String string : episodes) {
                                            String[] ep_string = string.split("/");
                                            System.out.println();
                                            list.add(new Episode(ep_string[0], ep_string[1]));
                                        }

                                        series.addEpisodes("Season " + (i + 1), list);
                                    }
                                }
                            }

                            // in the end call the same function to clean up
                            update_production();
                        }
                    });
                    panel_center.add(modifyProduction);

                    // Repaint the panel
                    panel_center.repaint();
                }
            });
            button.addMouseListener(new CustomMouseAdapter());

            down.add(button);
        }

        // create a scroll pane and add the button panel to it
        JScrollPane scrollPane = new JScrollPane(down);

        // add the scrollPane to the JSplitPane
        splitPane.setBottomComponent(scrollPane);

        // revalidate and repaint the central panel
        panel_center.revalidate();
        panel_center.repaint();
    }

    // aux method to return a number
    public static int isNumber(String str, int min, int max) {
        try {
            int number = Integer.parseInt(str);
            if (min <= number && number <= max)
                return number;
            return -1;
        } catch (NumberFormatException e) {
            System.out.println("Not a number: " + str);
            return -1;
        }
    }

    // method that checks the input from the user for the DoB
    public static String isDOB(String str, String format) {
        try {
            SimpleDateFormat inputFormatter = new SimpleDateFormat(format);
            Date date = inputFormatter.parse(str);

            SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return outputFormatter.format(date);

        } catch (ParseException e) {
            System.out.println("Not a valid format for the date of birthday : " + str);
            return null; // Handle the exception as needed
        }
    }

    public static void view_requests() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.removeAll();
        panel_center.setLayout(null);

        // display only the requests that this user has created or received
        List<Object> list = new ArrayList<>();

        for (Request request : IMDB.request_list) {
            if (request.username.equals(user.username))
                list.add(request);
            else if (request.to.equals(user.username))
                list.add(request);
            else if (request.to.equals("ADMIN") && user instanceof Admin)
                list.add(request);
        }

        // save the initial list
        save_search_list = list;
        displayList(list, "SEARCH");

        panel_center.repaint();
    }

    public static void update_actor() {
        // clear the center panel
        clear_panel(panel_center);
        panel_center.removeAll();
        panel_center.setLayout(null);

        List<Object> list = new ArrayList<>();
        for (Object object : ((Staff) user).getUserCollection()) {
            if (object instanceof Actor)
                list.add(object);
        }
        // save the initial list
        save_search_list = list;

        panel_center.removeAll();

        // split the panel
        JPanel up = new JPanel();
        JPanel down = new JPanel();

        // create a JSplitPane and set its properties
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, up, down);
        splitPane.setDividerLocation(100);

        // set both sides of the split to be non-resizable
        splitPane.setResizeWeight(0.5); // Equal size for both sides
        splitPane.setEnabled(false);

        // add the JSplitPane to the central panel
        panel_center.setLayout(new BorderLayout());
        panel_center.add(splitPane, BorderLayout.CENTER);

        // call function to add into the upper part of the screen based on type
        displaySearch(list, up);


        // add into the down panel the list of buttons
        down.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        down.setPreferredSize(new Dimension(200, (list.size() + 1) * (heigth_button + 3)));

        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();

            // get the name and photo
            List<Object> aux = return_info_for_display(element);
            JButton button = new JButton(aux.get(0).toString());
            button.setPreferredSize(new Dimension(600, heigth_button));

            // add an action listener
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clear_panel(panel_center);
                    panel_center.removeAll();

                    Actor actor = (Actor) element;
                    // clear the center panel
                    clear_panel(panel_center);
                    panel_center.removeAll();
                    panel_center.setLayout(null);

                    // label for the name
                    JLabel name_label = new JLabel("Enter the name");
                    name_label.setBounds(100, 30, 150, 30);
                    name_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    panel_center.add(name_label);

                    JTextField name = new JTextField();
                    name.setBounds(300, 30, 250, 30);
                    name.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    name.setText(actor.name);
                    panel_center.add(name);

                    // label for the biography
                    JLabel bio_label = new JLabel("Biography");
                    bio_label.setBounds(100, 70, 150, 30);
                    bio_label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    panel_center.add(bio_label);

                    JTextArea bio = new JTextArea();
                    bio.setLineWrap(true);
                    bio.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    bio.setBounds(300, 70, 300, 150);
                    bio.setText(actor.biography);
                    bio.getDocument().addDocumentListener(new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            SwingUtilities.invokeLater(() -> checkTextLength(e.getDocument()));
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            SwingUtilities.invokeLater(() -> checkTextLength(e.getDocument()));
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                            SwingUtilities.invokeLater(() -> checkTextLength(e.getDocument()));
                        }

                        private void checkTextLength(Document document) {
                            int maxLength = 350; // Set your desired maximum length
                            if (document.getLength() > maxLength) {
                                SwingUtilities.invokeLater(() -> {
                                    try {
                                        document.remove(maxLength, document.getLength() - maxLength);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                });
                            }
                        }
                    });
                    panel_center.add(bio);

                    // add other fields for the description
                    JLabel text2 = new JLabel("Add performances (one per line)");
                    text2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    text2.setBounds(30, 250, 250, 30);  // Adjusted the Y position
                    panel_center.add(text2);

                    text2 = new JLabel("Format: name, type (Movie/Series)");
                    text2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    text2.setBounds(30, 300, 250, 30);  // Adjusted the Y position
                    panel_center.add(text2);

                    JTextArea performances = new JTextArea();
                    performances.setLineWrap(true);
                    performances.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    JScrollPane scrollPane1 = new JScrollPane(performances);
                    String str = "";
                    for (Map.Entry<String, String> entry : actor.performances) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        str += key + ", " + value + "\n";
                    }
                    performances.setText(str);
                    scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    scrollPane1.setBounds(300, 250, 250, 300);
                    panel_center.add(scrollPane1);

                    // add create actor button
                    JButton modify = new JButton("Modify");
                    modify.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    modify.setBounds(90, 440, 150, 50);
                    modify.addMouseListener(new CustomMouseAdapter());
                    modify.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (performances.getText().isEmpty() || bio.getText().isEmpty() || name.getText().isEmpty())
                                return;

                            actor.name = name.getText();
                            actor.biography = bio.getText();
                            actor.performances.clear();

                            String text = performances.getText();
                            String[] lines = text.split("\\n");

                            for (String line : lines) {
                                String[] words = line.split(", ");

                                int index = 1;
                                String name = "", type = "";
                                for (String word : words) {
                                    if (index == 1)
                                        name = word;
                                    if (index == 2)
                                        type = word;
                                    if (index == 2 && (word.equals("Movie") == false && word.equals("Series") == false)) {
                                        System.out.println("wrong format");
                                        return;
                                    }
                                    index++;
                                }
                                if (index != 3) {
                                    System.out.println("Not enough numbers");
                                    return;
                                }

                                actor.add(name, type);
                            }
                            update_actor();
                        }
                    });
                    panel_center.add(modify);

                    panel_center.revalidate();
                    panel_center.repaint();
                }
            });
            button.addMouseListener(new CustomMouseAdapter());

            down.add(button);
        }

        // create a scroll pane and add the button panel to it
        JScrollPane scrollPane = new JScrollPane(down);

        // add the scrollPane to the JSplitPane
        splitPane.setBottomComponent(scrollPane);

        // revalidate and repaint the central panel
        panel_center.revalidate();
        panel_center.repaint();
    }

    public static void exit() {
        IMDB.running = false;
        if (main_frame != null) {
            main_frame.dispose();
        }
        System.exit(0);
    }

    public static void logout() {
        IMDB.login = false;
        if (main_frame != null) {
            main_frame.dispose();
        }
        open_imdb_window();
    }

    // class to change the cursor when it is hoovering over a button
    static class CustomMouseAdapter extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            ((Component) e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            ((Component) e.getSource()).setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    // ActionListener implementation with a constructor parameter to identify the button
    static class ButtonListener implements ActionListener {
        private String operation;

        public ButtonListener(String operation) {
            this.operation = operation;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            // Perform different actions based on the buttonNumber
            switch (operation) {
                case "View all":
                    view_all();
                    break;
                case "Search":
                    search();
                    break;
                case "Create a request":
                    create_a_request();
                    break;
                case "Delete from system":
                    delete_from_system();
                    break;
                case "Add production":
                    add_production();
                    break;
                case "Add actor":
                    add_actor();
                    break;
                case "View favorites":
                    view_favorites();
                    break;
                case "Update Prod. details":
                    update_production();
                    break;
                case "View requests":
                    view_requests();
                    break;
                case "Add user":
                    add_user();
                    break;
                case "My reviews":
                    view_reviews();
                    break;
                case "Update Actor details":
                    update_actor();
                    break;
                case "Delete user":
                    delete_user();
                    break;
                case "Logout":
                    logout();
                    break;
                case "Exit":
                    exit();
                    break;
                default:
                    break;
            }
        }
    }
}
