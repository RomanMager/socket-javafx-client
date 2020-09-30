package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class PrimaryController {

    @FXML
    private Button connectButton;
    @FXML
    private Button sendButton;
    @FXML
    private TextField ipAddressInputField;
    @FXML
    private TextField portInputField;
    @FXML
    private Label infoLabel;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private TextArea resultTextArea;

    private Socket socket = null;
    private InputStream inputStream;
    private OutputStream outputStream;

    public PrimaryController() {
    }

    @FXML
    public void connect() {
        String ipAddress = ipAddressInputField.getText();
        String port = portInputField.getText();

        if (ipAddress.isBlank() | port.isBlank()) {
            setLabelText(Cause.EMPTY_FIELDS);
            return;
        }

        try {
            socket = new Socket(InetAddress.getByName(ipAddress),
                                Integer.parseInt(port));
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            setLabelText(Cause.CONNECTION_REFUSED);
        }

        if (socket != null) {
            setLabelText(Cause.SUCCESS);
        }
    }

    @FXML
    public void send(ActionEvent actionEvent) {
        if (socket == null) {
            setLabelText(Cause.CONNECTION_REFUSED);
        }

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            String letters = inputTextArea.getText();

            outputStream.write(letters.getBytes());

            byte[] bytes = new byte[1024];
            inputStream.read(bytes);
            String result = new String(bytes);
            resultTextArea.setText(result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLabelText(Cause cause) {
        switch (cause) {
            case CONNECTION_REFUSED:
                infoLabel.setText("Something went wrong...\nCannot establish connection.");
                infoLabel.setTextFill(Paint.valueOf("red"));
                break;
            case EMPTY_FIELDS:
                infoLabel.setText("Please, enter valid IP Address and Port number.");
                infoLabel.setTextFill(Paint.valueOf("red"));
                break;
            case SUCCESS:
                infoLabel.setText("Successfully connected!");
                infoLabel.setTextFill(Paint.valueOf("green"));
                break;
        }
    }

    private enum Cause {
        CONNECTION_REFUSED,
        EMPTY_FIELDS,
        SUCCESS
    }
}
