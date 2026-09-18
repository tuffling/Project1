import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    private JTextField idField;
    private JTextField titleField;
    private JTextField mentorField;
    private JTextField dateField;
    private JTextField locationField;
    private JTextField maxField;

    private JTextArea outputArea;

    // One mentorship session. The participant count must be mutable for Register.
    public static class Session {
        private final int id;
        private final String title;
        private final String mentor;
        private final String date;
        private final String location;
        private int participants;
        private final int maxParticipants;

        public Session(int id, String title, String mentor, String date,
                       String location, int maxParticipants) {
            this.id = id;
            this.title = title;
            this.mentor = mentor;
            this.date = date;
            this.location = location;
            this.participants = 0;
            this.maxParticipants = maxParticipants;
        }

        public int id() {
            return id;
        }

        public String title() {
            return title;
        }

        public String mentor() {
            return mentor;
        }

        public String date() {
            return date;
        }

        public String location() {
            return location;
        }

        public int participants() {
            return participants;
        }

        public int maxParticipants() {
            return maxParticipants;
        }

        public void registerParticipant() {
            participants++;
        }
    }

    // One node in the linked list of sessions.
    public record SessionNode(Session first, SessionNode rest) {}

    private SessionNode sessions;

    public MainGUI() {
        // null represents an empty linked list.
        sessions = null;

        setTitle("Employee Mentorship and Inclusion Manager");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        createGUI();
        setVisible(true);
    }

    private void createGUI() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(8, 2, 5, 5));
        idField = new JTextField();
        titleField = new JTextField();
        mentorField = new JTextField();
        dateField = new JTextField();
        locationField = new JTextField();
        maxField = new JTextField();
        inputPanel.add(new JLabel("Session ID"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Title"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Mentor"));
        inputPanel.add(mentorField);
        inputPanel.add(new JLabel("Date"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Location"));
        inputPanel.add(locationField);
        inputPanel.add(new JLabel("Max Participants"));
        inputPanel.add(maxField);
        add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);
        add(scroll, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Session");
        JButton displayButton = new JButton("Display");
        JButton searchButton = new JButton("Search");
        JButton removeButton = new JButton("Remove");
        JButton registerButton = new JButton("Register");
        JButton exitButton = new JButton("Exit");
        buttonPanel.add(addButton);
        buttonPanel.add(displayButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addSession());
        displayButton.addActionListener(e -> displaySessions());
        searchButton.addActionListener(e -> searchSession());
        removeButton.addActionListener(e -> removeSession());
        registerButton.addActionListener(e -> registerParticipant());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void clearFields() {
        idField.setText("");
        titleField.setText("");
        mentorField.setText("");
        dateField.setText("");
        locationField.setText("");
        maxField.setText("");
        idField.requestFocus();
    }

    // Return the session with this ID, or null if it is not present.
    private Session searchByID(int id) {
        SessionNode current = sessions;

        while (current != null) {
            if (current.first().id() == id) {
                return current.first();
            }

            current = current.rest();
        }

        return null;
    }

    // 1. Add a session in date order.
    private void addSession() {
        try {
            int id = Integer.parseInt(idField.getText());
            String title = titleField.getText();
            String mentor = mentorField.getText();
            String date = dateField.getText();
            String location = locationField.getText();
            int maxParticipants = Integer.parseInt(maxField.getText());

            if (searchByID(id) != null) {
                outputArea.setText("A session with that ID already exists.");
                return;
            }

            if (maxParticipants < 0) {
                outputArea.setText("Max participants cannot be negative.");
                return;
            }

            Session session = new Session(
                    id, title, mentor, date, location, maxParticipants
            );

            // Save the nodes that come before the insertion location.
            SessionNode before = null;
            SessionNode current = sessions;

            while (current != null
                    && current.first().date().compareTo(date) <= 0) {
                before = new SessionNode(current.first(), before);
                current = current.rest();
            }

            // Insert the new session before the first later session.
            SessionNode result = new SessionNode(session, current);

            // Put the earlier nodes back in their original order.
            while (before != null) {
                result = new SessionNode(before.first(), result);
                before = before.rest();
            }

            sessions = result;
            outputArea.setText("Session Added Successfully\n");
            clearFields();
        }
        catch (Exception e) {
            outputArea.setText("Invalid input");
        }
    }

    // 2. Display every session in chronological order.
    private void displaySessions() {
        outputArea.setText("");
        SessionNode current = sessions;

        while (current != null) {
            outputArea.append("ID: " + current.first().id() + "\n");
            outputArea.append("Title: " + current.first().title() + "\n");
            outputArea.append("Mentor: " + current.first().mentor() + "\n");
            outputArea.append("Date: " + current.first().date() + "\n");
            outputArea.append("Location: " + current.first().location() + "\n");
            outputArea.append("Participants: "
                    + current.first().participants() + "/"
                    + current.first().maxParticipants() + "\n");
            outputArea.append("\n--------------------\n");
            current = current.rest();
        }
    }

    // 3. Search by ID if present, mentor otherwise, and display results.
    private void searchSession() {
        outputArea.setText("");

        if (!idField.getText().trim().isEmpty()) {
            int id = Integer.parseInt(idField.getText().trim());
            Session result = searchByID(id);

            if (result != null) {
                outputArea.append("ID: " + result.id() + "\n");
                outputArea.append("Title: " + result.title() + "\n");
                outputArea.append("Mentor: " + result.mentor() + "\n");
                outputArea.append("Date: " + result.date() + "\n");
                outputArea.append("Location: " + result.location() + "\n");
                outputArea.append("Participants: " + result.participants()
                        + "/" + result.maxParticipants() + "\n");
            }
            else {
                outputArea.setText("Session not found.");
            }
        }
        else if (!mentorField.getText().trim().isEmpty()) {
            String mentor = mentorField.getText().trim();
            SessionNode reversedResult = null;
            SessionNode current = sessions;

            // Build a linked list containing only this mentor's sessions.
            while (current != null) {
                if (current.first().mentor().equals(mentor)) {
                    reversedResult = new SessionNode(
                            current.first(), reversedResult
                    );
                }

                current = current.rest();
            }

            // Reverse the matches so they remain in chronological order.
            SessionNode result = null;

            while (reversedResult != null) {
                result = new SessionNode(reversedResult.first(), result);
                reversedResult = reversedResult.rest();
            }

            if (result != null) {
                current = result;

                while (current != null) {
                    outputArea.append("ID: " + current.first().id() + "\n");
                    outputArea.append("Title: " + current.first().title() + "\n");
                    outputArea.append("Mentor: " + current.first().mentor() + "\n");
                    outputArea.append("Date: " + current.first().date() + "\n");
                    outputArea.append("Location: "
                            + current.first().location() + "\n");
                    outputArea.append("Participants: "
                            + current.first().participants() + "/"
                            + current.first().maxParticipants() + "\n");
                    outputArea.append("\n--------------------\n");
                    current = current.rest();
                }
            }
            else {
                outputArea.setText(
                        "No session found for mentor: " + mentor
                );
            }
        }
        else {
            outputArea.setText("Please enter a Session ID or Mentor name.");
        }
    }

    // 4. Remove the session with the ID in the ID field.
    private void removeSession() {
        int id = Integer.parseInt(idField.getText());
        SessionNode before = null;
        SessionNode current = sessions;

        // Save nodes until the session being removed is reached.
        while (current != null && current.first().id() != id) {
            before = new SessionNode(current.first(), before);
            current = current.rest();
        }

        if (current == null) {
            outputArea.setText("Session not found.");
            return;
        }

        // Skip the matching node.
        SessionNode result = current.rest();

        // Put the earlier nodes back in their original order.
        while (before != null) {
            result = new SessionNode(before.first(), result);
            before = before.rest();
        }

        sessions = result;
        outputArea.setText("Session removed successfully.");
    }

    // 5. Register one participant for the session with the specified ID.
    private void registerParticipant() {
        int id = Integer.parseInt(idField.getText());
        Session result = searchByID(id);

        if (result == null) {
            outputArea.setText("Session not found.");
        }
        else if (result.participants() >= result.maxParticipants()) {
            outputArea.setText("Registration failed: session is full.");
        }
        else {
            result.registerParticipant();
            outputArea.setText("Participant registered successfully.\n"
                    + "Participants: " + result.participants() + "/"
                    + result.maxParticipants());
        }
    }

    public static void main(String[] args) {
        new MainGUI();
    }
}
