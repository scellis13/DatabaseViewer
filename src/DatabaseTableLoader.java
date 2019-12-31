/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DatabaseTableLoader {
    private String  quickQuery,
                    advancedQuery, 
                    quickFieldStatement, 
                    colSelectStatement, 
                    advancedFieldStatement, 
                    genreStatement,
                    dateStatement,
                    sortStatement, 
                    limitStatement;
    
    private final DatabaseDisplay databaseDisplay;
    private final Connection myConn;
    private final SelectionDisplay selectionDisplay;
    private TableView databaseTableView;
    private ObservableList<ObservableList<String>> tableData;
    private Thread queryThread;
    private Task<Void> queryTask;
    private HashMap<String, Integer> colLookUpMap;
    
    public DatabaseTableLoader(DatabaseDisplay databaseDisplay, Connection conn, SelectionDisplay selectionDisplay){
        this.databaseDisplay = databaseDisplay;
        this.myConn = conn;
        this.selectionDisplay = selectionDisplay;
    }
    
    public void generateData(boolean quick){
        try {
            queryTask.cancel();
            queryThread.interrupt();
        } catch (NullPointerException ex){};
        
        queryTask = new Task<Void>(){
            
            @Override
            protected synchronized Void call() throws Exception{
                
                Platform.runLater(()->{
                    if(!databaseDisplay.piShown()){
                        databaseDisplay.showPI();
                        databaseDisplay.getAdvancedSearchButton().setDisable(true);
                        databaseDisplay.getQuickAdvancedClearFilterButton().setDisable(true);
                    }
                });
                
                if(quick){
                    try {
                        queryThread.sleep(1000);
                        showDataQuick();
                    } catch (Exception e){};
                } else {
                    showDataAdvanced();
                }
                
                Platform.runLater(()->{
                    if(databaseDisplay.piShown()){
                        databaseDisplay.hidePI();
                        databaseDisplay.getAdvancedSearchButton().setDisable(false);
                        databaseDisplay.getQuickAdvancedClearFilterButton().setDisable(false);
                    }
                });
                
                return null;
            }
        };
        
        queryThread = new Thread(queryTask);
        queryThread.setDaemon(true);
        queryThread.start();
    }
    
    private void showDataQuick(){
        
        
        quickFieldStatement = generateSearchStatement(databaseDisplay.getQuickTextField());
        
        
        quickQuery = colSelectStatement + " where " + quickFieldStatement
                + " AND " + advancedFieldStatement + genreStatement + dateStatement + sortStatement;
        quickQuery += " limit 500;";

        loadDatabase(quickQuery);

    }
    
    private void showDataAdvanced(){

        colSelectStatement = getColumnSelectStatement();
        quickFieldStatement = generateSearchStatement(databaseDisplay.getQuickTextField());
        advancedFieldStatement = generateSearchStatement(databaseDisplay.getAdvancedTextField());
        genreStatement = generateGenreStatementExact();
        dateStatement = generateDateStatement();
        sortStatement = generateSortStatement();
        limitStatement = generateLimitStatement();
        
        advancedQuery = colSelectStatement + " where " + quickFieldStatement
                + " AND " + advancedFieldStatement + genreStatement + dateStatement + sortStatement;
        advancedQuery += limitStatement + ";";

        loadDatabase(advancedQuery);
        
    }
    
    private String getColumnSelectStatement(){
        
        String selectColumns = "SELECT ";
        
        if(databaseDisplay.getColsSelected().contains("song_artist")){
            selectColumns+="song_artist, ";
        }
        if(databaseDisplay.getColsSelected().contains("song_title")){
            selectColumns+="song_title, ";
        }
        if(databaseDisplay.getColsSelected().contains("date_added")){
            selectColumns+="date_added, ";
        }
        if(databaseDisplay.getColsSelected().contains("time_added")){
            selectColumns+="time_added, ";
        }
        if(databaseDisplay.getColsSelected().contains("genre_list")){
            selectColumns+="genre_list, ";
        }
        if(databaseDisplay.getColsSelected().contains("duration")){
            selectColumns+="duration, ";
        }
        if(databaseDisplay.getColsSelected().contains("size")){
            selectColumns+="size, ";
        }
        if(databaseDisplay.getColsSelected().contains("file_name")){
            selectColumns+="file_name, ";
        }
        if(databaseDisplay.getColsSelected().contains("file_type")){
            selectColumns+="file_type, ";
        }
        if(databaseDisplay.getColsSelected().contains("path")){
            selectColumns+="path, ";
        }
        
        selectColumns = selectColumns.substring(0, selectColumns.length()-2);
        selectColumns += " from gotradio_db.songs";
        return selectColumns;
    }
    
    public int getColumn(String s){
        return colLookUpMap.get(s);
    }
    
    private String generateSearchStatement(String search){

        //Split by Spaces
        String searchSplitBySpaces = search;
        String searchSplitBySpaces_temp = "";
        String[] spaceSplit = search.split(" ");
        
        if(spaceSplit.length>1){
            for(String str : spaceSplit){
                String apostropheSearch = str;
                if(str.contains("'")){
                    if(str.equals("'")){
                        apostropheSearch += "'";
                    } else {
                        String refinedSearch = "";
                        String[] arr = str.split("'");

                        if(arr.length>1){
                            for(String s : arr){
                                refinedSearch += s + "''";
                            }
                            apostropheSearch = refinedSearch.substring(0,refinedSearch.length()-2);
                        } 
                    }
                }
                searchSplitBySpaces_temp += " (file_name like '%"+apostropheSearch+"%') AND";
            }
            searchSplitBySpaces = searchSplitBySpaces_temp.substring(0,searchSplitBySpaces_temp.length()-4);   
        } else {
            String apostropheSearch = searchSplitBySpaces;
                if(searchSplitBySpaces.contains("'")){
                    if(searchSplitBySpaces.equals("'")){
                        apostropheSearch += "'";
                    } else {
                        String refinedSearch = "";
                        String[] arr = searchSplitBySpaces.split("'");

                        if(arr.length>1){
                            for(String s : arr){
                                refinedSearch += s + "''";
                            }
                            apostropheSearch = refinedSearch.substring(0,refinedSearch.length()-2);
                        } 
                    }
                }    
            
            searchSplitBySpaces = "(file_name like '%" + apostropheSearch + "%')";
        }
        
        System.out.println("Filename Search: " + searchSplitBySpaces);
        
        return searchSplitBySpaces;
    }
    
    private String generateGenreStatementExact(){
        String tempStatement = "";
        if(!databaseDisplay.getGenresSelected().isEmpty()){
            tempStatement = "AND (";
                for(String genre : databaseDisplay.getGenresSelected()){
                    tempStatement+="(genre_list like '" + genre + "' or "
                            + "genre_list like '% " + genre + "%' or "
                            + "genre_list like '%" + genre + ",%' or "
                            + "genre_list like '%," + genre + "%') or";
                }
            tempStatement = tempStatement.substring(0, tempStatement.length()-3);
            tempStatement += ")";
        } 
        return tempStatement;
    }
    
    private String generateDateStatement(){
        
        if(databaseDisplay.isDateFilterSelected()){
            dateStatement = "AND ( date_added";
            if(databaseDisplay.getDateOption().equals("Between")){
                String option = " between ";
                LocalDate leftDate = databaseDisplay.getDateValueLeft();
                LocalDate rightDate = databaseDisplay.getDateValueRight();
                if(leftDate.isBefore(rightDate)){
                    String dateLeft = "'" + databaseDisplay.getDateValueLeft().toString() + "'";
                    String dateRight = "'" + databaseDisplay.getDateValueRight().toString() + "'";
                    dateStatement += option + dateLeft + " and " + dateRight;
                } else {
                    String dateLeft = "'" + databaseDisplay.getDateValueRight().toString() + "'";
                    String dateRight = "'" + databaseDisplay.getDateValueLeft().toString() + "'";
                    dateStatement += option + dateLeft + " and " + dateRight;
                }
            } else if(databaseDisplay.getDateOption().equals("Before")){
                String option = " < ";
                String dateLeft = "'" + databaseDisplay.getDateValueLeft().toString() + "'";
                dateStatement += option + dateLeft;
            } else if (databaseDisplay.getDateOption().equals("After")){
                String option = " > ";
                String dateLeft = "'" + databaseDisplay.getDateValueLeft().toString() + "'";
                dateStatement += option + dateLeft;
            } else {
                //get only the left date picker
                String option = " = ";
                String dateLeft = "'" + databaseDisplay.getDateValueLeft().toString() + "'";
                dateStatement += option + dateLeft;
            }
            dateStatement += ")";
        } else {
            dateStatement = "";
        }
        return dateStatement;
    }  
    
    private String generateSortStatement(){
        String orderStatement = "";
        if(databaseDisplay.getColsSelected().contains("date_added")){
            if(databaseDisplay.getColsSelected().contains("time_added")){
                orderStatement += " order by date_added desc, time_added desc";
            } else {
                orderStatement += " order by date_added desc";
            }
        }
        
        return orderStatement;
    }
    
    private String generateLimitStatement(){
        String statement = " limit " + databaseDisplay.getLimitSelected();
        return statement;
    }
    
    private void loadDatabase(String search){
        GRDB.totalSearches++;
        Platform.runLater(()->{
            selectionDisplay.setSearchCount(GRDB.totalSearches);
        });
        
        try{
            ResultSet result;

            result = myConn.prepareStatement(search).executeQuery();

            ResultSetMetaData data = result.getMetaData();
            int cols = data.getColumnCount();
            List<String> colList = Collections.synchronizedList(new ArrayList<String>(cols));

            TableView databaseTableViewTemp = new TableView();
            databaseTableViewTemp.setTableMenuButtonVisible(true);
            databaseTableViewTemp.getSelectionModel().setSelectionMode(
            SelectionMode.MULTIPLE);
            //databaseTableView.setPrefWidth(BOUNDS.getWidth()*.75);
            for(int i = 1; i <= cols; i++){
                final int finalInd = i - 1;
                colList.add(data.getColumnName(i));
                TableColumn<ObservableList<String>, String> tblCol = new TableColumn(data.getColumnName(i));
                tblCol.setCellValueFactory(param ->
                        new ReadOnlyObjectWrapper<>(param.getValue().get(finalInd))
                    );
                databaseTableViewTemp.getColumns().add(tblCol); 
            }

            colLookUpMap = new HashMap<>();
            for(int i = 0; i < cols; i++){
                colLookUpMap.put(colList.get(i), i);
            }

            ObservableList<ObservableList<String>> tableDataTemp = FXCollections.observableArrayList();

            while(result.next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i = 0; i < cols; i++){
                   row.add(result.getString(colList.get(i)));
                }
                tableDataTemp.add(row);
            }
                Platform.runLater(()->{
                    databaseTableViewTemp.setItems(tableDataTemp);
                    databaseDisplay.setTableView(databaseTableViewTemp);
                });


        } catch (SQLException ex){
            System.out.println("Query Execute Failed.");
            System.out.println(ex.getMessage());
        }
    }
}
