package com.port.serial;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


// piece of Observer Pattern
public class StringValueClass {

    private static String value ;
    protected PropertyChangeSupport propertyChangeSupport;


    public StringValueClass( ){
        propertyChangeSupport = new PropertyChangeSupport(this);
        System.out.println("Created support");
    }

    public void  setText(String text) {
        String oldText = this.value;
        propertyChangeSupport.firePropertyChange("MyTextProperty", oldText,text);
        System.out.println("value : "+ value);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
