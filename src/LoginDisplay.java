/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LoginDisplay {
    private VBox loginFrame;
    private HBox buttonBox;
    private Button submitButton, cancelButton;
    private TextField userField;
    private PasswordField passwordField;
    private ProgressIndicator pi;
    private Label statusLabel; 
    
    public LoginDisplay(){
        initializePane();
    }
    
    private void initializePane(){
         loginFrame = new VBox();
            loginFrame.setMinSize(500, 250);
            loginFrame.setMaxSize(500, 250);
            loginFrame.setStyle("-fx-background-color: gainsboro;"
                    + "-fx-border-color: grey;"
                    + "-fx-border-width: 3px;");
            loginFrame.setAlignment(Pos.CENTER);
            loginFrame.setPadding(new Insets(20, 20, 20, 20));
            loginFrame.setSpacing(10);

            //Login Label
            Label loginLabel = new Label("Please enter your Database Login Information:");
            loginLabel.setFont(new Font("Times New Roman", 20));
            //Username HBox
            HBox usernameBox = new HBox();
            usernameBox.setSpacing(20);
            usernameBox.setAlignment(Pos.CENTER_LEFT);
                Label userLabel = new Label("Username: ");
                    userLabel.setMinWidth(75);
                    userField = new TextField("");
                    userField.setPrefWidth(500);
            usernameBox.getChildren().addAll(userLabel, userField);
            //Password HBox
            HBox passwordBox = new HBox();
            passwordBox.setSpacing(20);
            passwordBox.setAlignment(Pos.CENTER_LEFT);
                Label passwordLabel = new Label("Password: ");
                    passwordLabel.setMinWidth(75);
                    passwordField = new PasswordField();
                    passwordField.setText("");
                    passwordField.setPrefWidth(500);
            passwordBox.getChildren().addAll(passwordLabel, passwordField);
            //Button HBox
            buttonBox = new HBox();
            buttonBox.setSpacing(20);
            buttonBox.setAlignment(Pos.CENTER_LEFT);
                submitButton = new Button("Login");
                    submitButton.setPrefWidth(100);
                cancelButton = new Button("Cancel");
                    cancelButton.setPrefWidth(100);
                pi = new ProgressIndicator();
                pi.setMaxSize(25, 25);
            statusLabel = new Label("");
            statusLabel.setAlignment(Pos.CENTER_LEFT);
            statusLabel.setFont(Font.font("Times New Roman", 12));
            buttonBox.getChildren().addAll(submitButton, cancelButton, statusLabel);
            //Add elements to loginFrame
            loginFrame.getChildren().addAll(loginLabel, usernameBox, passwordBox, buttonBox);
    }
    
    public void setButtonAction(Button b, EventHandler<ActionEvent> event){
        b.setOnAction(event);
    }
    
    public VBox getPane(){
        return this.loginFrame;
    }
    
    public Button getSubmitButton(){
        return this.submitButton;
    }
    
    public Button getCancelButton(){
        return this.cancelButton;
    }
    
    public String getUserTextField(){
        return userField.getText();
    }
    
    public String getPasswordField(){
        return passwordField.getText();
    }
    
    public Label getStatusLabel(){
        return this.statusLabel;
    }
    
    public void showPI(){
        buttonBox.getChildren().add(pi);
    }
    
    public void hidePI(){
        buttonBox.getChildren().remove(pi);
    }
    
    
}
