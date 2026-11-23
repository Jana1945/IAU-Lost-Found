package com.example.iaulostfound;

public class Report {

    public static final String TYPE_LOST = "LOST";
    public static final String TYPE_FOUND = "FOUND";

    public static final String STATUS_LOST = "LOST";
    public static final String STATUS_FOUND = "FOUND";
    public static final String STATUS_RETURNED = "RETURNED";
    public static final String STATUS_PENDING = "PENDING";

    /** Intent extra key for passing a report id between screens. */
    public static final String EXTRA_REPORT_ID = "extra_report_id";

    private long id;
    private final String type;
    private final String title;
    private final String category;
    private final String description;
    private final String date;              // yyyy-MM-dd
    private final String building;
    private final String specificLocation;
    private final String status;
    private final String collectionPoint;   // optional, may be null

    public Report(String type, String title, String category, String description,
                  String date, String building, String specificLocation,
                  String status, String collectionPoint) {
        this.type = type;
        this.title = title;
        this.category = category;
        this.description = description;
        this.date = date;
        this.building = building;
        this.specificLocation = specificLocation;
        this.status = status;
        this.collectionPoint = collectionPoint;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getBuilding() { return building; }
    public String getSpecificLocation() { return specificLocation; }
    public String getStatus() { return status; }
    public String getCollectionPoint() { return collectionPoint; }
}