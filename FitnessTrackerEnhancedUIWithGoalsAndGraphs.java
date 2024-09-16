import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

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
}

// Main class for the fitness tracker UI
public class FitnessTrackerEnhancedUIWithGoalsAndGraphs extends JFrame {
    private JTextField nameField, durationField, weeklyDurationGoalField, weeklyCaloriesGoalField;
    private JComboBox<String> exerciseDropdown;
    private JTextArea outputArea;
    private User user;
    private DefaultListModel<String> workoutListModel;
    private Random random = new Random();

    public FitnessTrackerEnhancedUIWithGoalsAndGraphs() {
        setTitle("Fitness Tracker");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for user inputs
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create input fields and labels
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel exerciseLabel = new JLabel("Exercise:");
        exerciseDropdown = new JComboBox<>(new String[]{"Walking", "Running", "Yoga", "Sports", "Swimming", "Cycling", "Hiking/Trekking"});
        JLabel durationLabel = new JLabel("Duration (mins):");
        durationField = new JTextField();

        // Goal inputs
        JLabel durationGoalLabel = new JLabel("Weekly Duration Goal (mins):");
        weeklyDurationGoalField = new JTextField();
        JLabel caloriesGoalLabel = new JLabel("Weekly Calories Goal:");
        weeklyCaloriesGoalField = new JTextField();

        // Add input fields and labels to input panel
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(exerciseLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(exerciseDropdown, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(durationLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(durationField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(durationGoalLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(weeklyDurationGoalField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(caloriesGoalLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(weeklyCaloriesGoalField, gbc);

        // Create buttons
        JButton logButton = new JButton("Log Workout");
        JButton progressButton = new JButton("View Progress");
        JButton goalButton = new JButton("Check Goals");
        JButton graphButton = new JButton("Show Graph");

        // Add buttons to input panel
        gbc.gridx = 0; gbc.gridy = 5;
        inputPanel.add(logButton, gbc);
        gbc.gridx = 1;
        inputPanel.add(progressButton, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        inputPanel.add(goalButton, gbc);
        gbc.gridx = 1;
        inputPanel.add(graphButton, gbc);

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

        goalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkGoals();
            }
        });

        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showWorkoutGraph();
            }
        });

        setVisible(true);
    }

    // Method to log the workout
    private void logWorkout() {
        if (user == null) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                outputArea.setText("Please enter your name first.");
                return;
            }
            user = new User(name);
        }

        String exercise = (String) exerciseDropdown.getSelectedItem();
        String durationText = durationField.getText().trim();

        if (exercise.isEmpty() || durationText.isEmpty()) {
            outputArea.setText("Please fill in all workout details.");
            return;
        }

        try {
            int duration = Integer.parseInt(durationText);
            int calories = (int) (duration * getMET(exercise) * 3.5 * 70 / 200); // Example calorie calculation
            Workout workout = new Workout(exercise, duration, calories);
            user.logWorkout(workout);
            workoutListModel.addElement(workout.toString());
            outputArea.setText("Workout logged: " + workout);
            clearFields();
        } catch (NumberFormatException ex) {
            outputArea.setText("Please enter valid numbers for duration.");
        }
    }

    // Example MET values for different exercises
    private double getMET(String exercise) {
        switch (exercise) {
            case "Walking": return 3.8;
            case "Running": return 9.8;
            case "Yoga": return 2.5;
            case "Sports": return 6.0;
            case "Swimming": return 7.0;
            case "Cycling": return 8.0;
            case "Hiking/Trekking": return 6.5;
            default: return 1.0;
        }
    }

    // Method to show progress
    private void showProgress() {
        if (user == null) {
            outputArea.setText("Please log a workout first.");
        } else {
            outputArea.setText(user.getProgress());
        }
    }

    // Method to check goals
    private void checkGoals() {
        if (user == null) {
            outputArea.setText("Please log a workout first.");
            return;
        }

        int totalDuration = user.getWorkoutLog().stream().mapToInt(w -> w.duration).sum();
        int totalCalories = user.getWorkoutLog().stream().mapToInt(w -> w.caloriesBurned).sum();

        int durationGoal = Integer.parseInt(weeklyDurationGoalField.getText());
        int caloriesGoal = Integer.parseInt(weeklyCaloriesGoalField.getText());

        StringBuilder goalStatus = new StringBuilder("Goal Progress:\n");
        goalStatus.append("Total Duration: ").append(totalDuration).append(" mins (Goal: ").append(durationGoal).append(" mins)\n");
        goalStatus.append("Total Calories: ").append(totalCalories).append(" cal (Goal: ").append(caloriesGoal).append(" cal)\n");

        outputArea.setText(goalStatus.toString());
    }

    // Method to show the workout graph
    private void showWorkoutGraph() {
        if (user == null || user.getWorkoutLog().isEmpty()) {
            outputArea.setText("No workouts to display in the graph.");
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Workout workout : user.getWorkoutLog()) {
            dataset.addValue(workout.duration, "Duration", workout.exerciseName);
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
            "Workout Duration Over Time",
            "Exercise",
            "Duration (mins)",
            dataset
        );

        ChartPanel chartPanel = new ChartPanel(lineChart);
        JFrame chartFrame = new JFrame("Workout Graph");
        chartFrame.setContentPane(chartPanel);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }

    // Clear input fields
    private void clearFields() {
        durationField.setText("");
    }

    public static void main(String[] args) {
        new FitnessTrackerEnhancedUIWithGoalsAndGraphs();
    }
}
