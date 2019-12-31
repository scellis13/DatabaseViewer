/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import static java.rmi.Naming.lookup;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DatabaseDisplay {
    private final DatabaseContextMenu dbContextMenu;
    private StackPane stackPane;
    private VBox quickSearchPaneBorder, sqlTableBox, advancedSearchPaneBorder, transparentPane;
    private HBox quickSearchBox, advancedSearchButtonBox, copierPaneSwitchBox;
    private Button refreshButton, clearButton, filterButton, advancedClearButton, 
            clearFiltersButton, clearAdvancedFilterButton, hideAdvancedPaneButton,
            advancedSearchButton = new Button(""), copyListButton;
    private TextField quickSearchField = new TextField(""), advancedSearchField = new TextField("");
    private Label sqlTableLabel, sqlTableSize, sqlTableSelectedSize, copyListLabel;
    private TableView databaseTableView;
    private CheckBox artistCheckBox, titleCheckBox, dateCheckBox, timeCheckBox, genreCheckBox, durationCheckBox, sizeCheckBox, filenameCheckBox, filetypeCheckBox, pathCheckBox, addAllGenres, removeAllGenres;
    private ComboBox limitSelection, dateComboBoxLeft, dateComboBoxRight;
    private RadioButton activateDateFilter;
    private DatePicker datePickerLeft, datePickerRight;
    private ProgressIndicator pi, piTable;
    
    private HashMap<String, CheckBox> genreCheckBoxList;
    
    private ArrayList<String> genresSelected = new ArrayList<>();
    private ArrayList<String> colsSelected = new ArrayList<>();
    private ArrayList<String> genreList;
    private boolean filterIsShowing = false, piShown = false, piShownTable = false;
    
    private final String sqlArtistCol = "song_artist";
    private final String sqlTitleCol = "song_title";
    private final String sqlDateCol = "date_added";
    private final String sqlTimeCol = "time_added";
    private final String sqlGenreCol = "genre_list";
    private final String sqlDurationCol = "duration";
    private final String sqlSizeCol = "size";
    private final String sqlFilenameCol = "file_name";
    private final String sqlFiletypeCol = "file_type";
    private final String sqlPathCol = "path";
    private ArrayList<String> colsList = new ArrayList<>();
        
    //Images
    private static final Image CLEAR_BUTTON_IMG = new Image(GRDB.class.getResourceAsStream("/resources/pics/clear_button.jpg"));
    private static final Image CLEAR_BUTTON_IMG_PRESSED = new Image(GRDB.class.getResourceAsStream("/resources/pics/clear_button_pressed.png"));
    private static final Image REFRESH_BUTTON_IMG = new Image(GRDB.class.getResourceAsStream("/resources/pics/refresh_button.jpg"));
    private static final Image REFRESH_BUTTON_IMG_PRESSED = new Image(GRDB.class.getResourceAsStream("/resources/pics/refresh_button_pressed.jpg"));
    private static final Image CLOSE_BUTTON_IMG_NORMAL = new Image(GRDB.class.getResourceAsStream("/resources/pics/closeButton_normal.png"));
    private static final Image CLOSE_BUTTON_IMG_HIGHLIGHT = new Image(GRDB.class.getResourceAsStream("/resources/pics/closeButton_highlight.png"));
    private static final Image CLOSE_BUTTON_IMG_PRESSED = new Image(GRDB.class.getResourceAsStream("/resources/pics/closeButton_pressed.png"));
    
    public DatabaseDisplay(DatabaseContextMenu menu){
        this.dbContextMenu = menu;
        initializePane();
    }
    
    private void initializePane(){
        quickSearchPane();
        advancedSearchPane();
        tableLoadIndicatorPane();
        
        stackPane = new StackPane();
        stackPane.setPadding(new Insets(20, 20, 20, 20));
        stackPane.setAlignment(Pos.CENTER_RIGHT);
        stackPane.getChildren().addAll(quickSearchPaneBorder);
    }
    
    public StackPane getPane(){
        return this.stackPane;
    }
    
    private void quickSearchPane(){
        quickSearchPaneBorder = new VBox();
        quickSearchPaneBorder.setAlignment(Pos.TOP_CENTER);
        quickSearchPaneBorder.setSpacing(20);
        
        
        databaseTableView = new TableView();

        //Create HBox for Quick Search Refresh Button, Label and TextField
            //Refresh Button
            refreshButton = new Button();
            refreshButton.setMinSize(30, 30);
            
                //Normal View of Refresh Button
                BackgroundImage backgroundImage = new BackgroundImage(
                    REFRESH_BUTTON_IMG, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundPosition.DEFAULT, 
                    BackgroundSize.DEFAULT);
                Background backgroundNormal = new Background(backgroundImage);
                
                //Pressed View of Refresh Button
                BackgroundImage backgroundImagePressed = new BackgroundImage(
                    REFRESH_BUTTON_IMG_PRESSED, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundPosition.DEFAULT, 
                    BackgroundSize.DEFAULT);
                Background backgroundPressed = new Background(backgroundImagePressed);
                //Set Actions to change Button Background    
                refreshButton.setBackground(backgroundNormal);
                refreshButton.setOnMousePressed(event -> {
                    refreshButton.setBackground(backgroundPressed);
                });
                refreshButton.setOnMouseReleased(event -> {
                    refreshButton.setBackground(backgroundNormal);
                });
            //End of Creating Refresh Button
            
            //Clear Button
            clearButton = new Button();
            clearButton.setMinSize(30, 30);
            
                //Normal View of Refresh Button
                BackgroundImage backgroundImageClear = new BackgroundImage(
                    CLEAR_BUTTON_IMG, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundPosition.DEFAULT, 
                    BackgroundSize.DEFAULT);
                Background backgroundNormalClear = new Background(backgroundImageClear);
                
                //Pressed View of Clear Button
                BackgroundImage backgroundImageClearPressed = new BackgroundImage(
                    CLEAR_BUTTON_IMG_PRESSED, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundPosition.DEFAULT, 
                    BackgroundSize.DEFAULT);
                Background backgroundNormalClearPressed = new Background(backgroundImageClearPressed);
                //Set Actions to change Button Background    
                clearButton.setBackground(backgroundNormalClear);
                clearButton.setOnMousePressed(event -> {
                    clearButton.setBackground(backgroundNormalClearPressed);
                });
                clearButton.setOnMouseReleased(event -> {
                    clearButton.setBackground(backgroundNormalClear);
                });
            
        
            //End of Creating Clear Button
            
            //Create AdvancedSearchButton
            filterButton = new Button("Advanced Filter");
            filterButton.setOnAction(e->{
                if(filterIsShowing){
                    hideAdvancedSearchPane();
                } else {
                    showAdvancedSearchPane();
                }
            });
            
            //Create Quick Advanced Search Clear Button
            clearAdvancedFilterButton = new Button("Clear Filters");
            clearAdvancedFilterButton.setOnAction(e->{
                clearFilters();
                advancedSearchButton.fire();
            });
            
        Label quickSearchLabel = new Label("Quick Search:");
        quickSearchLabel.setFont(new Font("Times New Roman", 20));
        
        
        
        quickSearchField.setPrefWidth(500);
        quickSearchField.setPrefHeight(quickSearchLabel.getHeight());
        quickSearchBox = new HBox(refreshButton, quickSearchLabel, quickSearchField, clearButton, filterButton, clearAdvancedFilterButton);
        quickSearchBox.setAlignment(Pos.CENTER_LEFT);
        quickSearchBox.setSpacing(10);
        
       
        
        //Create VBox for Label and TableView
            sqlTableLabel = new Label("SQL/Database Search Results");
            sqlTableLabel.setFont(new Font("Times New Roman", 30));
            sqlTableSize = new Label("Total: ");
            sqlTableSize.setFont(new Font("Times New Roman", 12));
            sqlTableSize.setAlignment(Pos.CENTER_LEFT);
            sqlTableSelectedSize = new Label("Songs Selected: " );
            sqlTableSelectedSize.setFont(new Font("Times New Roman", 12));
            sqlTableSelectedSize.setAlignment(Pos.CENTER_LEFT);
            
            copierPaneSwitchBox = new HBox();
            copierPaneSwitchBox.setSpacing(10);
            copierPaneSwitchBox.setAlignment(Pos.CENTER_LEFT);
                copyListButton = new Button("View Copy List");
                copyListLabel = new Label("");
            copierPaneSwitchBox.getChildren().addAll(copyListButton, copyListLabel);
            
            sqlTableBox = new VBox(sqlTableSize, sqlTableSelectedSize, databaseTableView, copierPaneSwitchBox);
            sqlTableBox.setAlignment(Pos.CENTER_LEFT);
        
        
        
        
        //Add Elements to Quick Search Pane Border
        quickSearchPaneBorder.getChildren().addAll(quickSearchBox, sqlTableLabel, sqlTableBox);
    }
    
    private void advancedSearchPane(){

        advancedSearchPaneBorder = new VBox();
        advancedSearchPaneBorder.setStyle(
                "-fx-background-color: #f4f4f4;"
                + "-fx-border-color: grey;"
                + "-fx-border-style: solid hidden solid solid;"
                + "-fx-border-width: 3px;");
        advancedSearchPaneBorder.setMinWidth(0);
        advancedSearchPaneBorder.setPrefWidth(700);
        advancedSearchPaneBorder.setMaxWidth(700);
        //advancedSearchPaneBorder.setStyle("-fx-border-color: black;");
        advancedSearchPaneBorder.setPadding(new Insets(0, 20, 20, 0));
        advancedSearchPaneBorder.setSpacing(30);
        advancedSearchPaneBorder.setAlignment(Pos.TOP_CENTER);
        
            hideAdvancedPaneButton = new Button();
            hideAdvancedPaneButton.setMinSize(30,30);
            hideAdvancedPaneButton.setMaxSize(30,30);
                BackgroundImage backgroundImageCloseNormal = new BackgroundImage(
                    CLOSE_BUTTON_IMG_NORMAL,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
                Background backgroundCloseNormal = new Background(backgroundImageCloseNormal);
                
                BackgroundImage backgroundImageCloseHighlight = new BackgroundImage(
                    CLOSE_BUTTON_IMG_HIGHLIGHT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
                Background backgroundCloseHighlight = new Background(backgroundImageCloseHighlight);
                
                BackgroundImage backgroundImageClosePressed = new BackgroundImage(
                    CLOSE_BUTTON_IMG_PRESSED,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
                Background backgroundClosePressed = new Background(backgroundImageClosePressed);
                
            hideAdvancedPaneButton.setBackground(backgroundCloseNormal);
            hideAdvancedPaneButton.setOnMousePressed(event -> {
                hideAdvancedPaneButton.setBackground(backgroundClosePressed);
            });
            hideAdvancedPaneButton.setOnMouseReleased(event -> {
                hideAdvancedPaneButton.setBackground(backgroundCloseNormal);
            });
            hideAdvancedPaneButton.setOnMouseEntered(event ->{
                hideAdvancedPaneButton.setBackground(backgroundCloseHighlight);
            });
            hideAdvancedPaneButton.setOnMouseExited(event ->{
                hideAdvancedPaneButton.setBackground(backgroundCloseNormal);
            });
            
            HBox hideAdvancedPaneBox = new HBox();
            hideAdvancedPaneBox.setAlignment(Pos.CENTER_LEFT);
            hideAdvancedPaneBox.getChildren().add(hideAdvancedPaneButton);
            //Label "Advanced Search Filters"
            Label advancedSearchLabel = new Label("Advanced Search Filters");
            advancedSearchLabel.setFont(new Font("Times New Roman", 30));

            //HBox: Label, TextField, Clear Button
            HBox advancedSearchFilterBox = new HBox();
            advancedSearchFilterBox.setSpacing(10);
            advancedSearchFilterBox.setAlignment(Pos.CENTER);
            advancedSearchFilterBox.setMinWidth(advancedSearchPaneBorder.getMaxWidth());
                Label filterBoxLabel = new Label("Advanced Search:");
                filterBoxLabel.setFont(new Font("Times New Roman", 20));
                advancedSearchField.setPrefWidth(400);
                advancedSearchField.setPrefHeight(filterBoxLabel.getHeight());
                //Clear Button
                advancedClearButton = new Button();
                advancedClearButton.setMinSize(30, 30);

                    //Normal View of Refresh Button
                    BackgroundImage backgroundImageClear = new BackgroundImage(
                        CLEAR_BUTTON_IMG, 
                        BackgroundRepeat.NO_REPEAT, 
                        BackgroundRepeat.NO_REPEAT, 
                        BackgroundPosition.DEFAULT, 
                        BackgroundSize.DEFAULT);
                    Background backgroundNormalClear = new Background(backgroundImageClear);

                    //Pressed View of Clear Button
                    BackgroundImage backgroundImageClearPressed = new BackgroundImage(
                        CLEAR_BUTTON_IMG_PRESSED, 
                        BackgroundRepeat.NO_REPEAT, 
                        BackgroundRepeat.NO_REPEAT, 
                        BackgroundPosition.DEFAULT, 
                        BackgroundSize.DEFAULT);
                    Background backgroundNormalClearPressed = new Background(backgroundImageClearPressed);
                    //Set Actions to change Button Background    
                    advancedClearButton.setBackground(backgroundNormalClear);
                    advancedClearButton.setOnMousePressed(event -> {
                        advancedClearButton.setBackground(backgroundNormalClearPressed);
                    });
                    advancedClearButton.setOnMouseReleased(event -> {
                        advancedClearButton.setBackground(backgroundNormalClear);
                    });
                //End of Creating Clear Button
            advancedSearchFilterBox.getChildren().addAll(filterBoxLabel,advancedSearchField, advancedClearButton);

            //VBox for Label and Column HBox
            VBox columnsVBox = new VBox();
            columnsVBox.setSpacing(10);
            columnsVBox.setAlignment(Pos.CENTER);
            columnsVBox.setMinWidth(advancedSearchPaneBorder.getMaxWidth());
                //Label "Include Columns"
                Label columnsLabel = new Label("Select Columns to Appear in Search:");
                columnsLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
                int columnLabelSize = 75;
                //HBoxTop: 5 Checkboxes, Artist, Title, Date, Time, Genre
                HBox columnSelectionHBoxTop = new HBox();
                columnSelectionHBoxTop.setAlignment(Pos.CENTER);
                columnSelectionHBoxTop.setSpacing(25);
                columnSelectionHBoxTop.setMinWidth(columnsVBox.getMinWidth());
                    HBox artistHBox = new HBox();
                    
                    artistHBox.setSpacing(10);
                    //artistHBox.setAlignment(Pos.CENTER);
                        artistCheckBox = new CheckBox();
                        artistCheckBox.setOnAction(e->{
                            if(artistCheckBox.isSelected()){
                                colsSelected.add(sqlArtistCol);
                                advancedSearchButton.setDisable(false);
                            } else {
                                colsSelected.remove(sqlArtistCol);
                                if(colsSelected.isEmpty()){
                                    advancedSearchButton.setDisable(true);
                                }
                            }
                        });
                        artistCheckBox.fire();
                        
                        Label artistCheckBoxLabel = new Label("Artist");
                        artistCheckBoxLabel.setMinWidth(columnLabelSize);
                        artistCheckBoxLabel.setMaxWidth(columnLabelSize);
                    artistHBox.getChildren().addAll(artistCheckBox, artistCheckBoxLabel);

                    HBox titleHBox = new HBox();
                    
                    titleHBox.setSpacing(10);
                    //titleHBox.setAlignment(Pos.CENTER);
                        titleCheckBox = new CheckBox();
                        titleCheckBox.setOnAction(e->{
                            if(titleCheckBox.isSelected()){
                                colsSelected.add(sqlTitleCol);
                                advancedSearchButton.setDisable(false);
                            } else {
                                colsSelected.remove(sqlTitleCol);
                                if(colsSelected.isEmpty()){
                                    advancedSearchButton.setDisable(true);
                                }
                            }
                        });
                        titleCheckBox.fire();
                        
                        Label titleCheckBoxLabel = new Label("Title");
                        titleCheckBoxLabel.setMinWidth(columnLabelSize);
                        titleCheckBoxLabel.setMaxWidth(columnLabelSize);
                    titleHBox.getChildren().addAll(titleCheckBox, titleCheckBoxLabel);

                    HBox dateHBox = new HBox();
                    
                    dateHBox.setSpacing(10);
                    //dateHBox.setAlignment(Pos.CENTER);
                        dateCheckBox = new CheckBox();
                        dateCheckBox.setOnAction(e->{
                            if(dateCheckBox.isSelected()){
                                colsSelected.add(sqlDateCol);
                                advancedSearchButton.setDisable(false);
                            } else {
                                colsSelected.remove(sqlDateCol);
                                if(colsSelected.isEmpty()){
                                    advancedSearchButton.setDisable(true);
                                }
                            }
                        });
                        dateCheckBox.fire();
                        
                        Label dateCheckBoxLabel = new Label("Date");
                        dateCheckBoxLabel.setMinWidth(columnLabelSize);
                        dateCheckBoxLabel.setMaxWidth(columnLabelSize);
                    dateHBox.getChildren().addAll(dateCheckBox, dateCheckBoxLabel);

                    HBox timeHBox = new HBox();
                    
                    timeHBox.setSpacing(10);
                    //timeHBox.setAlignment(Pos.CENTER);
                        timeCheckBox = new CheckBox();
                        timeCheckBox.setOnAction(e->{
                            if(timeCheckBox.isSelected()){
                                colsSelected.add(sqlTimeCol);
                                advancedSearchButton.setDisable(false);
                            } else {
                                colsSelected.remove(sqlTimeCol);
                                if(colsSelected.isEmpty()){
                                    advancedSearchButton.setDisable(true);
                                }
                            }
                        });
                        timeCheckBox.fire();
                        
                        Label timeCheckBoxLabel = new Label("Time");
                        timeCheckBoxLabel.setMinWidth(columnLabelSize);
                        timeCheckBoxLabel.setMaxWidth(columnLabelSize);
                    timeHBox.getChildren().addAll(timeCheckBox, timeCheckBoxLabel);

                    HBox genreHBox = new HBox();
                    
                    genreHBox.setSpacing(10);
                    //genreHBox.setAlignment(Pos.CENTER);
                        genreCheckBox = new CheckBox();
                        genreCheckBox.setOnAction(e->{
                            if(genreCheckBox.isSelected()){
                                colsSelected.add(sqlGenreCol);
                                advancedSearchButton.setDisable(false);
                            } else {
                                colsSelected.remove(sqlGenreCol);
                                if(colsSelected.isEmpty()){
                                    advancedSearchButton.setDisable(true);
                                }
                            }
                        });
                        genreCheckBox.fire();
                        
                        Label genreCheckBoxLabel = new Label("Genre");
                        genreCheckBoxLabel.setMinWidth(columnLabelSize);
                        genreCheckBoxLabel.setMaxWidth(columnLabelSize);
                    genreHBox.getChildren().addAll(genreCheckBox, genreCheckBoxLabel);

                columnSelectionHBoxTop.getChildren().addAll(
                        artistHBox, titleHBox, dateHBox, timeHBox, genreHBox
                        );

                //HBoxBot: 5 Checkboxes, Duration, Sie, Filename, FileType, Path
                HBox columnSelectionHBoxBot = new HBox();
                columnSelectionHBoxBot.setAlignment(Pos.CENTER);
                columnSelectionHBoxBot.setSpacing(25);
                columnSelectionHBoxBot.setMinWidth(columnsVBox.getMinWidth());
                    HBox durationHBox = new HBox();
                    durationHBox.setSpacing(10);
                    //durationHBox.setAlignment(Pos.CENTER);
                        durationCheckBox = new CheckBox();
                        durationCheckBox.setOnAction(e->{
                            if(durationCheckBox.isSelected()){
                                colsSelected.add(sqlDurationCol);
                                advancedSearchButton.setDisable(false);
                            } else {
                                colsSelected.remove(sqlDurationCol);
                                if(colsSelected.isEmpty()){
                                    advancedSearchButton.setDisable(true);
                                }
                            }
                        });
                        durationCheckBox.fire();
                        
                        Label durationCheckBoxLabel = new Label("Duration");
                        durationCheckBoxLabel.setMinWidth(columnLabelSize);
                        durationCheckBoxLabel.setMaxWidth(columnLabelSize);
                    durationHBox.getChildren().addAll(durationCheckBox, durationCheckBoxLabel);

                    HBox sizeHBox = new HBox();
                    sizeHBox.setSpacing(10);
                    //sizeHBox.setAlignment(Pos.CENTER);
                        sizeCheckBox = new CheckBox();
                        sizeCheckBox.setOnAction(e->{
                            if(sizeCheckBox.isSelected()){
                                colsSelected.add(sqlSizeCol);
                                advancedSearchButton.setDisable(false);
                            } else {
                                colsSelected.remove(sqlSizeCol);
                                if(colsSelected.isEmpty()){
                                    advancedSearchButton.setDisable(true);
                                }
                            }
                        });
                        sizeCheckBox.fire();
                        
                        Label sizeCheckBoxLabel = new Label("Size");
                        sizeCheckBoxLabel.setMinWidth(columnLabelSize);
                        sizeCheckBoxLabel.setMaxWidth(columnLabelSize);
                    sizeHBox.getChildren().addAll(sizeCheckBox, sizeCheckBoxLabel);

                    HBox filenameHBox = new HBox();
                    filenameHBox.setSpacing(10);
                    //filenameHBox.setAlignment(Pos.CENTER);
                        filenameCheckBox = new CheckBox();
                        filenameCheckBox.setOnAction(e->{
                            if(filenameCheckBox.isSelected()){
                                colsSelected.add(sqlFilenameCol);
                                advancedSearchButton.setDisable(false);
                            } else {
                                colsSelected.remove(sqlFilenameCol);
                                if(colsSelected.isEmpty()){
                                    advancedSearchButton.setDisable(true);
                                }
                            }
                        });
                        filenameCheckBox.fire();
                        
                        Label filenameCheckBoxLabel = new Label("Filename");
                        filenameCheckBoxLabel.setMinWidth(columnLabelSize);
                        filenameCheckBoxLabel.setMaxWidth(columnLabelSize);
                    filenameHBox.getChildren().addAll(filenameCheckBox, filenameCheckBoxLabel);

                    HBox filetypeHBox = new HBox();
                    filetypeHBox.setSpacing(10);
                    //filetypeHBox.setAlignment(Pos.CENTER);
                        filetypeCheckBox = new CheckBox();
                        filetypeCheckBox.setOnAction(e->{
                            if(filetypeCheckBox.isSelected()){
                                colsSelected.add(sqlFiletypeCol);
                                advancedSearchButton.setDisable(false);
                            } else {
                                colsSelected.remove(sqlFiletypeCol);
                                if(colsSelected.isEmpty()){
                                    advancedSearchButton.setDisable(true);
                                }
                            }
                        });
                        filetypeCheckBox.setSelected(false);
                        
                        Label filetypeCheckBoxLabel = new Label("File Type");
                        filetypeCheckBoxLabel.setMinWidth(columnLabelSize);
                        filetypeCheckBoxLabel.setMaxWidth(columnLabelSize);
                    filetypeHBox.getChildren().addAll(filetypeCheckBox, filetypeCheckBoxLabel);

                    HBox pathHBox = new HBox();
                    pathHBox.setSpacing(10);
                    //pathHBox.setAlignment(Pos.CENTER);
                        pathCheckBox = new CheckBox();
                        pathCheckBox.setOnAction(e->{
                            if(pathCheckBox.isSelected()){
                                colsSelected.add(sqlPathCol);
                                advancedSearchButton.setDisable(false);
                            } else {
                                colsSelected.remove(sqlPathCol);
                                if(colsSelected.isEmpty()){
                                    advancedSearchButton.setDisable(true);
                                }
                            }
                        });
                        pathCheckBox.setSelected(false);
                        
                        Label pathCheckBoxLabel = new Label("Path");
                        pathCheckBoxLabel.setMinWidth(columnLabelSize);
                        pathCheckBoxLabel.setMaxWidth(columnLabelSize);
                    pathHBox.getChildren().addAll(pathCheckBox, pathCheckBoxLabel);

                columnSelectionHBoxBot.getChildren().addAll(
                        durationHBox, sizeHBox, filenameHBox, filetypeHBox, pathHBox
                        );
            columnsVBox.getChildren().addAll(columnsLabel, columnSelectionHBoxTop, columnSelectionHBoxBot);

            //VBox: Label, HBox: All Genre Checkboxes and Abbreviations
            VBox genreDropDownBox = new VBox();
            genreDropDownBox.setAlignment(Pos.CENTER);
            genreDropDownBox.setSpacing(25);
            genreDropDownBox.setMinWidth(advancedSearchPaneBorder.getMaxWidth());
            genreDropDownBox.setPadding(new Insets(20, 20, 20, 20));
                HBox genreDropDownBoxTop = new HBox();
                genreDropDownBoxTop.setSpacing(25);
                genreDropDownBoxTop.setAlignment(Pos.CENTER);
                    Label genreDropDownBoxLabel = new Label("Genre(s):");
                    genreDropDownBoxLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
                    addAllGenres = new CheckBox();
                    removeAllGenres = new CheckBox();
                    addAllGenres.selectedProperty().addListener( (o, oldV, newV) -> {
                        if(addAllGenres.isSelected()){
                            removeAllGenres.setSelected(false);
                            //Set all Genres selected to True
                            for(Map.Entry value : genreCheckBoxList.entrySet()){
                                CheckBox vCB = (CheckBox) value.getValue();
                                vCB.setSelected(true);
                                //genresSelected.add((String)value.getKey());
                            }
                        }
                    });
                    removeAllGenres.selectedProperty().addListener((o, oldV, newV) -> {
                       if(removeAllGenres.isSelected()){
                           addAllGenres.setSelected(false);
                           //Set all Genres selected to False
                           for(Map.Entry value : genreCheckBoxList.entrySet()){
                                CheckBox vCB = (CheckBox) value.getValue();
                                vCB.setSelected(false);
                                //genresSelected.remove((String)value.getKey());
                            }
                       } 
                    });

                genreDropDownBoxTop.getChildren().addAll(addAllGenres, genreDropDownBoxLabel, removeAllGenres);

                FlowPane genreDropDownBoxMid = new FlowPane(); 
                genreDropDownBoxMid.setHgap(10);
                genreDropDownBoxMid.setVgap(10);
                    genreList = new ArrayList<>();
                    for(GENRES g : GENRES.values()){
                        genreList.add(g.getAbbreviation());
                    }
                    genreCheckBoxList = new HashMap<>();
                    for(String genre : genreList){
                        HBox genreBox = new HBox();
                        genreBox.setSpacing(5);
                            CheckBox genreCB = new CheckBox();
                            genreCB.selectedProperty().addListener((o, oldV, newV)-> {

                                if(genreCB.isSelected()){
                                    genresSelected.add(genre);
                                    advancedSearchButton.setDisable(false);
                                    if(genresSelected.size()==genreList.size()){
                                        addAllGenres.setSelected(true);
                                    }
                                    if(removeAllGenres.isSelected()){
                                        removeAllGenres.setSelected(false);
                                    }
                                } else {
                                    genresSelected.remove(genre);
                                    if(genresSelected.isEmpty()){
                                        advancedSearchButton.setDisable(true);
                                        removeAllGenres.setSelected(true);
                                    }
                                    if(addAllGenres.isSelected()){
                                        addAllGenres.setSelected(false);
                                    }
                                }
                            });
                            Label genreLB = new Label(genre);
                            genreLB.setFont(Font.font("Times New Roman", 12));
                            genreLB.setMinWidth(35);
                            genreLB.setMaxWidth(35);

                            //Add Genre and CB to hashmap 
                            genreCheckBoxList.put(genre, genreCB);

                        genreBox.getChildren().addAll(genreCB, genreLB);
                        genreDropDownBoxMid.getChildren().add(genreBox);
                    }
                    addAllGenres.setSelected(true); //Default true for all genres
            genreDropDownBox.getChildren().addAll(genreDropDownBoxTop, genreDropDownBoxMid);

            
            //HBox: Label, VBox: HBox(1 Dropdown, 3 TextFields, 3 Labels), HBox(1 Dropdown, 3 TextFields, 3 Labels)
            HBox dateFilterContainer = new HBox();
            dateFilterContainer.setSpacing(10);
            dateFilterContainer.setAlignment(Pos.CENTER_LEFT);
            dateFilterContainer.setPadding(new Insets(0,0,0,20));
                activateDateFilter = new RadioButton();
                activateDateFilter.setSelected(false);
                activateDateFilter.setOnAction(e->{
                   if(activateDateFilter.isSelected()){
                       dateComboBoxLeft.setDisable(false);
                       datePickerLeft.setDisable(false);

                   } else {
                       dateComboBoxLeft.setDisable(true);
                       dateComboBoxRight.setDisable(true);
                       datePickerLeft.setDisable(true);
                       datePickerRight.setDisable(true);
                   }
                });
                Label dateFilterLabel = new Label("Date:");
                dateFilterLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
                dateFilterLabel.setMinWidth(100);
                dateFilterLabel.setMaxWidth(100);

                ObservableList<String> dateComboBoxChoicesLeft = FXCollections.observableArrayList();
                dateComboBoxChoicesLeft.add("Equals");
                dateComboBoxChoicesLeft.add("Between");
                dateComboBoxChoicesLeft.add("Before");
                dateComboBoxChoicesLeft.add("After");
                dateComboBoxLeft = new ComboBox(dateComboBoxChoicesLeft);
                dateComboBoxLeft.setMinWidth(150);
                dateComboBoxLeft.setMaxWidth(150);
                dateComboBoxLeft.setDisable(true);
                dateComboBoxLeft.setValue("Equals");
                dateComboBoxLeft.setOnAction(e->{
                   if(dateComboBoxLeft.getValue().equals("Between")){
                       dateComboBoxRight.setDisable(false);
                       datePickerRight.setDisable(false);
                   } else {
                       dateComboBoxRight.setDisable(true);
                       datePickerRight.setDisable(true);
                   }
                });
                
                datePickerLeft = new DatePicker();
                datePickerLeft.setDisable(true);
                datePickerLeft.setMinWidth(50);
                datePickerLeft.setValue(LocalDate.now());
                
                ObservableList<String> dateComboBoxChoicesRight = FXCollections.observableArrayList();
                dateComboBoxChoicesRight.add("And");
                dateComboBoxRight = new ComboBox(dateComboBoxChoicesRight);
                dateComboBoxRight.setMinWidth(150);
                dateComboBoxRight.setMaxWidth(150);
                dateComboBoxRight.setDisable(true);
                dateComboBoxRight.setValue("And");
                        
                datePickerRight = new DatePicker();
                datePickerRight.setDisable(true);
                datePickerRight.setMinWidth(50);
                datePickerRight.setValue(LocalDate.now().minusDays(7));
                    
            dateFilterContainer.getChildren().addAll
            (
                activateDateFilter, dateFilterLabel, 
                dateComboBoxLeft, datePickerLeft, 
                dateComboBoxRight, datePickerRight
            );

            //Button "Search"
            advancedSearchButtonBox = new HBox();
            advancedSearchButtonBox.setAlignment(Pos.CENTER_RIGHT);
            advancedSearchButtonBox.setSpacing(25);
            
                Label limitLabel = new Label("Data Limit");
            
                limitSelection = new ComboBox();
                limitSelection.getStyleClass().add("center-aligned");
                limitSelection.getItems().addAll(500, 1000, 2500, 5000, 10000, 25000, 50000);
                limitSelection.setValue(5000);
                
                clearFiltersButton = new Button("Reset Filters");
                clearFiltersButton.setMinWidth(150);
                clearFiltersButton.setMaxWidth(150);

                advancedSearchButton = new Button("Submit Search");
                advancedSearchButton.setMinWidth(150);
                advancedSearchButton.setMaxWidth(150);
                
                pi = new ProgressIndicator();
                
            advancedSearchButtonBox.getChildren().addAll(limitLabel, limitSelection, clearFiltersButton, advancedSearchButton);
        
        advancedSearchPaneBorder.getChildren().addAll(hideAdvancedPaneBox, advancedSearchLabel, 
                advancedSearchFilterBox, columnsVBox, genreDropDownBox, 
                dateFilterContainer, advancedSearchButtonBox);
    }
    
    private void tableLoadIndicatorPane(){
        transparentPane = new VBox();
        
        transparentPane.setMinSize(databaseTableView.getMinWidth(), 50);
        transparentPane.setMaxSize(databaseTableView.getMaxWidth(), 150);
        transparentPane.setPrefSize(databaseTableView.getPrefWidth(), 150);
        transparentPane.setAlignment(Pos.TOP_CENTER);
            piTable = new ProgressIndicator();
            piTable.setMinSize(50, 50);
            piTable.setMaxSize(150, 150);
        transparentPane.getChildren().add(piTable);
    }
    
    public void setButtonAction(Button b, EventHandler<ActionEvent> event){
        b.setOnAction(event);
    }
    
    public void setTableView(TableView view){
        this.databaseTableView = view;
        setTableViewDefaults();
        sqlTableBox.getChildren().clear();
        sqlTableBox.getChildren().addAll(sqlTableSize, sqlTableSelectedSize, databaseTableView, copierPaneSwitchBox);
        
    }
    
    private void setTableViewDefaults(){
        databaseTableView.setPrefHeight(stackPane.getMaxHeight()*.75);
        
        databaseTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                sqlTableSelectedSize.setText("Songs Selected: " + databaseTableView.getSelectionModel().getSelectedItems().size());
            }
        });
        
        databaseTableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) databaseTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });
        
        sqlTableSize.setText("Total: " + databaseTableView.getItems().size());
        
        databaseTableView.setContextMenu(dbContextMenu.getContextMenu());
    }
    
    public void showAdvancedSearchPane(){
        stackPane.getChildren().add(advancedSearchPaneBorder);
        filterIsShowing = true;
    }
    
    public void hideAdvancedSearchPane(){
        stackPane.getChildren().remove(advancedSearchPaneBorder);
        filterIsShowing = false;
    }
    
    public ArrayList<String> getGenresSelected(){
        return this.genresSelected;
    }
    
    public ArrayList<String> getColsSelected(){
        return this.colsSelected;
    }
    
    public String getQuickTextField(){
        return this.quickSearchField.getText();
    }
    
    public TextField getQuickSearchField(){
        return this.quickSearchField;
    }
    
    public TextField getAdvancedSearchField(){
        return this.advancedSearchField;
    }
    
    public String getAdvancedTextField(){
        return this.advancedSearchField.getText();
    }
    
    public TableView getTableView(){
        return this.databaseTableView;
    }
    
    public Button getRefreshButton(){
        return this.refreshButton;
    }
    
    public Button getQuickClearButton(){
        return this.clearButton;
    }
    
    public Button getQuickAdvancedClearFilterButton(){
        return this.clearAdvancedFilterButton;
    }
    
    public Button getHideAdvancedPaneButton(){
        return this.hideAdvancedPaneButton;
    }
    
    public void clearFilters(){
        advancedClearButton.fire();
        setDefaultColumnsSelected();
        setDefaultDateFilter();
        addAllGenres.setSelected(true);
        limitSelection.setValue(5000);

    }
    
    private void setDefaultColumnsSelected(){
        //Change this method when default columns want to be changed.
        if(!artistCheckBox.isSelected()){
            artistCheckBox.fire();
        }
        if(!titleCheckBox.isSelected()){
            titleCheckBox.fire();
        }
        if(!dateCheckBox.isSelected()){
            dateCheckBox.fire();
        }
        if(!timeCheckBox.isSelected()){
            timeCheckBox.fire();
        }
        if(!genreCheckBox.isSelected()){
            genreCheckBox.fire();
        }
        if(!durationCheckBox.isSelected()){
            durationCheckBox.fire();
        }
        if(!sizeCheckBox.isSelected()){
            sizeCheckBox.fire();
        }
        if(!filenameCheckBox.isSelected()){
            filenameCheckBox.fire();
        }
        if(filetypeCheckBox.isSelected()){
            filetypeCheckBox.fire();
        }
        if(pathCheckBox.isSelected()){
            pathCheckBox.fire();
        }
    }
    
    private void setDefaultDateFilter(){
        dateComboBoxLeft.setValue("Equals");
        datePickerLeft.setValue(LocalDate.now());
        datePickerRight.setValue(LocalDate.now().minusDays(7));
        if(activateDateFilter.isSelected()){
            activateDateFilter.fire();
        }
    }
    
    public Button getAdvancedClearButton(){
        return this.advancedClearButton;
    }
    
    public Button getAdvancedSearchButton(){
        return this.advancedSearchButton;
    }
    
    public Button getClearFiltersButton(){
        return this.clearFiltersButton;
    }
    
    public Object getLimitSelected(){
        return this.limitSelection.getValue();
    }
    
    public void showPI(){
        advancedSearchButtonBox.getChildren().add(pi);
        stackPane.getChildren().add(transparentPane);
        this.piShown = true;
    }
    
    public void hidePI(){
        advancedSearchButtonBox.getChildren().remove(pi);
        stackPane.getChildren().remove(transparentPane);
        this.piShown = false;
    }
    
    public boolean piShown(){
        return this.piShown;
    }

    public boolean isDateFilterSelected(){
        if(activateDateFilter.isSelected()){
            return true;
        } else {
            return false;
        }
    }
    
    public String getDateOption(){
        return (String) dateComboBoxLeft.getValue();
    }
    
    public LocalDate getDateValueLeft(){
        return datePickerLeft.getValue();
    }
    
    public LocalDate getDateValueRight(){
        return datePickerRight.getValue();
    }
    
    public void setCopyListButtonAction(EventHandler<ActionEvent> event){
        this.copyListButton.setOnAction(event);
    }
    
    public void setCopyListLabel(String s){
        this.copyListLabel.setText(s);
    }
    
    private void createColsList(){
        colsList.add(sqlArtistCol);
        colsList.add(sqlTitleCol);
        colsList.add(sqlDateCol);
        colsList.add(sqlTimeCol);
        colsList.add(sqlGenreCol);
        colsList.add(sqlDurationCol);
        colsList.add(sqlSizeCol);
        colsList.add(sqlFilenameCol);
        colsList.add(sqlFiletypeCol);
        colsList.add(sqlPathCol);
    }
    
    public ArrayList<String> getColsList(){
        return this.colsList;
    }
}
