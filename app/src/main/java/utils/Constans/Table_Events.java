package utils.Constans;

/**
 * Created by Ravid on 28/10/2015.
 */
public final class Table_Events {

    public final static String Table_Name = "Events";

    public final static String Event_ID = "Event_ID";
    private final static String Event_ID_SQL_Params = "varchar NOT NULL primary key";
    public final static String Name = "Name";
    private final static String Name_SQL_Params = "varchar NOT NULL";
    public final static String Location = "Location";
    private final static String Location_SQL_Params = "varchar NOT NULL";
    public final static String Start_Date = "Start_Date";
    private final static String Start_Date_SQL_Params = "varchar NOT NULL";
    public final static String Start_Time = "Start_Time";
    private final static String Start_Time_SQL_Params = "varchar NOT NULL";
    public final static String End_Date = "End_Date";
    private final static String End_Date_SQL_Params = "varchar NOT NULL";
    public final static String End_Time = "End_Time";
    private final static String End_Time_SQL_Params = "varchar NOT NULL";
    public final static String Description = "Description";
    private final static String Description_SQL_Params = "varchar";
    public final static String Image_Path = "Image_Path";
    private final static String ImagePath_SQL_Params = "varchar NOT NULL";
    public final static String Update_Time = "Update_Time";
    private final static String Update_Time_SQL_Params = "varchar NOT NULL";


    public final static String[] getAllFields(){
        return new String[]{Event_ID, Name, Location, Start_Date, Start_Time, End_Date, End_Time, Description, Image_Path, Update_Time};
    }

    public final static String[] getAllFields_Except_Event_ID(){
        return new String[]{Name, Location, Start_Date, Start_Time, End_Date, End_Time, Description, Image_Path, Update_Time};
    }


    public final static String[] getAllSqlParams(){
        return new String[]{Event_ID_SQL_Params, Name_SQL_Params, Location_SQL_Params, Start_Date_SQL_Params, Start_Time_SQL_Params, End_Date_SQL_Params,
                End_Time_SQL_Params, Description_SQL_Params, ImagePath_SQL_Params, Update_Time_SQL_Params};
    }

    public static int Size(){
        return getAllFields().length;
    }

    public static int parseInt(String Field){
        switch (Field) {
            case Event_ID: {return 0; }
            case Name: {return 1; }
            case Location: {return 2; }
            case Start_Date: {return 3; }
            case Start_Time: {return 4; }
            case End_Date: {return 5;}
            case End_Time: {return 6; }
            case Description: {return 7; }
            case Image_Path: {return 8; }
            case Update_Time: {return 9; }
        }
        return -1;
    }

}
