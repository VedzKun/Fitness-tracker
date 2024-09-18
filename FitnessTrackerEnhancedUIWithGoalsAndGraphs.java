import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Class representing a workout
class Workout {
    String exerciseName;
    int duration; // in minutes
    int caloriesBurned;

    public Workout(String exerciseName, int duration, int caloriesBurned) {
        this.exerciseName = exerciseName;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
    }

    @Override
    public String toString() {
        return exerciseName + " - " + duration + " mins, " + caloriesBurned + " cal";
    }
}

// Class representing a user
class User {
    String name;
    ArrayList<Workout> workoutLog;

    public User(String name) {
        this.name = name;
        this.workoutLog = new ArrayList<>();
    }

    public void logWorkout(Workout workout) {
        workoutLog.add(workout);
    }

    public String getProgress() {
        if (workoutLog.isEmpty()) {
            return "No workouts logged yet.";
        }

        StringBuilder history = new StringBuilder("Workout history for " + name + ":\n");
        int totalDuration = 0;
        int totalCalories = 0;

        for (Workout workout : workoutLog) {
            history.append(workout.toString()).append("\n");
            totalDuration += workout.duration;
            totalCalories += workout.caloriesBurned;
        }

        history.append("\nTotal workout duration: ").append(totalDuration).append(" mins");
        history.append("\nTotal calories burned: ").append(totalCalories).append(" cal");
        return history.toString();
    }

    public ArrayList<Workout> getWorkoutLog() {
        return workoutLog;
    }

    @Override
    public String toString() {
        return name;
    }
}

// Main class for the fitness tracker UI
public class FitnessTrackerEnhancedUI extends JFrame {
    private JTextField exerciseField, durationField;
    private JTextArea outputArea;
    private JComboBox<User> userDropdown;
    private JComboBox<String> exerciseDropdown;
    private DefaultListModel<String> workoutListModel;
    private List<User> users;
    private User currentUser;

    public FitnessTrackerEnhancedUI() {
        users = new ArrayList<>();
        setTitle("Fitness Tracker");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for user inputs
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create input fields and labels
        JLabel userLabel = new JLabel("Select User:");
        userDropdown = new JComboBox<>();
        userDropdown.setPreferredSize(new Dimension(150, 30)); // Set size of user dropdown
        userDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentUser = (User) userDropdown.getSelectedItem();
                if (currentUser != null) {
                    outputArea.setText("Switched to user: " + currentUser.name);
                }
            }
        });

        JLabel exerciseLabel = new JLabel("Exercise:");
        exerciseDropdown = new JComboBox<>(new String[]{"Walking", "Running", "Yoga", "Sports", "Swimming", "Cycling", "Hiking/Trekking"});
        exerciseDropdown.setPreferredSize(new Dimension(150, 30)); // Set size of exercise dropdown

        JLabel durationLabel = new JLabel("Duration (mins):");
        durationField = new JTextField();
        durationField.setPreferredSize(new Dimension(150, 30)); // Set size of duration text field

        // Add input fields and labels to input panel
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(userDropdown, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(exerciseLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(exerciseDropdown, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(durationLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(durationField, gbc);

        // Create buttons
        JButton logButton = new JButton("Record Workout");
        JButton progressButton = new JButton("View Progress");
        JButton clearButton = new JButton("Clear History");
        JButton exportButton = new JButton("Export Workouts");
        JButton addUserButton = new JButton("Add User");

        // Add buttons to input panel
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(logButton, gbc);
        gbc.gridx = 1;
        inputPanel.add(progressButton, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(clearButton, gbc);
        gbc.gridx = 1;
        inputPanel.add(exportButton, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        inputPanel.add(addUserButton, gbc);

        // Workout history display panel
        workoutListModel = new DefaultListModel<>();
        JList<String> workoutList = new JList<>(workoutListModel);
        JScrollPane workoutListScrollPane = new JScrollPane(workoutList);
        workoutListScrollPane.setPreferredSize(new Dimension(200, 300));

        // Output area for text
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add components to main frame
        add(inputPanel, BorderLayout.WEST);
        add(workoutListScrollPane, BorderLayout.EAST);
        add(scrollPane, BorderLayout.SOUTH);

        // Button Actions
        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logWorkout();
            }
        });

        progressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProgress();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearHistory();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportWorkoutHistory();
            }
        });

        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        setVisible(true);
    }

    // Method to log the workout
    private void logWorkout() {
        if (currentUser == null) {
            outputArea.setText("Please select a user first.");
            return;
        }

        String exercise = (String) exerciseDropdown.getSelectedItem();
        String durationText = durationField.getText().trim();

        if (exercise == null || durationText.isEmpty()) {
            outputArea.setText("Please enter all workout details.");
            return;
        }

        try {
            int duration = Integer.parseInt(durationText);
            int calories = calculateCalories(exercise, duration);
            Workout workout = new Workout(exercise, duration, calories);

            currentUser.logWorkout(workout);
            workoutListModel.addElement(workout.toString());
            outputArea.setText("Workout Recorded: " + workout);
            clearFields();
        } catch (NumberFormatException ex) {
            outputArea.setText("Please enter valid numbers for duration.");
        }
    }

    // Method to calculate calories burned
    private int calculateCalories(String exercise, int duration) {
        int met = getMETValue(exercise);
        return (int) (met * duration * 3.5 * 70 / 200); // 70 kg is the average weight
    }

    // Method to get MET value based on exercise type
    private int getMETValue(String exercise) {
        switch (exercise.toLowerCase()) {
            case "walking":
                return 3;
            case "running":
                return 7;
            case "yoga":
                return 2;
            case "sports":
                return 6;
            case "swimming":
                return 7;
            case "cycling":
                return 6;
            case "hiking/trekking":
                return 5;
            default:
                return 1;
        }
    }

    // Method to show progress
    private void showProgress() {
        if (currentUser == null) {
            outputArea.setText("Please select a user first.");
        } else {
            outputArea.setText(currentUser.getProgress());
        }
    }

    // Method to clear the workout history
    private void clearHistory() {
        if (currentUser != null) {
            workoutListModel.clear();
            currentUser.workoutLog.clear();
            outputArea.setText("Workout history cleared.");
        }
    }

    // Method to export workout history to a file
    private void exportWorkoutHistory() {
        if (currentUser != null && !currentUser.getWorkoutLog().isEmpty()) {
            try (FileWriter writer = new FileWriter("workout_history.txt")) {
                for (Workout workout : currentUser.getWorkoutLog()) {
                    writer.write(workout.toString() + "\n");
                }
                outputArea.setText("Workout history exported to workout_history.txt");
            } catch (IOException e) {
                outputArea.setText("Error encountered. Please try again.");
            }
        } else {
            outputArea.setText("Add workouts to export.");
        }
    }

    // Method to clear input fields
    private void clearFields() {
        exerciseField.setText("");
        durationField.setText("");
    }

    // Method to add a new user
    private void addUser() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField nameField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                User newUser = new User(name);
                users.add(newUser);
                userDropdown.addItem(newUser);
                outputArea.setText("Member added: " + name);
            } else {
                outputArea.setText("Please provide a valid name.");
            }
        }
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FitnessTrackerEnhancedUI();
            }
        });
    }
}
