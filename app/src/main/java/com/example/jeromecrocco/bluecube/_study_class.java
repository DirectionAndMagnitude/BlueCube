package com.example.jeromecrocco.bluecube;

public final class _study_class {
    // fields


    private int studyID;
    private String title;
    private String motivation;
    private String objective;
    private String expType;
    private String expText;

    private String procText;
    private String procType;

    private String dataText;
    private String dataType;



    private static _study_class study = new _study_class();

    // constructors
    private _study_class() {
    }

    public static synchronized _study_class getInstance() {
        //Singleton Design Pattern
        if (study == null) {
            study = new _study_class();
        }
        return study;
    }

    ;

    public void setExpData(String text,String spinner) {
        this.setExp(text, spinner);

    }

    public void setIntroData(int id, String stitle, String smotivation, String sobjective) {
        this.setID(id);
        this.setStudyName(stitle);
        this.setMotivation(smotivation);
        this.setObjective((sobjective));

    }

    // properties
    public void setID(int id) {
        this.studyID = id;
    }

    public int getID() {
        return this.studyID;
    }

    public void setStudyName(String title) {
        this.title = title;
    }

    public String getStudyName() {
        return this.title;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getMotivation() {
        return this.motivation;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getObjective() {
        return this.objective;
    }



    public void setExp(String text,String spinner){
        this.expType = spinner;
        this.expText = text;
    }

    public String getExpText() {
        return this.expText;
    }

    public String getExpType() {
        return this.expType;
    }



    public void setData(String text,String spinner){
        this.dataType = spinner;
        this.dataText = text;
    }

    public String getDataText(){ return this.dataText;}
    public String getDataType(){ return this.dataType;}




    public void setProcData(String text, String spinner){
        this.procText = text;
        this.procType = spinner;
    }

    public String getProcSteps(){ return this.procType; }

    public String getProcText(){ return this.procText; }

}
