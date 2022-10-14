package org.cnpem.fitotron.file;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.File;

public class ObservableFile implements ObservableValue<File> {

    private ChangeListener<? super File> addChangeListener, removeChangeListener;
    private InvalidationListener addInvalidationListener, removeInvalidationListener;
    private File file;

    public ObservableFile(){
        //Construtor vazio...
    }

    @Override
    public void addListener(ChangeListener<? super File> listener) {
        this.addChangeListener = listener;
    }

    @Override
    public void removeListener(ChangeListener<? super File> listener) {
        this.removeChangeListener = listener;
    }

    public void setValue(File file){
        File oldValue = this.file;
        File newValue = file;
        this.file = newValue;
        if(addChangeListener!=null){
            addChangeListener.changed(null,oldValue,newValue);
        }
    }

    @Override
    public File getValue() {
        return file;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        this.addInvalidationListener = listener;
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        this.removeInvalidationListener = listener;
    }
}
