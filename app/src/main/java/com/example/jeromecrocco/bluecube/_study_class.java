package com.example.jeromecrocco.bluecube;

public class _study_class {
    // fields
    private int studyID;
    private String studyName;
    private String motivation;
    private String objective;

    // constructors
    public _study_class() {}


    public _study_class(int id, String studyname,String m,String o) {
        this.studyID = id;
        this.studyName = studyname;
        this.motivation = m;
        this.objective = o;

    }
    // properties
    public void setID(int id) {
        this.studyID = id;
    }
    public int getID() {
        return this.studyID;
    }
    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }
    public String getStudyName() {
        return this.studyName;
    }
}
