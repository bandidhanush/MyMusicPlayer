package com.dhanush.mp3.dhanush.mp3musicplayer;

import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class MusicPlayer implements ActionListener {

    // Frame components
    private JFrame frame;
    private JLabel songName;
    private JButton select, play, pause, resume, stop;
    private JPanel playerPanel, controlPanel;
    private Icon iconPlay, iconPause, iconResume, iconStop;

    // Music file handling components
    private JFileChooser fileChooser;
    private FileInputStream fileInputStream;
    private BufferedInputStream bufferedInputStream;
    private File selectedFile = null;
    private String filename, filePath;
    private long totalLength, pauseLength;
    private Player player;
    private Thread playThread, resumeThread;

    public MusicPlayer() {
        // Initialize UI components
        initUI();
        // Add action listeners to buttons
        addActionEvents();
        // Initialize threads
        playThread = new Thread(runnablePlay);
        resumeThread = new Thread(runnableResume);
    }

    private void initUI() {
        // Set songName Label to center
        songName = new JLabel("", SwingConstants.CENTER);

        // Create buttons for selecting a song
        select = new JButton("Select Mp3");

         // Create JLabel for displaying the resized picture
        JLabel pictureLabel = new JLabel();

        // Load the original image
        ImageIcon originalIcon = new ImageIcon("C:/music-player-java-code/Dhanush-Mp3MusicPlayer/music-player-icons/DhanushMusic.png");

        // Resize the image
        int width = 100;  // Set the desired width
        int height = 100; // Set the desired height
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImage);

        pictureLabel.setIcon(resizedIcon);
        pictureLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create Panels
        playerPanel = new JPanel(); // Music Selection Panel
        controlPanel = new JPanel(); // Control Selection Panel

        // Create icons for buttons
        iconPlay = new ImageIcon("C:/music-player-java-code/Dhanush-Mp3MusicPlayer/music-player-icons/play-button.png");
        iconPause = new ImageIcon("C:/music-player-java-code/Dhanush-Mp3MusicPlayer/music-player-icons/pause-button.png");
        iconResume = new ImageIcon("C:/music-player-java-code/Dhanush-Mp3MusicPlayer/music-player-icons/resume-button.png");
        iconStop = new ImageIcon("C:/music-player-java-code/Dhanush-Mp3MusicPlayer/music-player-icons/stop-button.png");

        // Create image buttons
        play = new JButton(iconPlay);
        pause = new JButton(iconPause);
        resume = new JButton(iconResume);
        stop = new JButton(iconStop);

        // Set Layout of PlayerPanel
        playerPanel.setLayout(new GridLayout(3, 1));

        // Add components in PlayerPanel
        playerPanel.add(select);
        playerPanel.add(pictureLabel);
        playerPanel.add(songName);

        // Set Layout of ControlPanel
        controlPanel.setLayout(new GridLayout(1, 4));

        // Add components in ControlPanel
        controlPanel.add(play);
        controlPanel.add(pause);
        controlPanel.add(resume);
        controlPanel.add(stop);

        // Set buttons background color
        // Replace the existing color settings with the following:

play.setBackground(new Color(72, 133, 237)); // A shade of blue
pause.setBackground(new Color(255, 87, 34)); // A shade of orange
resume.setBackground(new Color(76, 175, 80)); // A shade of green
stop.setBackground(new Color(211, 47, 47)); // A shade of red


        // Initialize the frame
        frame = new JFrame();

        ImageIcon icon = new ImageIcon("C:/music-player-java-code/Dhanush-Mp3MusicPlayer/music-player-icons/DhanushMusic.png");

        // Set Frame's Title
        frame.setTitle("DHANUSH MUSIC PLAYER");

        frame.setIconImage(icon.getImage());

        // Add panels in Frame
        frame.add(playerPanel, BorderLayout.NORTH);
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Set Frame background color
        frame.setBackground(Color.CYAN);
        frame.setSize(400, 300);
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addActionEvents() {
        // Register action listener to buttons
        select.addActionListener(this);
        play.addActionListener(this);
        pause.addActionListener(this);
        resume.addActionListener(this);
        stop.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(select)) {
            chooseFile();
        } else if (e.getSource().equals(play)) {
            playMusic();
        } else if (e.getSource().equals(pause)) {
            pauseMusic();
        } else if (e.getSource().equals(resume)) {
            resumeMusic();
        } else if (e.getSource().equals(stop)) {
            stopMusic();
        }
    }

    private void chooseFile() {
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:\\Users"));
        fileChooser.setDialogTitle("Select Mp3");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files","mp3"));
        if (fileChooser.showOpenDialog(select) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filename = selectedFile.getName();
            filePath = selectedFile.getPath();
            songName.setText("File Selected: " + filename);
        }
    }

    private void playMusic() {
        if (selectedFile != null) {
            playThread.start();
            songName.setText("Now playing: " + filename);
        } else {
            songName.setText("No File was selected!");
        }
    }

    private void pauseMusic() {
        if (player != null && selectedFile != null) {
            try {
                pauseLength = fileInputStream.available();
                player.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void resumeMusic() {
        if (selectedFile != null) {
            resumeThread.start();
        } else {
            songName.setText("No File was selected!");
        }
    }

    private void stopMusic() {
        if (player != null) {
            player.close();
            songName.setText("");
        }
    }

    private final Runnable runnablePlay = () -> {
        try {
            fileInputStream = new FileInputStream(selectedFile);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            player = new Player(bufferedInputStream);
            totalLength = fileInputStream.available();
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private final Runnable runnableResume = () -> {
        try {
            fileInputStream = new FileInputStream(selectedFile);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            player = new Player(bufferedInputStream);
            fileInputStream.skip(totalLength - pauseLength);
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MusicPlayer mp = new MusicPlayer();
        });
    }
}
